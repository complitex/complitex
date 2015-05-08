/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.strategy.web.edit;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.AttributeType;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.StringCultureBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.StringCultures;
import org.complitex.common.web.component.DisableAwareDropDownChoice;
import org.complitex.common.web.component.DomainObjectComponentUtil;
import org.complitex.common.web.component.IDisableAwareChoiceRenderer;
import org.complitex.keconnection.organization.strategy.KeOrganizationStrategy;
import org.complitex.keconnection.organization.strategy.entity.Organization;
import org.complitex.keconnection.organization_type.strategy.KeConnectionOrganizationTypeStrategy;
import org.complitex.organization.entity.RemoteDataSource;
import org.complitex.organization.strategy.OrganizationStrategy;
import org.complitex.organization.strategy.web.edit.OrganizationEditComponent;

import javax.ejb.EJB;
import java.util.List;

/**
 *
 * @author Artem
 */
public class KeOrganizationEditComponent extends OrganizationEditComponent {

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeOrganizationStrategy organizationStrategy;

    @EJB
    private StringCultureBean stringBean;
    private WebMarkupContainer readyCloseOmSection;
    private WebMarkupContainer omSection;
    private WebMarkupContainer dataSourceContainer;
    private IModel<RemoteDataSource> dataSourceModel;

    public KeOrganizationEditComponent(String id, boolean disabled) {
        super(id, disabled);
    }

    @Override
    protected Organization getDomainObject() {
        return (Organization) super.getDomainObject();
    }

    @Override
    protected void init() {
        super.init();

        final boolean isDisabled = isDisabled();

        final Organization organization = getDomainObject();

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

        //reference to jdbc data source. Only for calculation centres.
        {
            dataSourceContainer = new WebMarkupContainer("dataSourceContainer");
            dataSourceContainer.setOutputMarkupPlaceholderTag(true);
            add(dataSourceContainer);
            final IModel<String> dataSourceLabelModel = new ResourceModel("dataSourceLabel");
            dataSourceContainer.add(new Label("dataSourceLabel", dataSourceLabelModel));
            dataSourceModel = new Model<>();

            final String currentDataSource = organization.getStringValue(OrganizationStrategy.DATA_SOURCE);
            final List<RemoteDataSource> allDataSources = organizationStrategy.findRemoteDataSources(currentDataSource);

            for (RemoteDataSource ds : allDataSources) {
                if (ds.isCurrent()) {
                    dataSourceModel.setObject(ds);
                    break;
                }
            }

            DisableAwareDropDownChoice<RemoteDataSource> dataSource =
                    new DisableAwareDropDownChoice<>("dataSource", dataSourceModel, allDataSources,
                            new IDisableAwareChoiceRenderer<RemoteDataSource>() {

                                @Override
                                public Object getDisplayValue(RemoteDataSource remoteDataSource) {
                                    return remoteDataSource.getDataSource();
                                }

                                @Override
                                public boolean isDisabled(RemoteDataSource remoteDataSource) {
                                    return !remoteDataSource.isExist();
                                }

                                @Override
                                public String getIdValue(RemoteDataSource remoteDataSource, int index) {
                                    return remoteDataSource.getDataSource();
                                }
                            });
            dataSource.setRequired(true);
            dataSource.setLabel(dataSourceLabelModel);
            dataSource.setEnabled(enabled());
            dataSourceContainer.add(dataSource);
            dataSourceContainer.setVisible(isCalculationCenter());
        }
    }

    public boolean isCalculationCenter() {
        for (DomainObject organizationType : getOrganizationTypesModel().getObject()) {
            if (organizationType.getObjectId().equals(KeConnectionOrganizationTypeStrategy.CALCULATION_MODULE)) {
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
        target.add(dataSourceContainer.setVisible(isCalculationCenter()));
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

        final Organization organization = getDomainObject();

        if (!isServicingOrganization()) {
            //Readiness to close operating month.
            organization.removeAttribute(KeOrganizationStrategy.READY_CLOSE_OPER_MONTH);
        }

        if (!isCalculationCenter()) {
            //data source
            getDomainObject().removeAttribute(OrganizationStrategy.DATA_SOURCE);
        } else {
            //data source
            String dataSource = dataSourceModel.getObject().getDataSource();
            organization.setStringValue(OrganizationStrategy.DATA_SOURCE, dataSource);
        }
    }
}
