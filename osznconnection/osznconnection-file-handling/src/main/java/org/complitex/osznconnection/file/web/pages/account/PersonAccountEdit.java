package org.complitex.osznconnection.file.web.pages.account;

import com.google.common.collect.Lists;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.service.SessionBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.web.component.organization.OrganizationIdPicker;
import org.complitex.correction.web.AbstractCorrectionList;
import org.complitex.osznconnection.file.entity.PersonAccount;
import org.complitex.osznconnection.file.service.PersonAccountBean;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;
import org.complitex.template.web.component.toolbar.DeleteItemButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;

import javax.ejb.EJB;
import java.util.List;

/**
 * Страница для редактирования записей в локальной таблице номеров л/c.
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class PersonAccountEdit extends FormTemplatePage {

    public static final String CORRECTION_ID = "correction_id";

    @EJB
    private PersonAccountBean personAccountBean;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private SessionBean sessionBean;

    private PersonAccount personAccount;

    public PersonAccountEdit(PageParameters params) {
        Long correctionId = params.get(CORRECTION_ID).toOptionalLong();

        personAccount = personAccountBean.getPersonAccount(correctionId);

        //Проверка доступа к данным
        if (!sessionBean.isAuthorized(personAccount.getUserOrganizationId())) {
            throw new UnauthorizedInstantiationException(this.getClass());
        }

        init();
    }

    private void saveOrUpdate() {
        try {
            personAccountBean.save(personAccount);
            back(true);
        } catch (Exception e) {
            error(getString("db_error"));
            log().error("", e);
        }
    }

    private void delete() {
        try {
            personAccountBean.delete(personAccount);
            back(false);
        } catch (Exception e) {
            error(getString("db_error"));
            log().error("", e);
        }
    }

    private void back(boolean useScrolling) {
        if (useScrolling) {
            PageParameters backPageParameters = new PageParameters();
            backPageParameters.set(AbstractCorrectionList.SCROLL_PARAMETER, personAccount.getId());
            setResponsePage(PersonAccountList.class, backPageParameters);
        } else {
            setResponsePage(PersonAccountList.class);
        }
    }

    private void init() {
        IModel<String> labelModel = new ResourceModel("label");
        add(new Label("title", labelModel));
        add(new Label("label", labelModel));

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        final IModel<PersonAccount> model = new CompoundPropertyModel<PersonAccount>(personAccount);
        Form<PersonAccount> form = new Form<PersonAccount>("form", model);
        add(form);

        form.add(new TextField<String>("puAccountNumber").setRequired(true).setEnabled(false));
        form.add(new TextField<String>("lastName").setRequired(true).setEnabled(false));
        form.add(new TextField<String>("firstName").setRequired(true).setEnabled(false));
        form.add(new TextField<String>("middleName").setRequired(true).setEnabled(false));
        form.add(new TextField<String>("city").setRequired(true).setEnabled(false));
        form.add(new TextField<String>("streetType").setEnabled(false));
        form.add(new TextField<String>("street").setRequired(true).setEnabled(false));
        form.add(new TextField<String>("buildingNumber").setRequired(true).setEnabled(false));
        form.add(new TextField<String>("buildingCorp").setEnabled(false));
        form.add(new TextField<String>("apartment").setRequired(true).setEnabled(false));
        form.add(new TextField<String>("accountNumber").setRequired(true));


        form.add(new OrganizationIdPicker("oszn", new PropertyModel<Long>(model, "organizationId"),
                OsznOrganizationTypeStrategy.PRIVILEGE_DEPARTMENT_TYPE,
                OsznOrganizationTypeStrategy.SUBSIDY_DEPARTMENT_TYPE));

        form.add(new OrganizationIdPicker("calculationCenter", new PropertyModel<Long>(model, "calculationCenterId"),
                OsznOrganizationTypeStrategy.BILLING_TYPE));

        form.add(new OrganizationIdPicker("userOrganization", new PropertyModel<Long>(model, "userOrganizationId"),
                OsznOrganizationTypeStrategy.BILLING_TYPE));

        //save-cancel functional
        AjaxButton submit = new AjaxButton("submit", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                saveOrUpdate();
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }
        };
        form.add(submit);
        Link<Void> cancel = new Link<Void>("cancel") {

            @Override
            public void onClick() {
                back(true);
            }
        };
        form.add(cancel);
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbar = Lists.newArrayList();
        toolbar.add(new DeleteItemButton(id) {

            @Override
            protected void onClick() {
                delete();
            }
        });
        return toolbar;
    }
}
