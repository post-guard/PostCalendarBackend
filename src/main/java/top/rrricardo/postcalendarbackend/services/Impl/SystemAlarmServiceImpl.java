package top.rrricardo.postcalendarbackend.services.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.enums.AlarmType;
import top.rrricardo.postcalendarbackend.exceptions.TimePointEventException;
import top.rrricardo.postcalendarbackend.exceptions.TimeSpanEventException;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.Alarm;
import top.rrricardo.postcalendarbackend.services.SystemAlarmService;
import top.rrricardo.postcalendarbackend.services.SystemClockService;
import top.rrricardo.postcalendarbackend.services.TimePointEventService;
import top.rrricardo.postcalendarbackend.services.TimeSpanEventService;
import top.rrricardo.postcalendarbackend.websockets.AlarmWebSocketServer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.PriorityQueue;

@Service
public class SystemAlarmServiceImpl implements SystemAlarmService, DisposableBean {
    private static final PriorityQueue<Alarm> alarms = new PriorityQueue<>();
    private static LocalDate refreshDate;
    private static boolean running = false;
    private final TimeSpanEventService timeSpanEventService;
    private final TimePointEventService timePointEventService;
    private final SystemClockService systemClockService;
    private final UserMapper userMapper;
    private final Logger logger;

    public SystemAlarmServiceImpl(
            TimeSpanEventService timeSpanEventService,
            TimePointEventService timePointEventService,
            SystemClockService systemClockService,
            UserMapper userMapper) {
        this.timeSpanEventService = timeSpanEventService;
        this.timePointEventService = timePointEventService;
        this.systemClockService = systemClockService;
        this.userMapper = userMapper;

        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public void start() {
        logger.info("闹钟线程启动");
        running = true;

        // 闹钟线程
        var alarmThread = new Thread(() -> {
            while (running) {
                var now = systemClockService.getNow();
                var alarm = alarms.peek();

                if (alarm == null || now.isBefore(alarm.getTime())) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }

                synchronized (alarms) {
                    while (true) {
                        now = systemClockService.getNow();
                        alarm = alarms.peek();

                        if (alarm == null || now.isBefore(alarm.getTime())) {
                            break;
                        }

                        alarms.poll();
                        logger.info("闹钟触发成功：{}", alarm);
                        AlarmWebSocketServer.sendAlarm(alarm);
                    }
                }
            }
        });

        alarmThread.start();

        logger.info("闹钟刷新线程启动");
        // 刷新闹钟队列线程
        var refreshThread = new Thread(() -> {
            while (running) {
                try {
                    var today = systemClockService.getNow().toLocalDate();

                    // 当天已经在刷新时间之后
                    if (refreshDate == null) {
                        logger.info("刷新闹钟队列日期: {}", today);
                        refreshAlarms(today);
                        refreshDate = today;
                    } else {
                        while (today.isAfter(refreshDate)) {
                            refreshDate = refreshDate.plusDays(1);
                            logger.info("刷新闹钟队列日期: {}", refreshDate);
                            refreshAlarms(refreshDate);
                        }
                    }

                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        refreshThread.start();
    }

    @Override
    @Async
    public void refreshAlarms() {
        var today = systemClockService.getNow();
        logger.info("程序主动触发闹钟队列刷新");

        // 当调用这个接口时 清空闹钟列表
        alarms.clear();

        refreshAlarms(today, LocalDateTime.of(
                today.toLocalDate(),
                LocalTime.MAX
        ));
    }

    @Override
    public void destroy() {
        running = false;
    }

    /**
     * 刷新指定日期的闹钟到闹钟列表中
     *
     * @param date 需要刷新的列表
     */
    private void refreshAlarms(LocalDate date) {
        refreshAlarms(
                LocalDateTime.of(date, LocalTime.MIN),
                LocalDateTime.of(date, LocalTime.MAX)
        );
    }

    /**
     * 将指定时间段的事件闹钟刷新到队列中
     * 这个时间段必须在在一天内
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     */
    private void refreshAlarms(LocalDateTime beginTime, LocalDateTime endTime) {
        var users = userMapper.getUsers();
        var date = beginTime.toLocalDate();

        for (var user : users) {
            var userId = user.getId();
            // 添加时间段闹钟
            try {
                var timeSpanEvents = timeSpanEventService.queryUserEvent(userId, beginTime, endTime);
                var timePointEvents = timePointEventService.queryUserEvents(userId, beginTime, endTime);

                synchronized (alarms) {
                    for (var event : timeSpanEvents) {
                        alarms.add(new Alarm(event, AlarmType.OneHour, userId));
                        alarms.add(new Alarm(event, AlarmType.OnTime, userId));
                    }

                    for (var event : timePointEvents) {
                        alarms.add(new Alarm(event, AlarmType.OneHour, userId));
                        alarms.add(new Alarm(event, AlarmType.OnTime, userId));
                    }

                    alarms.add(new Alarm(AlarmType.Tomorrow, LocalDateTime.of(
                            beginTime.getYear(),
                            beginTime.getMonth(),
                            beginTime.getDayOfMonth(),
                            20,
                            0,
                            0
                    ), userId,
                            timeSpanEventService.queryUserEvent(userId,
                                    LocalDateTime.of(date, LocalTime.MIN),
                                    LocalDateTime.of(date, LocalTime.MAX)),
                            timePointEventService.queryUserEvents(userId,
                                    LocalDateTime.of(date, LocalTime.MIN),
                                    LocalDateTime.of(date, LocalTime.MAX))));
                }
            } catch (TimeSpanEventException e) {
                logger.error("查询时间段时间错误", e);
            } catch (TimePointEventException exception) {
                logger.error("查询时间点事件错误", exception);
            }
        }
    }
}
