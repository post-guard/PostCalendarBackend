package top.rrricardo.postcalendarbackend.websockets;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/clock/{id}")
public class ClockWebSocketServer {
    private static final ConcurrentHashMap<Integer, Session> sessionMap = new ConcurrentHashMap<>();
    private final Logger logger;

    public ClockWebSocketServer() {
        logger = LoggerFactory.getLogger(getClass());
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("id") int id) {
        sessionMap.put(id, session);

        logger.info("用户：{}时钟推送服务连接成功", id);
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") int id) {
        sessionMap.remove(id);

        logger.info("用户{}关闭时钟推送服务", id);
    }

    @OnError
    public void onError(Session session, Throwable exception, @PathParam("id") int id) {
        sessionMap.remove(id);

        logger.error("用户{}时钟推送服务异常", id, exception);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        logger.debug("闹钟websocket收到：{}", message);
    }

    /**
     * 发送时钟
     * @param time 需要发送的时钟
     */
    public static void sendClock(LocalDateTime time) throws IOException {
        for (var session : sessionMap.values()) {
            session.getBasicRemote().sendText(time.toString());
        }
    }
}
