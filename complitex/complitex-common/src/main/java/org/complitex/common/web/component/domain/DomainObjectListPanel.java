package org.complitex.common.web.component.domain;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.converter.BooleanConverter;
import org.complitex.common.converter.DateConverter;
import org.complitex.common.entity.*;
import org.complitex.common.strategy.EntityBean;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.common.util.StringValueUtil;
import org.complitex.common.web.DictionaryFwSession;
import org.complitex.common.web.component.ShowMode;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;
import org.complitex.common.web.component.datatable.DataProvider;
import org.complitex.common.web.component.organization.OrganizationIdPicker;
import org.complitex.common.web.component.paging.PagingNavigator;
import org.complitex.common.web.component.search.CollapsibleSearchPanel;
import org.complitex.common.web.component.type.BooleanPanel;
import org.complitex.common.web.component.type.DatePanel;
import org.complitex.common.web.component.type.StringPanel;

import javax.ejb.EJB;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DomainObjectListPanel extends Panel {
    @EJB
    private StrategyFactory strategyFactory;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private EntityBean entityBean;

    private String entity;
    private String strategyName;
    private DomainObjectFilter filter;
    private WebMarkupContainer content;
    private DataView<DomainObject> dataView;
    private CollapsibleSearchPanel searchPanel;
    private final String page;

    private boolean radioSelect;

    public DomainObjectListPanel(String id, String entity, String strategyName, boolean radioSelect) {
        super(id);

        this.entity = entity;
        this.strategyName = strategyName;
        this.page = getClass().getName() + "#" + entity;

        this.radioSelect = radioSelect;

        init();
    }

    private IStrategy getStrategy() {
        return strategyFactory.getStrategy(strategyName, entity);
    }

    public DomainObjectFilter getFilter() {
        return filter;
    }

    public void refreshContent(AjaxRequestTarget target) {
        content.setVisible(true);
        if (target != null) {
            dataView.setCurrentPage(0);
            target.add(content);
        }
    }

    private void init() {
        IModel<String> labelModel = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return getStrategy().getPluralEntityLabel(getLocale());
            }
        };

        add(new Label("label", labelModel));

        content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);

        //Example
        filter = getSession().getPreferenceObject(page, PreferenceKey.FILTER_OBJECT, new DomainObjectFilter(entity));

        //Search
        final List<String> searchFilters = getStrategy().getSearchFilters();
        content.setVisible(searchFilters == null || searchFilters.isEmpty());
        add(content);

        final IModel<ShowMode> showModeModel = new Model<>(ShowMode.ACTIVE);
        searchPanel = new CollapsibleSearchPanel("searchPanel", getSession().getGlobalSearchComponentState(),
                searchFilters, getStrategy().getSearchCallback(), ShowMode.ACTIVE, true, showModeModel);
        add(searchPanel);
        searchPanel.initialize();

        //Column List
        final List<EntityAttribute> columnEntityAttributes = entityBean.getAttributeTypes(getStrategy().getColumnAttributeTypeIds());

        for (EntityAttribute eat : columnEntityAttributes) {
            filter.addAttributeFilter(new AttributeFilter(eat.getId()));
        }

        //Configure filter from component state session
        if (searchFilters != null) {
            Map<String, Long> ids = new HashMap<String, Long>();

            for (String filterEntity : searchFilters) {
                DomainObject domainObject = getSession().getGlobalSearchComponentState().get(filterEntity);
                if (domainObject != null) {
                    ids.put(filterEntity, domainObject.getObjectId());
                }
            }
            getStrategy().configureFilter(filter, ids, null);
        }

        //Form
        final Form filterForm = new Form("filterForm");
        content.add(filterForm);

        //Data Provider
        final DataProvider<DomainObject> dataProvider = new DataProvider<DomainObject>() {

            @Override
            protected Iterable<? extends DomainObject> getData(long first, long count) {
                //store preference, but before clear data order related properties.
                {
                    filter.setAsc(false);
                    filter.setOrderByAttributeTypeId(null);
                    getSession().putPreferenceObject(page, PreferenceKey.FILTER_OBJECT, filter);
                }

                //store state
                getSession().storeGlobalSearchComponentState();

                if (!Strings.isEmpty(getSort().getProperty())) {
                    filter.setOrderByAttributeTypeId(Long.valueOf(getSort().getProperty()));
                }

                if (showModeModel.getObject() != null) {
                    filter.setStatus(showModeModel.getObject().name());
                }

                filter.setLocaleId(stringLocaleBean.convert(getLocale()).getId());
                filter.setAsc(getSort().isAscending());
                filter.setFirst(first);
                filter.setCount(count);

                return getStrategy().getList(filter);
            }

            @Override
            public Long getSize() {
                if (showModeModel.getObject() != null) {
                    filter.setStatus(showModeModel.getObject().name());
                }

                filter.setLocaleId(stringLocaleBean.convert(getLocale()).getId());

                return getStrategy().getCount(filter);
            }
        };
        dataProvider.setSort(String.valueOf(getStrategy().getDefaultSortAttributeTypeId()), SortOrder.ASCENDING);

        //Data View
        final RadioGroup<DomainObject> radioGroup = new RadioGroup<>("radioGroup", new Model<>());
        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        filterForm.add(radioGroup);

        dataView = new DataView<DomainObject>("data", dataProvider, 10) {

            @Override
            protected void populateItem(Item<DomainObject> item) {
                DomainObject object = item.getModelObject();

                item.add(new Radio<>("radio", item.getModel()).setVisible(radioSelect));
                item.add(new Label("object_id", Model.of(object.getObjectId())));

                ListView<EntityAttribute> dataColumns = new ListView<EntityAttribute>("dataColumns", columnEntityAttributes) {
                    @Override
                    protected void populateItem(ListItem<EntityAttribute> item) {
                        Attribute a = object.getAttribute(item.getModelObject().getId());

                        item.add(new Label("dataColumn", a != null ? getStrategy().displayAttribute(a, getLocale()) : ""));
                    }
                };
                item.add(dataColumns);

                BookmarkablePageLink<WebPage> detailsLink = new BookmarkablePageLink<WebPage>("detailsLink",
                        getStrategy().getEditPage(), getStrategy().getEditPageParams(object.getObjectId(), null, null));
                detailsLink.add(new Label("editMessage", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        if (DomainObjectAccessUtil.canAddNew(strategyName, entity)) {
                            return getString("edit");
                        } else {
                            return getString("view");
                        }
                    }
                }));
                item.add(detailsLink.setVisible(!radioSelect));
            }
        };
        radioGroup.add(dataView);

        //Filter Form Columns
        ListView<EntityAttribute> columns = new ListView<EntityAttribute>("columns", columnEntityAttributes) {

            @Override
            protected void populateItem(ListItem<EntityAttribute> item) {
                final EntityAttribute entityAttribute = item.getModelObject();
                ArrowOrderByBorder column = new ArrowOrderByBorder("column", String.valueOf(entityAttribute.getId()),
                        dataProvider, dataView, content);
                column.add(new Label("columnName", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return Strings.capitalize(StringValueUtil.getValue(entityAttribute.getNames(), getLocale()).
                                toLowerCase(getLocale()));
                    }
                }));
                item.add(column);
            }
        };
        columns.setReuseItems(true);
        filterForm.add(columns);

        //Filters
        ListView<EntityAttribute> filters = new ListView<EntityAttribute>("filters", columnEntityAttributes) {

            @Override
            protected void populateItem(ListItem<EntityAttribute> item) {
                EntityAttribute entityAttribute = item.getModelObject();

                final AttributeFilter attributeFilter = filter.getAttributeExample(entityAttribute.getId());

                final IModel<String> filterModel = new Model<String>() {

                    @Override
                    public String getObject() {
                        return attributeFilter.getValue();
                    }

                    @Override
                    public void setObject(String object) {
                        if (!Strings.isEmpty(object)) {
                            attributeFilter.setValue(object);
                        }
                    }
                };

                Component filter = new StringPanel("filter", Model.of(""), false, null, true);

                if (entityAttribute.getValueType().isSimple()) {
                    switch (entityAttribute.getValueType()) {
                        case STRING:
                        case STRING_VALUE:
                        case INTEGER:
                        case DECIMAL: {
                            filter = new StringPanel("filter", filterModel, false, null, true);
                        }
                        break;
                        case DATE: {
                            IModel<Date> dateModel = new Model<Date>() {

                                DateConverter dateConverter = new DateConverter();

                                @Override
                                public void setObject(Date object) {
                                    if (object != null) {
                                        filterModel.setObject(dateConverter.toString(object));
                                    }
                                }

                                @Override
                                public Date getObject() {
                                    if (!Strings.isEmpty(filterModel.getObject())) {
                                        return dateConverter.toObject(filterModel.getObject());
                                    }
                                    return null;
                                }
                            };
                            filter = new DatePanel("filter", dateModel, false, null, true);
                        }
                        break;
                        case BOOLEAN: {
                            IModel<Boolean> booleanModel = new Model<Boolean>() {

                                BooleanConverter booleanConverter = new BooleanConverter();

                                @Override
                                public void setObject(Boolean object) {
                                    if (object != null) {
                                        filterModel.setObject(booleanConverter.toString(object));
                                    }
                                }

                                @Override
                                public Boolean getObject() {
                                    if (!Strings.isEmpty(filterModel.getObject())) {
                                        return booleanConverter.toObject(filterModel.getObject());
                                    }
                                    return null;
                                }
                            };
                            filter = new BooleanPanel("filter", booleanModel, null, true);
                        }
                        break;
                    }
                }else if (ValueType.ENTITY.equals(entityAttribute.getValueType()) && entityAttribute.getReferenceId() == 900){
                    filter = new OrganizationIdPicker("filter", new IModel<Long>() {
                        @Override
                        public Long getObject() {
                            return filterModel.getObject() == null? null : Long.valueOf(filterModel.getObject());
                        }

                        @Override
                        public void setObject(Long object) {
                            if (object != null) {
                                filterModel.setObject(String.valueOf(object));
                            } else {
                                filterModel.setObject(null);
                            }
                        }

                        @Override
                        public void detach() {

                        }
                    });
                }

                item.add(filter);
            }
        };
        filters.setReuseItems(true);
        filterForm.add(filters);

        filterForm.add(new TextField<>("objectId", new PropertyModel<>(filter, "objectId")));

        //Reset Action
        AjaxLink reset = new AjaxLink("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();

                filter.setObjectId(null);
                for (EntityAttribute attrType : columnEntityAttributes) {
                    AttributeFilter attrExample = filter.getAttributeExample(attrType.getId());
                    attrExample.setValue(null);
                }

                target.add(content);
            }
        };
        filterForm.add(reset);

        //Filter Action
        AjaxLink submit = new AjaxLink("submit") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(content);
            }
        };
        filterForm.add(submit);

        //Navigator
        filterForm.add(new PagingNavigator("navigator", dataView, getClass().getName() + "#" + entity, content));

        WebMarkupContainer actionContainer = new WebMarkupContainer("actionContainer");
        actionContainer.setVisible(radioSelect);
        filterForm.add(actionContainer);

        //Actions
        actionContainer.add(new AjaxLink("select") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onSelect(target, radioGroup.getModelObject());
            }
        });

        actionContainer.add(new AjaxLink("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onCancel(target);
            }
        });
    }

    protected void onSelect(AjaxRequestTarget target, DomainObject domainObject){
    }

    protected void onCancel(AjaxRequestTarget target){
    }

    public final CollapsibleSearchPanel getSearchPanel() {
        return searchPanel;
    }

    @Override
    public DictionaryFwSession getSession() {
        return (DictionaryFwSession) super.getSession();
    }
}
