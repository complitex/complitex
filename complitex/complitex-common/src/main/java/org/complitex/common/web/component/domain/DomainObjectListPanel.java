package org.complitex.common.web.component.domain;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.converter.BooleanConverter;
import org.complitex.common.converter.DateConverter;
import org.complitex.common.entity.*;
import org.complitex.common.strategy.*;
import org.complitex.common.util.StringCultures;
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
    private StringCultureBean stringBean;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private EntityBean entityBean;

    private String entity;
    private String strategyName;
    private DomainObjectFilter example;
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

    public DomainObjectFilter getExample() {
        return example;
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

        add(new Label("title", labelModel));
        add(new Label("label", labelModel));

        content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);

        //Example
        example = getSession().getPreferenceObject(page, PreferenceKey.FILTER_OBJECT, new DomainObjectFilter(entity));

        //Search
        final List<String> searchFilters = getStrategy().getSearchFilters();
        content.setVisible(searchFilters == null || searchFilters.isEmpty());
        add(content);

        final IModel<ShowMode> showModeModel = new Model<ShowMode>(ShowMode.ACTIVE);
        searchPanel = new CollapsibleSearchPanel("searchPanel", getSession().getGlobalSearchComponentState(),
                searchFilters, getStrategy().getSearchCallback(), ShowMode.ALL, true, showModeModel);
        add(searchPanel);
        searchPanel.initialize();

        //Column List
        final List<AttributeType> columnAttributeTypes = entityBean.getAttributeTypes(getStrategy().getColumnAttributeTypeIds());

        for (AttributeType eat : columnAttributeTypes) {
            example.addAttributeFilter(new AttributeFilter(eat.getId()));
        }

        //Configure example from component state session
        if (searchFilters != null) {
            Map<String, Long> ids = new HashMap<String, Long>();

            for (String filterEntity : searchFilters) {
                DomainObject domainObject = getSession().getGlobalSearchComponentState().get(filterEntity);
                if (domainObject != null) {
                    ids.put(filterEntity, domainObject.getObjectId());
                }
            }
            getStrategy().configureExample(example, ids, null);
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
                    example.setAsc(false);
                    example.setOrderByAttributeTypeId(null);
                    getSession().putPreferenceObject(page, PreferenceKey.FILTER_OBJECT, example);
                }

                //store state
                getSession().storeGlobalSearchComponentState();

                if (!Strings.isEmpty(getSort().getProperty())) {
                    example.setOrderByAttributeTypeId(Long.valueOf(getSort().getProperty()));
                }

                example.setStatus(showModeModel.getObject().name());
                example.setLocaleId(stringLocaleBean.convert(getLocale()).getId());
                example.setAsc(getSort().isAscending());
                example.setFirst(first);
                example.setCount(count);

                return getStrategy().getList(example);
            }

            @Override
            public Long getSize() {
                example.setStatus(showModeModel.getObject().name());
                example.setLocaleId(stringLocaleBean.convert(getLocale()).getId());

                return getStrategy().getCount(example);
            }
        };
        dataProvider.setSort(String.valueOf(getStrategy().getDefaultSortAttributeTypeId()), SortOrder.ASCENDING);

        //Data View
        final RadioGroup<DomainObject> radioGroup = new RadioGroup<>("radioGroup", new Model<>());
        filterForm.add(radioGroup);

        dataView = new DataView<DomainObject>("data", dataProvider, 1) {

            @Override
            protected void populateItem(Item<DomainObject> item) {
                DomainObject object = item.getModelObject();

                item.add(new Radio<>("radio", item.getModel()).setVisible(radioSelect));
                item.add(new Label("order", Model.of(object.getObjectId())));

                ListView<AttributeType> dataColumns = new ListView<AttributeType>("dataColumns", columnAttributeTypes) {
                    @Override
                    protected void populateItem(ListItem<AttributeType> item) {
                        item.add(new Label("dataColumn", getStrategy().displayAttribute(
                                object.getAttribute(item.getModelObject().getId()), getLocale())));
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
        ListView<AttributeType> columns = new ListView<AttributeType>("columns", columnAttributeTypes) {

            @Override
            protected void populateItem(ListItem<AttributeType> item) {
                final AttributeType attributeType = item.getModelObject();
                ArrowOrderByBorder column = new ArrowOrderByBorder("column", String.valueOf(attributeType.getId()),
                        dataProvider, dataView, content);
                column.add(new Label("columnName", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return Strings.capitalize(StringCultures.getValue(attributeType.getAttributeNames(), getLocale()).
                                toLowerCase(getLocale()));
                    }
                }));
                item.add(column);
            }
        };
        columns.setReuseItems(true);
        filterForm.add(columns);

        //Filters
        ListView<AttributeType> filters = new ListView<AttributeType>("filters", columnAttributeTypes) {

            @Override
            protected void populateItem(ListItem<AttributeType> item) {
                AttributeType attributeType = item.getModelObject();
                final AttributeFilter attributeFilter = example.getAttributeExample(attributeType.getId());

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

                String name = attributeType.getAttributeValueTypes().get(0).getValueType().toUpperCase();

                if (SimpleTypes.isSimpleType(name)) {
                    switch (SimpleTypes.valueOf(name)) {
                        case STRING:
                        case BIG_STRING:
                        case STRING_CULTURE:
                        case INTEGER:
                        case DOUBLE: {
                            filter = new StringPanel("filter", filterModel, false, null, true);
                        }
                        break;
                        case DATE:
                        case DATE2:
                        case MASKED_DATE: {
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
                }else if ("ORGANIZATION".equals(name)){
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

        //Reset Action
        AjaxLink reset = new AjaxLink("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();

                example.setObjectId(null);
                for (AttributeType attrType : columnAttributeTypes) {
                    AttributeFilter attrExample = example.getAttributeExample(attrType.getId());
                    attrExample.setValue(null);
                }

                target.add(content);
            }
        };
        filterForm.add(reset);

        //Submit Action
        AjaxButton submit = new AjaxButton("submit", filterForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(content);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        };
        filterForm.add(submit);

        //Navigator
        filterForm.add(new PagingNavigator("navigator", dataView, getClass().getName() + "#" + entity, content));

        WebMarkupContainer actionContainer = new WebMarkupContainer("actionContainer");
        actionContainer.setVisible(radioSelect);
        filterForm.add(actionContainer);

        actionContainer.add(new AjaxSubmitLink("select") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
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
