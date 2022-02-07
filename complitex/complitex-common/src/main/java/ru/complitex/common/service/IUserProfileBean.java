package ru.complitex.common.service;

import ru.complitex.common.exception.WrongCurrentPasswordException;

import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.02.11 17:31
 */
public interface IUserProfileBean {

    String getFullName(Long userId, Locale locale);


    void updatePassword(String currentPassword, final String password) throws WrongCurrentPasswordException;


    void updateMainUserOrganization(long mainUserOrganizationId);
}
