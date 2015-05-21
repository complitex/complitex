package org.complitex.organization.strategy.web.edit;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.*;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.complitex.common.entity.*;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.web.component.*;
import org.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import org.complitex.common.web.component.domain.DomainDropDownChoice;
import org.complitex.common.web.component.domain.DomainObjectAccessUtil;
import org.complitex.common.web.component.domain.DomainObjectEditPanel;
import org.complitex.common.web.component.search.SearchComponentState;
import org.complitex.common.web.component.search.WiQuerySearchComponent;
import org.complitex.organization_type.strategy.OrganizationTypeStrategy;

import javax.ejb.EJB;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.complitex.common.strategy.organization.IOrganizationStrategy.*;

public class OrganizationEditComponent extends AbstractComplexAttributesPanel {

    @EJB
    private StrategyFactory strategyFactory;

    @EJB(name = "OrganizationStrategy")
    private IOrganizationStrategy organizationStrategy;

    @EJB
    private OrganizationTypeStrategy organizationTypeStrategy;
    private SearchComponentState districtSearchComponentState;
    private IModel<List<DomainObject>> organizationTypesModel;
    private WebMarkupContainer districtContainer;
    private WebMarkupContainer districtRequiredContainer;
    private WebMarkupContainer parentContainer;

    private WebMarkupContainer dataSourceContainer;
    private IModel<RemoteDataSource> dataSourceModel;

    private WebMarkupContainer serviceBillingContainer;

    public OrganizationEditComponent(String id, boolean disabled) {
        super(id, disabled);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(new PackageResourceReference(OrganizationEditComponent.class,
                OrganizationEditComponent.class.getSimpleName() + ".css")));
    }

    protected boolean isNew() {
        return getDomainObject().getObjectId() == null;
    }

    @Override
    protected void init() {
        final DomainObject organization = getDomainObject();

        // organization type container
        final List<? extends DomainObject> allOrganizationTypes = organizationTypeStrategy.getAll();
        DomainObjectDisableAwareRenderer renderer = new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return organizationTypeStrategy.displayDomainObject(object, getLocale());
            }
        };

        organizationTypesModel = new IModel<List<DomainObject>>() {

            private List<DomainObject> organizationTypes = Lists.newArrayList();

            {
                for (Attribute attribute : organization.getAttributes(ORGANIZATION_TYPE)) {
                    if (attribute.getValueId() != null) {
                        for (DomainObject organizationType : allOrganizationTypes) {
                            if (organizationType.getObjectId().equals(attribute.getValueId())) {
                                organizationTypes.add(organizationType);
                            }
                        }
                    }
                }
            }

            @Override
            public List<DomainObject> getObject() {
                return organizationTypes;
            }

            @Override
            public void setObject(List<DomainObject> organizationTypes) {
                this.organizationTypes = organizationTypes;
            }

            @Override
            public void detach() {
            }
        };

        if (isNew()) {
            DomainObject defaultOrganizationType = null;
            for (DomainObject organizationType : allOrganizationTypes) {
                if (organizationType.getObjectId().equals(OrganizationTypeStrategy.USER_ORGANIZATION_TYPE)
                        && (organizationType.getStatus() == StatusType.ACTIVE)) {
                    defaultOrganizationType = organizationType;
                    break;
                }
            }
            if (defaultOrganizationType == null) {
                for (DomainObject organizationType : allOrganizationTypes) {
                    if (organizationType.getStatus() == StatusType.ACTIVE) {
                        defaultOrganizationType = organizationType;
                        break;
                    }
                }
            }
            if (defaultOrganizationType != null) {
                organizationTypesModel.setObject(Lists.newArrayList(defaultOrganizationType));
            }
        }

        DisableAwareListMultipleChoice<DomainObject> organizationType =
                new DisableAwareListMultipleChoice<DomainObject>("organizationType", organizationTypesModel,
                allOrganizationTypes, renderer);
        organizationType.add(AttributeModifier.replace("size", allOrganizationTypes.size() > 8 ? "8"
                : String.valueOf(allOrganizationTypes.size())));
        if (isOrganizationTypeEnabled()) {
            organizationType.add(new AjaxFormComponentUpdatingBehavior("onclick") {

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    onOrganizationTypeChanged(target);
                }
            });
        } else {
            organizationType.setEnabled(false);
        }
        add(organizationType);

        //district container
        districtContainer = new WebMarkupContainer("districtContainer");
        districtContainer.setOutputMarkupPlaceholderTag(true);
        add(districtContainer);

        //district required container
        districtRequiredContainer = new WebMarkupContainer("districtRequiredContainer");
        districtContainer.add(districtRequiredContainer);

        //parent container
        parentContainer = new WebMarkupContainer("parentContainer");
        parentContainer.setOutputMarkupPlaceholderTag(true);
        add(parentContainer);

        //district
        districtSearchComponentState = new SearchComponentState();
        Attribute districtAttribute = organization.getAttribute(IOrganizationStrategy.DISTRICT);
        if (districtAttribute != null) {
            Long districtId = districtAttribute.getValueId();
            if (districtId != null) {
                IStrategy districtStrategy = strategyFactory.getStrategy("district");
                DomainObject district = districtStrategy.getDomainObject(districtId, true);
                EntityObjectInfo info = districtStrategy.findParentInSearchComponent(districtId, null);
                if (info != null) {
                    districtSearchComponentState = districtStrategy.getSearchComponentStateForParent(info.getId(), info.getEntityName(), null);
                    districtSearchComponentState.put("district", district);
                }
            }
        }

        districtContainer.add(new WiQuerySearchComponent("district", districtSearchComponentState, ImmutableList.of("city", "district"),
                null, ShowMode.ACTIVE, enabled()));
        districtContainer.setVisible(isDistrictVisible());
        districtRequiredContainer.setVisible(isDistrictRequired());

        //parent
        final Attribute parentAttribute = organization.getAttribute(IOrganizationStrategy.USER_ORGANIZATION_PARENT);
        IModel<Long> parentModel = new Model<Long>();
        if (parentAttribute != null) {
            parentModel = new Model<Long>() {

                @Override
                public Long getObject() {
                    return parentAttribute.getValueId();
                }

                @Override
                public void setObject(Long object) {
                    parentAttribute.setValueId(object);
                }
            };
        }

        if (isNew()) {
            parentContainer.add(new UserOrganizationPicker("parent", parentModel, true) {

                @Override
                protected void onUpdate(AjaxRequestTarget target, DomainObject newOrganization) {
                    if (newOrganization != null && newOrganization.getObjectId() != null && newOrganization.getObjectId() > 0) {
                        DomainObjectEditPanel editPanel = findParent(DomainObjectEditPanel.class);
                        editPanel.updateParentPermissions(target, newOrganization.getSubjectIds());
                    }
                }
            });
        } else {
            Set<Long> excludeOrganizationIds = Sets.newHashSet(organization.getObjectId());
            excludeOrganizationIds.addAll(organizationStrategy.getTreeChildrenOrganizationIds(organization.getObjectId()));
            Long[] excludeAsArray = new Long[excludeOrganizationIds.size()];
            UserOrganizationPicker parent = new UserOrganizationPicker("parent", parentModel, true,
                    excludeOrganizationIds.toArray(excludeAsArray));
            parent.setEnabled(enabled());
            parentContainer.add(parent);
        }
        parentContainer.setVisible(isParentVisible());

        //reference to jdbc data source. Only for calculation centres.
        {
            dataSourceContainer = new WebMarkupContainer("dataSourceContainer");
            dataSourceContainer.setOutputMarkupPlaceholderTag(true);
            add(dataSourceContainer);

            dataSourceContainer.add(new Label("dataSourceLabel", new ResourceModel("dataSourceLabel")));
            dataSourceModel = new Model<>();

            String currentDataSource = organization.getStringValue(IOrganizationStrategy.DATA_SOURCE);
            List<RemoteDataSource> allDataSources = organizationStrategy.findRemoteDataSources(currentDataSource);

            for (RemoteDataSource ds : allDataSources) {
                if (ds.isCurrent()) {
                    dataSourceModel.setObject(ds);
                    break;
                }
            }

            DisableAwareDropDownChoice<RemoteDataSource> dataSource =
                    new DisableAwareDropDownChoice<RemoteDataSource>("dataSource", dataSourceModel, allDataSources,
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
            dataSource.setEnabled(enabled());
            dataSourceContainer.add(dataSource);
            dataSourceContainer.setVisible(isCalculationCenter());
        }

        //service billing
        {
            serviceBillingContainer = new WebMarkupContainer("serviceBillingContainer");
            serviceBillingContainer.setOutputMarkupPlaceholderTag(true);
            serviceBillingContainer.setVisible(isUserOrganization());
            add(serviceBillingContainer);

            ListView<Attribute> serviceBillings = new ListView<Attribute>("serviceBillings",
                    new LoadableDetachableModel<List<? extends Attribute>>() {
                @Override
                protected List<? extends Attribute> load() {
                    return organization.getAttributes(SERVICE);
                }
            }) {
                @Override
                protected void populateItem(ListItem<Attribute> item) {
                    Attribute attribute = item.getModelObject();

                    item.add(new DomainDropDownChoice("service", "service", new PropertyModel<>(item.getModel(), "valueId"))
                            .setRequired(true));
                    item.add(new DomainDropDownChoice("billing", "organization", new PropertyModel<>(
                            organization.getAttribute(BILLING, attribute.getAttributeId()), "valueId")){
                        @Override
                        protected void onFilter(DomainObjectFilter filter) {
                            filter.addAdditionalParam(ORGANIZATION_TYPE_PARAMETER,
                                    Collections.singletonList(OrganizationTypeStrategy.BILLING_TYPE));
                        }
                    }.setRequired(true));
                    item.add(new AjaxLink("remove") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            organization.removeAttribute(attribute.getAttributeTypeId(), attribute.getAttributeId());
                            target.add(serviceBillingContainer);
                        }
                    });
                }
            };
            serviceBillingContainer.add(serviceBillings);

            serviceBillingContainer.add(new AjaxLink("add") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    organization.addAttributes(SERVICE, BILLING);
                    target.add(serviceBillingContainer);
                }
            });
        }
    }

    protected boolean isOrganizationTypeEnabled() {
        return enabled();
    }

    protected boolean enabled() {
        return !isDisabled() && DomainObjectAccessUtil.canEdit(getStrategyName(), "organization", getDomainObject());
    }

    protected String getStrategyName() {
        return null;
    }

    protected void onOrganizationTypeChanged(AjaxRequestTarget target) {
        //parent container
        boolean parentContainerWasVisible = parentContainer.isVisible();
        parentContainer.setVisible(isParentVisible());
        boolean parentContainerVisibleNow = parentContainer.isVisible();
        if (parentContainerWasVisible ^ parentContainerVisibleNow) {
            target.add(parentContainer);
        }

        // district container
        boolean districtContainerWasVisible = districtContainer.isVisible();
        boolean districtRequiredContainerWasVisible = districtRequiredContainer.isVisible();
        districtContainer.setVisible(isDistrictVisible());
        districtRequiredContainer.setVisible(isDistrictRequired());
        boolean districtContainerVisibleNow = districtContainer.isVisible();
        boolean districtRequiredContainerVisibleNow = districtRequiredContainer.isVisible();
        if ((districtContainerWasVisible ^ districtContainerVisibleNow)
                || (districtRequiredContainerWasVisible ^ districtRequiredContainerVisibleNow)) {
            target.add(districtContainer);
        }

        //data source
        {
            dataSourceContainer.setVisible(isCalculationCenter());
            target.add(dataSourceContainer);
        }

        {
            serviceBillingContainer.setVisible(isUserOrganization());
            target.add(serviceBillingContainer);
        }
    }

    public boolean isDistrictEntered() {
        DomainObject district = districtSearchComponentState.get("district");
        Long districtId = district != null ? district.getObjectId() : null;
        return districtId != null && districtId > 0;
    }

    protected boolean isParentVisible() {
        for (DomainObject organizationType : getOrganizationTypesModel().getObject()) {
            if (organizationType.getObjectId().equals(OrganizationTypeStrategy.USER_ORGANIZATION_TYPE)
                    || organizationType.getObjectId().equals(OrganizationTypeStrategy.SERVICING_ORGANIZATION_TYPE)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isDistrictVisible() {
        for (DomainObject organizationType : getOrganizationTypesModel().getObject()) {
            if (organizationType.getObjectId().equals(OrganizationTypeStrategy.USER_ORGANIZATION_TYPE)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isUserOrganization() {
        for (DomainObject organizationType : getOrganizationTypesModel().getObject()) {
            if (organizationType.getObjectId().equals(OrganizationTypeStrategy.USER_ORGANIZATION_TYPE)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCalculationCenter() {
        for (DomainObject organizationType : getOrganizationTypesModel().getObject()) {
            if (organizationType.getObjectId().equals(OrganizationTypeStrategy.BILLING_TYPE)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isDistrictRequired() {
        return false;
    }

    protected IModel<List<DomainObject>> getOrganizationTypesModel() {
        return organizationTypesModel;
    }

    @Override
    public void onUpdate() {
        onPersist();
    }

    @Override
    public void onInsert() {
        onPersist();
    }

    protected void onPersist() {
        //district
        Attribute districtAttribute = getDomainObject().getAttribute(IOrganizationStrategy.DISTRICT);
        if (isDistrictVisible()) {
            districtAttribute.setValueId(isDistrictEntered() ? districtSearchComponentState.get("district").getObjectId() : null);
        } else {
            districtAttribute.setValueId(null);
        }

        //parent
        Attribute parentAttribute = getDomainObject().getAttribute(IOrganizationStrategy.USER_ORGANIZATION_PARENT);
        if (!isParentVisible()) {
            parentAttribute.setValueId(null);
        }

        //organization types
        getDomainObject().removeAttribute(ORGANIZATION_TYPE);
        List<DomainObject> organizationTypes = getOrganizationTypesModel().getObject();
        if (organizationTypes != null && !organizationTypes.isEmpty()) {
            Collections.sort(organizationTypes, (o1, o2) -> o1.getObjectId().compareTo(o2.getObjectId()));
            long attributeId = 1;
            for (DomainObject organizationType : getOrganizationTypesModel().getObject()) {
                Attribute attribute = new Attribute();
                attribute.setAttributeId(attributeId++);
                attribute.setAttributeTypeId(ORGANIZATION_TYPE);
                attribute.setValueTypeId(ORGANIZATION_TYPE);
                attribute.setValueId(organizationType.getObjectId());
                getDomainObject().addAttribute(attribute);
            }
        }

        if (!isCalculationCenter()) {
            //data source
            getDomainObject().removeAttribute(DATA_SOURCE);
        } else {
            //data source
            getDomainObject().setStringValue(DATA_SOURCE, dataSourceModel.getObject().getDataSource());

        }
    }
}
