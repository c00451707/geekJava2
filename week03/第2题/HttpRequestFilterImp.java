package netty.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class HttpRequestFilterImp implements HttpRequestFilter {
    @Override
    public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        // 过滤请求
        if (fullRequest.uri().contains("api/hello")) {
            // 添加请求头
            fullRequest.headers().get("xjava","kimmking added by Huster-Bin");
        }
    }
}
