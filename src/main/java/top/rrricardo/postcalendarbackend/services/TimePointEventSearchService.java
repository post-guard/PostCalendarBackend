package top.rrricardo.postcalendarbackend.services;

import top.rrricardo.postcalendarbackend.exceptions.TimePointEventSearchException;
import top.rrricardo.postcalendarbackend.models.TimePointEvent;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

/**
 * 搜索时间点服务接口
 */
public interface TimePointEventSearchService {

    /**
     * 按照用户ID和前缀搜索事件
     * @param userId 用户ID
     * @param prefix 前缀
     * @return 符合条件的事件列表
     */
    CustomList<TimePointEvent> searchByUserIdAndPrefix(int userId, String prefix) throws TimePointEventSearchException;

    /**
     * 在修改某个用户的事件之后
     * 刷新该用户的字典树
     * @param userId 需要刷新的用户
     */
    void refreshUserTree(int userId) throws TimePointEventSearchException;
}
