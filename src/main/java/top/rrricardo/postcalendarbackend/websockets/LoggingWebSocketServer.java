package top.rrricardo.postcalendarbackend.websockets;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import top.rrricardo.postcalendarbackend.commons.LoggingQueue;


@Component
@ServerEndpoint("/websocket/logging")
public class LoggingWebSocketServer implements DisposableBean {
    private final Logger logger;
    private boolean running = true;

    public LoggingWebSocketServer() {
        logger = LoggerFactory.getLogger(LoggingWebSocketServer.class);
    }

    @OnOpen
    public void onOpen(Session session) {
        LoggingQueue.start();
        var thread = new Thread(() -> {
            try {
                while (running) {
                    var message = LoggingQueue.popLogging();

                    if (message != null) {
                        session.getBasicRemote().sendText(message);
                    }
                }
            } catch (Exception e) {
                logger.error("发送日志消息失败：" + e.getMessage());
            }
        });

        thread.start();
    }

    @OnError
    public void onError(Session session, Throwable exception) {
        logger.warn("日志WebSocket错误：" + exception.getMessage());

        LoggingQueue.stop();
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        logger.info("日志WebSocket收到信息：" + message);
    }

    @OnClose
    public void onClose(Session session) {
        LoggingQueue.stop();
    }

    @Override
    public void destroy() {
        running = false;
    }
}
