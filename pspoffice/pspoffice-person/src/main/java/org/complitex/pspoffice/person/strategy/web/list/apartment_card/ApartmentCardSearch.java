/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.list.apartment_card;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import static com.google.common.collect.ImmutableList.*;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.CollapsibleSearchComponent;
import org.complitex.dictionary.web.component.search.CollapsibleSearchPanel;
import org.complitex.dictionary.web.component.search.ISearchCallback;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.web.component.autocomplete.EnhancedAddressSearchComponent;
import org.complitex.pspoffice.person.strategy.web.edit.apartment_card.ApartmentCardEdit;
import org.complitex.pspoffice.person.strategy.web.list.apartment_card.grid.ApartmentCardsGrid;
import org.complitex.pspoffice.person.strategy.web.list.apartment_card.grid.ApartmentsGrid;
import org.complitex.pspoffice.person.strategy.web.list.apartment_card.grid.BuildingsGrid;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.component.toolbar.search.CollapsibleSearchToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.odlabs.wiquery.core.javascript.JsQuery;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class ApartmentCardSearch extends FormTemplatePage {

    private class AddressSearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
            Long apartmentId = ids.get("apartment");
            if (apartmentId != null) {
                target.appendJavaScript(String.valueOf(new JsQuery(submit).$().chain("click").render()));
            }
        }
    }
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    private CollapsibleSearchPanel searchPanel;
    private IndicatingAjaxLink<Void> submit;

    public ApartmentCardSearch() {
        add(new Label("title", new ResourceModel("title")));
        add(new Label("address_label", new ResourceModel("address_label")));

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        final SearchComponentState addressSearchComponentState = apartmentCardStrategy.restoreSearchState(getTemplateSession());
        WebMarkupContainer searchPanelContainer = new WebMarkupContainer("searchPanelContainer");
        searchPanelContainer.setOutputMarkupId(true);
        add(searchPanelContainer);
        searchPanel = new CollapsibleSearchPanel("searchPanel", addressSearchComponentState,
                of("country", "region", "city", "street", "building", "apartment"), new AddressSearchCallback(),
                ShowMode.ACTIVE, true, new Model<ShowMode>(ShowMode.ACTIVE)) {

            @EJB
            private SessionBean sessionBean;

            @Override
            protected CollapsibleSearchComponent newSearchComponent(String id, SearchComponentState searchComponentState,
                    List<String> searchFilters, ISearchCallback callback, ShowMode showMode, boolean enabled) {
                return new EnhancedAddressSearchComponent(id, searchComponentState, searchFilters, callback, showMode,
                        enabled, sessionBean.getUserOrganizationObjectIds());
            }
        };
        searchPanelContainer.add(searchPanel);

        submit = new IndicatingAjaxLink<Void>("search") {

            private Long getObjectId(String entity) {
                final DomainObject object = addressSearchComponentState.get(entity);
                if (object != null && object.getId() != null && object.getId() > 0) {
                    return object.getId();
                }
                return null;
            }

            private void storeAddressSearchInfo() {
                apartmentCardStrategy.storeSearchState(getTemplateSession(), addressSearchComponentState);
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                final Long apartmentId = getObjectId("apartment");
                if (apartmentId != null) {
                    storeAddressSearchInfo();
                    // квартира введена.
                    final int count = apartmentCardStrategy.countByAddress("apartment", apartmentId);
                    if (count > 1) {
                        // выводим грид поквартирных карточек.
                        setResponsePage(new ApartmentCardsGrid(apartmentId));
                        return;
                    } else if (count == 1) {
                        // переходим на страницу редактирования поквартирной карточки.
                        setResponsePage(
                                new ApartmentCardEdit(apartmentCardStrategy.findOneByAddress("apartment", apartmentId), null));
                    } else {
                        setResponsePage(new ApartmentCardNotFound(apartmentId));
                    }
                    return;
                }
                // квартира не введена.

                final Long buildingId = getObjectId("building");
                if (buildingId != null) {
                    storeAddressSearchInfo();
                    //дом введен -> выводим грид квартир для заданного дома.
                    setResponsePage(new ApartmentsGrid(buildingId));
                    return;
                }
                // дом не введен.

                final Long streetId = getObjectId("street");
                final Long cityId = getObjectId("city");
                if (cityId != null && streetId != null) {
                    storeAddressSearchInfo();
                    //город и улица введены -> выводим грид домов для заданного города и улицы.
                    setResponsePage(new BuildingsGrid(cityId, streetId));
                    return;
                }
                // улица не введена.

                if (cityId != null) {
                    storeAddressSearchInfo();
                    // город введен -> выводим грид домов.
                    setResponsePage(new BuildingsGrid(cityId));
                    return;
                }

                // город не введен -> ошибка, это обязательное поле.
                error(getString("address_invalid"));
                target.add(messages);
            }
        };
        submit.setOutputMarkupId(true);
        add(submit);
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return of(new CollapsibleSearchToolbarButton(id, searchPanel));
    }
}
