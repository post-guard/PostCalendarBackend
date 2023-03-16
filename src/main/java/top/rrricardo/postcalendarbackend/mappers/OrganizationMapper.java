package top.rrricardo.postcalendarbackend.mappers;

import top.rrricardo.postcalendarbackend.models.Organization;

import java.util.List;

public interface OrganizationMapper {
    /**
     * 获得所有的组织列表
     * @return 组织列表
     */
    List<Organization> getOrganizations();

    /**
     * 通过单个ID获得指定的组织
     * @param id 组织ID
     * @return 获得的组织 若为空说明ID对应的组织不存在
     */
    Organization getOrganizationById(int id);

    /**
     * 创建组织
     * @param organization 需要创建的组织
     */
    void createOrganization(Organization organization);

    /**
     * 更新组织信息
     * @param organization 更新的组织信息
     */
    void updateOrganization(Organization organization);

    /**
     * 删除指定的组织
     * @param id 需要删除的组织ID
     */
    void deleteOrganization(int id);
}
