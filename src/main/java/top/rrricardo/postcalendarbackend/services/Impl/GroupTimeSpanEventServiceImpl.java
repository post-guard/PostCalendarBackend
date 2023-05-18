package top.rrricardo.postcalendarbackend.services.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.exceptions.AvlNodeRepeatException;
import top.rrricardo.postcalendarbackend.exceptions.TimeSpanEventException;
import top.rrricardo.postcalendarbackend.mappers.GroupMapper;
import top.rrricardo.postcalendarbackend.mappers.TimeSpanEventMapper;
import top.rrricardo.postcalendarbackend.models.TimeSpanEvent;
import top.rrricardo.postcalendarbackend.services.TimeSpanEventService;
import top.rrricardo.postcalendarbackend.utils.generic.AvlTree;
import top.rrricardo.postcalendarbackend.utils.generic.CustomHashTable;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.time.LocalDateTime;

@Service("groupTimeSpanEvent")
public class GroupTimeSpanEventServiceImpl extends TimeSpanEventService {
    private final CustomHashTable<Integer, AvlTree<TimeSpanEvent>> groupEventForest = new CustomHashTable<>();

    private final TimeSpanEventMapper eventMapper;
    private final Logger logger;
    private final GroupMapper groupMapper;

    public GroupTimeSpanEventServiceImpl(GroupMapper groupMapper, TimeSpanEventMapper eventMapper) {
        this.eventMapper = eventMapper;
        this.groupMapper = groupMapper;
        this.logger = LoggerFactory.getLogger(GroupTimeSpanEventServiceImpl.class);

        readDataFromDatabase();
    }

    @Override
    public void addEvent(TimeSpanEvent event) throws TimeSpanEventException {
        var groupId = event.getGroupId();

        addEventHelper(event, groupEventForest, groupId);
        eventMapper.createEvent(event);
    }

    @Override
    public void removeEvent(TimeSpanEvent event) throws TimeSpanEventException {
        var groupId = event.getGroupId();

        removeEventHelper(event, groupEventForest, groupId);
        eventMapper.deleteEvent(event.getId());
    }

    @Override
    public void updateEvent(TimeSpanEvent event) throws TimeSpanEventException {
        var groupId = event.getGroupId();

        updateEventHelper(event, groupEventForest, groupId);
        eventMapper.updateEvent(event);
    }

    @Override
    public CustomList<TimeSpanEvent> queryEvent(int id, LocalDateTime begin, LocalDateTime end)
            throws TimeSpanEventException {
        return queryEventHelper(groupEventForest, id, begin, end);
    }

    private void readDataFromDatabase() {
        groupEventForest.clear();

        var groups = groupMapper.getGroups();

        for (var group : groups) {
            var tree = new AvlTree<TimeSpanEvent>();

            groupEventForest.put(group.getId(), tree);
        }

        var events = new CustomList<>(eventMapper.getEvents());

        for (var event : events) {
            var groupId = event.getGroupId();

            if (groupId != 0) {
                var tree = groupEventForest.get(groupId);

                if (tree == null) {
                    logger.error("遇到了数据库中不存在的组织：" + groupId);
                } else {
                    try {
                        tree.insert(event);
                    } catch (AvlNodeRepeatException e) {
                        logger.warn("组织： " + groupId + " 冲突的事件：" + e.getMessage());
                    }
                }
            }
        }

    }
}
