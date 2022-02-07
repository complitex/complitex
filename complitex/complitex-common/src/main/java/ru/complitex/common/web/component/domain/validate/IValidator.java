package ru.complitex.common.web.component.domain.validate;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.web.component.domain.DomainObjectEditPanel;

/**
 *
 * @author Artem
 */
public interface IValidator {

    boolean validate(DomainObject object, DomainObjectEditPanel editPanel);
}
