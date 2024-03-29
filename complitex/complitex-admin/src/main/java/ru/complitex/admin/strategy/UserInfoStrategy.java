package ru.complitex.admin.strategy;

import ru.complitex.admin.web.UserInfoComplexAttributesPanel;
import ru.complitex.common.entity.Attribute;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.NameBean;
import ru.complitex.common.util.StringUtil;
import ru.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.08.2010 14:43:55
 */
@Stateless
public class UserInfoStrategy extends TemplateStrategy {
    public final static Long LAST_NAME = 1000L;
    public final static Long FIRST_NAME = 1001L;
    public final static Long MIDDLE_NAME = 1002L;

    @EJB
    private NameBean nameBean;

    @Override
    public String getEntityName() {
        return "user_info";
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        Attribute firstName = object.getAttribute(FIRST_NAME);
        Attribute middleName = object.getAttribute(MIDDLE_NAME);
        Attribute lastName = object.getAttribute(LAST_NAME);

        String s = "";

        if (lastName != null){
            s += StringUtil.valueOf(nameBean.getLastName(lastName.getValueId()));
        }
        if (firstName != null){
            s += " " + StringUtil.valueOf(nameBean.getFirstName(firstName.getValueId()));
        }
        if (middleName != null){
            s += " " + StringUtil.valueOf(nameBean.getMiddleName(middleName.getValueId()));
        }

        return s;
    }

    @Override
    public String displayAttribute(Attribute attribute, Locale locale) {
        if (FIRST_NAME.equals(attribute.getEntityAttributeId())){
            return StringUtil.valueOf(nameBean.getFirstName(attribute.getValueId()));
        }else if (MIDDLE_NAME.equals(attribute.getEntityAttributeId())){
            return StringUtil.valueOf(nameBean.getMiddleName(attribute.getValueId()));
        }else if (LAST_NAME.equals(attribute.getEntityAttributeId())){
            return StringUtil.valueOf(nameBean.getLastName(attribute.getValueId()));
        }

        return super.displayAttribute(attribute, locale);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADMIN_MODULE_EDIT};
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass() {
        return UserInfoComplexAttributesPanel.class;
    }
}
