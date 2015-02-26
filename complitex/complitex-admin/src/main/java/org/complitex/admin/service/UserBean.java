package org.complitex.admin.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.complitex.admin.strategy.UserInfoStrategy;
import org.complitex.common.entity.*;
import org.complitex.common.service.AbstractBean;
import org.complitex.common.strategy.EntityBean;
import org.complitex.common.util.DateUtil;
import org.complitex.common.web.DictionaryFwSession;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 15:05:09
 */
@Stateless
public class UserBean extends AbstractBean {
    public static final String STATEMENT_PREFIX = UserBean.class.getCanonicalName();

    @EJB
    private UserInfoStrategy userInfoStrategy;

    @EJB
    private EntityBean entityBean;

    public User newUser() {
        User user = new User();

        user.setUserGroups(new ArrayList<UserGroup>());
        user.setUserInfo(userInfoStrategy.newInstance());

        return user;
    }


    public boolean isUniqueLogin(String login) {
        return (Boolean) sqlSession().selectOne(STATEMENT_PREFIX + ".isUniqueLogin", login);
    }


    public User getUser(Long id) {
        return getUser(id, true);
    }


    public User getUser(Long id, boolean createUserInfo) {
        User user = (User) sqlSession().selectOne(STATEMENT_PREFIX + ".selectUser", id);

        if (user.getUserInfoObjectId() != null) {
            user.setUserInfo(userInfoStrategy.getDomainObject(user.getUserInfoObjectId(), false));
        } else if (createUserInfo) {
            user.setUserInfo(userInfoStrategy.newInstance());
        }

        return user;
    }


    public void save(User user, DictionaryFwSession session) {
        //удаление дубликатов организаций
        Map<Long, UserOrganization> userOrganizationMap = new HashMap<Long, UserOrganization>();
        for (UserOrganization userOrganization : user.getUserOrganizations()) {
            userOrganizationMap.put(userOrganization.getOrganizationObjectId(), userOrganization);
        }

        //установка главной организации, если не установлено пользователем
        boolean hasMain = false;
        for (UserOrganization userOrganization : userOrganizationMap.values()) {
            if (userOrganization.isMain()) {
                hasMain = true;
                break;
            }
        }
        if (!hasMain && !userOrganizationMap.isEmpty()) {
            userOrganizationMap.values().iterator().next().setMain(true);
        }

        if (user.getId() == null) { //Сохранение нового пользователя
            //сохранение информации о пользователе
            userInfoStrategy.insert(user.getUserInfo(), DateUtil.getCurrentDate());
            user.setUserInfoObjectId(user.getUserInfo().getObjectId());

            user.setPassword(DigestUtils.md5Hex(user.getLogin())); //md5 password

            sqlSession().insert(STATEMENT_PREFIX + ".insertUser", user);

            //сохранение групп привилегий
            for (UserGroup userGroup : user.getUserGroups()) {
                userGroup.setLogin(user.getLogin());
                sqlSession().insert(STATEMENT_PREFIX + ".insertUserGroup", userGroup);
            }

            //сохранение организаций
            for (UserOrganization userOrganization : userOrganizationMap.values()) {
                if (userOrganization.getOrganizationObjectId() != null) {
                    userOrganization.setUserId(user.getId());
                    sqlSession().insert(STATEMENT_PREFIX + ".insertUserOrganization", userOrganization);
                }
            }

        } else { //Редактирование пользователя
            User dbUser = (User) sqlSession().selectOne(STATEMENT_PREFIX + ".selectUser", user.getId());

            //удаление групп привилегий
            for (UserGroup dbUserGroup : dbUser.getUserGroups()) {
                boolean contain = false;

                for (UserGroup userGroup : user.getUserGroups()) {
                    if (userGroup.getGroupName().equals(dbUserGroup.getGroupName())) {
                        contain = true;
                        break;
                    }
                }

                if (!contain) {
                    sqlSession().delete(STATEMENT_PREFIX + ".deleteUserGroup", dbUserGroup.getId());
                }
            }

            //добавление групп привилегий
            for (UserGroup userGroup : user.getUserGroups()) {
                boolean contain = false;

                for (UserGroup dbUserGroup : dbUser.getUserGroups()) {
                    if (userGroup.getGroupName().equals(dbUserGroup.getGroupName())) {
                        contain = true;
                        break;
                    }
                }

                if (!contain) {
                    userGroup.setLogin(user.getLogin());
                    sqlSession().insert(STATEMENT_PREFIX + ".insertUserGroup", userGroup);
                }
            }

            //обновление и удаление организаций
            for (UserOrganization dbUserOrganization : dbUser.getUserOrganizations()) {
                boolean contain = false;

                for (UserOrganization userOrganization : userOrganizationMap.values()) {
                    if (userOrganization.getOrganizationObjectId() == null) {
                        continue;
                    }

                    if (userOrganization.getOrganizationObjectId().equals(dbUserOrganization.getOrganizationObjectId())) {
                        contain = true;

                        //обновление главной организации пользователя
                        if (dbUserOrganization.isMain() != userOrganization.isMain()) {
                            sqlSession().update(STATEMENT_PREFIX + ".updateUserOrganization", userOrganization);
                            session.setMainUserOrganization(null);
                        }

                        break;
                    }
                }

                if (!contain) {
                    sqlSession().delete(STATEMENT_PREFIX + ".deleteUserOrganization", dbUserOrganization.getId());
                }
            }

            //добавление организаций
            for (UserOrganization userOrganization : userOrganizationMap.values()) {
                if (userOrganization.getOrganizationObjectId() == null) {
                    continue;
                }

                boolean contain = false;

                for (UserOrganization dbUserOrganization : dbUser.getUserOrganizations()) {
                    if (userOrganization.getOrganizationObjectId().equals(dbUserOrganization.getOrganizationObjectId())) {
                        contain = true;
                        break;
                    }
                }

                if (!contain) {
                    userOrganization.setUserId(user.getId());
                    sqlSession().insert(STATEMENT_PREFIX + ".insertUserOrganization", userOrganization);
                }
            }

            //изменение пароля
            if (user.getNewPassword() != null) {
                user.setPassword(DigestUtils.md5Hex(user.getNewPassword())); //md5 password
                sqlSession().update(STATEMENT_PREFIX + ".updateUser", user);
            } else {
                user.setPassword(null); //не обновлять пароль
            }

            //сохранение информации о пользователе
            if (user.getUserInfoObjectId() != null) {
                DomainObject userInfo = user.getUserInfo();
                userInfoStrategy.update(userInfoStrategy.getDomainObject(userInfo.getObjectId(), false), userInfo, DateUtil.getCurrentDate());
            } else {
                userInfoStrategy.insert(user.getUserInfo(), DateUtil.getCurrentDate());
                user.setUserInfoObjectId(user.getUserInfo().getObjectId());
                sqlSession().update(STATEMENT_PREFIX + ".updateUser", user);
            }
        }
    }


    public List<User> getUsers(UserFilter filter) {
        List<User> users = sqlSession().selectList(STATEMENT_PREFIX + ".selectUsers", filter);
        for (User user : users) {
            if (user.getUserInfoObjectId() != null) {
                user.setUserInfo(userInfoStrategy.getDomainObject(user.getUserInfoObjectId(), false));
            }
        }

        return users;
    }


    public Long getUsersCount(UserFilter filter) {
        return sqlSession().selectOne(STATEMENT_PREFIX + ".selectUsersCount", filter);
    }

    public UserFilter newUserFilter() {
        UserFilter userFilter = new UserFilter();

        for (AttributeType attributeType : entityBean.getEntity(userInfoStrategy.getEntityName()).getAttributeTypes()) {
            userFilter.getAttributeFilters().add(new AttributeFilter(attributeType.getId()));
        }

        return userFilter;
    }

    public List<Attribute> getAttributeColumns(DomainObject object) {
        if (object == null) {
            return userInfoStrategy.newInstance().getAttributes();
        }

        List<AttributeType> attributeTypes = entityBean.getEntity(userInfoStrategy.getEntityName()).getAttributeTypes();
        List<Attribute> attributeColumns = new ArrayList<>(attributeTypes.size());

        for (AttributeType attributeType : attributeTypes) {
            for (Attribute attribute : object.getAttributes()) {
                if (attribute.getAttributeTypeId().equals(attributeType.getId())) {
                    attributeColumns.add(attribute);
                }
            }
        }

        return attributeColumns;
    }
}
