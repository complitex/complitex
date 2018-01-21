package org.complitex.correction.web.component;

import com.google.common.collect.Lists;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.EntityBean;
import org.complitex.common.web.component.DisableAwareDropDownChoice;
import org.complitex.common.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.common.web.component.ShowMode;
import org.complitex.common.web.component.search.ISearchCallback;
import org.complitex.common.web.component.search.SearchComponentState;
import org.complitex.common.web.component.search.WiQuerySearchComponent;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.service.CorrectionBean;

import javax.ejb.EJB;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Artem
 */
public class AddressCorrectionInputPanel extends Panel {
    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private EntityBean entityBean;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    public AddressCorrectionInputPanel(String id, final Correction correction) {
        super(id);

        boolean isDistrict = "district".equals(correction.getEntityName());
        boolean isStreet = "street".equals(correction.getEntityName());
        boolean isBuilding = "building".equals(correction.getEntityName());
        boolean isApartment = "apartment".equals(correction.getEntityName());
        boolean isRoom = "room".equals(correction.getEntityName());

        //District
        final WebMarkupContainer districtContainer = new WebMarkupContainer("districtContainer");
        add(districtContainer);
        districtContainer.setVisible(isDistrict);

        districtContainer.add(new Label("districtLabel", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return entityBean.getEntity("district").getName(getLocale());
            }
        }));
        districtContainer.add(new TextField<>("district", new PropertyModel<String>(correction, "correction"))
                .setOutputMarkupId(true));

        //Street
        final WebMarkupContainer streetContainer = new WebMarkupContainer("streetContainer");
        streetContainer.setOutputMarkupId(true);
        streetContainer.setVisible(isStreet || isBuilding || isApartment || isRoom);
        add(streetContainer);

        streetContainer.add(new Label("streetTypeLabel", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return entityBean.getEntity("street_type").getName(getLocale());
            }
        }).setVisible(isStreet));

        streetContainer.add(new Label("streetLabel", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return entityBean.getEntity("street").getName(getLocale());
            }
        }).setVisible(isStreet));

        //Building
        final WebMarkupContainer buildingContainer = new WebMarkupContainer("buildingContainer");
        buildingContainer.setVisible(isBuilding);
        add(buildingContainer);

        buildingContainer.add(new Label("buildingLabel", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return entityBean.getEntity("building").getName(getLocale());
            }
        }));

        //Building corp
        final WebMarkupContainer buildingCorpContainer = new WebMarkupContainer("buildingCorpContainer");
        buildingCorpContainer.setVisible(isBuilding);
        add(buildingCorpContainer);

        //Apartment
        final WebMarkupContainer apartmentContainer = new WebMarkupContainer("apartmentContainer");
        apartmentContainer.setVisible(isApartment);
        add(apartmentContainer);

        apartmentContainer.add(new Label("apartmentLabel", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return entityBean.getEntity("apartment").getName(getLocale());
            }
        }));

        //Room
        final WebMarkupContainer roomContainer = new WebMarkupContainer("roomContainer");
        roomContainer.setVisible(isRoom);
        add(roomContainer);

        roomContainer.add(new Label("roomLabel", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return entityBean.getEntity("room").getName(getLocale());
            }
        }));

        List<String> filter = isBuilding || isApartment || isRoom ? Lists.newArrayList("city", "street") : Lists.newArrayList("city");
        if (isApartment || isRoom) {
            filter.add("building");
        }
        if (isRoom) {
            filter.add("apartment");
        }

        //City
        add(new WiQuerySearchComponent("search_component", new SearchComponentState(), filter, new ISearchCallback() {
            @Override
            public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
                Long countryObjectId = ids.get("country");
                Long regionObjectId = ids.get("region");
                Long cityObjectId = ids.get("city");
                Long streetObjectId = ids.get("street");
                Long buildingObjectId = ids.get("building");
                Long apartmentObjectId = ids.get("apartment");

                switch (correction.getEntityName()){
                    case "region":
                        correction.setParentId(countryObjectId);
                        break;
                    case "city":
                        correction.setParentId(regionObjectId);
                        break;
                    case "district":
                    case "street":
                        correction.setParentId(cityObjectId);
                        break;
                    case "building":
                        correction.setParentId(streetObjectId);
                        break;
                    case "apartment":
                        correction.setParentId(buildingObjectId);
                        break;
                    case "room":
                        if (apartmentObjectId != null && apartmentObjectId > 0) {
                            correction.setAdditionalParentId(apartmentObjectId);
                        }else if (buildingObjectId != null && buildingObjectId > 0) {
                            correction.setParentId(buildingObjectId);
                        }
                        break;
                }
            }
        }, ShowMode.ACTIVE, true));

        //StreetType
        DomainObjectFilter example = new DomainObjectFilter();
        List<? extends DomainObject> streetTypes = streetTypeStrategy.getList(example);
        Collections.sort(streetTypes, new Comparator<DomainObject>() {
            @Override
            public int compare(DomainObject o1, DomainObject o2) {
                return streetTypeStrategy.displayFullName(o1, getLocale())
                        .compareTo(streetTypeStrategy.displayFullName(o2, getLocale()));
            }
        });

        final IModel<DomainObject> streetTypeModel = new Model<>();
        DomainObjectDisableAwareRenderer renderer = new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return streetTypeStrategy.displayFullName(object, getLocale());
            }
        };

        DisableAwareDropDownChoice streetTypeSelect = new DisableAwareDropDownChoice<>("streetType", streetTypeModel,
                streetTypes, renderer);
        streetTypeSelect.add(new AjaxFormComponentUpdatingBehavior("change") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if ("street".equals(correction.getEntityName())){
                    correction.setAdditionalParentId(streetTypeModel.getObject().getObjectId());
                }
            }
        });
        streetTypeSelect.setVisible(isStreet);
        streetContainer.add(streetTypeSelect);

        //Street
        streetContainer.add(new TextField<>("street", new PropertyModel<>(correction, "correction")).setVisible(isStreet));

        //Building
        buildingContainer.add(new TextField<>("building", new PropertyModel<>(correction, "correction")));

        //Building corp
        buildingCorpContainer.add(new TextField<>("buildingCorp", new PropertyModel<>(correction, "correctionCorp")));

        //Apartment
        apartmentContainer.add(new TextField<>("apartment", new PropertyModel<>(correction, "correction")));
    
        //Room
        roomContainer.add(new TextField<>("room", new PropertyModel<>(correction, "correction")));
    }
}
