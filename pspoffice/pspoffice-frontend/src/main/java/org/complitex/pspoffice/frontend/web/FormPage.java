package org.complitex.pspoffice.frontend.web;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.jquery.JQuery;
import de.agilecoders.wicket.jquery.function.Function;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.visit.IVisitor;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;

public abstract class FormPage extends BasePage{
    private static final MetaDataKey<Boolean> ERROR = new MetaDataKey<Boolean>() {};

    private Component feedback;

    private Class<? extends IRequestablePage> returnPage;
    private PageParameters returnPageParameters;

    private Form form;

    protected FormPage() {
        add(feedback = new NotificationPanel("feedback").setOutputMarkupId(true));

        form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        form.add(new AjaxSubmitLink("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                validate(form, target);

                FormPage.this.onSubmit(target);

                Response response = put();

                boolean error = false;

                if (response != null){
                    if (response.getStatus() == CREATED.getStatusCode()){
                        getSession().info("Запись добавлена");
                    } else if (response.getStatus() == OK.getStatusCode()){
                        getSession().info("Запись обновлена");
                    }else {
                        error = true;
                        getSession().error(response.getStatusInfo().getReasonPhrase() + ": " + response.readEntity(String.class));
                        target.add(feedback);
                    }
                }

                if (returnPage != null && !error) {
                    setResponsePage(returnPage, returnPageParameters);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                validate(form, target);

                FormPage.this.onError(target);
            }
        });

        form.add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                if (returnPage != null) {
                    setResponsePage(returnPage, returnPageParameters);
                }

                FormPage.this.onCancel();
            }
        });
    }

    protected Form getForm() {
        return form;
    }

    protected void setReturnPage(Class<? extends IRequestablePage> returnPage) {
        this.returnPage = returnPage;
    }

    protected void setReturnPage(Class<? extends IRequestablePage> returnPage, PageParameters returnPageParameters){
        this.returnPage = returnPage;
        this.returnPageParameters = returnPageParameters;
    }

    private void validate(Form<?> form, AjaxRequestTarget target){
        form.visitFormComponents((IVisitor<FormComponent<?>, Void>) (component, iVisit) -> {
            if (!component.isValid()){
                component.setMetaData(ERROR, true);
                target.appendJavaScript(JQuery.$(component).closest(".form-group").chain(new Function("addClass", "has-error")).build());
            } else if (component.getMetaData(ERROR) != null && component.getMetaData(ERROR)){
                component.setMetaData(ERROR, false);
                target.appendJavaScript(JQuery.$(component).closest(".form-group").chain(new Function("addClass", "has-error")).build());
            }
        });
    }

    protected Response put(){
        return null;
    }

    protected void onSubmit(AjaxRequestTarget target){

    }

    protected void onError(AjaxRequestTarget target){

    }

    protected void onCancel(){

    }
}
