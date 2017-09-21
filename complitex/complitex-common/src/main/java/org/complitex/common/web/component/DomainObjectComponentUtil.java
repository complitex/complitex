package org.complitex.common.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.converter.BooleanConverter;
import org.complitex.common.converter.DateConverter;
import org.complitex.common.converter.DoubleConverter;
import org.complitex.common.converter.IntegerConverter;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.web.component.type.*;
import org.complitex.common.web.model.AttributeStringModel;
import org.complitex.common.web.model.SimpleTypeModel;
import org.complitex.entity.Entity;
import org.complitex.entity.EntityAttribute;
import org.complitex.entity.StringValue;
import org.complitex.entity.StringValueUtil;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.complitex.common.util.EjbBeanLocator.getBean;
import static org.complitex.common.web.component.domain.DomainObjectAccessUtil.canEdit;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.04.2014 16:59
 */
public class DomainObjectComponentUtil {
    public static final String INPUT_COMPONENT_ID = "input";

    public static IModel<String> labelModel(final List<StringValue> attributeNames, final Locale locale) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return Strings.capitalize(StringValueUtil.getValue(attributeNames, locale).toLowerCase(locale));
            }
        };
    }

    public static Component newInputComponent(String entityName, String strategyName, DomainObject object,
            Attribute attribute, final Locale locale, boolean isHistory) {
        StrategyFactory strategyFactory = getBean(StrategyFactory.class);
        IStrategy strategy = strategyFactory.getStrategy(strategyName, entityName);

        Entity entity = strategy.getEntity();
        EntityAttribute entityAttribute = entity.getAttribute(attribute.getEntityAttributeId());
        IModel<String> labelModel = labelModel(entityAttribute.getNames(), locale);

        Component input = null;

        switch (entityAttribute.getValueType()) {
            case STRING: {
                input = new StringPanel(INPUT_COMPONENT_ID, new AttributeStringModel(attribute), entityAttribute.isRequired(),
                        labelModel, !isHistory && canEdit(strategyName, entityName, object));
            }
            break;
//            case BIG_STRING: {
//                input = new BigStringPanel(INPUT_COMPONENT_ID, new AttributeStringModel(attribute), entityAttribute.isMandatory(),
//                        labelModel, !isHistory && canEdit(strategyName, entityName, object));
//            }
//            break;
            case STRING_VALUE: {
                IModel<List<StringValue>> model = new PropertyModel<>(attribute, "stringValues");
                input = new StringValuePanel(INPUT_COMPONENT_ID, model, entityAttribute.isRequired(), labelModel,
                        !isHistory && canEdit(strategyName, entityName, object));
            }
            break;
            case INTEGER: {
                IModel<Integer> model = new SimpleTypeModel<>(attribute, new IntegerConverter());
                input = new IntegerPanel(INPUT_COMPONENT_ID, model, entityAttribute.isRequired(), labelModel,
                        !isHistory && canEdit(strategyName, entityName, object));
            }
            break;
            case DATE: {
                IModel<Date> model = new SimpleTypeModel<>(attribute, new DateConverter());
                input = new DatePanel(INPUT_COMPONENT_ID, model, entityAttribute.isRequired(), labelModel,
                        !isHistory && canEdit(strategyName, entityName, object));
            }
            break;
//            case DATE2: {
//                IModel<Date> model = new SimpleTypeModel<>(attribute, new DateConverter());
//                input = new Date2Panel(INPUT_COMPONENT_ID, model, entityAttribute.isMandatory(), labelModel,
//                        !isHistory && canEdit(strategyName, entityName, object));
//            }
//            break;
//            case MASKED_DATE: {
//                IModel<Date> model = new SimpleTypeModel<>(attribute, new DateConverter());
//                input = new MaskedDateInputPanel(INPUT_COMPONENT_ID, model, entityAttribute.isMandatory(), labelModel,
//                        !isHistory && canEdit(strategyName, entityName, object));
//            }
//            break;
            case BOOLEAN: {
                IModel<Boolean> model = new SimpleTypeModel<>(attribute, new BooleanConverter());
                input = new BooleanPanel(INPUT_COMPONENT_ID, model, labelModel,
                        !isHistory && canEdit(strategyName, entityName, object));
            }
            break;
            case DECIMAL: {
                IModel<Double> model = new SimpleTypeModel<>(attribute, new DoubleConverter());
                input = new DoublePanel(INPUT_COMPONENT_ID, model, entityAttribute.isRequired(), labelModel,
                        !isHistory && canEdit(strategyName, entityName, object));
            }
            break;
//            case GENDER: {
//                IModel<Gender> model = new SimpleTypeModel<>(attribute, new GenderConverter());
//                input = new GenderPanel(INPUT_COMPONENT_ID, model, entityAttribute.isMandatory(), labelModel,
//                        !isHistory && canEdit(strategyName, entityName, object));
//            }
//            break;
        }

        if (input == null) {
            throw new IllegalStateException("Input component for attribute type " + entityAttribute.getId() + " is not recognized.");
        }

        return input;
    }
}
