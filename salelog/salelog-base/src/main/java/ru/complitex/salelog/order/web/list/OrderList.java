package ru.complitex.salelog.order.web.list;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.strategy.region.RegionStrategy;
import ru.complitex.common.converter.BigDecimalConverter;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.Person;
import ru.complitex.common.web.component.DatePicker;
import ru.complitex.common.web.component.datatable.DataProvider;
import ru.complitex.common.web.component.paging.PagingNavigator;
import ru.complitex.template.web.component.toolbar.AddItemButton;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.template.TemplatePage;
import ru.complitex.salelog.entity.CallGirl;
import ru.complitex.salelog.order.entity.Order;
import ru.complitex.salelog.order.entity.OrderStatus;
import ru.complitex.salelog.order.entity.ProductSale;
import ru.complitex.salelog.order.service.OrderBean;
import ru.complitex.salelog.order.web.edit.OrderEditPanel;
import ru.complitex.salelog.service.CallGirlBean;
import ru.complitex.salelog.service.ProductBean;
import ru.complitex.salelog.web.security.SecurityRole;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static ru.complitex.common.util.PageUtil.newSorting;
import static ru.complitex.salelog.order.service.OrderBean.OrderExt;

/**
 * @author Pavel Sknar
 */
@AuthorizeInstantiation(SecurityRole.ORDER_VIEW)
public class OrderList extends TemplatePage {

    private static final SimpleDateFormat CREATE_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static final BigDecimalConverter BIG_DECIMAL_CONVERTER = new BigDecimalConverter(2);

    @EJB
    private OrderBean orderBean;

    @EJB
    private CallGirlBean callGirlBean;

    @EJB
    private ProductBean productBean;

    @EJB
    private RegionStrategy regionStrategy;

    private OrderEditPanel createPanel;

    public OrderList() {
        init();
    }
    
    private void init() {

        final IModel<OrderExt> filterModel = new CompoundPropertyModel<>(getFilterObject());

        IModel<String> labelModel = new ResourceModel("label");

        add(new Label("title", labelModel));
        add(new Label("label", labelModel));

        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupPlaceholderTag(true);
        container.setVisible(true);
        add(container);

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        container.add(messages);

        //Form
        final Form<OrderExt> filterForm = new Form<>("filterForm", filterModel);
        container.add(filterForm);

        //Data Provider
        final DataProvider<Order> dataProvider = new DataProvider<Order>() {

            @Override
            protected Iterable<? extends Order> getData(long first, long count) {
                FilterWrapper<OrderExt> filterWrapper = FilterWrapper.of(filterModel.getObject(), first, count);
                filterWrapper.setAscending(getSort().isAscending());
                filterWrapper.setSortProperty(getSort().getProperty());
                filterWrapper.setLike(true);

                return orderBean.getOrders(filterWrapper);
            }

            @Override
            protected Long getSize() {
                FilterWrapper<OrderExt> filterWrapper = FilterWrapper.of(filterModel.getObject());
                filterWrapper.setLike(true);

                return orderBean.getCount(filterWrapper);
            }
        };
        dataProvider.setSort("order_object_id", SortOrder.ASCENDING);

        //Data View
        DataView<Order> dataView = new DataView<Order>("data", dataProvider, 1) {

            @Override
            protected void populateItem(Item<Order> item) {
                final Order order = item.getModelObject();
                DomainObject region = order.getRegionId() != null? regionStrategy.getDomainObject(order.getRegionId(), false) : null;

                item.add(new Label("id", order.getId().toString()));
                item.add(new Label("createDate", order.getCreateDate() != null ? CREATE_DATE_FORMAT.format(order.getCreateDate()) : ""));
                item.add(new Label("callGirlCode", order.getCallGirl() != null? order.getCallGirl().getCode(): ""));
                item.add(new Label("customer", order.getCustomer() != null? order.getCustomer().toString(): ""));
                item.add(new Label("phones", order.getPhones()));
                item.add(new Label("region", region != null? regionStrategy.displayDomainObject(region, getLocale()): ""));
                item.add(new Label("address", order.getAddress()));
                item.add(new Label("comment", order.getComment()));
                item.add(new Label("status", order.getStatus() != null? order.getStatus().getLabel(getLocale()): ""));

                final DataProvider<ProductSale> dataProvider = new DataProvider<ProductSale>() {

                    @Override
                    protected Iterable<? extends ProductSale> getData(long first, long count) {

                        return order.getProductSales();
                    }

                    @Override
                    protected Long getSize() {
                        return Long.valueOf(order.getProductSales().size());
                    }
                };

                item.add(new DataView<ProductSale>("productCodeView", dataProvider) {

                    @Override
                    protected void populateItem(Item<ProductSale> item) {
                        final ProductSale sale = item.getModelObject();

                        item.add(new Label("productCode", sale.getProduct().getCode()));
                    }
                });
                item.add(new DataView<ProductSale>("priceView", dataProvider) {

                    @Override
                    protected void populateItem(Item<ProductSale> item) {
                        final ProductSale sale = item.getModelObject();

                        item.add(new Label("price", BIG_DECIMAL_CONVERTER.convertToString(sale.getPrice(), getLocale())));
                    }
                });
                item.add(new DataView<ProductSale>("countView", dataProvider) {

                    @Override
                    protected void populateItem(Item<ProductSale> item) {
                        final ProductSale sale = item.getModelObject();

                        item.add(new Label("count", Integer.toString(sale.getCount())));
                    }
                });
                item.add(new DataView<ProductSale>("totalCostView", dataProvider) {

                    @Override
                    protected void populateItem(Item<ProductSale> item) {
                        final ProductSale sale = item.getModelObject();

                        item.add(new Label("totalCost", BIG_DECIMAL_CONVERTER.convertToString(sale.getTotalCost(), getLocale())));
                    }
                });

                AjaxLink detailsLink = new AjaxLink("detailsLink") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        createPanel.open(target, order.getId());
                    }
                };
                detailsLink.add(new Label("editMessage", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return isOrderEditor()? getString("edit"): getString("view");
                    }
                }));
                item.add(detailsLink);
            }
        };
        filterForm.add(dataView);

        //Sorting
        filterForm.add(newSorting("header.", dataProvider, dataView, filterForm, true,
                "order_object_id", "order_create_date", "cg_code", "order_customer", "order_phones", "order_region_id",
                "order_address", "order_comment", "order_status_code"));

        //Filters
        filterForm.add(new DatePicker<Date>("createDateFrom"));
        filterForm.add(new DatePicker<Date>("createDateTo"));

        filterForm.add(new TextField<String>("callGirlCode", new IModel<String>() {
            @Override
            public String getObject() {
                return filterModel.getObject().getCallGirl().getCode();
            }

            @Override
            public void setObject(String object) {
                filterModel.getObject().getCallGirl().setCode(object);
            }

            @Override
            public void detach() {

            }
        }));

        filterForm.add(new TextField<String>("customer", new PersonModel() {

            @Override
            protected void setPerson(Person person) {
                filterModel.getObject().setCustomer(person);
            }

            @Override
            protected Person getPerson() {
                return filterModel.getObject().getCustomer();
            }
        }));

        filterForm.add(new TextField<>("phones"));

        filterForm.add(new DropDownChoice<DomainObject>("region",
                new IModel<DomainObject>() {
                    @Override
                    public DomainObject getObject() {
                        Order filterObject = filterModel.getObject();
                        return filterObject.getRegionId() != null ? regionStrategy.getDomainObject(filterObject.getRegionId(), false) : null;
                    }

                    @Override
                    public void setObject(DomainObject region) {
                        filterModel.getObject().setRegionId(region.getObjectId());
                    }

                    @Override
                    public void detach() {

                    }
                },
                regionStrategy.getList(new DomainObjectFilter()),
                new IChoiceRenderer<DomainObject>() {
                    @Override
                    public Object getDisplayValue(DomainObject region) {
                        return regionStrategy.displayDomainObject(region, getLocale());
                    }

                    @Override
                    public String getIdValue(DomainObject region, int i) {
                        return region != null ? region.getObjectId().toString() : "-1";
                    }

                    @Override
                    public DomainObject getObject(String id, IModel<? extends List<? extends DomainObject>> choices) {
                        return choices.getObject().stream().filter(c -> id.equals(c.getObjectId().toString())).findAny().orElse(null);
                    }
                }
        ));

        filterForm.add(new TextField<>("address"));

        filterForm.add(new TextField<>("comment"));

        filterForm.add(new DropDownChoice<>("status",
                Arrays.asList(OrderStatus.values()),
                new IChoiceRenderer<OrderStatus>() {
                    @Override
                    public Object getDisplayValue(OrderStatus status) {
                        return status.getLabel(getLocale());
                    }

                    @Override
                    public String getIdValue(OrderStatus status, int i) {
                        return status.getId().toString();
                    }

                    @Override
                    public OrderStatus getObject(String id, IModel<? extends List<? extends OrderStatus>> choices) {
                        return choices.getObject().stream().filter(c -> id.equals(c.getId().toString())).findAny().get();
                    }
                }
        ));

        filterForm.add(new TextField<>("productCode"));

        //Reset Action
        AjaxLink reset = new AjaxLink("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                filterModel.setObject(getFilterObject());

                target.add(container);
            }
        };
        filterForm.add(reset);

        //Submit Action
        AjaxButton submit = new AjaxButton("submit", filterForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                target.add(container);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        };
        filterForm.add(submit);

        //Navigator
        container.add(new PagingNavigator("navigator", dataView, getPreferencesPage(), container));

        createPanel = new OrderEditPanel("orderCreate", new Model<>(getString("order")), new OrderEditPanel.CallBack() {
            @Override
            public void update(AjaxRequestTarget target) {
                target.add(container);
            }
        });
        add(createPanel);

        if (!getTemplateWebApplication().hasAnyRole(ru.complitex.template.web.security.SecurityRole.ADMIN_MODULE_EDIT) &&
                isOrderEditor()) {
            createPanel.open(null, null);
        }

    }

    private boolean isOrderEditor() {
        return getTemplateWebApplication().hasAnyRole(SecurityRole.ORDER_EDIT);
    }

    private OrderExt getFilterObject() {
        OrderExt filterObject = new OrderExt();
        filterObject.setCallGirl(new CallGirl());
        filterObject.setCustomer(new Person());
        return filterObject;
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return isOrderEditor() ? ImmutableList.of(new AddItemButton(id, true) {

            @Override
            protected void onClick(AjaxRequestTarget target) {
                createPanel.open(target, null);
            }
        }) : super.getToolbarButtons(id);
    }

    private PageParameters getEditPageParams(Long id) {
        PageParameters parameters = new PageParameters();
        if (id != null) {
            parameters.add("orderId", id);
        }
        return parameters;
    }

    private abstract class PersonModel extends Model<String> {
        @Override
        public String getObject() {
            Person person = getPerson();
            return person != null? person.toString() : "";
        }

        @Override
        public void setObject(String fio) {
            if (StringUtils.isBlank(fio)) {
                setPerson(new Person());
            } else {
                fio = fio.trim();
                String[] personFio = fio.split(" ", 3);

                Person person = new Person();

                if (personFio.length > 0) {
                    person.setLastName(personFio[0]);
                }
                if (personFio.length > 1) {
                    person.setFirstName(personFio[1]);
                }
                if (personFio.length > 2) {
                    person.setMiddleName(personFio[2]);
                }

                setPerson(person);
            }
        }

        protected abstract void setPerson(Person person);

        protected abstract Person getPerson();
    }
}
