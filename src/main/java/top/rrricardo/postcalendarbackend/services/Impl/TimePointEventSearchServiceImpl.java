package top.rrricardo.postcalendarbackend.services.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.exceptions.TimePointEventSearchException;
import top.rrricardo.postcalendarbackend.mappers.GroupLinkMapper;
import top.rrricardo.postcalendarbackend.mappers.TimePointEventMapper;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.TimePointEvent;
import top.rrricardo.postcalendarbackend.services.TimePointEventSearchService;
import top.rrricardo.postcalendarbackend.utils.DictionaryTree;
import top.rrricardo.postcalendarbackend.utils.generic.CustomHashTable;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

@Service
public class TimePointEventSearchServiceImpl implements TimePointEventSearchService {
    private final CustomHashTable<Integer, DictionaryTree> forests = new CustomHashTable<>();

    private final TimePointEventMapper eventMapper;
    private final UserMapper userMapper;
    private final GroupLinkMapper groupLinkMapper;
    private final Logger logger;

    public TimePointEventSearchServiceImpl(
            TimePointEventMapper eventMapper,
            UserMapper userMapper,
            GroupLinkMapper groupLinkMapper) {
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
        this.groupLinkMapper = groupLinkMapper;

        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public CustomList<TimePointEvent> searchByUserIdAndPrefix(int userId, String prefix) throws TimePointEventSearchException {
        var user = userMapper.getUserById(userId);
        if (user == null) {
            throw new TimePointEventSearchException("刷新的用户不存在");
        }

        var tree = forests.get(userId);

        if (tree == null) {
            generateUserTree(userId);
        }

        tree = forests.get(userId);

        return tree.search(prefix);
    }

    @Override
    public void refreshUserTree(int userId) throws TimePointEventSearchException {
        var user = userMapper.getUserById(userId);
        if (user == null) {
            throw new TimePointEventSearchException("刷新的用户不存在");
        }

        var tree = forests.get(userId);

        // 使用懒加载模式
        // 没有调用就不生成
        if (tree != null) {
            generateUserTree(userId);
        }
    }

    @Async
    protected void generateUserTree(int userId) {
        logger.info("用户：{}生成字典树", userId);

        var events = eventMapper.getEvents();
        var userEvents = new CustomList<TimePointEvent>();
        var groupLinks = groupLinkMapper.getGroupLinksByUserId(userId);
        var groupIdList = new CustomList<Integer>();

        for (var groupLink : groupLinks) {
            groupIdList.add(groupLink.getGroupId());
        }

        for (var event : events) {
            if (userId == event.getUserId()) {
                userEvents.add(event);
            }

            if (groupIdList.indexOf(event.getGroupId()) > -1) {
                // 用户在该事件所属的组织中
                userEvents.add(event);
            }
        }

        var tree = new DictionaryTree();
        for (var event : userEvents) {
            tree.insert(event);
        }

        forests.put(userId, tree);
    }
}
