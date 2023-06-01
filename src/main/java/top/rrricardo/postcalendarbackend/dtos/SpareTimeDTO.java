package top.rrricardo.postcalendarbackend.dtos;

import java.time.LocalDateTime;

public class SpareTimeDTO {
    private LocalDateTime beginTime;
    private LocalDateTime endTime;

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
