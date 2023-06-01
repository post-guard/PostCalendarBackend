package top.rrricardo.postcalendarbackend.services.Impl;

import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.commons.LoggingQueue;
import top.rrricardo.postcalendarbackend.services.LoggingService;
import top.rrricardo.postcalendarbackend.websockets.LoggingWebSocketServer;

@Service
public class LoggingServiceImpl implements LoggingService {

    private boolean running = false;

    @Override
    public void start() {
        running = true;

        var thread = new Thread(() -> {
            while (running) {
                var message = LoggingQueue.popLogging();

                if (message != null) {
                    LoggingWebSocketServer.sendMessage(message);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException exception) {
                    throw new RuntimeException(exception);
                }
            }
        });

        thread.start();
    }
}
