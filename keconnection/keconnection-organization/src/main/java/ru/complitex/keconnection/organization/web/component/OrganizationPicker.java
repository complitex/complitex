package ru.complitex.keconnection.organization.web.component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import ru.complitex.common.entity.AttributeFilter;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.web.component.datatable.DataProvider;
import ru.complitex.common.web.component.paging.PagingNavigator;
import ru.complitex.keconnection.organization.strategy.KeOrganizationStrategy;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;
import java.util.Collections;

import static ru.complitex.common.strategy.organization.IOrganizationStrategy.NAME;

/**
 *
 * @author Artem
 */
@Deprecated
public class OrganizationPicker extends FormComponentPanel<DomainObject> {

    private static final TextTemplate CENTER_DIALOG_JS =
            new PackageTextTemplate(OrganizationPicker.class, "CenterDialog.js");

    @EJB
    private StringLocaleBean stringLocaleBean;

    private boolean showData;
    private final DomainObjectFilter example;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeOrganizationStrategy keOrganizationStrategy;

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new PackageResourceReference(
                OrganizationPicker.class, OrganizationPicker.class.getSimpleName() + ".css")));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
                OrganizationPicker.class, OrganizationPicker.class.getSimpleName() + ".js")));
    }

    public OrganizationPicker(String id, IModel<DomainObject> model, long organizationTypeId) {
        this(id, model, false, null, true, organizationTypeId);
    }

    public OrganizationPicker(String id, IModel<DomainObject> model, boolean required,
            IModel<String> labelModel, boolean enabled, long organizationTypeId) {
        super(id, model);

        setRequired(required);
        setLabel(labelModel);

        final Label organizationLabel = new Label("organizationLabel",
                new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        DomainObject organization = getModelObject();
                        if (organization != null) {
                            return keOrganizationStrategy.displayShortNameAndCode(organization, getLocale());
                        } else {
                            return getString("organization_not_selected");
                        }
                    }
                });
        organizationLabel.setOutputMarkupId(true);
        add(organizationLabel);

        final Dialog lookupDialog = new Dialog("lookupDialog") {

            {
                getOptions().putLiteral("width", "auto");
            }
        };

        lookupDialog.setModal(true);
        lookupDialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        lookupDialog.setCloseOnEscape(false);
        add(lookupDialog);
        lookupDialog.setVisibilityAllowed(enabled);
        add(lookupDialog);

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        lookupDialog.add(content);

        final Form<Void> filterForm = new Form<Void>("filterForm");
        content.add(filterForm);

        this.example = newExample(organizationTypeId);

        final DataProvider<DomainObject> dataProvider = new DataProvider<DomainObject>() {

            @Override
            protected Iterable<? extends DomainObject> getData(long first, long count) {
                if (!showData) {
                    return Collections.emptyList();
                }
                example.setLocaleId(stringLocaleBean.convert(getLocale()).getId());
                example.setFirst(first);
                example.setCount(count);
                return keOrganizationStrategy.getList(example);
            }

            @Override
            protected Long getSize() {
                if (!showData) {
                    return 0L;
                }

                example.setLocaleId(stringLocaleBean.convert(getLocale()).getId());

                return keOrganizationStrategy.getCount(example);
            }
        };

        filterForm.add(new TextField<>("nameFilter", new Model<String>() {

            @Override
            public String getObject() {
                return example.getAttributeExample(NAME).getValue();
            }

            @Override
            public void setObject(String name) {
                example.getAttributeExample(NAME).setValue(name);
            }
        }));
        filterForm.add(new TextField<>("codeFilter", new Model<String>() {

            @Override
            public String getObject() {
                return example.getAttributeExample(KeOrganizationStrategy.CODE).getValue();
            }

            @Override
            public void setObject(String code) {
                example.getAttributeExample(KeOrganizationStrategy.CODE).setValue(code);
            }
        }));

        final IModel<DomainObject> organizationModel = new Model<>();

        final AjaxLink select = new AjaxLink("select") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (organizationModel.getObject() == null) {
                    throw new IllegalStateException("Unexpected behaviour.");
                } else {
                    OrganizationPicker.this.getModel().setObject(organizationModel.getObject());
                    clearAndCloseLookupDialog(organizationModel, target, lookupDialog, content, this);
                    target.add(organizationLabel);
                }
            }
        };
        select.setOutputMarkupPlaceholderTag(true);
        select.setVisible(false);
        content.add(select);

        final RadioGroup<DomainObject> radioGroup = new RadioGroup<>("radioGroup", organizationModel);
        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                toggleSelectButton(select, target, organizationModel);
            }
        });
        filterForm.add(radioGroup);

        DataView<DomainObject> data = new DataView<DomainObject>("data", dataProvider) {

            @Override
            protected void populateItem(Item<DomainObject> item) {
                final DomainObject organization = item.getModelObject();

                item.add(new Radio<>("radio", item.getModel(), radioGroup));
                item.add(new Label("name", organization.getStringValue(NAME, getLocale())));
                item.add(new Label("code", keOrganizationStrategy.getCode(organization)));
            }
        };
        radioGroup.add(data);

        PagingNavigator pagingNavigator = new PagingNavigator("navigator", data, content) {

            @Override
            public boolean isVisible() {
                return showData;
            }
        };
        pagingNavigator.setOutputMarkupPlaceholderTag(true);
        content.add(pagingNavigator);

        IndicatingAjaxButton find = new IndicatingAjaxButton("find", filterForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                showData = true;
                target.add(content);
                target.appendJavaScript(CENTER_DIALOG_JS.asString(
                        ImmutableMap.of("dialogId", lookupDialog.getMarkupId())));
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        };
        filterForm.add(find);

        AjaxLink cancel = new AjaxLink("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                clearAndCloseLookupDialog(organizationModel, target, lookupDialog, content, select);
            }
        };
        content.add(cancel);

        AjaxLink choose = new AjaxLink("choose") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                lookupDialog.open(target);
            }
        };
        choose.setVisibilityAllowed(enabled);
        add(choose);
    }

    private void toggleSelectButton(Component select, AjaxRequestTarget target, IModel<DomainObject> organizationModel) {
        boolean wasVisible = select.isVisible();
        select.setVisible(organizationModel.getObject() != null);
        if (select.isVisible() ^ wasVisible) {
            target.add(select);
        }
    }

    private void clearAndCloseLookupDialog(IModel<DomainObject> organizationModel,
            AjaxRequestTarget target, Dialog lookupDialog, WebMarkupContainer content, Component select) {
        organizationModel.setObject(null);
        select.setVisible(false);
        this.showData = false;
        clearExample();
        target.add(content);
        lookupDialog.close(target);
    }

    private DomainObjectFilter newExample(long organizationTypeId) {
        DomainObjectFilter e = new DomainObjectFilter();
        e.addAttributeFilter(new AttributeFilter(NAME));
        e.addAttributeFilter(new AttributeFilter(KeOrganizationStrategy.CODE));
        e.addAdditionalParam(KeOrganizationStrategy.ORGANIZATION_TYPE_PARAMETER,
                ImmutableList.of(organizationTypeId));
        return e;
    }

    private void clearExample() {
        example.getAttributeExample(NAME).setValue(null);
        example.getAttributeExample(KeOrganizationStrategy.CODE).setValue(null);
    }

    @Override
    public void convertInput() {
        setConvertedInput(getModelObject());
    }
}
