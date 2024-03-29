package ru.complitex.organization.strategy.web.edit;

import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.common.web.component.domain.DomainObjectEditPanel;
import ru.complitex.common.web.component.domain.validate.IValidator;

import java.text.MessageFormat;
import java.util.Locale;

import static ru.complitex.common.strategy.organization.IOrganizationStrategy.CODE;
import static ru.complitex.common.strategy.organization.IOrganizationStrategy.NAME;

/**
 *
 * @author Artem
 */
public class OrganizationValidator implements IValidator {

    private Locale systemLocale;

    public OrganizationValidator(Locale systemLocale) {
        this.systemLocale = systemLocale;
    }
    private OrganizationEditComponent organizationEditComponent;

    @Override
    public boolean validate(DomainObject organization, DomainObjectEditPanel editPanel) {
        return validate(organization, getEditComponent(editPanel));
    }

    protected boolean validate(DomainObject organization, OrganizationEditComponent editComponent) {
        boolean valid = checkDistrict(organization, editComponent);

        if (valid) {
            valid = checkUniqueness(organization, editComponent);
        }

        return valid;
    }

    private OrganizationEditComponent getEditComponent(DomainObjectEditPanel editPanel) {
        if (organizationEditComponent == null) {
            organizationEditComponent = editPanel.visitChildren(OrganizationEditComponent.class,
                    new IVisitor<OrganizationEditComponent, OrganizationEditComponent>() {

                        @Override
                        public void component(OrganizationEditComponent object, IVisit<OrganizationEditComponent> visit) {
                            visit.stop(object);
                        }
                    });
        }

        return organizationEditComponent;
    }

    protected boolean checkDistrict(DomainObject object, OrganizationEditComponent editComponent) {
        return true;
    }

    private boolean checkUniqueness(DomainObject object, OrganizationEditComponent editComponent) {
        IOrganizationStrategy organizationStrategy = EjbBeanLocator.getBean(IOrganizationStrategy.BEAN_NAME);

        boolean valid = true;

        Long byName = organizationStrategy.validateName(object.getObjectId(), object.getStringValue(NAME, systemLocale), systemLocale);
        if (byName != null) {
            valid = false;
            editComponent.error(MessageFormat.format(editComponent.getString("unique_name"), byName));
        }

        Long byCode = organizationStrategy.validateCode(object.getObjectId(), object.getStringValue(CODE, systemLocale));
        if (byCode != null) {
            valid = false;
            editComponent.error(MessageFormat.format(editComponent.getString("unique_code"), byCode));
        }

        return valid;
    }
}
