package top.rrricardo.postcalendarbackend.services.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.services.SystemClockService;
import top.rrricardo.postcalendarbackend.websockets.ClockWebSocketServer;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class SystemClockServiceImpl implements SystemClockService, DisposableBean {
    private static boolean running = false;
    private final Logger logger;

    public SystemClockServiceImpl() {
        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public LocalDateTime getNow() {
        return ClockTask.now;
    }

    @Override
    public int getTime() {
        return ClockTask.time;
    }

    @Override
    public void speedUp(int time) {
        ClockTask.time = time * ClockTask.time;
    }

    @Override
    public void speedDown(int time) {
        var result = ClockTask.time / time;

        ClockTask.time = Math.max(result, 1);
    }

    @Override
    public void setNow(LocalDateTime dateTime) {
        ClockTask.now = dateTime;
    }

    @Override
    public void start() {
        running = true;

        var thread = new Thread(new ClockTask());
        thread.start();

        var websocketThread = new Thread(() -> {
            while (running) {
                try {
                    ClockWebSocketServer.sendClock(getNow());
                } catch (IOException e) {
                    logger.warn("时钟推送服务错误", e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        websocketThread.start();
    }

    @Override
    public void reset() {
        ClockTask.now = LocalDateTime.now();
        ClockTask.time = 1;
    }

    @Override
    public void destroy() {
        running = false;
    }

    private static class ClockTask implements Runnable {
        private static LocalDateTime now;
        private static int time = 1;

        @Override
        public void run() {
            now = LocalDateTime.now();

            while (running) {
                try {
                    Thread.sleep(1000);
                    now = now.plusSeconds(time);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
