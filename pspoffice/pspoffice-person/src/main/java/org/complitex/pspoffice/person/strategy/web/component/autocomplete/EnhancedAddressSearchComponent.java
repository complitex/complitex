/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component.autocomplete;

import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.FormComponent;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.CollapsibleSearchComponent;
import org.complitex.dictionary.web.component.search.ISearchCallback;
import org.complitex.dictionary.web.component.search.SearchComponentState;

/**
 *
 * @author Artem
 */
public class EnhancedAddressSearchComponent extends CollapsibleSearchComponent {

    private EnhancedAddressAutocompleteComponent apartmentComponent;
    private EnhancedAddressAutocompleteComponent roomComponent;
    private List<Long> userOrganizationIds;

    public EnhancedAddressSearchComponent(String id, SearchComponentState searchComponentState,
            List<String> searchFilters, ISearchCallback callback, ShowMode showMode, boolean enabled,
            List<Long> userOrganizationIds) {
        super(id, searchComponentState, searchFilters, callback, showMode, enabled);
        this.userOrganizationIds = userOrganizationIds;
    }

    @Override
    protected FormComponent<DomainObject> newAutocompleteComponent(String id, String entity) {
        if (entity.equals("apartment")) {
            DomainObject building = getModelObject("building");
            return apartmentComponent = newEnhancedAddressAutocompleteComponent(id, entity,
                    building != null && building.getId() > 0);
        }
        if (entity.equals("room")) {
            DomainObject building = getModelObject("building");
            DomainObject apartment = getModelObject("apartment");
            return roomComponent = newEnhancedAddressAutocompleteComponent(id, entity,
                    (building != null && building.getId() > 0) || (apartment != null && apartment.getId() > 0));
        }

        return super.newAutocompleteComponent(id, entity);
    }

    private EnhancedAddressAutocompleteComponent newEnhancedAddressAutocompleteComponent(String id, final String entity,
            boolean canCreate) {
        EnhancedAddressAutocompleteComponent enhancedFilterComponent = new EnhancedAddressAutocompleteComponent(id,
                getModel(getIndex(entity)), newAutocompleteItemRenderer(entity), canCreate, entity, userOrganizationIds) {

            @Override
            protected DomainObject getParentObject() {
                if (entity.equals("apartment")) {
                    return EnhancedAddressSearchComponent.this.getModelObject("building");
                } else {
                    DomainObject apartment = EnhancedAddressSearchComponent.this.getModelObject("apartment");
                    return (apartment != null && apartment.getId() > 0) ? apartment
                            : EnhancedAddressSearchComponent.this.getModelObject("building");
                }
            }

            @Override
            protected String getParentEntity() {
                if (entity.equals("apartment")) {
                    return "building";
                } else {
                    DomainObject apartment = EnhancedAddressSearchComponent.this.getModelObject("apartment");
                    return (apartment != null && apartment.getId() > 0) ? "apartment" : "building";
                }
            }

            @Override
            public List<DomainObject> getValues(String term) {
                return EnhancedAddressSearchComponent.this.getValues(term, entity);
            }

            @Override
            public DomainObject getValueOnSearchFail(String input) {
                return EnhancedAddressSearchComponent.this.getValueOnSearchFail(input);
            }

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                EnhancedAddressSearchComponent.this.onUpdate(target, entity);
            }

            @Override
            void onCreate(AjaxRequestTarget target) {
                EnhancedAddressSearchComponent.this.onCreate(target, entity);
            }
        };
        enhancedFilterComponent.setAutoUpdate(true);
        return enhancedFilterComponent;
    }

    private void onCreate(AjaxRequestTarget target, String entity) {
        onUpdate(target, entity);
    }

    @Override
    protected void onSelect(AjaxRequestTarget target, String entity) {
        super.onSelect(target, entity);
        
        if (entity.equals("building")) {
            DomainObject building = getModelObject("building");
            if (apartmentComponent != null) {
                apartmentComponent.setCanCreate(building != null && building.getId() > 0, target);
            }
            if (roomComponent != null) {
                roomComponent.setCanCreate(building != null && building.getId() > 0, target);
            }
        } else if (entity.equals("apartment")) {
            DomainObject building = getModelObject("building");
            DomainObject apartment = getModelObject("apartment");
            if (roomComponent != null) {
                roomComponent.setCanCreate((building != null && building.getId() > 0) || (apartment != null && apartment.getId() > 0),
                        target);
            }
        }
    }

    @Override
    protected Component getAutocompleteField(FormComponent<DomainObject> autocompleteComponent) {
        if (autocompleteComponent instanceof EnhancedAddressAutocompleteComponent) {
            return ((EnhancedAddressAutocompleteComponent) autocompleteComponent).getAutocompleteField();
        } else {
            return super.getAutocompleteField(autocompleteComponent);
        }
    }
}
