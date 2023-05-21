package top.rrricardo.postcalendarbackend.services.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.exceptions.AvlNodeRepeatException;
import top.rrricardo.postcalendarbackend.exceptions.TimePointEventException;
import top.rrricardo.postcalendarbackend.mappers.TimePointEventMapper;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.TimePointEvent;
import top.rrricardo.postcalendarbackend.services.TimePointEventService;
import top.rrricardo.postcalendarbackend.utils.generic.AvlTree;
import top.rrricardo.postcalendarbackend.utils.generic.CustomHashTable;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.time.LocalDateTime;

@Service("userTimePointEvent")
public class UserTimePointEventServiceImpl extends TimePointEventService {
    private final CustomHashTable<Integer, AvlTree<TimePointEvent>> userEventForest = new CustomHashTable<>();
    private final TimePointEventMapper eventMapper;
    private final UserMapper userMapper;
    private final Logger logger;

    public UserTimePointEventServiceImpl(UserMapper userMapper, TimePointEventMapper eventMapper) {
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
        this.logger = LoggerFactory.getLogger(UserTimePointEventServiceImpl.class);

        readDataFromDatabase();
    }

    @Override
    public void addEvent(TimePointEvent event) throws TimePointEventException {
        addEventHelper(event, userEventForest, event.getUserId());
        eventMapper.createEvent(event);
    }

    @Override
    public void removeEvent(TimePointEvent event) throws TimePointEventException {
        removeEventHelper(event, userEventForest, event.getUserId());
        eventMapper.deleteEvent(event.getId());
    }

    @Override
    public void updateEvent(TimePointEvent event) throws TimePointEventException {
        updateEventHelper(event, userEventForest, event.getUserId());
        eventMapper.updateEvent(event);
    }

    @Override
    public CustomList<TimePointEvent> queryEvent(int id, LocalDateTime begin, LocalDateTime end) throws TimePointEventException {
        return queryEventHelper(userEventForest, id, begin, end);
    }


    private void readDataFromDatabase() {
        userEventForest.clear();

        var users = userMapper.getUsers();

        for(var user : users) {
            var tree = new AvlTree<TimePointEvent>();

            userEventForest.put(user.getId(), tree);
        }

        var events = eventMapper.getEvents();

        for (var event : events) {
            var userId = event.getUserId();

            if (userId != 0) {
                var tree = userEventForest.get(userId);

                if (tree == null) {
                    logger.error("见鬼了");
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
