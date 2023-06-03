package top.rrricardo.postcalendarbackend.services.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.dtos.SpareTimeDTO;
import top.rrricardo.postcalendarbackend.models.TimeSpanEvent;
import top.rrricardo.postcalendarbackend.services.SpareTimeService;
import top.rrricardo.postcalendarbackend.services.TimeSpanEventService;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;

@Service
public class SpareTimeServiceImpl implements SpareTimeService {
    private final TimeSpanEventService timeSpanEventService;
    private final Logger logger;

    public SpareTimeServiceImpl(TimeSpanEventService timeSpanEventService) {
        this.timeSpanEventService = timeSpanEventService;

        logger = LoggerFactory.getLogger(getClass());
    }
    @Override
    public CustomList<SpareTimeDTO> queryUserSpareTime(int userId, LocalDate date, Duration length) {
        try {
            var events = timeSpanEventService.queryUserEvent(userId,
                LocalDateTime.of(date, LocalTime.MIN),
                LocalDateTime.of(date, LocalTime.MAX));

            var spareTimes = findSpareTimeHelper(events, (int) length.getSeconds());
            var result = new CustomList<SpareTimeDTO>();

            for (var time : spareTimes) {
                var spareTime = new SpareTimeDTO();
                spareTime.setBeginTime(LocalDateTime.of(date, LocalTime.ofSecondOfDay(time.beginTimeSecond)));
                spareTime.setEndTime(LocalDateTime.of(date, LocalTime.ofSecondOfDay(time.endTimeSecond)));
                result.add(spareTime);
            }

            return result;
        } catch (Exception e) {
            logger.warn("查询用户{} 日期{}的时间点事件失败", userId, date, e);
            return null;
        }

    }

    @Override
    public CustomList<SpareTimeDTO> queryGroupSpareTime(int groupId, LocalDate date, Duration length) {
        try {
            var events = timeSpanEventService.queryGroupEvent(groupId,
                LocalDateTime.of(date, LocalTime.MIN),
                LocalDateTime.of(date, LocalTime.MAX));

            var spareTimes = findSpareTimeHelper(events, (int) length.getSeconds());
            var result = new CustomList<SpareTimeDTO>();

            for (var time : spareTimes) {
                var spareTime = new SpareTimeDTO();
                spareTime.setBeginTime(LocalDateTime.of(date, LocalTime.ofSecondOfDay(time.beginTimeSecond)));
                spareTime.setEndTime(LocalDateTime.of(date, LocalTime.ofSecondOfDay(time.endTimeSecond)));
                result.add(spareTime);
            }

            return result;
        } catch (Exception e) {
            logger.warn("查询组织{} 日期{}的事件失败", groupId, date, e);
            return null;
        }
    }

    private CustomList<SpareTime> findSpareTimeHelper(CustomList<TimeSpanEvent> events, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("时间段长度不能为负数");
        }

        // 生成时间占用数组
        // 表示每一秒被占用的次数
        var occupationArray = new byte[86400];
        for (var event : events) {
            var beginTimeSecond = event.getBeginDateTime().toLocalTime().toSecondOfDay();
            var endTimeSecond = event.getEndDateTime().toLocalTime().toSecondOfDay();

            for (var i = beginTimeSecond; i <= endTimeSecond; i++) {
                occupationArray[i]++;
            }
        }

        var spareTimes = new CustomList<SpareTime>();

        var pos = 0;
        // 上一个选定时间段的占用率
        // 用来实现加法增乘法减策略
        var lastOccupation = Integer.MAX_VALUE;
        // 增加的范围
        var delta = 1;

        while (pos + length < 86400) {
            // 计算当前选定时间段的占用率
            var occupation = 0;
            for (var i = pos; i < pos + length; i++) {
                occupation = occupation + occupationArray[i];
            }

            var spareTime = new SpareTime();
            spareTime.beginTimeSecond = pos;
            spareTime.endTimeSecond = pos + length;
            spareTime.occupation = occupation;
            spareTimes.add(spareTime);

            if (occupation == 0) {
                // 该阶段没有占用
                pos = pos + length;
                continue;
            }

            if (occupation >= lastOccupation) {
                // 当前占用率大于上一段占用率
                pos = pos + delta;
                delta++;
                if (delta > length) {
                    // 一次移动的距离不能超过当前时间段的长度
                    delta = length;
                }
            } else {
                // 占用率下降
                delta = 1;
                pos = pos + delta;
            }
            lastOccupation = occupation;
        }

        spareTimes.sort(Comparator.comparingInt(a -> a.occupation));

        var result = new CustomList<SpareTime>();

        // 这里的pos表示的是spareTimes的索引
        pos = 0;
        while (result.getSize() < 3) {
            if (pos >= spareTimes.getSize()) {
                break;
            }

            var spareTime = spareTimes.get(pos);
            if (spareTime.beginTimeSecond < 3600 * 6) {
                // 排除太早的提醒
                pos++;
                continue;
            }

            // 标记是否同已经添加的时间段冲突
            var conflict = false;
            for (var time : result) {
                if (spareTime.beginTimeSecond < time.endTimeSecond
                        && spareTime.endTimeSecond > time.beginTimeSecond) {
                            conflict = true;
                            break;
                        }
            }

            if (!conflict) {
                result.add(spareTime);
            }
            pos++;
        }

        return result;
    }


    /**
     * 空闲时间段类
     */
    private static class SpareTime {
        private int beginTimeSecond;
        private int endTimeSecond;

        private int occupation;
    }
}
