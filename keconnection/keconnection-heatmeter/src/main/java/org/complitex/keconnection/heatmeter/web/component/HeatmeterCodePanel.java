package org.complitex.keconnection.heatmeter.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.web.component.DisableAwareDropDownChoice;
import org.complitex.common.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.common.web.component.ShowMode;
import org.complitex.common.web.component.search.CollapsibleInputSearchComponent;
import org.complitex.common.web.component.search.ISearchCallback;
import org.complitex.common.web.component.search.SearchComponentState;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConnection;
import org.complitex.keconnection.organization.strategy.KeOrganizationStrategy;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 03.10.12 16:31
 */
public class HeatmeterCodePanel extends Panel {
    @EJB
    private BuildingStrategy buildingStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private CityStrategy cityStrategy;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeOrganizationStrategy organizationStrategy;

    public HeatmeterCodePanel(String id, final IModel<List<HeatmeterConnection>> model) {
        super(id, model);

        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        ListView listView = new ListView<HeatmeterConnection>("list", model) {
            @Override
            protected void populateItem(ListItem<HeatmeterConnection> item) {
                final HeatmeterConnection heatmeterConnection = item.getModelObject();

                //SearchComponentState
                final SearchComponentState searchComponentState = new SearchComponentState();

                if (heatmeterConnection.getBuildingId() != null) {
                    DomainObject building = buildingStrategy.getDomainObject(heatmeterConnection.getBuildingId(), true);
                    searchComponentState.put("building", building);

//                    if (building != null && building.getPrimaryStreetId() != null) {
//                        DomainObject street = streetStrategy.getDomainObject(building.getPrimaryStreetId(), true);
//                        searchComponentState.put("street", street);
//
//                        if (street != null && street.getParentId() != null) {
//                            DomainObject city = cityStrategy.getDomainObject(street.getParentId(), true);
//                            searchComponentState.put("city", city);
//                        }
//                    }
                }

                //Organization
                final IModel<DomainObject> organizationModel = new Model<DomainObject>(){
                    @Override
                    public void setObject(DomainObject object) {
                        super.setObject(object);

                        heatmeterConnection.setOrganizationId(object != null ? object.getObjectId() : null);
                        updateHeatmeterCode(heatmeterConnection);
                    }
                };

                if (heatmeterConnection.getOrganizationId() != null) {
                    DomainObject organization = organizationStrategy.getDomainObject(heatmeterConnection.getOrganizationId(), true);

                    if (organization != null){
                        organizationModel.setObject(organization);
                    }
                }else {
                    organizationModel.setObject(new DomainObject());
                }

                final DisableAwareDropDownChoice<DomainObject> organizations = new DisableAwareDropDownChoice<>("organization",
                        organizationModel,
                        new LoadableDetachableModel<List<? extends DomainObject>>() {
                            @Override
                            protected List<? extends DomainObject> load() {
                                List<DomainObject> list = new ArrayList<>();

                                DomainObject b = searchComponentState.get("building");

                                if (b != null) {
                                    DomainObject building = buildingStrategy.getDomainObject(b.getObjectId(), true);
//
//                                    if (building != null && building.getBuildingCodes() != null){
//                                        for (BuildingCode buildingCode : building.getBuildingCodes()){
//                                            list.add(organizationStrategy.getDomainObject(buildingCode.getOrganizationId(), true));
//                                        }
//                                    }
                                }

                                return list;
                            }
                        },
                        new DomainObjectDisableAwareRenderer() {

                            @Override
                            public Object getDisplayValue(DomainObject object) {
                                return organizationStrategy.displayDomainObject(object, getLocale());
                            }
                        });
                organizations.setOutputMarkupId(true);

                item.add(organizations);

                //Building
                item.add(new CollapsibleInputSearchComponent("building", searchComponentState,
                        Arrays.asList("city", "street", "building"), new ISearchCallback() {
                    @Override
                    public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
                        heatmeterConnection.setBuildingId(ids.get("building"));
                        updateHeatmeterCode(heatmeterConnection);

                        target.add(organizations);
                    }
                }, ShowMode.ALL, true));

                item.add(new AjaxLink<List<HeatmeterConnection>>("remove", model) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        getModelObject().remove(heatmeterConnection);

                        target.add(container);
                    }
                });
            }
        };
        container.add(listView);

        add(new AjaxLink<List<HeatmeterConnection>>("add", model) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getModelObject().add(new HeatmeterConnection());

                target.add(container);
            }
        });
    }

    private void updateHeatmeterCode(HeatmeterConnection code){
        if (code.getBuildingId() != null && code.getOrganizationId() != null){
            Long buildingCodeId = buildingStrategy.getBuildingCodeId(code.getOrganizationId(), code.getBuildingId());

            code.setObjectId(buildingCodeId);
        }
    }
}
