package ru.complitex.admin.service;

import ru.complitex.common.entity.AttributeFilter;
import ru.complitex.common.service.AbstractFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.08.2010 17:55:35
 */
public class UserFilter extends AbstractFilter{
    private String login;
    private Long organizationObjectId;
    private List<AttributeFilter> attributeFilters = new ArrayList<AttributeFilter>();
    private Long sortAttributeTypeId;
    private String groupName;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Long getOrganizationObjectId() {
        return organizationObjectId;
    }

    public void setOrganizationObjectId(Long organizationObjectId) {
        this.organizationObjectId = organizationObjectId;
    }

    public List<AttributeFilter> getAttributeFilters() {
        return attributeFilters;
    }

    public void setAttributeFilters(List<AttributeFilter> attributeFilters) {
        this.attributeFilters = attributeFilters;
    }

    public boolean isFilterAttributes(){
        for(AttributeFilter attributeFilter : attributeFilters){
            if (attributeFilter.getValue() != null){
                return true;
            }
        }

        return false;
    }

    public Long getSortAttributeTypeId() {
        return sortAttributeTypeId;
    }

    public void setSortAttributeTypeId(Long sortAttributeTypeId) {
        this.sortAttributeTypeId = sortAttributeTypeId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
