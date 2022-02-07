package ru.complitex.common.web.model;

import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.Attribute;
import ru.complitex.common.entity.StringValue;
import ru.complitex.common.util.Locales;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.04.2014 18:01
 */
public class AttributeStringModel implements IModel<String>{
    private Attribute attribute;

    public AttributeStringModel(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getObject() {
        StringValue s = attribute.getStringValue(Locales.getSystemLocaleId());

        return s != null ? s.getValue() : null;
    }

    @Override
    public void setObject(String object) {
        attribute.getStringValue(Locales.getSystemLocaleId()).setValue(object);
    }

    @Override
    public void detach() {

    }
}
