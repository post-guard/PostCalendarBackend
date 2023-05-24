package top.rrricardo.postcalendarbackend.services.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.exceptions.AvlNodeRepeatException;
import top.rrricardo.postcalendarbackend.exceptions.TimeSpanEventException;
import top.rrricardo.postcalendarbackend.mappers.GroupLinkMapper;
import top.rrricardo.postcalendarbackend.mappers.GroupMapper;
import top.rrricardo.postcalendarbackend.mappers.TimeSpanEventMapper;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.TimeSpanEvent;
import top.rrricardo.postcalendarbackend.services.TimeSpanEventService;
import top.rrricardo.postcalendarbackend.utils.generic.AvlTree;
import top.rrricardo.postcalendarbackend.utils.generic.CustomHashTable;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.time.LocalDateTime;

@Service
public class TimeSpanEventServiceImpl implements TimeSpanEventService {
    // 存储用户事件的森林
    private final CustomHashTable<Integer, AvlTree<TimeSpanEvent>> userEventForest = new CustomHashTable<>();
    // 存储组织事件的森林
    private final CustomHashTable<Integer, AvlTree<TimeSpanEvent>> groupEventForest = new CustomHashTable<>();

    private final GroupLinkMapper groupLinkMapper;
    private final TimeSpanEventMapper eventMapper;
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;
    private final Logger logger;

    public TimeSpanEventServiceImpl(
            UserMapper userMapper,
            GroupMapper groupMapper,
            GroupLinkMapper groupLinkMapper,
            TimeSpanEventMapper eventMapper) {
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
        this.groupMapper = groupMapper;
        this.groupLinkMapper = groupLinkMapper;
        this.logger = LoggerFactory.getLogger(TimeSpanEventServiceImpl.class);

        readDataFromDatabase();
    }

    @Override
    public void addUserEvent(TimeSpanEvent event) throws TimeSpanEventException {
        var userId = event.getUserId();

        // 判断冲突
        judgeUserTimeConflict(event.getBeginDateTime(), event.getEndDateTime(), userId);

        addEventHelper(event, userEventForest, userId);
    }

    @Override
    public void addGroupEvent(TimeSpanEvent event) throws TimeSpanEventException {
        var groupId = event.getGroupId();

        judgeGroupTimeConflict(event.getBeginDateTime(), event.getEndDateTime(), groupId);

        addEventHelper(event, groupEventForest, groupId);
    }

    @Override
    public void removeUserEvent(TimeSpanEvent event) throws TimeSpanEventException {
        var userId = event.getUserId();

        removeEventHelper(event, userEventForest, userId);
    }

    @Override
    public void removeGroupEvent(TimeSpanEvent event) throws TimeSpanEventException {
        var groupId = event.getGroupId();

        removeEventHelper(event, groupEventForest, groupId);
    }

    @Override
    public void updateUserEvent(TimeSpanEvent event) throws TimeSpanEventException {
        var userId = event.getUserId();

        var tree = userEventForest.get(userId);

        if (tree == null) {
            throw new TimeSpanEventException("欲修改的用户不存在");
        }

        var oldEvent = eventMapper.getEventById(event.getId());

        if (oldEvent == null) {
            throw new TimeSpanEventException("欲修改的事件不存在");
        }

        if (oldEvent.equals(event)) {
            throw new TimeSpanEventException("欲修改的事件同数据库中的事件相同");
        }

        // 找到树上的对象
        oldEvent = tree.find(oldEvent);
        if (oldEvent.getBeginDateTime().equals(event.getBeginDateTime())
                && oldEvent.getEndDateTime().equals(event.getEndDateTime())) {
            // 不修改事件的开始和结束时间
            // 直接修改原来的二叉树
            oldEvent.setName(event.getName());
            oldEvent.setDetails(event.getDetails());
            oldEvent.setPlaceId(event.getPlaceId());
        } else {
            // 修改事件的开始和结束时间
            // 那就删除事件重新添加
            tree.remove(oldEvent);

            // 在重新添加之前得判断冲突
            if (judgeConflictHelper(event, tree)) {
                throw new TimeSpanEventException("同用户的事件发生冲突");
            }

            var groupLinks = groupLinkMapper.getGroupLinksByUserId(userId);
            for (var groupLink : groupLinks) {
                var groupTree = groupEventForest.get(groupLink.getGroupId());

                if (groupTree != null) {
                    if (judgeConflictHelper(event, groupTree)) {
                        throw new TimeSpanEventException("同用户所在组织的事件发生冲突");
                    }
                }
            }


            try {
                tree.insert(event);
            } catch (AvlNodeRepeatException exception) {
                throw new TimeSpanEventException("发生冲突");
            }
        }
        eventMapper.updateEvent(event);
    }

    @Override
    public void updateGroupEvent(TimeSpanEvent event) throws TimeSpanEventException {
        var groupId = event.getGroupId();

        var tree = groupEventForest.get(groupId);

        if (tree == null) {
            throw new TimeSpanEventException("欲修改的组织不存在");
        }

        var oldEvent = eventMapper.getEventById(event.getId());

        if (oldEvent == null) {
            throw new TimeSpanEventException("欲修改的事件不存在");
        }

        if (oldEvent.equals(event)) {
            throw new TimeSpanEventException("欲修改的事件同数据库中的事件相同");
        }

        // 获得树上的对象
        oldEvent = tree.find(oldEvent);

        if (oldEvent.getBeginDateTime().equals(event.getBeginDateTime()) &&
                oldEvent.getEndDateTime().equals(event.getEndDateTime())) {
            // 没有改变开始和结束时间
            // 直接修改树上的对象
            oldEvent.setName(event.getName());
            oldEvent.setDetails(event.getDetails());
            oldEvent.setPlaceId(event.getPlaceId());
        } else {
            // 反之就要改树了
            tree.remove(oldEvent);

            if (judgeConflictHelper(event, tree)) {
                try {
                    tree.insert(oldEvent);
                } catch (AvlNodeRepeatException e) {
                    throw new TimeSpanEventException("发生冲突");
                }
                throw new TimeSpanEventException("同组织内的事件发生冲突");
            }

            var groupLinks = groupLinkMapper.getGroupLinksByGroupId(groupId);

            for (var groupLink : groupLinks) {
                var userTree = userEventForest.get(groupLink.getUserId());

                if (userTree != null) {
                    if (judgeConflictHelper(event, userTree)) {
                        try {
                            tree.insert(oldEvent);
                        } catch (AvlNodeRepeatException e) {
                            throw new TimeSpanEventException("发生冲突");
                        }
                        throw new TimeSpanEventException("同组织内成员的事件发生冲突");
                    }
                }
            }

            try {
                tree.insert(event);
            } catch (AvlNodeRepeatException e) {
                throw new TimeSpanEventException("发生冲突");
            }
        }
        eventMapper.updateEvent(event);
    }

    @Override
    public CustomList<TimeSpanEvent> queryUserEvent(int id, LocalDateTime begin, LocalDateTime end) throws TimeSpanEventException {
        var result = queryEventHelper(userEventForest, id, begin, end);

        // 查询用户事件的同时查询用户所在组织的事件
        var groupLinks = groupLinkMapper.getGroupLinksByUserId(id);
        for (var groupLink : groupLinks) {
            for (var item : queryEventHelper(groupEventForest, groupLink.getGroupId(), begin, end)) {
                result.add(item);
            }
        }

        return result;
    }

    @Override
    public CustomList<TimeSpanEvent> queryGroupEvent(int id, LocalDateTime begin, LocalDateTime end) throws TimeSpanEventException {
        return queryEventHelper(groupEventForest, id, begin, end);
    }

    @Override
    public void judgeUserTimeConflict(LocalDateTime beginTime, LocalDateTime endTime, int userId) throws TimeSpanEventException {
        var tree = userEventForest.get(userId);
        var user = userMapper.getUserById(userId);

        // 森林中不存在树不意味着不存在该用户
        // 可能是应用启动之后才新建的用户
        if (user == null) {
            throw new TimeSpanEventException("指定的用户不存在");
        }

        if (tree == null) {
            return;
        }

        var event = new TimeSpanEvent();
        event.setBeginDateTime(beginTime);
        event.setEndDateTime(endTime);

        judgeUserConflictHelper(event, userId, tree);
    }

    @Override
    public void judgeGroupTimeConflict(LocalDateTime beginTime, LocalDateTime endTime, int groupId) throws TimeSpanEventException {
        var tree = groupEventForest.get(groupId);
        var group = groupMapper.getGroupById(groupId);

        // 对于组织也存在着同样的道理
        if (group == null) {
            throw new TimeSpanEventException("指定的组织不存在");
        }

        if (tree == null) {
            return;
        }

        var event = new TimeSpanEvent();
        event.setBeginDateTime(beginTime);
        event.setEndDateTime(endTime);

        judgeGroupConflictHelper(event, groupId, tree);
    }

    private void readDataFromDatabase() {
        // 重建用户森林
        userEventForest.clear();

        var users = userMapper.getUsers();

        for (var user : users) {
            var tree = new AvlTree<TimeSpanEvent>();

            userEventForest.put(user.getId(), tree);
        }

        // 重建组织森林
        groupEventForest.clear();

        var groups = groupMapper.getGroups();

        for (var group : groups) {
            var tree = new AvlTree<TimeSpanEvent>();

            groupEventForest.put(group.getId(), tree);
        }

        var events = new CustomList<>(eventMapper.getEvents());

        for (var event : events) {
            var userId = event.getUserId();
            var groupId = event.getGroupId();

            if (userId != 0 && groupId == 0) {
                var tree = userEventForest.get(userId);

                if (tree == null) {
                    logger.error("遇到了用户数据库中不存在的用户：" + userId);
                } else {
                    try {
                        tree.insert(event);
                    } catch (AvlNodeRepeatException e) {
                        logger.warn("用户： " + userId + " 冲突的事件：" + e.getMessage());
                    }
                }
            } else if (groupId != 0 && userId == 0) {
                var tree = groupEventForest.get(groupId);

                if (tree == null) {
                    logger.error("遇到了用户数据库中不存在的组织：" + groupId);
                } else {
                    try {
                        tree.insert(event);
                    } catch (AvlNodeRepeatException e) {
                        logger.warn("组织： " + groupId + " 冲突的事件：" + e.getMessage());
                    }
                }
            } else {
                logger.error("别TM一天天就知道改数据库了");
            }
        }
    }

    /**
     * 添加事件辅助函数
     *
     * @param event  需要添加的时间
     * @param forest 需要添加的森林
     * @param id     欲添加的属主ID
     */
    private void addEventHelper(TimeSpanEvent event, CustomHashTable<Integer, AvlTree<TimeSpanEvent>> forest, int id)
            throws TimeSpanEventException {
        try {
            var tree = forest.get(id);

            if (tree == null) {
                // 存在在应用启动之后
                // 创建新用户的情况
                tree = new AvlTree<>();
                tree.insert(event);
                forest.put(id, tree);
            } else {
                tree.insert(event);
            }

            // 在添加成功之后将事件添加到数据库中
            eventMapper.createEvent(event);
        } catch (AvlNodeRepeatException e) {
            throw new TimeSpanEventException("发生冲突");
        }
    }

    /**
     * 删除事件辅助函数
     *
     * @param event  需要删除的事件
     * @param forest 需要删除的森林
     * @param id     属主ID
     */
    private void removeEventHelper(TimeSpanEvent event, CustomHashTable<Integer, AvlTree<TimeSpanEvent>> forest,
                                   int id) throws TimeSpanEventException {
        var oldEvent = eventMapper.getEventById(event.getId());

        if (oldEvent == null) {
            throw new TimeSpanEventException("欲删除的事件在数据库中不存在");
        }

        if (!oldEvent.equals(event)) {
            throw new TimeSpanEventException("欲删除的事件同数据库中存储的事件不相同");
        }

        var tree = forest.get(id);

        if (tree == null) {
            throw new TimeSpanEventException("欲删除的事件非法，属主ID：" + id + "不存在");
        }

        tree.remove(event);
        eventMapper.deleteEvent(event.getId());
    }

    /**
     * 查询事件辅助函数
     *
     * @param forest 查询的森林
     * @param id     属主ID
     * @param begin  开始时间
     * @param end    结束时间
     * @return 在时间段内的事件列表
     * @throws TimeSpanEventException 查询不存在的用户/组织引发的错误
     */
    private CustomList<TimeSpanEvent> queryEventHelper(CustomHashTable<Integer, AvlTree<TimeSpanEvent>> forest,
                                                       int id, LocalDateTime begin, LocalDateTime end)
            throws TimeSpanEventException {
        var tree = forest.get(id);

        if (tree == null) {
            throw new TimeSpanEventException("属主：" + id + "不存在");
        }

        var beginTimeObj = new TimeSpanEvent();
        beginTimeObj.setBeginDateTime(begin);
        beginTimeObj.setEndDateTime(begin);

        var endTimeObj = new TimeSpanEvent();
        endTimeObj.setBeginDateTime(end);
        endTimeObj.setEndDateTime(end);

        return tree.selectRange(beginTimeObj, endTimeObj);
    }

    /**
     * 判断是否发生冲突的辅助函数
     *
     * @param event 需要判断的事件
     * @param tree  需要判断的树
     * @return 为真为发生冲突，反之没有发生冲突
     */
    private boolean judgeConflictHelper(TimeSpanEvent event, AvlTree<TimeSpanEvent> tree) {
        var iterator = tree.iterator();

        TimeSpanEvent left;
        TimeSpanEvent right = null;

        if (iterator.hasNext()) {
            left = iterator.next();
        } else {
            // 在树中不存在元素
            return false;
        }

        if (iterator.hasNext()) {
            right = iterator.next();
        }

        if (event.compareTo(left) <= 0) {
            // 事件在树中事件的前面
            return !event.getEndDateTime().isBefore(left.getBeginDateTime());
        }

        while (iterator.hasNext()) {
            if (left.compareTo(event) <= 0 && event.compareTo(right) <= 0) {
                // 找到位于中间的状态
                return !left.getEndDateTime().isBefore(event.getBeginDateTime())
                        || !event.getEndDateTime().isBefore(right.getBeginDateTime());
            }

            left = right;
            right = iterator.next();
        }

        // 最后两个元素
        // 当然还有还要考虑树中只有一个元素的情况
        if (right != null) {
            if (left.compareTo(event) <= 0 && event.compareTo(right) <= 0) {
                // 找到位于中间的状态
                return !left.getEndDateTime().isBefore(event.getBeginDateTime())
                        || !event.getEndDateTime().isBefore(right.getBeginDateTime());
            } else {
                return !right.getEndDateTime().isBefore(event.getBeginDateTime());
            }
        } else {
            return !left.getEndDateTime().isBefore(event.getBeginDateTime());
        }
    }

    /**
     * 判断给指定用户添加事件是否发生冲突的辅助函数
     * @param event 需要添加的事件
     * @param userId 添加事件的用户
     * @param tree 即将添加的树
     * @throws TimeSpanEventException 如果发生冲突引发的异常
     */
    private void judgeUserConflictHelper(TimeSpanEvent event, int userId, AvlTree<TimeSpanEvent> tree)
        throws TimeSpanEventException {
        if (judgeConflictHelper(event, tree)) {
            throw new TimeSpanEventException("同用户的事件发生冲突");
        }

        var groupLinks = groupLinkMapper.getGroupLinksByUserId(userId);

        for (var groupLink : groupLinks) {
            var groupTree = groupEventForest.get(groupLink.getGroupId());

            if (judgeConflictHelper(event, groupTree)) {
                throw new TimeSpanEventException("同用户所在组织的事件发生冲突");
            }
        }
    }

    /**
     * 判断给指定组织添加事件是否发生冲突的辅助函数
     * @param event 需要添加的事件
     * @param groupId 添加事件的用户
     * @param tree 即将添加的树
     * @throws TimeSpanEventException 如果发生冲突引发的异常
     */
    private void judgeGroupConflictHelper(TimeSpanEvent event, int groupId, AvlTree<TimeSpanEvent> tree)
        throws TimeSpanEventException {
        if (judgeConflictHelper(event, tree)) {
            throw new TimeSpanEventException("同组织内的事件发生冲突");
        }

        var groupLinks = groupLinkMapper.getGroupLinksByGroupId(groupId);

        for (var groupLink : groupLinks) {
            var groupTree = groupEventForest.get(groupLink);

            if (judgeConflictHelper(event, groupTree)) {
                throw new TimeSpanEventException("同组织中用户的事件发生冲突");
            }
        }
    }
}
