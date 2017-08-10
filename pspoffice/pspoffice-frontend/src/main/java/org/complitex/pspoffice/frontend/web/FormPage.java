package org.complitex.pspoffice.frontend.web;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public abstract class FormPage extends BasePage{
    private Component feedback;

    private Class<? extends IRequestablePage> returnPage;
    private PageParameters returnPageParameters;

    private Form form;

    public FormPage() {
        add(feedback = new NotificationPanel("feedback").setOutputMarkupId(true));

        form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        form.add(new AjaxSubmitLink("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                getSession().warn("todo extract validate");

                if (returnPage != null) {
                    setResponsePage(returnPage, returnPageParameters);
                }

                FormPage.this.onSubmit(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {

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

    public Form getForm() {
        return form;
    }

    protected void setReturnPage(Class<? extends IRequestablePage> returnPage, PageParameters returnPageParameters){
        this.returnPage = returnPage;
        this.returnPageParameters = returnPageParameters;

    }

    protected void onSubmit(AjaxRequestTarget target){

    }

    protected void onError(AjaxRequestTarget target){

    }

    protected void onCancel(){

    }
}
