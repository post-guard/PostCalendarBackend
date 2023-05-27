package top.rrricardo.postcalendarbackend.websockets;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@ServerEndpoint("/websocket/clock")
public class ClockWebSocketServer {
    private static Session session = null;
    private final Logger logger;

    public ClockWebSocketServer() {
        logger = LoggerFactory.getLogger(getClass());
    }

    @OnOpen
    public void onOpen(Session session) {
        ClockWebSocketServer.session = session;

        logger.info("时钟推送服务连接成功");
    }

    @OnClose
    public void onClose(Session session) {
        ClockWebSocketServer.session = null;

        logger.info("时钟推送服务关闭");
    }

    @OnError
    public void onError(Session session, Throwable exception) {
        ClockWebSocketServer.session = null;

        logger.info("时钟推送服务遇到错误", exception);
    }

    /**
     * 发送时钟
     * @param time 需要发送的时钟
     */
    public static void sendClock(LocalDateTime time) throws IOException {
        if (session != null) {
            session.getBasicRemote().sendText(time.toString());
        }
    }
}
