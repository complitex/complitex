package ru.complitex.keconnection.tarif.strategy.web.edit;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.common.web.component.domain.DomainObjectEditPanel;
import ru.complitex.common.web.component.domain.validate.CodeValidator;
import ru.complitex.common.web.component.domain.validate.DefaultValidator;
import ru.complitex.common.web.component.domain.validate.IValidator;
import ru.complitex.keconnection.tarif.strategy.TarifGroupStrategy;

/**
 *
 * @author Artem
 */
public class TarifGroupValidator implements IValidator {

    private static class TarifGroupCodeValidator extends CodeValidator {

        TarifGroupCodeValidator() {
            super("tarif_group", TarifGroupStrategy.CODE);
        }

        @Override
        protected Long validateCode(Long id, String code) {
            TarifGroupStrategy tarifGroupStrategy = EjbBeanLocator.getBean(TarifGroupStrategy.class);
            return tarifGroupStrategy.validateCode(id, code);
        }
    }
    private final IValidator defaultValidator;
    private final IValidator codeValidator;

    public TarifGroupValidator() {
        defaultValidator = new DefaultValidator("tarif_group");
        codeValidator = new TarifGroupCodeValidator();
    }

    @Override
    public boolean validate(DomainObject object, DomainObjectEditPanel editPanel) {
        return defaultValidator.validate(object, editPanel) && codeValidator.validate(object, editPanel);
    }
}
