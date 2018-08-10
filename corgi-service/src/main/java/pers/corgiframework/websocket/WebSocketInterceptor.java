package pers.corgiframework.websocket;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket拦截器
 * Created by syk on 2018/8/10.
 */
public class WebSocketInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> map) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
            // 获取请求参数
            String userId = serverHttpRequest.getServletRequest().getParameter("userId");
            if (StringUtils.isNotBlank(userId)) {
                map.put("userId", Integer.valueOf(userId));
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }

}
