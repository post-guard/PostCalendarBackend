package top.rrricardo.postcalendarbackend.mappers;

import top.rrricardo.postcalendarbackend.models.Organization;
import top.rrricardo.postcalendarbackend.models.OrganizationLink;
import top.rrricardo.postcalendarbackend.models.User;

import java.util.List;

public interface OrganizationLinkMapper {
    /**
     * 获得所有的组织用户关系
     * @return 组织用户关系列表
     */
    List<OrganizationLink> getOrganizationLinks();

    /**
     * 获得指定用户所在的组织列表
     * @param userId 指定用户ID
     * @return 组织列表
     */
    List<Organization> getOrganizationLinksByUserId(int userId);

    /**
     * 获得指定组织用户的用户列表
     * @param organizationId 指定组织ID
     * @return 用户列表
     */
    List<User> getOrganizationLinksByOrganizationId(int organizationId);

    /**
     * 获得指定的组织用户关系
     * @param id 组织用户关系ID
     * @return 指定的用户组织
     */
    OrganizationLink getOrganizationLinkById(int id);

    /**
     * 创建组织用户关系
     * @param link 需要创建的组织用户关系对象
     */
    void createOrganizationLink(OrganizationLink link);

    /**
     * 修改组织用户关系
     * @param link 修改的组织用户关系信息
     */
    void updateOrganizationLink(OrganizationLink link);

    /**
     * 删除组织用户关系
     * @param id 需要删除的组织关系ID
     */
    void deleteOrganizationLink(int id);
}
