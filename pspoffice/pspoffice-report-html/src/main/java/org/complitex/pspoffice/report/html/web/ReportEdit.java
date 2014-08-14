package org.complitex.pspoffice.report.html.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.pspoffice.report.html.entity.Report;
import org.complitex.pspoffice.report.html.entity.ReportSql;
import org.complitex.pspoffice.report.html.service.ReportBean;
import org.complitex.pspoffice.report.html.service.ReportService;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.ajax.TinyMceAjaxButton;
import wicket.contrib.tinymce.settings.*;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static wicket.contrib.tinymce.settings.Button.*;
import static wicket.contrib.tinymce.settings.TinyMCESettings.Position.after;
import static wicket.contrib.tinymce.settings.TinyMCESettings.Position.before;
import static wicket.contrib.tinymce.settings.TinyMCESettings.Toolbar.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.06.12 18:35
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class ReportEdit extends FormTemplatePage {
    private final Logger log = LoggerFactory.getLogger(ReportEdit.class);

    private Pattern pattern = Pattern.compile("delete\\W|drop\\W|alter\\W|update\\W|create\\W|insert\\W",
            Pattern.CASE_INSENSITIVE);

    @EJB
    private ReportBean reportBean;

    @EJB
    private ReportService reportService;

    public ReportEdit() {
        init(null);
    }

    public ReportEdit(PageParameters parameters) {
        init(parameters.get("id").toLongObject());
    }

    private void init(Long id){
        add(new Label("title", getString("title")));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("messages");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        final Report report;
        if (id != null){
            report = reportBean.getReport(id);
        }
        else{
            report = new Report();

            List<ReportSql> reportSqlList = new ArrayList<>();
            reportSqlList.add(new ReportSql());
            report.setReportSqlList(reportSqlList);
        }

        //Form
        Form form = new Form<>("form");
        add(form);

        //Name
        form.add(new TextField<>("name", new PropertyModel<>(report, "name")).setRequired(true));

        //Markup
        TextArea markup = new TextArea<>("markup", new PropertyModel<>(report, "markup"));
        markup.setRequired(true);
        markup.add(new TinyMceBehavior(getSettings()));
        form.add(markup);

        //Sql container
        final WebMarkupContainer sqlContainer = new WebMarkupContainer("sql_container");
        sqlContainer.setOutputMarkupId(true);
        form.add(sqlContainer);

        //Sql list
        ListView reportSqlList = new ListView<ReportSql>("report_sql_list",
                new LoadableDetachableModel<List<? extends ReportSql>>() {
                    @Override
                    protected List<? extends ReportSql> load() {
                        return report.getReportSqlList();
                    }
                }) {
            @Override
            protected void populateItem(final ListItem<ReportSql> item) {
                TextArea sql = new TextArea<>("sql", new PropertyModel<>(item.getModel(), "sql"));
                sql.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        //todo
                    }
                });

                item.add(sql);
            }
        };
        reportSqlList.setReuseItems(true);
        sqlContainer.add(reportSqlList);

        //Add sql
        sqlContainer.add(new AjaxSubmitLink("add") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                report.getReportSqlList().add(new ReportSql());

                target.add(sqlContainer);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //no error
            }

            @Override
            public boolean isVisible() {
                return report.getReportSqlList().size() < 12;
            }
        }.setDefaultFormProcessing(false));

        //Remove sql
        sqlContainer.add(new AjaxSubmitLink("remove") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                report.getReportSqlList().remove(report.getReportSqlList().size() - 1);

                target.add(sqlContainer);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //no error
            }

            @Override
            public boolean isVisible() {
                return report.getReportSqlList().size() > 1;
            }
        }.setDefaultFormProcessing(false));

        final Label preview = new Label("preview", Model.of("")){
            @Override
            public boolean isVisible() {
                return !getDefaultModelObject().toString().isEmpty();
            }
        };
        preview.setOutputMarkupPlaceholderTag(true);
        preview.setOutputMarkupId(true);
        preview.setEscapeModelStrings(false);
        add(preview);

        //Preview
        form.add(new TinyMceAjaxButton("preview"){
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (validateSql(report)) {
                    preview.setDefaultModelObject(reportService.fillMarkup(report));
                }

                target.add(feedbackPanel);
                target.add(preview);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        //Submit
        form.add(new TinyMceAjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (validateSql(report)) {
                    reportBean.save(report);

                    ReportEdit.this.info(getStringFormat("saved", DateUtil.getCurrentDate()));
                }

                target.add(feedbackPanel);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        //Back
        form.add(new org.apache.wicket.markup.html.form.Button("cancel"){
            @Override
            public void onSubmit() {
                setResponsePage(ReportList.class);
            }
        }.setDefaultFormProcessing(false));
    }

    private boolean validateSql(Report report){
        for (ReportSql reportSql : report.getReportSqlList()){
            String sql = reportSql.getSql();

            if (sql != null && pattern.matcher(sql).find()){
                ReportEdit.this.error(getString("error_illegal_sql"));

                return false;
            }
        }

        return true;
    }

    private TinyMCESettings getSettings(){
        TinyMCESettings settings = new TinyMCESettings(TinyMCESettings.Theme.advanced);

        ContextMenuPlugin contextMenuPlugin = new ContextMenuPlugin();
        settings.register(contextMenuPlugin);

        // first toolbar
        settings.add(fontselect, first, after);
        settings.add(fontsizeselect, first, after);

        // second toolbar
        PastePlugin pastePlugin = new PastePlugin();
        SearchReplacePlugin searchReplacePlugin = new SearchReplacePlugin();
        DateTimePlugin dateTimePlugin = new DateTimePlugin();
        dateTimePlugin.setDateFormat("Date: %m-%d-%Y");
        dateTimePlugin.setTimeFormat("Time: %H:%M");
        PreviewPlugin previewPlugin = new PreviewPlugin();
        settings.add(cut, second, before);
        settings.add(copy, second, before);
        settings.add(pastePlugin.getPasteButton(), second, before);
        settings.add(pastePlugin.getPasteTextButton(), second, before);
        settings.add(pastePlugin.getPasteWordButton(), second, before);
        settings.add(separator, second, before);
        settings.add(searchReplacePlugin.getSearchButton(), second, before);
        settings.add(searchReplacePlugin.getReplaceButton(), second, before);
        settings.add(separator, second, before);
        settings.add(Button.separator, second, after);
        settings.add(dateTimePlugin.getDateButton(), second, after);
        settings.add(dateTimePlugin.getTimeButton(), second, after);
        settings.add(separator, second, after);
        settings.add(previewPlugin.getPreviewButton(), second, after);
        settings.add(separator, second, after);
        settings.add(forecolor, second, after);
        settings.add(backcolor, second, after);

        // third toolbar
        TablePlugin tablePlugin = new TablePlugin();
        PrintPlugin printPlugin = new PrintPlugin();
        FullScreenPlugin fullScreenPlugin = new FullScreenPlugin();
        DirectionalityPlugin directionalityPlugin = new DirectionalityPlugin();
        settings.add(tablePlugin.getTableControls(), third, before);
        settings.add(Button.separator, third, after);
        settings.add(printPlugin.getPrintButton(), third, after);
        settings.add(Button.separator, third, after);
        settings.add(directionalityPlugin.getLtrButton(), third, after);
        settings.add(directionalityPlugin.getRtlButton(), third, after);
        settings.add(Button.separator, third, after);
        settings.add(fullScreenPlugin.getFullscreenButton(), third, after);

        // other settings
        settings.setToolbarAlign(TinyMCESettings.Align.left);
        settings.setToolbarLocation(TinyMCESettings.Location.top);
        settings.setStatusbarLocation(TinyMCESettings.Location.bottom);
        settings.setResizing(true);

        //skin
        settings.addCustomSetting("skin: \"o2k7\"");
        settings.addCustomSetting("skin_variant : \"silver\"");

        //custom tag
        settings.addCustomSetting("extended_valid_elements : \"list\"");
        settings.addCustomSetting("custom_elements: \"list\"");

        //html editor size
        settings.addCustomSetting("theme_advanced_source_editor_width: 800");
        settings.addCustomSetting("theme_advanced_source_editor_height: 600");

        //cleanup
        settings.addCustomSetting("verify_html : false");

        return settings;
    }
}
