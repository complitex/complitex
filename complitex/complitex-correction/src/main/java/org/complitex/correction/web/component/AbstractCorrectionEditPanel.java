package org.complitex.correction.web.component;

import com.google.common.collect.ImmutableMap;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.service.ModuleBean;
import org.complitex.common.service.SessionBean;
import org.complitex.common.web.component.organization.OrganizationIdPicker;
import org.complitex.correction.entity.Correction;
import org.complitex.organization_type.strategy.OrganizationTypeStrategy;
import org.complitex.template.web.template.TemplateSession;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Locale;

/**
 * Абстрактная панель для редактирования коррекций.
  */
public abstract class AbstractCorrectionEditPanel<T extends Correction> extends Panel {

    @EJB
    private SessionBean sessionBean;

    @EJB
    private ModuleBean moduleBean;

    private Long correctionId;

    private T correction;

    private WebMarkupContainer form;
    private Panel correctionInputPanel;

    public AbstractCorrectionEditPanel(String id, Long correctionId) {
        super(id);

        this.correctionId = correctionId;

        correction = isNew() ? newCorrection() : getCorrection(correctionId);

        init();
    }

    public boolean isNew() {
        return correctionId == null;
    }

    protected abstract T getCorrection(Long correctionId);

    protected abstract T newCorrection();

    @Override
    public TemplateSession getSession() {
        return (TemplateSession) super.getSession();
    }

    protected T getCorrection() {
        return correction;
    }

    protected String displayCorrection() {
        return correction.getCorrection();
    }

    protected abstract IModel<String> internalObjectLabel(Locale locale);

    protected abstract WebMarkupContainer internalObjectPanel(String id);

    protected abstract String getNullObjectErrorMessage();

    protected String getNullCorrectionErrorMessage() {
        return new StringResourceModel("Required", Model.ofMap(ImmutableMap.of("label", getString("correction")))).getObject();
    }

    protected boolean freezeOrganization() {
        return false;
    }

    protected boolean checkCorrectionEmptiness() {
        return true;
    }

    protected final boolean validate() {
        boolean valid = preValidate();
        if (checkCorrectionEmptiness() && Strings.isEmpty(getCorrection().getCorrection())) {
            error(getNullCorrectionErrorMessage());
            valid = false;
        }

        if (getCorrection().getObjectId() == null) {
            error(getNullObjectErrorMessage());
            valid = false;
        }

        if (isNew()) {
            if (correction.getOrganizationId() == null) {
                error(getString("error_organization"));
                valid = false;
            }

//            if (correction.getUserOrganizationId() == null) {
//                error(getString("error_user_organization"));
//                valid = false;
//            }
        }


        if (valid && validateExistence()) {
            error(getString("exist"));
            valid = false;
        }
        return valid;
    }

    protected boolean preValidate() {
        return true;
    }

    protected abstract boolean validateExistence();

    protected void back(boolean useScrolling) {
        PageParameters backPageParameters = getBackPageParameters();
        if (backPageParameters == null && useScrolling) {
            backPageParameters = new PageParameters();
        }

        if (backPageParameters != null) {
            setResponsePage(getBackPageClass(), backPageParameters);
        } else {
            setResponsePage(getBackPageClass());
        }
    }

    protected abstract Class<? extends Page> getBackPageClass();

    protected abstract PageParameters getBackPageParameters();

    protected abstract void save();

    protected abstract void delete();

    public void executeDeletion() {
        try {
            delete();
            back(false);
        } catch (Exception e) {
            error(getString("db_error"));
            LoggerFactory.getLogger(getClass()).error("", e);
        }
    }

    protected WebMarkupContainer getFormContainer() {
        return form;
    }

    protected boolean isOrganizationCodeRequired() {
        return false;
    }

    protected Panel getCorrectionInputPanel(String id) {
        return new DefaultCorrectionInputPanel(id, new PropertyModel<String>(getCorrection(), "correction"));
    }

    protected abstract IModel<String> getTitleModel();

    protected void init() {
        IModel<String> titleModel = getTitleModel();
        add(new Label("title", titleModel));
        add(new Label("label", titleModel));

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        form = new Form<Void>("form");
        add(form);

        WebMarkupContainer codeRequiredContainer = new WebMarkupContainer("codeRequiredContainer");
        form.add(codeRequiredContainer);

        boolean isOrganizationCodeRequired = isOrganizationCodeRequired();

        codeRequiredContainer.setVisible(isOrganizationCodeRequired);

        TextField<String> code = new TextField<>("code", new PropertyModel<String>(correction, "externalId"));
        code.setRequired(isOrganizationCodeRequired);

        form.add(code);

        //Organization
        final OrganizationIdPicker organization = new OrganizationIdPicker("organizationId",
                new PropertyModel<>(correction, "organizationId"),
                getOrganizationTypeIds());
        form.add(organization);

        //User Organization
        form.add(new OrganizationIdPicker("userOrganizationId",
                new PropertyModel<>(correction, "userOrganizationId"),
                OrganizationTypeStrategy.USER_ORGANIZATION_TYPE));

        if (isNew()) {
            correction.setModuleId(moduleBean.getModuleId());
        }

        form.add(new Label("internalObjectLabel", internalObjectLabel(getLocale())));
        form.add(internalObjectPanel("internalObject"));

        //correction input panel
        correctionInputPanel = getCorrectionInputPanel("correctionInput");
        correctionInputPanel.setVisible(isNew());
        if (correctionInputPanel.isVisible() && freezeOrganization()) {
            correctionInputPanel.setOutputMarkupId(true);
        }
        form.add(correctionInputPanel);
        //correction label
        form.add(new Label("correctionLabel", !isNew() ? displayCorrection() : "").setVisible(!isNew()));

        //save-cancel functional
        AjaxSubmitLink submit = new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (AbstractCorrectionEditPanel.this.validate()) {
                    save();
                    back(true);
                }else {
                    target.add(messages);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }
        };
        form.add(submit);

        Link cancel = new Link("cancel") {

            @Override
            public void onClick() {
                back(true);
            }
        };
        form.add(cancel);
    }

    protected Long[] getOrganizationTypeIds(){
        return null;
    }

}
