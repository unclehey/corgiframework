package pers.corgiframework.websocket;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * WebSocket处理器
 * Created by syk on 2018/8/10.
 */
@Component
public class MyHandler implements WebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyHandler.class);

    // 在线用户列表（登录）
    private static final Map<Integer, WebSocketSession> loginUsers;
    // 在线用户列表（所有）
    private static final List<WebSocketSession> allUsers;
    // 用户标识
    private static final String CLIENT_ID = "userId";
    static {
        loginUsers = Maps.newHashMap();
        allUsers = Lists.newArrayList();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        allUsers.add(session);
        Integer userId = getClientId(session);
        if (null != userId) {
            loginUsers.put(userId, session);
        }
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        allUsers.remove(session);
        Integer userId = getClientId(session);
        if (null != userId) {
            loginUsers.remove(userId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        try {
            if(session.isOpen()){
                session.close();
            }
            allUsers.remove(session);
            Integer userId = getClientId(session);
            if (null != userId) {
                loginUsers.remove(userId);
            }
        } catch (Exception e) {
            if (!(e instanceof EOFException)) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */
    public void sendMessageToAllUsers(TextMessage message) {
        for (WebSocketSession user : allUsers) {
            try {
                if (user.isOpen()) {
                    user.sendMessage(message);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 发送信息给指定用户
     * @param clientIds
     * @param message
     * @return
     */
    public void sendMessageToUser(List<Integer> clientIds, TextMessage message) {
        for (Integer clientId : clientIds) {
            if (loginUsers.get(clientId) == null) {
                continue;
            }
            WebSocketSession session = loginUsers.get(clientId);
            try {
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 获取用户标识
     * @param session
     * @return
     */
    private Integer getClientId(WebSocketSession session) {
        Integer clientId = null;
        try {
            if (null != session.getAttributes().get(CLIENT_ID)) {
                clientId = (Integer) session.getAttributes().get(CLIENT_ID);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return clientId;
    }

}
