package top.rrricardo.postcalendarbackend.services;

import java.time.LocalDateTime;

public interface SystemClockService {
    /**
     * 获得当前的时间
     * @return 当前系统时间
     */
    LocalDateTime getNow();

    /**
     * 获得当前放大倍数
     * @return 放大倍数
     */
    int getTime();

    /**
     * 加快时间流逝
     * @param time 需要加快的倍速
     */
    void speedUp(int time);

    /**
     * 减慢时间的流逝
     * @param time 需要减慢的倍速
     */
    void speedDown(int time);

    /**
     * 启动时间服务
     */
    void start();
}
