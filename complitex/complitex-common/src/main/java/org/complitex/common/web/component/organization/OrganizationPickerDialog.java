package org.complitex.common.web.component.organization;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModelComparator;
import org.complitex.common.entity.AttributeFilter;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.Locales;
import org.complitex.common.web.component.datatable.DataProvider;
import org.complitex.common.web.component.paging.PagingNavigator;
import org.complitex.common.web.component.wiquery.ExtendedDialog;
import org.complitex.common.web.model.AttributeExampleModel;

import javax.ejb.EJB;
import java.util.Objects;

import static org.complitex.common.strategy.organization.IOrganizationStrategy.*;

/**
 * @author Anatoly Ivanov
 *         Date: 015 15.08.14 17:42
 */
public class OrganizationPickerDialog extends Panel {
    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    protected IOrganizationStrategy organizationStrategy;

    private ExtendedDialog dialog;
    private WebMarkupContainer content;

    private IModel<DomainObject> organizationModel;

    public OrganizationPickerDialog(String id, IModel<DomainObject> organizationModel, Long... organizationTypeIds) {
        super(id);

        this.organizationModel = organizationModel;

        dialog = new ExtendedDialog("lookupDialog") {{getOptions().putLiteral("width", "auto");}};

        dialog.setModal(true);
        dialog.setVisibilityAllowed(isEnabled());
        add(dialog);

        content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        content.setVisible(false);
        dialog.add(content);

        final Form form = new Form("filterForm");
        content.add(form);

        final DomainObjectFilter example = new DomainObjectFilter();

        example.addAttributeExample(new AttributeFilter(NAME));
        example.addAttributeExample(new AttributeFilter(CODE));
        example.setLocaleId(Locales.getLocaleId(getLocale()));

        if (organizationTypeIds != null && organizationTypeIds.length > 0) {
            example.addAdditionalParam(ORGANIZATION_TYPE_PARAMETER, organizationTypeIds);
        }

        final DataProvider<DomainObject> dataProvider = new DataProvider<DomainObject>() {
            @Override
            protected Iterable<? extends DomainObject> getData(long first, long count) {
                example.setFirst(first);
                example.setCount(count);

                return organizationStrategy.getList(example);
            }

            @Override
            protected Long getSize() {
                return organizationStrategy.getCount(example);
            }
        };

        form.add(new TextField<>("nameFilter", new AttributeExampleModel(example, NAME)));

        form.add(new TextField<>("codeFilter", new AttributeExampleModel(example, CODE)));

        final RadioGroup<DomainObject> radioGroup = new RadioGroup<DomainObject>("radioGroup", organizationModel){
            @Override
            public IModelComparator getModelComparator() {
                return new IModelComparator() {
                    @Override
                    public boolean compare(Component component, Object newObject) {
                        return !(component.getDefaultModelObject() == null || newObject == null)
                                && Objects.equals(((DomainObject) component.getDefaultModelObject()).getObjectId(),
                                ((DomainObject) newObject).getObjectId());
                    }
                };
            }
        };
        form.add(radioGroup);

        DataView<DomainObject> data = new DataView<DomainObject>("data", dataProvider) {

            @Override
            protected void populateItem(Item<DomainObject> item) {
                final DomainObject organization = item.getModelObject();

                item.add(new Radio<>("radio", item.getModel(), radioGroup));
                item.add(new Label("name", organization.getStringValue(NAME, getLocale())));
                item.add(new Label("code", organizationStrategy.getCode(organization)));
            }
        };
        radioGroup.add(data);

        PagingNavigator pagingNavigator = new PagingNavigator("navigator", data, content) ;
        content.add(pagingNavigator);

        IndicatingAjaxButton find = new IndicatingAjaxButton("find", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dialog.center(target);

                target.add(content);
            }
        };
        form.add(find);

        final AjaxSubmitLink select = new AjaxSubmitLink("select", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dialog.close(target);
                content.setVisible(false);

                onSelect(target);
            }
        };

        content.add(select);

        AjaxLink<Void> cancel = new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.close(target);
            }
        };
        content.add(cancel);
    }

    public void open(AjaxRequestTarget target){
        content.setVisible(true);
        target.add(content);

        dialog.open(target);
    }

    protected IModel<DomainObject> getOrganizationModel(){
        return organizationModel;
    }

    protected void onSelect(AjaxRequestTarget target){
    }
}