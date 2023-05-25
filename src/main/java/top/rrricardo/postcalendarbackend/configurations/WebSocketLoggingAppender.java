package top.rrricardo.postcalendarbackend.configurations;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import top.rrricardo.postcalendarbackend.commons.LoggingQueue;

public class WebSocketLoggingAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private Layout<ILoggingEvent> layout;

    @Override
    protected void append(ILoggingEvent event) {
        if (event == null || !isStarted()) {
            return;
        }

        LoggingQueue.pushLogging(layout.doLayout(event));
    }

    public Layout<ILoggingEvent> getLayout() {
        return layout;
    }

    public void setLayout(Layout<ILoggingEvent> layout) {
        this.layout = layout;
    }
}
