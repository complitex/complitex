package org.complitex.keconnection.organization.strategy.web.edit;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.AttributeType;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.StringCultureBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.StringCultures;
import org.complitex.common.web.component.DomainObjectComponentUtil;
import org.complitex.keconnection.organization.strategy.KeOrganizationStrategy;
import org.complitex.keconnection.organization.strategy.entity.KeOrganization;
import org.complitex.keconnection.organization_type.strategy.KeConnectionOrganizationTypeStrategy;
import org.complitex.organization.strategy.web.edit.OrganizationEditComponent;

import javax.ejb.EJB;

public class KeOrganizationEditComponent extends OrganizationEditComponent {

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeOrganizationStrategy organizationStrategy;

    @EJB
    private StringCultureBean stringBean;
    private WebMarkupContainer readyCloseOmSection;
    private WebMarkupContainer omSection;

    public KeOrganizationEditComponent(String id, boolean disabled) {
        super(id, disabled);
    }

    @Override
    protected KeOrganization getDomainObject() {
        return (KeOrganization) super.getDomainObject();
    }

    @Override
    protected void init() {
        super.init();

        final boolean isDisabled = isDisabled();

        final KeOrganization organization = getDomainObject();

        //Readiness to close operating month. It is servicing organization only attribute.
        {
            readyCloseOmSection = new WebMarkupContainer("readyCloseOmSection");
            readyCloseOmSection.setOutputMarkupPlaceholderTag(true);
            add(readyCloseOmSection);

            final long attributeTypeId = KeOrganizationStrategy.READY_CLOSE_OPER_MONTH;
            Attribute attribute = organization.getAttribute(attributeTypeId);
            if (attribute == null) {
                attribute = new Attribute();
                attribute.setAttributeTypeId(attributeTypeId);
                attribute.setObjectId(organization.getObjectId());
                attribute.setAttributeId(1L);
                attribute.setStringCultures(StringCultures.newStringCultures());
            }
            final AttributeType attributeType =
                    organizationStrategy.getEntity().getAttributeType(attributeTypeId);
            readyCloseOmSection.add(new Label("label",
                    DomainObjectComponentUtil.labelModel(attributeType.getAttributeNames(), getLocale())));
            readyCloseOmSection.add(new WebMarkupContainer("required").setVisible(attributeType.isMandatory()));

            readyCloseOmSection.add(DomainObjectComponentUtil.newInputComponent("organization", getStrategyName(),
                    organization, attribute, getLocale(), true));

            //initial visibility
            readyCloseOmSection.setVisible(isServicingOrganization());
        }

        //Operating month. Only for servicing organizations.
        {
            omSection = new WebMarkupContainer("omSection");
            omSection.setOutputMarkupPlaceholderTag(true);
            add(omSection);

            omSection.add(new Label("om", organization.getOperatingMonth(getLocale())));

            //initial visibility
            omSection.setVisibilityAllowed(!isDisabled);
            omSection.setVisible(isServicingOrganization());
        }
    }

    public boolean isCalculationCenter() {
        for (DomainObject organizationType : getOrganizationTypesModel().getObject()) {
            if (organizationType.getObjectId().equals(KeConnectionOrganizationTypeStrategy.BILLING_TYPE)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onOrganizationTypeChanged(AjaxRequestTarget target) {
        super.onOrganizationTypeChanged(target);

        target.add(readyCloseOmSection.setVisible(isServicingOrganization()));
        target.add(omSection.setVisible(isServicingOrganization()));
    }

    @Override
    protected String getStrategyName() {
        return KeOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME;
    }

    public boolean isServicingOrganization() {
        for (DomainObject organizationType : getOrganizationTypesModel().getObject()) {
            if (organizationType.getObjectId().equals(KeConnectionOrganizationTypeStrategy.SERVICING_ORGANIZATION_TYPE)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean isOrganizationTypeEnabled() {
        Long organizationId = getDomainObject().getObjectId();
        return !(organizationId != null && (organizationId == KeOrganizationStrategy.ITSELF_ORGANIZATION_OBJECT_ID))
                && super.isOrganizationTypeEnabled();
    }

    @Override
    protected void onPersist() {
        super.onPersist();

        final KeOrganization organization = getDomainObject();

        if (!isServicingOrganization()) {
            //Readiness to close operating month.
            organization.removeAttribute(KeOrganizationStrategy.READY_CLOSE_OPER_MONTH);
        }
    }
}
