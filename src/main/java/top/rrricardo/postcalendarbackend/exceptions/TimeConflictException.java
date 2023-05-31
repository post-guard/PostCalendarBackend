package top.rrricardo.postcalendarbackend.exceptions;

import top.rrricardo.postcalendarbackend.models.TimeSpanEvent;

import java.time.LocalDateTime;

/**
 * 时间冲突异常
 */
public class TimeConflictException extends Exception {
    private final int userId;
    private final int groupId;
    private final LocalDateTime beginTime;
    private final LocalDateTime endTime;

   public TimeConflictException(int userId, int groupId, LocalDateTime beginTime, LocalDateTime endTime) {
       this.userId = userId;
       this.groupId = groupId;
       this.beginTime = beginTime;
       this.endTime = endTime;
   }

    public int getUserId() {
        return userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

}
