package ru.complitex.common.web.component.domain.validate;

import ru.complitex.common.entity.Attribute;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.common.util.StringValueUtil;
import ru.complitex.common.web.component.domain.DomainObjectEditPanel;

import java.util.Locale;

/**
 *
 * @author Artem
 */
public abstract class CodeValidator implements IValidator {

    private static final String RESOURCE_BUNDLE = CodeValidator.class.getName();
    private final String entity;
    private final String strategyName;
    private final long codeAttributeTypeId;

    public CodeValidator(String entity, String strategyName, long codeAttributeTypeId) {
        this.entity = entity;
        this.strategyName = strategyName;
        this.codeAttributeTypeId = codeAttributeTypeId;
    }

    public CodeValidator(String entity, long codeAttributeTypeId) {
        this(entity, null, codeAttributeTypeId);
    }

    @Override
    public boolean validate(DomainObject object, DomainObjectEditPanel editPanel) {
        Attribute codeAttribute = object.getAttribute(codeAttributeTypeId);
        if (codeAttribute == null) {
            throw new IllegalStateException("Domain object(entity = " + entity + ", id = " + object.getObjectId()
                    + ") has no attribute with attribute type id = " + codeAttributeTypeId + "!");
        }
        if (codeAttribute.getStringValues() == null) {
            throw new IllegalStateException("Attribute of domain object(entity = " + entity + ", id = " + object.getObjectId()
                    + ") with attribute type id = " + codeAttribute + " and attribute id = " + codeAttribute.getAttributeId()
                    + " has null lozalized values.");
        }

        StringLocaleBean stringLocaleBean = EjbBeanLocator.getBean(StringLocaleBean.class);
        String code = StringValueUtil.getValue(codeAttribute.getStringValues(), stringLocaleBean.getSystemLocale());

        Long existingId = validateCode(object.getObjectId(), code);
        if (existingId != null) {
            editPanel.error(getErrorMessage(existingId, code, editPanel.getLocale()));
            return false;
        }
        return true;
    }

    protected String getErrorMessage(Long existingId, String code, Locale locale) {
        IStrategy strategy = EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(strategyName, entity);
        String entityName = StringValueUtil.getValue(strategy.getEntity().getNames(), locale);
        return ResourceUtil.getFormatString(RESOURCE_BUNDLE, "code_validation_error", locale, entityName, existingId);
    }

    protected abstract Long validateCode(Long id, String code);
}
