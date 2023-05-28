package top.rrricardo.postcalendarbackend.enums;

/**
 * 表示闹钟类型的枚举
 */
public enum AlarmType {
    /**
     * 离事件开始还有一个小时
     */
    OneHour,

    /**
     * 事件即将开始
     */
    OnTime,

    /**
     * 明天的事件集合
     */
    Tomorrow
}
