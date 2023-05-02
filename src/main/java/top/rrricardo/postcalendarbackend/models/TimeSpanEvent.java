package top.rrricardo.postcalendarbackend.models;

import java.time.LocalDateTime;

/**
 * 时间段类型的事件
 */
public class TimeSpanEvent {
    private int id;
    /**
     * 日程名称
     */
    private String name;
    /**
     * 日程详情
     */
    private String details;
    /**
     * 用户ID
     */
    private int userId;
    /**
     * 群组ID
     */
    private int groupId;
    /**
     * 地点ID
     */
    private int placeId;
    /**
     * 日程开始时间
     */
    private LocalDateTime beginDateTime;
    /**
     * 日程结束时间
     */
    private LocalDateTime endDateTime;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public LocalDateTime getBeginDateTime() {
        return beginDateTime;
    }

    public void setBeginDateTime(LocalDateTime beginDateTime) {
        this.beginDateTime = beginDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}
