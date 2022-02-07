package ru.complitex.address.web.component;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.common.web.component.search.ISearchCallback;
import ru.complitex.common.web.component.search.SearchComponentState;
import ru.complitex.common.web.component.search.WiQuerySearchComponent;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author inheaven on 013 11.08.15 17:31
 */
public class AddressSearchComponent extends WiQuerySearchComponent {
    @EJB
    private StrategyFactory strategyFactory;

    public AddressSearchComponent(String id, SearchComponentState searchComponentState, List<String> searchFilters,
                                  ISearchCallback callback, ShowMode showMode, boolean enabled) {
        super(id, searchComponentState, searchFilters, callback, showMode, enabled);
    }

    public AddressSearchComponent(String id, SearchComponentState searchComponentState, List<String> searchFilters,
                                  ISearchCallback callback, ShowMode showMode, boolean enabled, boolean showColumns) {
        super(id, searchComponentState, searchFilters, callback, showMode, enabled, showColumns);
    }

    public AddressSearchComponent(String id, SearchComponentState componentState, List<SearchFilterSettings> searchFilterSettings,
                                  ISearchCallback callback) {
        super(id, componentState, searchFilterSettings, callback);
    }

    public AddressSearchComponent(String id, SearchComponentState state, List<String> searchFilters){
        super(id, state, searchFilters, null, ShowMode.ACTIVE, true);
    }

    @Override
    protected IChoiceRenderer<DomainObject> newAutocompleteItemRenderer(String entity) {
        if (getSearchComponentState().get(AddressEntity.CITY.getEntityName()) == null && entity.equals("street")){
            return new IChoiceRenderer<DomainObject>() {

                @Override
                public Object getDisplayValue(DomainObject object) {
                    if (object.getObjectId().equals(SearchComponentState.NOT_SPECIFIED_ID)) {
                        return getString(NOT_SPECIFIED_KEY);
                    } else {
                        return strategyFactory.getStrategy(entity).displayDomainObject(object, getLocale()) +
                                " (" + strategyFactory.getStrategy(AddressEntity.CITY.getEntityName())
                                .displayDomainObject(object.getParentId(), getLocale()) + ")";
                    }
                }

                @Override
                public String getIdValue(DomainObject object, int index) {
                    return String.valueOf(object.getObjectId());
                }

                @Override
                public DomainObject getObject(String id, IModel<? extends List<? extends DomainObject>> choices) {
                    return choices.getObject().stream().filter(c -> id.equals(String.valueOf(c.getObjectId()))).findAny().orElseGet(null);
                }
            };
        }

        return super.newAutocompleteItemRenderer(entity);
    }
}
