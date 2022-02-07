package ru.complitex.common.web.component.domain.validate;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.common.util.Locales;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.common.web.component.domain.DomainObjectEditPanel;

/**
 *
 * @author Artem
 */
public class DefaultValidator implements IValidator {

    private static final String RESOURCE_BUNDLE = DefaultValidator.class.getName();
    private String entity;

    public DefaultValidator(String entity) {
        this.entity = entity;
    }

    @Override
    public boolean validate(DomainObject object, DomainObjectEditPanel editPanel) {
        IStrategy strategy = EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(entity);

        Long existingObjectId = strategy.performDefaultValidation(object, Locales.getSystemLocale());

        if (existingObjectId != null) {
            String entityName = strategy.getEntity().getName(editPanel.getLocale());
            editPanel.error(ResourceUtil.getFormatString(RESOURCE_BUNDLE, "default_validation_error", editPanel.getLocale(), entityName,
                    existingObjectId));
            return false;
        }
        return true;
    }
}
