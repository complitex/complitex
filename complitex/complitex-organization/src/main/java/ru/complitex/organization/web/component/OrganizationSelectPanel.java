package ru.complitex.organization.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.web.component.datatable.DataProvider;
import ru.complitex.common.web.component.paging.PagingNavigator;
import ru.complitex.common.web.model.AttributeExampleModel;

import javax.ejb.EJB;
import java.util.List;

import static ru.complitex.common.strategy.organization.IOrganizationStrategy.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.01.14 18:49
 */
public class OrganizationSelectPanel extends Panel {
    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private IOrganizationStrategy organizationStrategy;

    public OrganizationSelectPanel(String id, List<Long> organizationTypeIds) {
        this(id, organizationTypeIds, false);
    }

    public OrganizationSelectPanel(String id, List<Long> organizationTypeIds, boolean balanceHolder) {
        super(id);

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        add(content);

        final Form<Void> filterForm = new Form<>("filterForm");
        content.add(filterForm);

        AjaxButton find = new AjaxButton("find", filterForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(content);
            }
        };
        filterForm.add(find);

        //Example
        final DomainObjectFilter example = new DomainObjectFilter().addAttributes(NAME, CODE);

        if (organizationTypeIds != null && !organizationTypeIds.isEmpty()) {
            example.addAdditionalParam(ORGANIZATION_TYPE_PARAMETER, organizationTypeIds);
        }

        if (balanceHolder){
            example.addAdditionalParam(IOrganizationStrategy.BALANCE_HOLDER_PARAMETER, true);
        }

        filterForm.add(new TextField<>("name", new AttributeExampleModel(example, NAME)));
        filterForm.add(new TextField<>("code", new AttributeExampleModel(example, CODE)));

        DataView<DomainObject> dataView = new DataView<DomainObject>("dataView", getDataProvider(example)) {

            @Override
            protected void populateItem(Item<DomainObject> item) {
                final DomainObject domainObject = item.getModelObject();

                item.add(new Label("name", domainObject.getStringValue(NAME, getLocale())));
                item.add(new Label("code", organizationStrategy.getCode(domainObject)));
                item.add(new AjaxLink("select") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        onSelect(target, domainObject);
                    }
                });
            }
        };
        filterForm.add(dataView);

        PagingNavigator pagingNavigator = new PagingNavigator("navigator", dataView, content);
        content.add(pagingNavigator);

        content.add(new AjaxLink("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                onCancel(target);
            }
        });
    }

    protected DataProvider<DomainObject> getDataProvider(final DomainObjectFilter example) {
        return new DataProvider<DomainObject>() {

            @Override
            protected Iterable<? extends DomainObject> getData(long first, long count) {
                example.setLocaleId(stringLocaleBean.convert(getLocale()).getId());
                example.setFirst(first);
                example.setCount(count);

                return organizationStrategy.getList(example);
            }

            @Override
            protected Long getSize() {
                example.setLocaleId(stringLocaleBean.convert(getLocale()).getId());
                return organizationStrategy.getCount(example);
            }
        };
    }

    protected void onSelect(AjaxRequestTarget target, DomainObject domainObject){
    }

    protected void onCancel(AjaxRequestTarget target){
    }
}
