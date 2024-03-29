package ru.complitex.admin.service;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.codec.digest.DigestUtils;
import ru.complitex.admin.strategy.UserInfoStrategy;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.common.service.IUserProfileBean;
import ru.complitex.common.service.SessionBean;
import ru.complitex.common.exception.WrongCurrentPasswordException;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.02.11 17:47
 */
@Stateless
public class UserProfileBean extends AbstractBean implements IUserProfileBean {

    public static final String NS = UserProfileBean.class.getName();
    @Resource
    private SessionContext sessionContext;
    @EJB
    private UserBean userBean;
    @EJB
    private UserInfoStrategy userInfoStrategy;
    @EJB
    private SessionBean sessionBean;

    @Override
    public String getFullName(Long userId, Locale locale) {
        DomainObject userInfo = userBean.getUser(userId, false).getUserInfo();
        return userInfo != null ? userInfoStrategy.displayDomainObject(userInfo, locale) : "";
    }


    public void updatePassword(String currentPassword, final String password) throws WrongCurrentPasswordException {
        final String login = sessionContext.getCallerPrincipal().getName();

        String currentPasswordSha256 = sqlSession().selectOne(NS + ".selectPassword", login);

        if (!currentPasswordSha256.equals(DigestUtils.sha256Hex(currentPassword))) {
            throw new WrongCurrentPasswordException();
        }

        final String md5Password = DigestUtils.sha256Hex(password);
        sqlSession().update(NS + ".updatePassword", ImmutableMap.of("login", login, "password", md5Password));
    }


    public void updateMainUserOrganization(long mainUserOrganizationId) {
        long userId = sessionBean.getCurrentUserId();
        sqlSession().update(NS + ".clearMainOrganizationStatus", userId);
        sqlSession().update(NS + ".updateMainUserOrganization",
                ImmutableMap.of("userId", userId, "mainUserOrganizationId", mainUserOrganizationId));
    }
}
