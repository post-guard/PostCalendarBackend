package top.rrricardo.postcalendarbackend.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.rrricardo.postcalendarbackend.models.Group;
import top.rrricardo.postcalendarbackend.models.GroupLink;
import top.rrricardo.postcalendarbackend.models.User;

import java.util.List;

@Mapper
public interface GroupLinkMapper {
    /**
     * 获得所有的组织用户关系
     * @return 组织用户关系列表
     */
    List<GroupLink> getGroupLinks();

    /**
     * 获得指定用户所在的组织列表
     * @param userId 指定用户ID
     * @return 组织列表
     */
    List<GroupLink> getGroupLinksByUserId(int userId);

    /**
     * 获得指定组织用户的用户列表
     * @param groupId 指定组织ID
     * @return 用户列表
     */
    List<GroupLink> getGroupLinksByGroupId(int groupId);

    /**
     * 获得指定的组织用户关系
     * @param id 组织用户关系ID
     * @return 指定的用户组织
     */
    GroupLink getGroupLinkById(int id);

    /**
     * 指定用户ID和组织ID获得组织用户关系
     * @param userId 用户ID
     * @param groupId 组织ID
     * @return 指定的用户组织关系
     */
    GroupLink getGroupLinkByUserIdAndGroupId(
            @Param("userId") int userId, @Param("groupId") int groupId);

    /**
     * 创建组织用户关系
     * @param link 需要创建的组织用户关系对象
     */
    void createGroupLink(GroupLink link);

    /**
     * 修改组织用户关系
     * @param link 修改的组织用户关系信息
     */
    void updateGroupLink(GroupLink link);

    /**
     * 删除组织用户关系
     * @param id 需要删除的组织关系ID
     */
    void deleteGroupLink(int id);
}
