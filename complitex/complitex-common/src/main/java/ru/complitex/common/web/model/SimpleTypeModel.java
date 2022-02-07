package ru.complitex.common.web.model;

import org.apache.wicket.model.IModel;
import ru.complitex.common.converter.IConverter;
import ru.complitex.common.entity.Attribute;
import ru.complitex.common.entity.StringValue;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.util.EjbBeanLocator;

import java.io.Serializable;

import static org.apache.wicket.util.string.Strings.isEmpty;

/**
* @author Anatoly A. Ivanov java@inheaven.ru
*         Date: 08.04.2014 14:23
*/
public class SimpleTypeModel<T extends Serializable> implements IModel<T> {

    private Attribute attribute;
    private IConverter<T> converter;

    public SimpleTypeModel(Attribute attribute, IConverter<T> converter) {
        this.attribute = attribute;
        this.converter = converter;
    }

    @Override
    public T getObject() {
        if (!isEmpty(getStringValue().getValue())) {
            return converter.toObject(getStringValue().getValue());
        }
        return null;
    }

    @Override
    public void setObject(T object) {
        if (object != null) {
            getStringValue().setValue(converter.toString(object));
        }
    }

    private StringValue getStringValue(){
        return attribute.getStringValue(EjbBeanLocator.getBean(StringLocaleBean.class).getSystemLocaleId());
    }

    @Override
    public void detach() {
    }
}
