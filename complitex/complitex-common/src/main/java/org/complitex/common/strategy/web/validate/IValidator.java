/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.common.strategy.web.validate;

import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.web.DomainObjectEditPanel;

/**
 *
 * @author Artem
 */
public interface IValidator {

    boolean validate(DomainObject object, DomainObjectEditPanel editPanel);
}
