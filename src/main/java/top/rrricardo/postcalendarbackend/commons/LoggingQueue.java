package top.rrricardo.postcalendarbackend.commons;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 阻塞日志队列
 */
public class LoggingQueue {
    private static final BlockingQueue<String> loggingQueue = new ArrayBlockingQueue<>(Integer.MAX_VALUE >> 4);
    private static Boolean websocketStarted = false;

    /**
     * 添加一个日志到队列中
     * @param message 需要添加的日志消息
     */
    public static void pushLogging(String message) {
        if (!websocketStarted) {
            return;
        }

        try {
            loggingQueue.add(message);
        } catch (IllegalStateException ignored) {

        }
    }

    /**
     * 从队列中拿出一条日志消息
     * @return 如果队列中没有消息返回null
     */
    public static String popLogging() {
        if (!websocketStarted) {
            return null;
        }

        try {
            return loggingQueue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    /**
     * 启动日志队列
     */
    public static void start() {
        websocketStarted = true;
    }

    /**
     * 停止日志队列
     */
    public static void stop() {
        websocketStarted = false;
    }
}
