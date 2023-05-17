package top.rrricardo.postcalendarbackend.components;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Component
@Service
@ServerEndpoint("/connect")
public class WebSocketServer {
    private Session session = null;
    private final Logger logger;

    public WebSocketServer() {
        this.logger = LoggerFactory.getLogger(WebSocketServer.class);
    }


    @OnOpen
    public void onOpen(Session session) {
        this.session = session;

        logger.info("建立WebSocket连接成功");
        sendTextMessage("Hello!");
    }

    @OnClose
    public void onClose() {
        logger.info("关闭连接");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("收到信息：" + message);

        sendTextMessage("收到： " + message);
    }

    @OnError
    public void onError(Session session, Throwable exception) {
        logger.warn("遇到错误：" + exception.getMessage());
    }

    public void sendTextMessage(String message) {
        try {
            if (session != null) {
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException exception) {
            logger.warn("发送信息失败：" + exception.getMessage());
        }
    }
}
