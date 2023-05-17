package top.rrricardo.postcalendarbackend.services.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.exceptions.AvlNodeRepeatException;
import top.rrricardo.postcalendarbackend.mappers.TimeSpanEventMapper;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.TimeSpanEvent;
import top.rrricardo.postcalendarbackend.services.TimeSpanEventService;
import top.rrricardo.postcalendarbackend.utils.generic.AvlTree;
import top.rrricardo.postcalendarbackend.utils.generic.CustomHashTable;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.time.LocalDateTime;

@Service("userTimeSpanEvent")
public class UserTimeSpanEventServiceImpl extends TimeSpanEventService {
    private final CustomHashTable<Integer, AvlTree<TimeSpanEvent>> userEventForest = new CustomHashTable<>();

    private final TimeSpanEventMapper eventMapper;
    private final UserMapper userMapper;
    private final Logger logger;

    public UserTimeSpanEventServiceImpl(UserMapper userMapper, TimeSpanEventMapper eventMapper) {
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
        this.logger = LoggerFactory.getLogger(UserTimeSpanEventServiceImpl.class);

        readDataFromDatabase();
    }

    @Override
    public void addEvent(TimeSpanEvent event) {
        var userId = event.getUserId();
        try {
            var tree = userEventForest.get(userId);
            if (tree == null) {
                tree = new AvlTree<>();

                tree.insert(event);
                userEventForest.put(userId, tree);
            } else {
                tree.insert(event);
            }

            eventMapper.createEvent(event);
        } catch (AvlNodeRepeatException e) {
            throw new IllegalArgumentException("冲突的时间");
        }
    }

    @Override
    public void removeEvent(TimeSpanEvent event) {
        var userId = event.getUserId();

        var tree = userEventForest.get(userId);

        if (tree == null) {
            throw new IllegalArgumentException("用户： " + userId + "不存在");
        }

        tree.remove(event);
        eventMapper.deleteEvent(event.getId());
    }

    @Override
    public void updateEvent(TimeSpanEvent event) {
        var userId = event.getUserId();

        var tree = userEventForest.get(userId);
        var oldEvent = getEventById(event.getId(), userId);

        if (oldEvent == null) {
            throw new IllegalArgumentException("修改的事件不存在");
        }

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
            try {
                tree.insert(event);
            } catch (AvlNodeRepeatException exception) {
                throw new IllegalArgumentException("冲突的时间");
            }
        }

        eventMapper.updateEvent(event);
    }

    @Override
    public CustomList<TimeSpanEvent> queryEvent(int id, LocalDateTime begin, LocalDateTime end) {
        var beginTimeObj = new TimeSpanEvent();
        beginTimeObj.setBeginDateTime(begin);
        beginTimeObj.setEndDateTime(begin);

        var endTimeObj = new TimeSpanEvent();
        endTimeObj.setBeginDateTime(end);
        endTimeObj.setEndDateTime(end);

        var tree = userEventForest.get(id);

        if (tree == null) {
            throw new IllegalArgumentException("用户：" + id + "不存在");
        }

        return tree.selectRange(beginTimeObj, endTimeObj);
    }

    private void readDataFromDatabase() {
        userEventForest.clear();

        var users = userMapper.getUsers();

        for (var user : users) {
            var tree = new AvlTree<TimeSpanEvent>();

            userEventForest.put(user.getId(), tree);
        }

        var events = new CustomList<>(eventMapper.getEvents());

        for (var event : events) {
            var userId = event.getUserId();

            if (userId != 0) {
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
            }
        }
    }

    private TimeSpanEvent getEventById(int eventId, int userId) {
        var tree = userEventForest.get(userId);

        for (var event : tree) {
            if (event.getId() == eventId) {
                return event;
            }
        }

        return null;
    }
}
