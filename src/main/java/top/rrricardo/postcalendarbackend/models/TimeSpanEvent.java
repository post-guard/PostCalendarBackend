package top.rrricardo.postcalendarbackend.models;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 时间段类型的事件
 */
public class TimeSpanEvent implements Comparable<TimeSpanEvent> {
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

    @Override
    public int compareTo(TimeSpanEvent event) {
        return getMiddleDateTime().compareTo(event.getMiddleDateTime());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof TimeSpanEvent event) {
            return id == event.id
                    && name.equals(event.name)
                    && details.equals(event.details)
                    && (userId == event.userId || groupId == event.groupId)
                    && placeId == event.placeId
                    && beginDateTime.equals(event.beginDateTime)
                    && endDateTime.equals(event.endDateTime);
        }

        return false;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        builder.append("事件名称：").append(name).append('\n');
        builder.append("事件详情：").append(details).append('\n');
        builder.append("事件发生的地点ID：").append(placeId).append('\n');
        if (userId != 0 && groupId == 0) {
            builder.append("用户ID：").append(userId).append('\n');
        } else if (userId == 0 && groupId != 0) {
            builder.append("组织ID：").append(groupId).append('\n');
        }
        builder.append("开始时间：").append(beginDateTime).append('\n');
        builder.append("结束时间：").append(endDateTime).append('\n');

        return builder.toString();
    }

    private LocalDateTime getMiddleDateTime() {
        var duration = Duration.between(beginDateTime, endDateTime);
        duration = duration.dividedBy(2);

        return beginDateTime.plus(duration);
    }
}
