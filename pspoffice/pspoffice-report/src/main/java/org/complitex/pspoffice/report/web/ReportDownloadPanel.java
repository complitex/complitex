package org.complitex.pspoffice.report.web;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.pspoffice.report.util.ReportGenerationUtil;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Locale;

public class ReportDownloadPanel extends Panel {

    public static final String RUSSIAN_REPORT_LOCALE = "ru_RU";
    public static final String UKRAIN_REPORT_LOCALE = "uk_UA";
    public static final String PDF_REPORT_FORMAT = "PDF";
    public static final String RTF_REPORT_FORMAT = "RTF";
    private Dialog dialog;

    private class ValueAttributeModifier extends Behavior {

        private final String attribute;
        private final String valueKey;

        private ValueAttributeModifier(String attribute, String valueKey) {
            this.attribute = attribute;
            this.valueKey = valueKey;
        }

        @Override
        public void onComponentTag(final Component component, final ComponentTag tag) {
            if (isEnabled(component)) {
                tag.put(attribute, ReportDownloadPanel.this.getString(valueKey));
            }
        }
    }

    public ReportDownloadPanel(String id, String title, final AbstractReportDownload<?> reportDownload, final boolean print) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setModal(true);
        dialog.setWidth(420);
        dialog.setTitle(title);
        dialog.setMinHeight(0);
        add(dialog);

        final Form<Void> form = new Form<Void>("form");
        dialog.add(form);

        final WebMarkupContainer typeContainer = new WebMarkupContainer("typeContainer");
        typeContainer.setVisible(!print);
        form.add(typeContainer);

        final IModel<String> typeModel = new Model<String>(PDF_REPORT_FORMAT);
        final IModel<String> localeModel = new Model<String>(getLocale().equals(new Locale("ru"))
                ? RUSSIAN_REPORT_LOCALE
                : UKRAIN_REPORT_LOCALE);

        typeContainer.add(new DropDownChoice<String>("type", typeModel,
                Arrays.asList(PDF_REPORT_FORMAT, RTF_REPORT_FORMAT)));

        form.add(new DropDownChoice<String>("locale", localeModel,
                Arrays.asList(RUSSIAN_REPORT_LOCALE, UKRAIN_REPORT_LOCALE),
                new IChoiceRenderer<String>() {

                    @Override
                    public Object getDisplayValue(String object) {
                        if (RUSSIAN_REPORT_LOCALE.equals(object)) {
                            return getString("ru");
                        }
                        if (UKRAIN_REPORT_LOCALE.equals(object)) {
                            return getString("uk");
                        }
                        return null;
                    }

                    @Override
                    public String getIdValue(String object, int index) {
                        return object;
                    }
                }));

        final WebMarkupContainer printContainer = new WebMarkupContainer("printContainer");
        printContainer.setOutputMarkupId(true);
        printContainer.setVisible(print);
        add(printContainer);
        final IModel<String> printKeyModel = new Model<String>();
        printContainer.add(new HiddenField<String>("printKey", printKeyModel).add(AttributeModifier.replace("name", "key")));
        printContainer.add(new HiddenField<String>("printLocale", localeModel).add(AttributeModifier.replace("name", "locale")));

        //Загрузить
        AjaxButton download = new AjaxButton("download", form) {
            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);

                attributes.getAjaxCallListeners().add(new AjaxCallListener(){
                    @Override
                    public AjaxCallListener onSuccess(CharSequence success) {
                        return super.onSuccess(dialog.close().render().toString() + success);
                    }
                });
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                final String sessionKey = reportDownload.getFileName(ReportGenerationUtil.getLocale(localeModel.getObject()));
                storeReportDownload(reportDownload, sessionKey);

                if (!print) {
                    PageParameters params = new PageParameters();
                    params.set("key", sessionKey);
                    params.set("type", typeModel.getObject());
                    params.set("locale", localeModel.getObject());
                    setResponsePage(DownloadPage.class, params);
                } else {
                    printKeyModel.setObject(sessionKey);
                    target.add(printContainer);
                    target.appendJavaScript("(function(){ $('#printForm').submit();})()");
                }
            }

            private void storeReportDownload(AbstractReportDownload<?> reportDownload, String key) {
                HttpSession session = ((HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest()).getSession();
                session.setAttribute(key, reportDownload);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        };
        download.add(new ValueAttributeModifier("value", print ? "print" : "download"));
        form.add(download);

        //Отмена
        form.add(new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.close(target);
            }
        });
    }

    public void open(AjaxRequestTarget target) {
        dialog.open(target);
    }
}
