package ru.complitex.keconnection.organization.service.exception;

import ru.complitex.common.exception.AbstractException;

import javax.ejb.ApplicationException;

/**
 *
 * @author Artem
 */
@ApplicationException(rollback = true)
public class RootOrganizationNotFound extends AbstractException {

    public RootOrganizationNotFound() {
        super("Не найдено ни одной организации первого уровня");
    }
}
