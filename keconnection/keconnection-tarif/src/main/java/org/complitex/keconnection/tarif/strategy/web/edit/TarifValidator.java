/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.tarif.strategy.web.edit;

import org.complitex.common.entity.DomainObject;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.common.web.component.domain.DomainObjectEditPanel;
import org.complitex.common.web.component.domain.validate.CodeValidator;
import org.complitex.common.web.component.domain.validate.DefaultValidator;
import org.complitex.common.web.component.domain.validate.IValidator;
import org.complitex.keconnection.tarif.strategy.TarifStrategy;

/**
 *
 * @author Artem
 */
public class TarifValidator implements IValidator {

    private static class TarifCodeValidator extends CodeValidator {

        TarifCodeValidator() {
            super("tarif", TarifStrategy.CODE);
        }

        @Override
        protected Long validateCode(Long id, String code) {
            TarifStrategy tarifStrategy = EjbBeanLocator.getBean(TarifStrategy.class);
            return tarifStrategy.validateCode(id, code);
        }
    }
    private final IValidator defaultValidator;
    private final IValidator codeValidator;

    public TarifValidator() {
        defaultValidator = new DefaultValidator("tarif");
        codeValidator = new TarifCodeValidator();
    }

    @Override
    public boolean validate(DomainObject object, DomainObjectEditPanel editPanel) {
        return defaultValidator.validate(object, editPanel) && codeValidator.validate(object, editPanel);
    }
}
