package top.rrricardo.postcalendarbackend.services.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.exceptions.AvlNodeRepeatException;
import top.rrricardo.postcalendarbackend.exceptions.TimeSpanEventException;
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
    public void addEvent(TimeSpanEvent event) throws TimeSpanEventException {
        var userId = event.getUserId();

        addEventHelper(event, userEventForest, userId);
        eventMapper.createEvent(event);
    }

    @Override
    public void removeEvent(TimeSpanEvent event) throws TimeSpanEventException {
        var userId = event.getUserId();

        removeEventHelper(event, userEventForest, userId);
        eventMapper.deleteEvent(event.getId());
    }

    @Override
    public void updateEvent(TimeSpanEvent event) throws TimeSpanEventException {
        var userId = event.getUserId();

        updateEventHelper(event, userEventForest, userId);
        eventMapper.updateEvent(event);
    }

    @Override
    public CustomList<TimeSpanEvent> queryEvent(int id, LocalDateTime begin, LocalDateTime end) throws TimeSpanEventException {
        return queryEventHelper(userEventForest, id, begin, end);
    }

    @Override
    public boolean judgeConflict(int id, TimeSpanEvent event) {
        var tree = userEventForest.get(id);

        if (tree == null) {
            return false;
        }

        return judgeConflictHelper(event, tree);
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
}
