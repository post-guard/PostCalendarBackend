package top.rrricardo.postcalendarbackend.dtos;

import java.time.LocalDateTime;

public class SystemTimeDTO {
    private int time;

    private LocalDateTime now;


    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public LocalDateTime getNow() {
        return now;
    }

    public void setNow(LocalDateTime now) {
        this.now = now;
    }
}
