package org.complitex.pspoffice.person.strategy.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.web.component.RangeNumbersPanel;
import org.complitex.dictionary.web.component.RangeNumbersPanel.NumbersList;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

/**
 *
 * @author Artem
 */
public abstract class AbstractAddressCreateDialog extends Panel {

    private final Logger log = LoggerFactory.getLogger(AbstractAddressCreateDialog.class);
    @EJB
    private AddressRendererBean addressRendererBean;
    private Dialog dialog;
    private final NumbersList numbersList = new NumbersList();
    private WebMarkupContainer content;
    private Form<Void> form;
    private String parentEntity;
    private DomainObject parentObject;
    private final List<Long> userOrganizationIds;
    private final Set<Long> subjectIds;

    protected AbstractAddressCreateDialog(String id, List<Long> userOrganizationIds) {
        super(id);
        this.userOrganizationIds = userOrganizationIds;
        this.subjectIds = newHashSet();
        init();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new PackageResourceReference(AbstractAddressCreateDialog.class,
                AbstractAddressCreateDialog.class.getSimpleName() + ".css")));
    }

    protected DomainObject getParentObject() {
        return parentObject;
    }

    protected String getParentEntity() {
        return parentEntity;
    }

    public void open(AjaxRequestTarget target, String number, String parentEntity, DomainObject parentObject) {
        this.parentEntity = parentEntity;
        this.parentObject = parentObject;
        this.numbersList.reset(number);
        dialog.open(target);
        content.setVisible(true);
        subjectIds.clear();
        form.replace(newPermissionPanel(subjectIds, parentObject.getSubjectIds()));
        target.add(content);
    }

    private void init() {
        dialog = new Dialog("dialog");
        dialog.setModal(true);
        dialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        dialog.setCloseOnEscape(false);
        dialog.setWidth(600);
        dialog.setResizable(false);
        dialog.setTitle(getTitleModel());
        add(dialog);

        content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        dialog.add(content);
        content.setVisible(false);

        content.add(new Label("label", getLabelModel()));
        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        content.add(messages);

        form = new Form<Void>("form");
        content.add(form);

        //parent address
        form.add(new Label("address", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return addressRendererBean.displayAddress(parentEntity, parentObject.getId(), getLocale());
            }
        }));

        //range numbers panel
        final RangeNumbersPanel rangeNumbersPanel = new RangeNumbersPanel("rangeNumbersPanel",
                getNumberLabelModel(), numbersList) {

            @Override
            protected FeedbackPanel initializeMessages() {
                return messages;
            }
        };
        form.add(rangeNumbersPanel);

        //permission panel
        form.add(new EmptyPanel("permissionPanel").setRenderBodyOnly(true));

        //submit button
        form.add(new IndicatingAjaxButton("submit", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    if (rangeNumbersPanel.validate()) {
                        final String numbersAsString = numbersList.asString();
                        final List<List<StringCulture>> numbers = numbersList.getNumbers();

                        if (numbers.size() == 1) {
                            DomainObject object = initObject(numbers.get(0));
                            object.setSubjectIds(subjectIds);
                            if (AbstractAddressCreateDialog.this.validate(object)) {
                                DomainObject saved = save(object);
                                close(target);
                                onCreate(target, saved);
                            } else {
                                target.add(messages);
                            }
                        } else {
                            beforeBulkSave(numbersAsString);
                            boolean bulkOperationSuccess = true;
                            for (List<StringCulture> number : numbers) {
                                DomainObject object = initObject(number);
                                object.setSubjectIds(subjectIds);
                                if (AbstractAddressCreateDialog.this.validate(object)) {
                                    try {
                                        bulkSave(object);
                                    } catch (Exception e) {
                                        bulkOperationSuccess = false;
                                        log.error("", e);
                                        onFailBulkSave(target, object, numbersAsString, numbersList.asString(number));
                                    }
                                } else {
                                    onInvalidateBulkSave(target, object, numbersAsString, numbersList.asString(number));
                                }
                            }
                            afterBulkSave(target, numbersAsString, bulkOperationSuccess);

                            getSession().getFeedbackMessages().clear();

                            close(target);
                            onCancel(target);
                        }
                    } else {
                        target.add(messages);
                    }
                } catch (Exception e) {
                    log.error("", e);
                    error(getString("db_error"));
                    target.add(messages);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }
        });

        //cancel button
        form.add(new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                close(target);
                onCancel(target);
            }
        });
    }

    private PermissionPanel newPermissionPanel(Set<Long> subjectIds, Set<Long> parentSubjectIds) {
        return new PermissionPanel("permissionPanel", userOrganizationIds, subjectIds, parentSubjectIds);
    }

    protected abstract String getTitle();

    private IModel<String> getTitleModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return getTitle();
            }
        };
    }

    private IModel<String> getLabelModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return getLabel();
            }
        };
    }

    private String getLabel() {
        return getTitle();
    }

    protected abstract DomainObject initObject(List<StringCulture> number);

    protected abstract boolean validate(DomainObject object);

    protected abstract String getNumberLabel();

    protected void beforeBulkSave(String numbers) {
    }

    protected void afterBulkSave(AjaxRequestTarget target, String numbers, boolean operationSuccessed) {
    }

    protected void onFailBulkSave(AjaxRequestTarget target, DomainObject failObject, String numbers, String failNumber) {
    }

    protected void onInvalidateBulkSave(AjaxRequestTarget target, DomainObject invalidObject, String numbers, String invalidNumber) {
    }

    private IModel<String> getNumberLabelModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return getNumberLabel();
            }
        };
    }

    protected abstract void bulkSave(DomainObject object);

    protected abstract DomainObject save(DomainObject object);

    private void close(AjaxRequestTarget target) {
        dialog.close(target);
        content.setVisible(false);
        target.add(content);
    }

    protected void onCancel(AjaxRequestTarget target) {
    }

    protected abstract void onCreate(AjaxRequestTarget target, DomainObject object);
}
