package ru.complitex.organization.exception;

import ru.complitex.common.exception.AbstractException;

import javax.ejb.ApplicationException;


@ApplicationException(rollback = true)
public class RootOrganizationNotFound extends AbstractException {

    public RootOrganizationNotFound() {
        super("Не найдено ни одной организации первого уровня");
    }
}
