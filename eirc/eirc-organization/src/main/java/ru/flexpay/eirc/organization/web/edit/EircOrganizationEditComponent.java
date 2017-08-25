package ru.flexpay.eirc.organization.web.edit;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.EntityAttribute;
import org.complitex.common.strategy.StringValueBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.StringValueUtil;
import org.complitex.common.web.component.DomainObjectComponentUtil;
import org.complitex.organization.strategy.web.edit.OrganizationEditComponent;
import ru.flexpay.eirc.dictionary.entity.OrganizationType;
import ru.flexpay.eirc.organization.entity.EircOrganization;
import ru.flexpay.eirc.organization.strategy.EircOrganizationStrategy;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Artem
 */
public class EircOrganizationEditComponent extends OrganizationEditComponent {
    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private EircOrganizationStrategy organizationStrategy;

    @EJB
    private StringValueBean stringBean;

    private WebMarkupContainer emailContainer;
    private WebMarkupContainer serviceContainer;

    private List<Attribute> services = null;

    public EircOrganizationEditComponent(String id, boolean disabled) {
        super(id, disabled);
    }

    @Override
    protected EircOrganization getDomainObject() {
        return (EircOrganization) super.getDomainObject();
    }

    @Override
    protected void init() {
        super.init();

        final boolean isDisabled = isDisabled();

        final EircOrganization organization = getDomainObject();

        // General attributes.
        {
            for (Map.Entry<Long, String> attribute : EircOrganizationStrategy.GENERAL_ATTRIBUTE_TYPES.entrySet()) {
                addAttributeContainer(attribute.getKey(), isDisabled, organization, attribute.getValue() + "Container");
            }
        }

        //E-mail. It is service provider organization only attribute.
        {
            emailContainer = addAttributeContainer(EircOrganizationStrategy.EMAIL, isDisabled, organization, "emailContainer");

            //initial visibility
            emailContainer.setVisible(isServiceProvider());
        }

        //Services. It is service provider organization only attribute.
        {
            serviceContainer = addServiceContainer(organization, "serviceContainer");

            //initial visibility
            serviceContainer.setVisible(isServiceProvider());
        }
    }

    private WebMarkupContainer addAttributeContainer(final long attributeTypeId, boolean disabled,
                                                     EircOrganization organization, String name) {
        WebMarkupContainer container = new WebMarkupContainer(name);
        container.setOutputMarkupPlaceholderTag(true);
        add(container);
        Attribute attribute = organization.getAttribute(attributeTypeId);
        if (attribute == null) {
            attribute = new Attribute();
            attribute.setAttributeTypeId(attributeTypeId);
            attribute.setObjectId(organization.getObjectId());
            attribute.setAttributeId(1L);
            attribute.setStringValues(StringValueUtil.newStringValues());
        }
        final EntityAttribute entityAttribute =
                organizationStrategy.getEntity().getAttribute(attributeTypeId);
        container.add(new Label("label",
                DomainObjectComponentUtil.labelModel(entityAttribute.getNames(), getLocale())));
        container.add(new WebMarkupContainer("required").setVisible(entityAttribute.isMandatory()));

        container.add(
                DomainObjectComponentUtil.newInputComponent("organization", getStrategyName(),
                        organization, attribute, getLocale(), disabled));

        return container;
    }

    private WebMarkupContainer addServiceContainer(EircOrganization organization, String name) {
        Long attributeTypeId = EircOrganizationStrategy.SERVICE;

        WebMarkupContainer container = new WebMarkupContainer(name);
        container.setOutputMarkupPlaceholderTag(true);
        add(container);
        Attribute attribute = organization.getAttribute(attributeTypeId);
        if (attribute == null) {
            attribute = new Attribute();
            attribute.setAttributeTypeId(attributeTypeId);
            attribute.setObjectId(organization.getObjectId());
            attribute.setAttributeId(1L);
            attribute.setStringValues(StringValueUtil.newStringValues());
        }
        final EntityAttribute entityAttribute =
                organizationStrategy.getEntity().getAttribute(attributeTypeId);
        container.add(new Label("label",
                DomainObjectComponentUtil.labelModel(entityAttribute.getNames(), getLocale())));
        container.add(new WebMarkupContainer("required").setVisible(entityAttribute.isMandatory()));

        ServiceAllowableListPanel panel = new ServiceAllowableListPanel("input", organization);
        services = panel.getServices();
        container.add(panel);

        return container;
    }

    @Override
    protected void onOrganizationTypeChanged(AjaxRequestTarget target) {
        super.onOrganizationTypeChanged(target);

        //e-mail.
        {
            boolean emailContainerWasVisible = emailContainer.isVisible();
            emailContainer.setVisible(isServiceProvider());
            boolean emailContainerVisibleNow = emailContainer.isVisible();
            if (emailContainerWasVisible ^ emailContainerVisibleNow) {
                target.add(emailContainer);
            }
        }
        
        // allowable services
        {
            boolean serviceContainerWasVisible = serviceContainer.isVisible();
            serviceContainer.setVisible(isServiceProvider());
            boolean serviceContainerVisibleNow = serviceContainer.isVisible();
            if (serviceContainerWasVisible ^ serviceContainerVisibleNow) {
                target.add(serviceContainer);
            }
        }
    }

    public boolean isServiceProvider() {
        List<DomainObject> types = getOrganizationTypesModel().getObject();
        for (DomainObject type : types) {
            if (type.getObjectId() != null &&
                    type.getObjectId().equals(OrganizationType.SERVICE_PROVIDER.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUserOrganization() {
        return super.isUserOrganization();
    }

    @Override
    protected boolean isDistrictVisible() {
        return super.isDistrictVisible() || isServiceProvider();
    }

    @Override
    protected void onPersist() {
        super.onPersist();

        final DomainObject organization = getDomainObject();

        organization.removeAttribute(EircOrganizationStrategy.SERVICE);
        if (!isServiceProvider()) {
            organization.removeAttribute(EircOrganizationStrategy.EMAIL);
        } else if (services != null) {
            long attributeId = 1;
            for (Attribute service : services) {
                service.setAttributeId(attributeId++);
                organization.addAttribute(service);
            }
        }
    }

    @Override
    protected String getStrategyName() {
        return EircOrganizationStrategy.EIRC_ORGANIZATION_STRATEGY_NAME;
    }
}
