package netty.noio01;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.ReferenceCountUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
// 继承了 ChannelInboundHandlerAdapter 也就是 我们整个 NettyServer启动以后 客户端的请求进来，我们读取客户端请求的这个Handler
public class HttpHandler extends ChannelInboundHandlerAdapter {
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    /**
     * 重写了 channelRead 方法，可以通过客户端连接Netty的这个通道里面直接读取到我们的数据
     *  通过 ChannelHandlerContext 这个参数以及第二个参数Object msg，msg代表着这次请求的所有数据包装类的这样一个对象，
     *  客户端这次请求 所有数据，它的Http协议的报文相关信息躲在这个msg中
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            //logger.info("channelRead流量接口请求开始，时间为{}", startTime);
            // 将 msg 转型成 HttpRequest 对象就可以拿到它的内部结构，例如：url等
            FullHttpRequest fullRequest = (FullHttpRequest) msg;
            String uri = fullRequest.uri();
            //logger.info("接收到的请求url为{}", uri);
            if (uri.contains("/test")) {
                handlerTest(fullRequest, ctx,"hello,huster bin");
            } else if (uri.contains("/week03/homework")) {
                handlerTest(fullRequest, ctx,null);
            } else {
                handlerTest(fullRequest, ctx,"hello other");
            }
    
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 在 handlerTest 处理方法里我们就可以 直接再给我们的客户端 发送Http相关的报文。
     * 这里我们要组装一个 HttpResponse（也是Netty已经实现的） 对象
     * @param fullRequest
     * @param ctx
     * @param msg
     */
    private void handlerTest(FullHttpRequest fullRequest, ChannelHandlerContext ctx, String msg) {
        FullHttpResponse response = null;
        try {
            if (msg == null) {
                HttpClientDemo httpClient = new HttpClientDemo();
                msg = httpClient.doGetTestOne();
            }

            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", response.content().readableBytes());

        } catch (Exception e) {
            System.out.println("处理出错:"+e.getMessage());
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
