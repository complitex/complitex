package org.complitex.common.web.component.domain.validate;

import org.complitex.common.entity.DomainObject;
import org.complitex.common.web.component.domain.DomainObjectEditPanel;

/**
 *
 * @author Artem
 */
public interface IValidator {

    boolean validate(DomainObject object, DomainObjectEditPanel editPanel);
}
