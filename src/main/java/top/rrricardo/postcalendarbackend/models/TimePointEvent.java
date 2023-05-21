package top.rrricardo.postcalendarbackend.models;

import java.time.LocalDateTime;

/**
 * 时间点事件
 */
public class TimePointEvent {
    private int id;
    /**
     * 事件名称
     */
    private String name;
    /**
     * 事件详情
     */
    private String details;
    /**
     * 事件结束时间
     */
    private LocalDateTime endDateTime;
    /**
     * 事件发生地点
     */
    private int placeId;
    /**
     * 事件类型
     */
    private int type;
    /**
     * 事件所属用户ID
     */
    private int userId;
    /**
     * 事件所属组织ID
     */
    private int groupId;


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

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public int getGroupId() {
        return groupId;
    }
}
