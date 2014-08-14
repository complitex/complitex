package org.complitex.common.web.model;

import org.apache.wicket.model.IModel;
import org.complitex.common.entity.Attribute;
import org.complitex.common.service.LocaleBean;
import org.complitex.common.util.EjbBeanLocator;

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
        return attribute.getStringCulture(EjbBeanLocator.getBean(LocaleBean.class).getSystemLocaleId()).getValue();
    }

    @Override
    public void setObject(String object) {
        attribute.getStringCulture(EjbBeanLocator.getBean(LocaleBean.class).getSystemLocaleId()).setValue(object);
    }

    @Override
    public void detach() {

    }
}
