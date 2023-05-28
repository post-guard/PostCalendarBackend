package top.rrricardo.postcalendarbackend.services;

import java.time.LocalDate;

public interface SystemAlarmService {
    void start();

    void refreshAlarms();
}
