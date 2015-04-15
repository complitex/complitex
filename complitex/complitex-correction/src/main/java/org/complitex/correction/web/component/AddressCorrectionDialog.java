package org.complitex.correction.web.component;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.address.entity.AddressEntity;
import org.complitex.address.entity.ExternalAddress;
import org.complitex.address.entity.LocalAddress;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.address.util.AddressRenderer;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.entity.PersonalName;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.web.component.DisableAwareDropDownChoice;
import org.complitex.common.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.common.web.component.ShowMode;
import org.complitex.common.web.component.search.SearchComponentState;
import org.complitex.common.web.component.search.WiQuerySearchComponent;
import org.complitex.correction.service.AddressCorrectionService;
import org.complitex.correction.service.exception.CorrectionException;
import org.complitex.correction.service.exception.DuplicateCorrectionException;
import org.complitex.correction.service.exception.MoreOneCorrectionException;
import org.complitex.correction.service.exception.NotFoundCorrectionException;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Collections;
import java.util.List;

public class AddressCorrectionDialog<T> extends Panel {
    @EJB
    private StrategyFactory strategyFactory;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private AddressCorrectionService addressCorrectionService;

    private AddressEntity addressEntity;
    private Dialog dialog;
    private WiQuerySearchComponent searchComponent;
    private SearchComponentState state;
    private DisableAwareDropDownChoice<DomainObject> streetTypeSelect;
    private FeedbackPanel messages;
    private WebMarkupContainer container;

    private PersonalName personalName;

    private ExternalAddress externalAddress;
    private LocalAddress localAddress;

    private IModel<DomainObject> streetTypeModel;

    private IModel<T> model;

    public AddressCorrectionDialog(String id) {
        super(id);

        //Диалог
        dialog = new Dialog("dialog") {

            {
                getOptions().putLiteral("width", "auto");
            }
        };
        dialog.setModal(true);
        dialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        dialog.setCloseOnEscape(false);
        dialog.setOutputMarkupId(true);
        add(dialog);

        //Контейнер для ajax
        container = new WebMarkupContainer("container");
        container.setOutputMarkupPlaceholderTag(true);

        dialog.add(container);

        //Панель обратной связи
        messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        container.add(messages);

        container.add(new Label("name", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return personalName.toString();
            }
        }));

        container.add(new Label("address", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return AddressRenderer.displayAddress(externalAddress, getLocale());
            }
        }));

        state = new SearchComponentState();
        // at start create fake search component
        searchComponent = new WiQuerySearchComponent("searchComponent", state, ImmutableList.of(""), null, ShowMode.ACTIVE, true);
        container.add(searchComponent);

        DomainObjectFilter example = new DomainObjectFilter();
        List<? extends DomainObject> streetTypes = streetTypeStrategy.getList(example);
        Collections.sort(streetTypes, (o1, o2) -> streetTypeStrategy.getName(o1, getLocale())
                .compareTo(streetTypeStrategy.getName(o2, getLocale())));

        streetTypeModel = new Model<>();
        DomainObjectDisableAwareRenderer renderer = new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return streetTypeStrategy.getName(object, getLocale());
            }
        };
        streetTypeSelect = new DisableAwareDropDownChoice<>("streetTypeSelect", streetTypeModel,
                streetTypes, renderer);
        streetTypeSelect.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                //update street type model.
            }
        });
        container.add(streetTypeSelect);

        AjaxLink<Void> save = new AjaxLink<Void>("save") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (validate(state)) {
                    try {
                        if (addressEntity.equals(AddressEntity.STREET_TYPE)) {
                            LocalAddress localAddress = new LocalAddress(state.getId("city"),
                                    getStreetTypeId(state.get("street")), state.getId("street"),
                                    state.getId("building"), state.getId("apartment"), state.getId("room"));

                            correctAddress(addressEntity, externalAddress, localAddress);
                        } else {
                            LocalAddress localAddress = new LocalAddress();
                            localAddress.setStreetTypeId(streetTypeModel.getObject() != null
                                    ? streetTypeModel.getObject().getId() : null);

                            correctAddress(addressEntity, externalAddress, localAddress);
                        }

                        onCorrect(target, model, addressEntity);

                        dialog.close(target);
                    } catch (DuplicateCorrectionException e) {
                        error(getString("duplicate_correction_error"));
                    } catch (MoreOneCorrectionException e) {
                        switch (e.getEntity()) {
                            case "city":
                                error(getString("more_one_local_city_correction"));
                                break;
                            case "street":
                                error(getString("more_one_local_street_correction"));
                                break;
                            case "street_type":
                                error(getString("more_one_local_street_type_correction"));
                                break;
                        }
                    } catch (NotFoundCorrectionException e) {
                        error(getString(e.getEntity() + "_not_found_correction"));
                    } catch (Exception e) {
                        error(getString("db_error"));
                        LoggerFactory.getLogger(getClass()).error("", e);
                    }
                }
                target.add(messages);
            }
        };
        container.add(save);

        AjaxLink<Void> cancel = new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(container);
                dialog.close(target);
            }
        };
        container.add(cancel);
    }

    private Long getStreetTypeId(DomainObject streetObject) {
        return streetObject == null ? null : streetStrategy.getStreetType(streetObject);
    }

    protected boolean validate(SearchComponentState componentState) {
        boolean validated = true;
        String errorMessageKey = null;
        switch (addressEntity) {
            case ROOM:
                DomainObject roomObject = componentState.get("room");
                validated = roomObject != null && roomObject.getObjectId() != null && roomObject.getObjectId() > 0;
            case APARTMENT:
                DomainObject apartmentObject = componentState.get("apartment");
                validated &= StringUtils.isEmpty(externalAddress.getApartment()) && apartmentObject == null && addressEntity == AddressEntity.ROOM ||
                        apartmentObject != null && apartmentObject.getObjectId() != null && apartmentObject.getObjectId() > 0;
            case BUILDING:
                DomainObject buildingObject = componentState.get("building");
                validated &= buildingObject != null && buildingObject.getObjectId() != null && buildingObject.getObjectId() > 0;
            case STREET:
                DomainObject streetObject = componentState.get("street");
                validated &= streetObject != null && streetObject.getObjectId() != null && streetObject.getObjectId() > 0;
            case CITY:
                errorMessageKey = "address_mistake";
                DomainObject cityObject = componentState.get("city");
                validated &= cityObject != null && cityObject.getObjectId() != null && cityObject.getObjectId() > 0;
                break;
            case STREET_TYPE:
                errorMessageKey = "street_type_required";
                DomainObject streetTypeObject = streetTypeModel.getObject();
                validated = streetTypeObject != null && streetTypeObject.getObjectId() != null && streetTypeObject.getObjectId() > 0;
                break;
        }
        if (!validated) {
            error(getString(errorMessageKey));
        }
        return validated;
    }

    private void initSearchComponentState(SearchComponentState componentState) {
        componentState.clear();

        if (localAddress.getCityId() != null) {
            componentState.put("city", findObject(localAddress.getCityId() , "city"));
        }

        if (localAddress.getStreetId()  != null) {
            componentState.put("street", findObject(localAddress.getStreetId(), "street"));
        }

        if (localAddress.getBuildingId() != null) {
            componentState.put("building", findObject(localAddress.getBuildingId(), "building"));
        }

        if (localAddress.getApartmentId() != null) {
            componentState.put("apartment", findObject(localAddress.getApartmentId(), "apartment"));
        }

        if (localAddress.getRoomId() != null) {
            componentState.put("room", findObject(localAddress.getRoomId(), "room"));
        }
    }

    private DomainObject findObject(Long objectId, String entity) {
        IStrategy strategy = strategyFactory.getStrategy(entity);
        return strategy.getDomainObject(objectId, true);
    }

    protected List<String> initFilters() {
        switch (addressEntity) {
            case CITY:
                return ImmutableList.of("city");
            case STREET:
                return ImmutableList.of("city", "street");
            case BUILDING:
                return ImmutableList.of("city", "street", "building");
            case APARTMENT:
                return ImmutableList.of("city", "street", "building", "apartment");
            case ROOM:
                return ImmutableList.of("city", "street", "building", "apartment", "room");
        }
        return ImmutableList.of("city", "street", "building");
    }

    public void open(AjaxRequestTarget target, IModel<T> model, PersonalName personalName, AddressEntity addressEntity,
                     ExternalAddress externalAddress, LocalAddress localAddress) {
        this.model = model;
        this.addressEntity = addressEntity;
        this.personalName = personalName;
        this.externalAddress = externalAddress;
        this.localAddress = localAddress;

        if (!addressEntity.equals(AddressEntity.STREET_TYPE)) {
            initSearchComponentState(state);
            WiQuerySearchComponent newSearchComponent = 
                    new WiQuerySearchComponent("searchComponent", state, initFilters(), null, ShowMode.ACTIVE, true);
            searchComponent.replaceWith(newSearchComponent);
            searchComponent = newSearchComponent;
            streetTypeSelect.setVisible(false);
        } else {
            streetTypeModel.setObject(null);
            searchComponent.setVisible(false);
            streetTypeSelect.setVisible(true);
        }

        target.add(container);
        dialog.open(target);
    }

    public void open(AjaxRequestTarget target, IModel<T> model, PersonalName personalName,
                     ExternalAddress externalAddress, LocalAddress localAddress) {
        open(target, model, personalName, localAddress.getFirstEmptyAddressEntity(), externalAddress, localAddress);
    }

    protected void correctAddress(AddressEntity addressEntity, ExternalAddress externalAddress, LocalAddress localAddress)
            throws CorrectionException{
        addressCorrectionService.correctLocalAddress(addressEntity, externalAddress, localAddress);
    }

    protected void onCorrect(AjaxRequestTarget target, IModel<T> model, AddressEntity addressEntity){
    }
}
