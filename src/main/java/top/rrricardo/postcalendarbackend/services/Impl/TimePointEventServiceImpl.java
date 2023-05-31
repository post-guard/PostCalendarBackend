package top.rrricardo.postcalendarbackend.services.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.exceptions.AvlNodeRepeatException;
import top.rrricardo.postcalendarbackend.exceptions.TimeConflictException;
import top.rrricardo.postcalendarbackend.exceptions.TimePointEventException;
import top.rrricardo.postcalendarbackend.exceptions.TimeSpanEventException;
import top.rrricardo.postcalendarbackend.mappers.GroupLinkMapper;
import top.rrricardo.postcalendarbackend.mappers.GroupMapper;
import top.rrricardo.postcalendarbackend.mappers.TimePointEventMapper;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.TimePointEvent;
import top.rrricardo.postcalendarbackend.services.TimePointEventService;
import top.rrricardo.postcalendarbackend.services.TimeSpanEventService;
import top.rrricardo.postcalendarbackend.utils.generic.AvlTree;
import top.rrricardo.postcalendarbackend.utils.generic.CustomHashTable;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.time.LocalDateTime;

@Service
public class TimePointEventServiceImpl implements TimePointEventService {
    private final CustomHashTable<Integer, AvlTree<Node>> userEventForest = new CustomHashTable<>();
    private final CustomHashTable<Integer, AvlTree<Node>> groupEventForest = new CustomHashTable<>();

    private final TimePointEventMapper eventMapper;
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;
    private final GroupLinkMapper groupLinkMapper;
    private final TimeSpanEventService timeSpanEventService;

    private final Logger logger;

    public TimePointEventServiceImpl(
            TimePointEventMapper eventMapper,
            UserMapper userMapper,
            GroupMapper groupMapper,
            GroupLinkMapper groupLinkMapper,
            TimeSpanEventService timeSpanEventService
    ) {
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
        this.groupMapper = groupMapper;
        this.groupLinkMapper = groupLinkMapper;
        this.timeSpanEventService = timeSpanEventService;

        logger = LoggerFactory.getLogger(TimePointEventServiceImpl.class);

        refreshDataFromDatabase();
    }

    @Override
    public void addUserEvent(TimePointEvent event) throws TimePointEventException, TimeConflictException {
        var userId = event.getUserId();

        var user = userMapper.getUserById(userId);
        if (user == null) {
            throw new TimePointEventException("给不存在的用户创建日程");
        }

        try {
            timeSpanEventService.judgeUserTimeConflict(event.getEndDateTime(), event.getEndDateTime(), userId);
        } catch (TimeSpanEventException e) {
            throw new TimePointEventException(e.getMessage());
        }

        addEventHelper(event, userEventForest, userId);
    }

    @Override
    public void addGroupEvent(TimePointEvent event) throws TimePointEventException, TimeConflictException {
        var groupId = event.getGroupId();

        var group = groupMapper.getGroupById(groupId);
        if (group == null) {
            throw new TimePointEventException("给不存在的组织创建日程");
        }

        try {
            timeSpanEventService.judgeGroupTimeConflict(event.getEndDateTime(), event.getEndDateTime(), groupId);
        } catch (TimeSpanEventException e) {
            throw new TimePointEventException(e.getMessage());
        }

        addEventHelper(event, groupEventForest, groupId);
    }

    @Override
    public void removeUserEvent(TimePointEvent event) throws TimePointEventException {
        var userId = event.getUserId();

        var user = userMapper.getUserById(userId);
        if (user == null) {
            throw new TimePointEventException("欲删除事件的用户不存在");
        }

        removeEventHelper(event, userEventForest, userId);
    }

    @Override
    public void removeGroupEvent(TimePointEvent event) throws TimePointEventException {
        var groupId = event.getGroupId();

        var group = groupMapper.getGroupById(groupId);
        if (group == null) {
            throw new TimePointEventException("欲删除事件的组织不存在");
        }

        removeEventHelper(event, groupEventForest, groupId);
    }

    @Override
    public void updateUserEvent(TimePointEvent event) throws TimePointEventException, TimeConflictException {
        var userId = event.getUserId();

        var user = userMapper.getUserById(userId);
        if (user == null) {
            throw new TimePointEventException("修改的用户不存在");
        }

        var oldEvent = eventMapper.getEventById(event.getId());

        if (oldEvent == null) {
            throw new TimePointEventException("欲修改的事件不存在");
        }

        var tree = userEventForest.get(userId);
        if (tree != null) {
            var node = new Node(oldEvent.getEndDateTime());
            node = tree.find(node);

            if (node != null) {
                var index = node.list.indexOf(oldEvent);

                // 区分是否修改时间
                if (oldEvent.getEndDateTime().equals(event.getEndDateTime())) {
                    oldEvent = node.list.get(index);
                    // 没有修改时间
                    oldEvent.setName(event.getName());
                    oldEvent.setDetails(event.getDetails());
                    oldEvent.setPlaceId(event.getPlaceId());
                    oldEvent.setType(event.getType());
                } else {
                    node.list.remove(index);

                    try {
                        timeSpanEventService.judgeUserTimeConflict(
                                event.getEndDateTime(),
                                event.getEndDateTime(),
                                userId);
                    } catch (TimeSpanEventException e) {
                        throw new TimePointEventException(e.getMessage());
                    }

                    addEvent2TreeHelper(tree, event);
                }

                eventMapper.updateEvent(event);
                return;
            }
        }

        throw new TimePointEventException("遇到了奇怪的问题");
    }

    @Override
    public void updateGroupEvent(TimePointEvent event) throws TimePointEventException, TimeConflictException {
        var groupId = event.getUserId();

        var group = groupMapper.getGroupById(groupId);
        if (group == null) {
            throw new TimePointEventException("修改的组织不存在");
        }

        var oldEvent = eventMapper.getEventById(event.getId());

        if (oldEvent == null) {
            throw new TimePointEventException("欲修改的事件不存在");
        }

        var tree = groupEventForest.get(groupId);
        if (tree != null) {
            var node = new Node(oldEvent.getEndDateTime());
            node = tree.find(node);

            if (node != null) {
                var index = node.list.indexOf(oldEvent);

                // 区分是否修改时间
                if (oldEvent.getEndDateTime().equals(event.getEndDateTime())) {
                    oldEvent = node.list.get(index);
                    // 没有修改时间
                    oldEvent.setName(event.getName());
                    oldEvent.setDetails(event.getDetails());
                    oldEvent.setPlaceId(event.getPlaceId());
                    oldEvent.setType(event.getType());
                } else {
                    node.list.remove(index);

                    try {
                        timeSpanEventService.judgeGroupTimeConflict(
                                event.getEndDateTime(),
                                event.getEndDateTime(),
                                groupId);
                    } catch (TimeSpanEventException e) {
                        throw new TimePointEventException(e.getMessage());
                    }

                    addEvent2TreeHelper(tree, event);
                }

                eventMapper.updateEvent(event);
                return;
            }
        }

        throw new TimePointEventException("遇到了奇怪的问题");
    }

    @Override
    public CustomList<TimePointEvent> queryUserEvents(int userId, LocalDateTime beginTime, LocalDateTime endTime)
            throws TimePointEventException {
        var user = userMapper.getUserById(userId);
        if (user == null) {
            throw new TimePointEventException("查询的用户不存在");
        }

        var result = queryEventHelper(userEventForest, userId, beginTime, endTime);

        var groupLinks = groupLinkMapper.getGroupLinksByUserId(userId);
        for (var groupLink : groupLinks) {
            for (var item : queryEventHelper(groupEventForest, groupLink.getGroupId(), beginTime, endTime)) {
                result.add(item);
            }
        }

        return result;
    }

    @Override
    public CustomList<TimePointEvent> queryGroupEvents(int groupId, LocalDateTime beginTime, LocalDateTime endTime) throws TimePointEventException {
        var group = groupMapper.getGroupById(groupId);
        if (group == null) {
            throw new TimePointEventException("查询的组织不存在");
        }

        return queryEventHelper(groupEventForest, groupId, beginTime, endTime);
    }

    private void refreshDataFromDatabase() {
        // 创建当前用户的树
        userEventForest.clear();
        var users = userMapper.getUsers();

        for (var user : users) {
            userEventForest.put(user.getId(), new AvlTree<>());
        }

        // 创建当前组织的树
        groupEventForest.clear();
        var groups = groupMapper.getGroups();

        for (var group : groups) {
            groupEventForest.put(group.getId(), new AvlTree<>());
        }

        // 从数据库中读出数据
        var events = eventMapper.getEvents();
        for (var event : events) {
            if (event.getUserId() != 0 && event.getGroupId() == 0) {
                // 为有效的用户事件
                var tree = userEventForest.get(event.getUserId());

                if (tree == null) {
                    logger.warn("谁他妈改我数据库？");
                    continue;
                }

                addEvent2TreeHelper(tree, event);
            } else if (event.getGroupId() != 0 && event.getUserId() == 0) {
                // 有效的组织事件
                var tree = groupEventForest.get(event.getGroupId());

                if (tree == null) {
                    logger.warn("一天不给老子点惊喜不行是吧");
                    continue;
                }

                addEvent2TreeHelper(tree, event);
            } else {
                logger.warn("创建这种屑事件的人一定要出重拳");
            }
        }
    }

    private void addEvent2TreeHelper(AvlTree<Node> tree, TimePointEvent event) {
        var node = new Node(event.getEndDateTime());
        node = tree.find(node);
        if (node == null) {
            node = new Node(event);
            try {
                tree.insert(node);
            } catch (AvlNodeRepeatException ignored) {
                logger.warn("又遇到奇怪的报错了（悲");
            }
        } else {
            node.addEvent(event);
        }
    }

    private void addEventHelper(TimePointEvent event, CustomHashTable<Integer, AvlTree<Node>> forest, int id) {
        var tree = forest.get(id);

        if (tree == null) {
            tree = new AvlTree<>();
            forest.put(id, tree);
        }

        addEvent2TreeHelper(tree, event);
        eventMapper.createEvent(event);
    }

    private void removeEventHelper(TimePointEvent event, CustomHashTable<Integer, AvlTree<Node>> forest, int id)
            throws TimePointEventException {
        var oldEvent = eventMapper.getEventById(event.getId());

        if (oldEvent == null) {
            throw new TimePointEventException("欲删除的事件不存在");
        }

        var tree = forest.get(id);

        if (tree != null) {
            // 理论上不会找不着
            var node = new Node(event.getEndDateTime());
            node = tree.find(node);
            if (node != null) {
                if (node.list.getSize() != 0) {
                    var index = node.list.indexOf(event);
                    if (index != -1) {
                        node.list.remove(index);
                        eventMapper.deleteEvent(event.getId());
                        return;
                    }
                }
            }
        }

        throw new TimePointEventException("遇到了奇怪的问题");
    }

    private CustomList<TimePointEvent> queryEventHelper(CustomHashTable<Integer, AvlTree<Node>> forest, int id,
                                                        LocalDateTime begin, LocalDateTime end) {
        var tree = forest.get(id);
        var result = new CustomList<TimePointEvent>();

        if (tree != null) {
            var nodes = tree.selectRange(new Node(begin), new Node(end));

            for (var node : nodes) {
                for (var item : node.list) {
                    result.add(item);
                }
            }
        }

        return result;
    }

    /**
     * 存储相同时间时间点事件的节点
     */
    private static class Node implements Comparable<Node> {
        private final CustomList<TimePointEvent> list;
        private final LocalDateTime endDateTime;

        Node(TimePointEvent event) {
            list = new CustomList<>();
            list.add(event);
            endDateTime = event.getEndDateTime();
        }

        Node(LocalDateTime endDateTime) {
            list = new CustomList<>();
            this.endDateTime = endDateTime;
        }

        void addEvent(TimePointEvent event) {
            list.add(event);
        }

        @Override
        public int compareTo(Node node) {
            return endDateTime.compareTo(node.endDateTime);
        }
    }
}
