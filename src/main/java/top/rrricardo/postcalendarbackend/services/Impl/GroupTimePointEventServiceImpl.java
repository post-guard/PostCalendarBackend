package top.rrricardo.postcalendarbackend.services.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.exceptions.AvlNodeRepeatException;
import top.rrricardo.postcalendarbackend.exceptions.TimePointEventException;
import top.rrricardo.postcalendarbackend.mappers.GroupMapper;
import top.rrricardo.postcalendarbackend.mappers.TimePointEventMapper;
import top.rrricardo.postcalendarbackend.models.TimePointEvent;
import top.rrricardo.postcalendarbackend.services.TimePointEventService;
import top.rrricardo.postcalendarbackend.utils.generic.AvlTree;
import top.rrricardo.postcalendarbackend.utils.generic.CustomHashTable;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.time.LocalDateTime;

@Service("groupTimePointEvent")
public class GroupTimePointEventServiceImpl extends TimePointEventService {
    private final CustomHashTable<Integer, AvlTree<TimePointEvent>> groupEventForest = new CustomHashTable<>();
    private final TimePointEventMapper eventMapper;
    private final GroupMapper groupMapper;
    private final Logger logger;

    public GroupTimePointEventServiceImpl(TimePointEventMapper eventMapper, GroupMapper groupMapper) {
        this.eventMapper = eventMapper;
        this.groupMapper = groupMapper;
        this.logger = LoggerFactory.getLogger(GroupTimePointEventServiceImpl.class);

        readDataFromDatabase();
    }

    @Override
    public void addEvent(TimePointEvent event) throws TimePointEventException {
        addEventHelper(event, groupEventForest, event.getGroupId());
        eventMapper.createEvent(event);
    }

    @Override
    public void removeEvent(TimePointEvent event) throws TimePointEventException {
        removeEventHelper(event, groupEventForest, event.getGroupId());
        eventMapper.deleteEvent(event.getId());
    }

    @Override
    public void updateEvent(TimePointEvent event) throws TimePointEventException {
        updateEventHelper(event, groupEventForest, event.getGroupId());
        eventMapper.updateEvent(event);
    }

    @Override
    public CustomList<TimePointEvent> queryEvent(int id, LocalDateTime begin, LocalDateTime end)
            throws TimePointEventException {
        return queryEventHelper(groupEventForest, id, begin, end);
    }

    private void readDataFromDatabase() {
        groupEventForest.clear();

        var groups = groupMapper.getGroups();

        for (var group : groups) {
            var tree = new AvlTree<TimePointEvent>();

            groupEventForest.put(group.getId(), tree);
        }

        var events = eventMapper.getEvents();

        for (var event : events) {
            var groupId = event.getGroupId();

            if (groupId != 0 ) {
                var tree = groupEventForest.get(groupId);

                if (tree == null) {
                    logger.error("如果运行到这里，那程序的人生已经结束了吧（悲）");
                } else {
                    try {
                        tree.insert(event);
                    } catch (AvlNodeRepeatException e) {
                        logger.warn("这数据库已经坏掉了，不如我们把她，，，，");
                    }
                }
            }
        }
    }
}
