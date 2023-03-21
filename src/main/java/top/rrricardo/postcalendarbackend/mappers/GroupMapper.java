package top.rrricardo.postcalendarbackend.mappers;

import org.apache.ibatis.annotations.Mapper;
import top.rrricardo.postcalendarbackend.models.Group;

import java.util.List;

@Mapper
public interface GroupMapper {
    /**
     * 获得所有的组织列表
     * @return 组织列表
     */
    List<Group> getGroups();

    /**
     * 通过单个ID获得指定的组织
     * @param id 组织ID
     * @return 获得的组织 若为空说明ID对应的组织不存在
     */
    Group getGroupById(int id);

    /**
     * 利用名称查询组织
     * @param name 组织名称
     * @return 组织
     */
    Group getGroupByName(String name);

    /**
     * 创建组织
     * @param group 需要创建的组织
     */
    void createGroup(Group group);

    /**
     * 更新组织信息
     * @param group 更新的组织信息
     */
    void updateGroup(Group group);

    /**
     * 删除指定的组织
     * @param id 需要删除的组织ID
     */
    void deleteGroup(int id);
}
