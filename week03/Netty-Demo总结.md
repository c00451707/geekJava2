对02nio.nio02的代码（利用Netty框架实现API网关例子）总结：（最后面还有问题）

步骤一、整个程序入口

// 这是多个后端url走随机路由的例子
String proxyServers = System.getProperty("proxyServers","http://localhost:8801,http://localhost:8802,http://localhost:8803");

HttpInboundServer server = new HttpInboundServer(port, Arrays.asList(proxyServers.split(",")));
try {
	server.run();
} catch (Exception ex){
	ex.printStackTrace();
}

步骤二、使用Recator主从模型 启动Server。

/**
 * Reactor 主从模型
 * 新建两个 EventLoopGroup 一个作为主 Reactor（bossGroup），一个作为从 Reactor（workerGroup）
 * 主 Reactor（bossGroup） 作用：负责处理所有的Client网络 请求链接 以及 请求的链接状态维护。
 * 从 Reactor（workerGroup）作用：负责将在内核中准备好了的请求的数据读取出来，然后做分发事件处理，将数据分发给业务线程处理。
 */
EventLoopGroup bossGroup = new NioEventLoopGroup(1);
EventLoopGroup workerGroup = new NioEventLoopGroup(16);

try {
	ServerBootstrap b = new ServerBootstrap();
	// 设置参数
	b.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.TCP_NODELAY, true)
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.childOption(ChannelOption.SO_REUSEADDR, true)
			.childOption(ChannelOption.SO_RCVBUF, 32 * 1024)
			.childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
			.childOption(EpollChannelOption.SO_REUSEPORT, true)
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
	// 将 主、从EventLoopGroup 与 ScoketChannel绑定。
	b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.DEBUG))
			.childHandler(new HttpInboundInitializer(this.proxyServers));

	Channel ch = b.bind(port).sync().channel();
	System.out.println("开启netty http服务器，监听地址和端口为 http://127.0.0.1:" + port + '/');
	ch.closeFuture().sync();
} finally {
	bossGroup.shutdownGracefully();
	workerGroup.shutdownGracefully();
}

步骤三、为 Channel的管道上添加 自定义Handler。

public class HttpInboundInitializer extends ChannelInitializer<SocketChannel> {
	
	private List<String> proxyServer;
	
	public HttpInboundInitializer(List<String> proxyServer) {
		this.proxyServer = proxyServer;
	}
	// 为管道添加 Handler的回调函数（ChannelInitializer 初始化，将Handler 添加到 SocketChannel的管道上去）
	@Override
	public void initChannel(SocketChannel ch) {
		// 通过 SocketChannel 获取 Channel的管道
		ChannelPipeline p = ch.pipeline();
		p.addLast(new HttpServerCodec());
		p.addLast(new HttpObjectAggregator(1024 * 1024));
		// 在Channel的管道上添加 HttpInboundHandler； HttpInboundHandler 负责 Channel 的IO数据读取。
		p.addLast(new HttpInboundHandler(this.proxyServer));
	}
}

// class ChannelInboundHandlerAdapter extends ChannelHandlerAdapter implements ChannelInboundHandler

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);
    private final List<String> proxyServer;
    private HttpOutboundHandler handler;
    private HttpRequestFilter filter = new HttpRequestFilterImp();
    
    public HttpInboundHandler(List<String> proxyServer) {
        this.proxyServer = proxyServer;
        this.handler = new HttpOutboundHandler(this.proxyServer);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
	
	// Channel 读取 IO 数据的回调函数
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            FullHttpRequest fullRequest = (FullHttpRequest) msg;
            handler.handle(fullRequest, ctx, filter);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}



步骤四、发送数据，并且将响应数据返回给前台。
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 *  这个类的理解： HttpOutboundHandler 并非真正的 Outbound,实际上 它只是一个 Service 类，Netty框架 当请求数据返回时会自动调用
 *  ChannelOutboundHandlerAdapter extends ChannelHandlerAdapter implements ChannelOutboundHandler 类的
 *  class ChannelOutboundHandlerAdapter extends ChannelHandlerAdapter implements ChannelOutboundHandler
 *  channelRead 方法，它比较适合对输出做统一处理。
 * HttpOutboundHandler 职责：负责与多个远程后端服务交互，并且将结果返回给前端。
 * 1、该类 通过 HttpEndpointRouter router 路由随机从多个厚度那服务列表平中获取一个后端服务IP+Port。
 * 2、该类 通过 HttpInboundHandler 传入过来的 HttpRequestFilter filter 过滤器，对 Netty过来的请求数据 做统一的过滤、处理。
 * 3、该类通过起一个 业务线程池 proxyService，来异步将请求的数据发送给选好的那个后端服务。
 * 4、该类 在proxyService 的某个线程中使用 httpclient 向指定的后端服务发送请求。
 * 5、当后端服务返回数据时，该类 通过 FutureCallback类的 completed回调函数获取 后端服务 响应体。
 * 6、该类利用 HttpResponseFilter filter 对后端响应数据进行统一的过滤或者统一处理后端的响应体数据。
 * 7、最后利用 ChannelHandlerContext ctx 的写方法 【ctx.write(response)】 将后端服务响应体发送出去，返回给前端。
 * 7、最后利用 ChannelHandlerContext ctx 的写方法 【ctx.write(response)】
 * 将后端服务响应体数据写入，在利用Netty框架发送数据给前台。
 * 数据返回给前台前会调用 ChannelHandlerAdapter类的 write 的回调函数
 *
 * 注意： 1、import io.netty.channel.ChannelInboundHandlerAdapter;
 *        2、import io.netty.channel.ChannelOutboundHandlerAdapter;
 *
 */
public class HttpOutboundHandler {
    private CloseableHttpAsyncClient httpclient;
    private ExecutorService proxyService;
    private List<String> backendUrls;

    HttpResponseFilter filter = new HeaderHttpResponseFilter();
    HttpEndpointRouter router = new RandomHttpEndpointRouter();

    public HttpOutboundHandler(List<String> backends) {

        this.backendUrls = backends.stream().map(this::formatUrl).collect(Collectors.toList());

        int cores = Runtime.getRuntime().availableProcessors();
        long keepAliveTime = 1000;
        int queueSize = 2048;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();//.DiscardPolicy();
        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory("proxyService"), handler);
        
        IOReactorConfig ioConfig = IOReactorConfig.custom()
                .setConnectTimeout(1000)
                .setSoTimeout(1000)
                .setIoThreadCount(cores)
                .setRcvBufSize(32 * 1024)
                .build();
        
        httpclient = HttpAsyncClients.custom().setMaxConnTotal(40)
                .setMaxConnPerRoute(8)
                .setDefaultIOReactorConfig(ioConfig)
                .setKeepAliveStrategy((response,context) -> 6000)
                .build();
        httpclient.start();
    }

    private String formatUrl(String backend) {
        return backend.endsWith("/") ? backend.substring(0,backend.length()-1) : backend;
    }
    
    public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, HttpRequestFilter filter) {
        String backendUrl = router.route(this.backendUrls);
        final String url = backendUrl + fullRequest.uri();
        filter.filter(fullRequest, ctx);
        proxyService.submit(()->fetchGet(fullRequest, ctx, url));
    }
    
    private void fetchGet(final FullHttpRequest inbound, final ChannelHandlerContext ctx, final String url) {
        final HttpGet httpGet = new HttpGet(url);
        //httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
        httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        httpGet.setHeader("mao", inbound.headers().get("mao"));
        // 利用 HttpClient 发送HttpGet 请求，向 backendUrls 后端服务列表
        httpclient.execute(httpGet, new FutureCallback<HttpResponse>() {
            @Override
            // 后端服务返回的响应 回调函数
            public void completed(final HttpResponse endpointResponse) {
                try {
                    handleResponse(inbound, ctx, endpointResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            public void failed(final Exception ex) {
                httpGet.abort();
                ex.printStackTrace();
            }
            
            @Override
            public void cancelled() {
                httpGet.abort();
            }
        });
    }
    
    private void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final HttpResponse endpointResponse) throws Exception {
        FullHttpResponse response = null;
        try {
            // endpointResponse 后端服务列表 返回的响应体
            byte[] body = EntityUtils.toByteArray(endpointResponse.getEntity());
            // 构建 一个响应体 存放后端服务响应体
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));

            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", Integer.parseInt(endpointResponse.getFirstHeader("Content-Length").getValue()));

            filter.filter(response);
        
        } catch (Exception e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            exceptionCaught(ctx, e);
        } finally {
            if (fullRequest != null) {
                // 将响应体 装进 Netty 服务响应体中,返回给前台
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    //response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
            }
            // 刷新上下文
            ctx.flush();
            //ctx.close();
        }
        
    }
    
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}


疑问：
1、对 Netty 框架的一些类的职责不清楚；FullHttpRequest、ChannelHandlerContext、FullHttpResponse 类的作用，有哪些功能？

2、ChannelInboundHandlerAdapter、ChannelOutboundHandlerAdapter 类各个方法怎么用？

3、Netty框架 处理业务的线程池需要 使用者自己创建，为什么它不做呢？

4、以上 代码 示例中，为什么对响应体的处理、过滤 不去实现 ChannelOutboundHandlerAdapter呢，而是统一写在自定义的 HttpOutboundHandler 类中，
此示例代码是否是最优实践（没用充分使用Netty提供的能力）？

5、Netty框架中是不是已经实现对前端请求的接收与发送，需要要我们去实现，Netty的实现代码在哪里可以看到呢，
ChannelHandlerContext ctx 这个对象 ctx.write、ctx.flush 方法是在干嘛？
