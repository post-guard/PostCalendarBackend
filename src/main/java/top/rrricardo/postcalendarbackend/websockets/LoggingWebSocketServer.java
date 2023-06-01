package top.rrricardo.postcalendarbackend.websockets;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import top.rrricardo.postcalendarbackend.commons.LoggingQueue;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


@Component
@ServerEndpoint("/websocket/logging/{id}")
public class LoggingWebSocketServer {
    private final static Logger logger = LoggerFactory.getLogger(LoggingWebSocketServer.class);
    private static final ConcurrentHashMap<Integer, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("id") int id) {
        sessionMap.put(id, session);
        logger.info("开始向用户{}发送日志", id);

        LoggingQueue.start();
    }

    @OnError
    public void onError(Session session, Throwable exception, @PathParam("id") int id) {
        sessionMap.remove(id);

        logger.error("给用户发送{}日志发生错误", id, exception);

        if (sessionMap.size() == 0) {
            LoggingQueue.stop();
        }

    }

    @OnMessage
    public void onMessage(Session session, String message) {
        logger.debug("闹钟websocket收到：{}", message);
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") int id) {
        sessionMap.remove(id);

        logger.info("停止向用户{}发送日志", id);
        if (sessionMap.size() == 0) {
            LoggingQueue.stop();
        }
    }

    public static void sendMessage(String message) {
        for (var pair : sessionMap.entrySet()) {
            try {
                pair.getValue().getBasicRemote().sendText(message);
            } catch (IOException exception) {
                logger.info("给用户{}发送日志遇到错误", pair.getKey(), exception);
            }
        }
    }
}
