package top.rrricardo.postcalendarbackend.websockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import top.rrricardo.postcalendarbackend.dtos.AlarmDTO;
import top.rrricardo.postcalendarbackend.models.Alarm;
import top.rrricardo.postcalendarbackend.models.TimePointEvent;
import top.rrricardo.postcalendarbackend.models.TimeSpanEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket/alarm/{id}")
@Component
public class AlarmWebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(AlarmWebSocketServer.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final ConcurrentHashMap<Integer, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("id") int id) {
        logger.info("用户： {} 连接成功", id);
        sessionMap.put(id, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") int id) {
        logger.info("用户：{} 退出连接", id);
        try (session) {
            sessionMap.remove(id);
        } catch (IOException e) {
            logger.error("关闭websocket连接失败", e);
        }
    }

    @OnError
    public void onError(Session session, @PathParam("id") int id, Throwable exception) {
        logger.warn("用户：{}遇到了错误", id, exception);

        try(session) {
            sessionMap.remove(id);
        } catch (IOException e) {
            logger.error("关闭websocket连接失败", e);
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        logger.debug("闹钟websocket收到：{}", message);
    }

    /**
     * 发送闹钟
     * @param alarm 需要发送的闹钟
     */
    public static void sendAlarm(Alarm alarm) {
        var response = new AlarmDTO();
        response.setAlarmTime(alarm.getTime());
        response.setMessage(alarm.getTypeString());
        response.setAlarmType(alarm.getType().ordinal());

        switch (alarm.getType()) {
            case OneHour, OnTime -> {
                if (alarm.getTimeSpanEvent() != null) {
                    var list = new ArrayList<TimeSpanEvent>();
                    list.add(alarm.getTimeSpanEvent());
                    response.setTimeSpanEvents(list);
                }
                if (alarm.getTimePointEvent() != null) {
                    var list = new ArrayList<TimePointEvent>();
                    list.add(alarm.getTimePointEvent());
                    response.setTimePointEvents(list);
                }
            }
            case Tomorrow -> {
                response.setTimeSpanEvents(alarm.getTimeSpanEvents().toList());
                response.setTimePointEvents(alarm.getTimePointEvents().toList());
            }
        }

        try {
            var result = objectMapper.writeValueAsString(response);

            var session = sessionMap.get(alarm.getUserId());

            if (session != null) {
                session.getBasicRemote().sendText(result);
                logger.info("发送闹钟给用户：{}成功：{}", alarm.getUserId(), alarm);
            }
        } catch (JsonProcessingException exception) {
            logger.error("格式化闹钟对象为JSON失败", exception);
        } catch (IOException e) {
            logger.error("发送闹钟失败", e);
        }
    }
}
