package org.complitex.common.web.model;

import org.apache.wicket.model.IModel;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.StringCulture;
import org.complitex.common.util.Locales;

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
        StringCulture s = attribute.getStringCulture(Locales.getSystemLocaleId());

        return s != null ? s.getValue() : null;
    }

    @Override
    public void setObject(String object) {
        attribute.getStringCulture(Locales.getSystemLocaleId()).setValue(object);
    }

    @Override
    public void detach() {

    }
}
