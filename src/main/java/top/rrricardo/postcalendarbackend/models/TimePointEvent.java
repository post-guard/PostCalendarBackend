package top.rrricardo.postcalendarbackend.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * 时间点事件
 */
public class TimePointEvent implements Comparable<TimePointEvent> {
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
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
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

    @Override
    public int compareTo(TimePointEvent event) {
        return this.endDateTime.compareTo(event.endDateTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof TimePointEvent event) {
            return id == event.id
                    && name.equals(event.name)
                    && details.equals(event.details)
                    && endDateTime.equals(event.endDateTime)
                    && (userId == event.userId || groupId == event.groupId)
                    && placeId == event.placeId
                    && type == event.type;
        }
        return false;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        builder.append("事件名称：").append(name).append('\n');
        builder.append("事件详情：").append(details).append('\n');
        builder.append("事件发生地点ID：").append(placeId).append('\n');
        if (userId != 0 && groupId == 0) {
            builder.append("用户ID：").append(userId).append('\n');
        } else if (userId == 0 && groupId != 0) {
            builder.append("组织ID：").append(groupId).append('\n');
        }
        builder.append("事件类型：").append(type).append('\n');
        builder.append("事件发生时间：").append(endDateTime).append('\n');

        return builder.toString();
    }
}
