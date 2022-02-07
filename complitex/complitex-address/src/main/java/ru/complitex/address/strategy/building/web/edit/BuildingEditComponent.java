package ru.complitex.address.strategy.building.web.edit;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import ru.complitex.address.strategy.building.BuildingStrategy;
import ru.complitex.address.strategy.building.entity.BuildingCode;
import ru.complitex.common.entity.Attribute;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.web.component.DomainObjectComponentUtil;
import ru.complitex.common.web.component.DomainObjectDisableAwareRenderer;
import ru.complitex.common.web.component.DomainObjectInputPanel;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import ru.complitex.common.web.component.domain.DomainObjectAccessUtil;
import ru.complitex.common.web.component.list.AjaxRemovableListView;
import ru.complitex.common.web.component.organization.OrganizationPicker;
import ru.complitex.common.web.component.search.CollapsibleInputSearchComponent;
import ru.complitex.common.web.component.search.SearchComponentState;
import ru.complitex.organization_type.strategy.OrganizationTypeStrategy;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

public class BuildingEditComponent extends AbstractComplexAttributesPanel {

    @EJB
    private StrategyFactory strategyFactory;

    private SearchComponentState districtSearchComponentState;
    private FeedbackPanel messages;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private IOrganizationStrategy organizationStrategy;

    public BuildingEditComponent(String id, boolean disabled) {
        super(id, disabled);
    }

    private FeedbackPanel findFeedbackPanel() {
        if (messages == null) {
            messages = getPage().visitChildren(FeedbackPanel.class, new IVisitor<FeedbackPanel, FeedbackPanel>() {

                @Override
                public void component(FeedbackPanel object, IVisit<FeedbackPanel> visit) {
                    visit.stop(object);
                }
            });
        }
        return messages;
    }

    protected String getBuildingStrategyName() {
        return null;
    }

    @Override
    protected void init() {
        final FeedbackPanel feedbackPanel = findFeedbackPanel();
        final WebMarkupContainer attributesContainer = new WebMarkupContainer("attributesContainer");
        attributesContainer.setOutputMarkupId(true);
        add(attributesContainer);

        IStrategy buildingStrategy = strategyFactory.getStrategy(getBuildingStrategyName(), "building");
        IStrategy districtStrategy = strategyFactory.getStrategy("district");

         DomainObject building = getDomainObject();

        final boolean enabled = !isDisabled() && DomainObjectAccessUtil.canEdit(getBuildingStrategyName(),
                "building", building);

        final SearchComponentState parentSearchComponentState = getInputPanel().getParentSearchComponentState();

        //district
        final WebMarkupContainer districtContainer = new WebMarkupContainer("districtContainer");
        attributesContainer.add(districtContainer);

        Label districtLabel = new Label("districtLabel",
                DomainObjectComponentUtil.labelModel(buildingStrategy.getEntity().getAttribute(BuildingStrategy.DISTRICT).
                        getNames(), getLocale()));
        districtContainer.add(districtLabel);
        districtSearchComponentState = new SearchComponentState() {

            @Override
            public DomainObject put(String entity, DomainObject object) {
                super.put(entity, object);

                if ("district".equals(entity)) {
                    building.setValueId(BuildingStrategy.DISTRICT, object.getObjectId());
                }
                return object;
            }
        };
        districtSearchComponentState.updateState(parentSearchComponentState);

        final Attribute districtAttribute = building.getAttribute(BuildingStrategy.DISTRICT);
        if (districtAttribute != null) {
            final Long districtId = districtAttribute.getValueId();
            if (districtId != null) {
                DomainObject district = districtStrategy.getDomainObject(districtId, true);
                districtSearchComponentState.put("district", district);
            }
        }
        districtContainer.add(new CollapsibleInputSearchComponent("district", districtSearchComponentState,
                ImmutableList.of("country", "region", "city", "district"), null, ShowMode.ACTIVE, enabled));
        districtContainer.setVisible(districtAttribute != null);

        //primary building address
        DomainObjectInputPanel primaryAddressPanel = new DomainObjectInputPanel("primaryAddress", building,
                "building", null, getInputPanel().getParentId(), getInputPanel().getParentEntity(), getInputPanel().getDate()) {

            @Override
            public SearchComponentState initParentSearchComponentState() {
                SearchComponentState primaryAddressComponentState = super.initParentSearchComponentState();

                if (building.getObjectId() == null) {
                    primaryAddressComponentState.updateState(parentSearchComponentState);
                }
                return primaryAddressComponentState;
            }
        };
        attributesContainer.add(primaryAddressPanel);

        //
        //Building Code
        //
        List<BuildingCode> buildingCodes = new ArrayList<>();

        if (building.getObjectId() == null) { // new building
            buildingCodes.add(new BuildingCode());
        }

        final List<? extends DomainObject> allServicingOrganizations = organizationStrategy.getAllOuterOrganizations(getLocale());

        final DomainObjectDisableAwareRenderer organizationRenderer = new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return organizationStrategy.displayDomainObject(object, getLocale());
            }
        };

        final WebMarkupContainer buildingOrganizationAssociationsContainer =
                new WebMarkupContainer("buildingOrganizationAssociationsContainer");
        buildingOrganizationAssociationsContainer.setVisible(!isDisabled() || !buildingCodes.isEmpty());
        add(buildingOrganizationAssociationsContainer);

        final WebMarkupContainer associationsUpdateContainer = new WebMarkupContainer("associationsUpdateContainer");
        associationsUpdateContainer.setOutputMarkupId(true);
        buildingOrganizationAssociationsContainer.add(associationsUpdateContainer);

        ListView<BuildingCode> associations =
                new AjaxRemovableListView<BuildingCode>("associations", buildingCodes) {

                    @Override
                    protected void populateItem(ListItem<BuildingCode> item) {
                        final WebMarkupContainer fakeContainer = new WebMarkupContainer("fakeContainer");
                        item.add(fakeContainer);

                        final BuildingCode association = item.getModelObject();

                        //organization
                        IModel<DomainObject> organizationModel = new Model<DomainObject>() {

                            @Override
                            public DomainObject getObject() {
                                Long organizationId = association.getOrganizationId();
                                if (organizationId != null) {
                                    for (DomainObject o : allServicingOrganizations) {
                                        if (organizationId.equals(o.getObjectId())) {
                                            return o;
                                        }
                                    }
                                }
                                return null;
                            }

                            @Override
                            public void setObject(DomainObject organization) {
                                association.setOrganizationId(organization != null
                                        ? organization.getObjectId() : null);
                            }
                        };
                        //initialize model:
                        Long organizationId = association.getOrganizationId();
                        if (organizationId != null) {
                            for (DomainObject o : allServicingOrganizations) {
                                if (organizationId.equals(o.getObjectId())) {
                                    organizationModel.setObject(o);
                                }
                            }
                        }

                        item.add(new OrganizationPicker("organization", organizationModel,
                                OrganizationTypeStrategy.SERVICING_ORGANIZATION_TYPE));

                        //building code
                        IModel<Integer> buildingCodeModel = new PropertyModel<Integer>(association, "buildingCode");
                        TextField<Integer> buildingCode = new TextField<>("buildingCode", buildingCodeModel);
                        buildingCode.setEnabled(enabled);
                        buildingCode.add(new AjaxFormComponentUpdatingBehavior("blur") {

                            @Override
                            protected void onUpdate(AjaxRequestTarget target) {
                                target.add(associationsUpdateContainer);
                            }
                        });
                        item.add(buildingCode);

                        //remove link
                        addRemoveLink("removeAssociation", item, null, associationsUpdateContainer).setVisible(enabled);
                    }

                    @Override
                    protected boolean approveRemoval(ListItem<BuildingCode> item) {
                        return buildingCodes.size() > 1;
                    }
                };
        associationsUpdateContainer.add(associations);
        AjaxLink<Void> addAssociation = new AjaxLink<Void>("addAssociation") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                buildingCodes.add(new BuildingCode());
                target.add(associationsUpdateContainer);
            }
        };
        addAssociation.setVisible(enabled);
        buildingOrganizationAssociationsContainer.add(addAssociation);
    }

    @Override
    public void onInsert() {
        beforePersist();
    }

    @Override
    public void onUpdate() {
        beforePersist();
    }

    private void beforePersist() {
        final Attribute districtAttribute = getDomainObject().getAttribute(BuildingStrategy.DISTRICT);
        final DomainObject district = districtSearchComponentState.get("district");
        if (district != null && district.getObjectId() > 0 && district.getObjectId() > 0) {
            districtAttribute.setValueId(district.getObjectId());
        } else {
            districtAttribute.setValueId(null);
        }
    }
}
