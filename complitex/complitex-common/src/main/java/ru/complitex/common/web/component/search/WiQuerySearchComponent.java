package ru.complitex.common.web.component.search;

import com.google.common.base.Function;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.string.Strings;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.strategy.StringValueBean;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.common.web.component.wiquery.autocomplete.AbstractAutocompleteComponent;
import ru.complitex.common.web.component.wiquery.autocomplete.AutocompleteAjaxComponent;

import javax.ejb.EJB;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Sets.newHashSet;
import static ru.complitex.common.entity.DomainObjectFilter.ComparisonType.EQUALITY;
import static ru.complitex.common.entity.DomainObjectFilter.ComparisonType.LIKE;

/**
 *
 * @author Artem
 */
public class WiQuerySearchComponent extends Panel {

    protected static final String NOT_SPECIFIED_KEY = "not_specified";
    public static final int AUTO_COMPLETE_SIZE = 10;

    public static class SearchFilterSettings implements Serializable {

        private String searchFilter;
        private boolean enabled;
        private boolean visible;
        private ShowMode showMode;

        public SearchFilterSettings(String searchFilter, boolean enabled, ShowMode showMode) {
            this.searchFilter = searchFilter;
            this.enabled = enabled;
            this.showMode = showMode;
            this.visible = true;
        }

        public SearchFilterSettings(String searchFilter, boolean enabled, boolean visible, ShowMode showMode) {
            this.searchFilter = searchFilter;
            this.enabled = enabled;
            this.visible = visible;
            this.showMode = showMode;
        }

        public boolean isVisible() {
            return visible;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getSearchFilter() {
            return searchFilter;
        }

        public ShowMode getShowMode() {
            return showMode;
        }
    }
    @EJB
    private StringValueBean stringBean;

    @EJB
    private StrategyFactory strategyFactory;

    @EJB
    private StringLocaleBean stringLocaleBean;

    private List<String> searchFilters;
    private List<SearchFilterSettings> searchFilterSettings;
    private ISearchCallback callback;
    private SearchComponentState searchComponentState;
    private boolean enabled;
    private List<IModel<DomainObject>> filterModels;
    private ShowMode showMode;
    private WebMarkupContainer searchContainer = new WebMarkupContainer("searchContainer");
    private Map<String, Component> filterInputFieldMap = newHashMap();

    private boolean showColumns = true;

    private String userPermissionString;

    public WiQuerySearchComponent(String id, SearchComponentState searchComponentState, List<String> searchFilters,
            ISearchCallback callback, ShowMode showMode, boolean enabled) {
        super(id);
        setOutputMarkupId(true);
        this.searchComponentState = searchComponentState;
        this.searchFilters = searchFilters;
        this.callback = callback;
        this.showMode = showMode;
        this.enabled = enabled;
        this.searchFilterSettings = null;
        init();
    }

    public WiQuerySearchComponent(String id, SearchComponentState searchComponentState, List<String> searchFilters,
                                  ISearchCallback callback, ShowMode showMode, boolean enabled, boolean showColumns){
        this(id, searchComponentState, searchFilters, callback, showMode, enabled);

        this.showColumns = showColumns;
    }
    /**
     * Used where some filters must have distinct from others settings. For example, some filters must be disabled but others not.
     * @param id
     * @param searchFilterSettings
     * @param callback
     */
    public WiQuerySearchComponent(String id, SearchComponentState componentState,
            List<SearchFilterSettings> searchFilterSettings, ISearchCallback callback) {
        super(id);
        setOutputMarkupId(true);
        this.searchComponentState = componentState;
        this.searchFilterSettings = searchFilterSettings;
        this.callback = callback;

        searchFilters = newArrayList();

        for (SearchFilterSettings searchFilterSetting : searchFilterSettings) {
            searchFilters.add(searchFilterSetting.getSearchFilter());
        }

        enabled = false;
        showMode = null;

        init();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new CssResourceReference(WiQuerySearchComponent.class,
                WiQuerySearchComponent.class.getSimpleName() + ".css")));
    }

    protected void init() {
        searchContainer.setOutputMarkupId(true);

        initFilterModel();

        ListView<String> filters = newFiltersListView("filters", searchFilters);
        searchContainer.add(filters);
        add(searchContainer);
    }

    protected void initFilterModel() {
        filterModels = newArrayList();
        for (String searchFilter : searchFilters) {
            IModel<DomainObject> model = new Model<>();
            DomainObject object = getSearchComponentState().get(searchFilter);
            model.setObject(object);
            filterModels.add(model);
        }
    }

    protected void handleVisibility(String entity, final Component autocompleteField) {
        boolean isVisible = setVisibility(entity, autocompleteField);
        if (isVisible) {
            setEnable(entity, autocompleteField);
        }
    }

    protected int getSize(String entity) {
        return strategyFactory.getStrategy(entity).getSearchTextFieldSize();
    }

    protected ListView<String> newFiltersListView(String id, List<String> searchFilters) {
        ListView<String> filters = new ListView<String>(id, searchFilters) {

            @Override
            protected void populateItem(final ListItem<String> item) {
                final String entity = item.getModelObject();

                FormComponent<DomainObject> filterComponent = newAutocompleteComponent("filter", entity);
                Component autocompleteField = getAutocompleteField(filterComponent);
                filterInputFieldMap.put(entity, autocompleteField);
                //visible/enabled
                handleVisibility(entity, filterComponent);

                //size
                int size = getSize(entity);

                if (size > 0) {
                    autocompleteField.add(AttributeModifier.replace("size", String.valueOf(size)));
                }

                autocompleteField.add(AttributeModifier.append("placeholder", strategyFactory.getStrategy(entity)
                        .getEntity().getName(getLocale())));

                item.add(filterComponent);
            }
        };
        //filters.setReuseItems(true);
        return filters;
    }

    protected Component getAutocompleteField(FormComponent<DomainObject> autocompleteComponent) {
        return ((AbstractAutocompleteComponent) autocompleteComponent).getAutocompleteField();
    }

    protected FormComponent<DomainObject> newAutocompleteComponent(String id, final String entity) {
        AutocompleteAjaxComponent<DomainObject> filterComponent = new AutocompleteAjaxComponent<DomainObject>(id,
                getModel(getIndex(entity)), newAutocompleteItemRenderer(entity)) {

            @Override
            public List<DomainObject> getValues(String term) {
                return WiQuerySearchComponent.this.getValues(term, entity);
            }

            @Override
            public DomainObject getValueOnSearchFail(String input) {
                return WiQuerySearchComponent.this.getValueOnSearchFail(input);
            }

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                WiQuerySearchComponent.this.onUpdate(target, entity);
            }
        };
        filterComponent.getAutocompleteField().setAppendTo(getParent().getMarkupId()); //fix z-index
        filterComponent.setAutoUpdate(true);

        return filterComponent;
    }

    protected ShowMode getShowMode(final String entity) {
        return getSearchFilterSettings() == null ? getShowModeSetting()
                : find(getSearchFilterSettings(), input -> entity.equals(input.getSearchFilter())).getShowMode();
    }

    protected List<DomainObject> getValues(String term, String entity) {
        final List<DomainObject> choiceList = newArrayList();

        final Map<String, DomainObject> previousInfo = getState(getIndex(entity) - 1);
        final ShowMode currentShowMode = getShowMode(entity);

        final List<? extends DomainObject> equalToExample = findByExample(entity, term, previousInfo, EQUALITY,
                currentShowMode, AUTO_COMPLETE_SIZE);
        choiceList.addAll(equalToExample);

        if (equalToExample.size() < AUTO_COMPLETE_SIZE) {

            final Set<Long> idsSet = newHashSet();
            for (DomainObject o : equalToExample) {
                idsSet.add(o.getObjectId());
            }

            final List<? extends DomainObject> likeExample = findByExample(entity, term, previousInfo, LIKE,
                    currentShowMode, AUTO_COMPLETE_SIZE);

            final Iterator<? extends DomainObject> likeIterator = likeExample.iterator();
            while (likeIterator.hasNext() && choiceList.size() < AUTO_COMPLETE_SIZE) {
                final DomainObject likeObject = likeIterator.next();
                if (!idsSet.contains(likeObject.getObjectId())) {
                    choiceList.add(likeObject);
                    idsSet.add(likeObject.getObjectId());
                }
            }
        }

        choiceList.add(new DomainObject(SearchComponentState.NOT_SPECIFIED_ID));
        return choiceList;
    }

    protected DomainObject getValueOnSearchFail(String input) {
        return new DomainObject(SearchComponentState.NOT_SPECIFIED_ID);
    }

    protected final boolean isSingleObjectVisible(IStrategy strategy) {
        final Map<String, DomainObject> previousInfo = getState(getIndex(strategy.getEntityName()) - 1);
        DomainObjectFilter example = new DomainObjectFilter();
        strategy.configureFilter(example, WiQuerySearchComponent.<Long>transformToIds(previousInfo), null);
        example.setStatus(getShowMode(strategy.getEntityName()).name());
        return strategy.getCount(example) == 1;
    }

    protected final void onUpdate(AjaxRequestTarget target, String entity) {
        final int index = getIndex(entity);
        final DomainObject modelObject = getModelObject(entity);

        final int size = searchFilters.size();
        int lastFilledIndex = index;
        if (index < size && modelObject != null) {
            for (int j = index + 1; j < size; j++) {
                final String currentEntity = searchFilters.get(j);
                IStrategy currentStrategy = strategyFactory.getStrategy(currentEntity);
                DomainObject currentObject = null;
                if (currentStrategy.allowProceedNextSearchFilter()) {
                    if (isSingleObjectVisible(currentStrategy)) {
                        currentObject = getValues(null, currentEntity).get(0);
                    }
                }
                setModelObject(j, currentObject);

                if (currentObject != null) {
                    lastFilledIndex = j;
                }
            }

            setFocus(target, lastFilledIndex + 1 < size ? searchFilters.get(lastFilledIndex + 1) : null);
        }

        onSelect(target, searchFilters.get(lastFilledIndex));

        updateSearchContainer(target);
        invokeCallback(lastFilledIndex, target);
    }

    protected void onSelect(AjaxRequestTarget target, String entity) {
    }

    protected final void updateSearchContainer(AjaxRequestTarget target) {
        target.add(searchContainer);
    }

    public final void setFocus(AjaxRequestTarget target, String searchFilter) {
        if (!Strings.isEmpty(searchFilter)) {
            target.focusComponent(filterInputFieldMap.get(searchFilter));
        }
    }

    protected IChoiceRenderer<DomainObject> newAutocompleteItemRenderer(final String entity) {
        return new IChoiceRenderer<DomainObject>() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                if (object.getObjectId().equals(SearchComponentState.NOT_SPECIFIED_ID)) {
                    return getString(NOT_SPECIFIED_KEY);
                } else {
                    return strategyFactory.getStrategy(entity).displayDomainObject(object, getLocale());
                }
            }

            @Override
            public String getIdValue(DomainObject object, int index) {
                return String.valueOf(object.getObjectId());
            }

            @Override
            public DomainObject getObject(String id, IModel<? extends List<? extends DomainObject>> choices) {
                return choices.getObject().stream().filter(c -> id.equals(String.valueOf(c.getObjectId()))).findAny().orElse(null);
            }
        };
    }

    protected final DomainObject getModelObject(final int index) {
        return getModel(index).getObject();
    }

    public final DomainObject getModelObject(final String entity) {
        return getModel(getIndex(entity)).getObject();
    }

    protected final IModel<DomainObject> getModel(final int index) {
        return filterModels.get(index);
    }

    protected final void setModelObject(final int index, DomainObject object) {
        getModel(index).setObject(object);
    }

    protected final ISearchCallback getCallback() {
        return callback;
    }

    protected final SearchComponentState getSearchComponentState() {
        return searchComponentState;
    }

    protected final List<SearchFilterSettings> getSearchFilterSettings() {
        return searchFilterSettings;
    }

    protected final ShowMode getShowModeSetting() {
        return showMode;
    }

    public ShowMode getShowMode() {
        return showMode;
    }

    public void setShowMode(ShowMode showMode) {
        this.showMode = showMode;
    }

    protected final boolean getEnabledSetting() {
        return enabled;
    }

    protected Map<String, DomainObject> getState(int index) {
        Map<String, DomainObject> objects = newHashMap();

        int idx = index;

        while (idx > -1) {
            DomainObject object = getModelObject(idx);
            objects.put(searchFilters.get(idx), object);
            idx--;

        }

        return objects;
    }

    public void invokeCallback() {
        invokeCallback(searchFilters.size() - 1, null);
    }

    protected final void invokeCallback(int index, AjaxRequestTarget target) {
        Map<String, DomainObject> finalState = getState(index);
        Map<String, Long> ids = transformToIds(finalState);

        searchComponentState.updateState(finalState);

        if (getCallback() != null) {
            callback.found(this, ids, target);
        }
    }

    protected static <T> Map<String, T> transformToIds(Map<String, DomainObject> objects) {
        return transformValues(objects, new Function<DomainObject, T>() {

            @Override
            @SuppressWarnings("unchecked")
            public T apply(DomainObject from) {
                return from != null ? (T) from.getObjectId() : null;
            }
        });
    }

    protected final void setEnable(final String entityFilter, Component textField) {
        if (getSearchFilterSettings() != null) {
            boolean isEnabled = find(getSearchFilterSettings(), settings -> settings.getSearchFilter().equals(entityFilter)).isEnabled();
            textField.setEnabled(isEnabled);
        } else {
            textField.setEnabled(getEnabledSetting());
        }
    }

    protected final boolean setVisibility(final String entityFilter, Component component) {
        if (getSearchFilterSettings() != null) {
            boolean isVisible = find(getSearchFilterSettings(), settings -> settings.getSearchFilter().equals(entityFilter)).isVisible();
            component.setVisible(isVisible);
            return isVisible;
        }
        return true;
    }

    protected final int getIndex(String entity) {
        for (int i = 0; i < searchFilters.size(); i++) {
            String searchFilter = searchFilters.get(i);
            if (searchFilter.equals(entity)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Entity " + entity + " is not found.");
    }

    protected final WebMarkupContainer getSearchContainer() {
        return searchContainer;
    }

    protected List<? extends DomainObject> findByExample(String entity, String searchTextInput,
            Map<String, DomainObject> previousInfo, DomainObjectFilter.ComparisonType comparisonType, ShowMode showMode, int size) {
        IStrategy strategy = strategyFactory.getStrategy(entity);

        DomainObjectFilter example = new DomainObjectFilter();

        strategy.configureFilter(example, WiQuerySearchComponent.<Long>transformToIds(previousInfo), searchTextInput);

        example.setOrderByAttributeTypeId(strategy.getDefaultOrderByAttributeId());
        example.setAsc(true);
        example.setCount(size);
        example.setLocaleId(stringLocaleBean.convert(getLocale()).getId());
        example.setComparisonType(comparisonType.name());
        example.setStatus(showMode.name());
//        example.setUserPermissionString(userPermissionString);

        return strategy.getList(example);
    }

    public void reinitialize(AjaxRequestTarget target) {
        for (int i = 0; i < searchFilters.size(); i++) {
            String filterEntity = searchFilters.get(i);
            DomainObject object = searchComponentState.get(filterEntity);
            setModelObject(i, object);
        }

        invokeCallback(searchFilters.size() - 1, target);
    }

    public boolean isShowColumns() {
        return showColumns;
    }

    public String getUserPermissionString() {
        return userPermissionString;
    }

    public WiQuerySearchComponent setUserPermissionString(String userPermissionString) {
        this.userPermissionString = userPermissionString;

        return this;
    }

    public List<String> getSearchFilters() {
        return searchFilters;
    }
}
