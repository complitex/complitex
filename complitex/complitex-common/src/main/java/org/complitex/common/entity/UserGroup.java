package org.complitex.common.entity;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 21.07.2010 15:26:28
 */
public class UserGroup implements Serializable{
    public enum GROUP_NAME{
        ADMINISTRATORS,
        EMPLOYEES,
        EMPLOYEES_CHILD_VIEW
    }

    private Long id;
    private String login;
    private String groupName;

    public UserGroup() {
    }

    public UserGroup(String groupName) {
        this.groupName = groupName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "UserGroup{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", groupName=" + groupName +
                '}';
    }
}
