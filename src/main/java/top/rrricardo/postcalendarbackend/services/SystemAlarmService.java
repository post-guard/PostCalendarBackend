package top.rrricardo.postcalendarbackend.services;

public interface SystemAlarmService {
    /**
     * 启动闹钟服务
     */
    void start();

    /**
     * 刷新闹钟队列
     * 用在修改事件之后
     */
    void refreshAlarms();
}
