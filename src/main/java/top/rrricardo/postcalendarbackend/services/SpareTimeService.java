package top.rrricardo.postcalendarbackend.services;

import top.rrricardo.postcalendarbackend.dtos.SpareTimeDTO;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.time.Duration;
import java.time.LocalDate;

/**
 * 查询空闲时间段服务
 */
public interface SpareTimeService {

    /**
     * 推荐指定用户指定时间冲突最少的三个时间段
     * @param userId 指定用户ID
     * @param date 指定日期
     * @param length 需要时间段长度
     * @return 推荐时间段列表
     */
    CustomList<SpareTimeDTO> queryUserSpareTime(int userId, LocalDate date, Duration length);

    /**
     * 推荐指定用户指定时间冲突最少的三个时间段
     * @param groupId 指定用户ID
     * @param date 指定日期
     * @param length 需要时间段长度
     * @return 推荐时间段列表
     */
    CustomList<SpareTimeDTO> queryGroupSpareTime(int groupId, LocalDate date, Duration length);
}
