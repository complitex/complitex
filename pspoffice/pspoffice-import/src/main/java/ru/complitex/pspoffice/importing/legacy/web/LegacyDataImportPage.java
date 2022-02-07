package ru.complitex.pspoffice.importing.legacy.web;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.time.Duration;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.ConfigBean;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.common.web.component.UserOrganizationPicker;
import ru.complitex.common.web.component.css.CssAttributeBehavior;
import ru.complitex.common.web.component.image.StaticImage;
import ru.complitex.common.web.component.list.AjaxRemovableListView;
import ru.complitex.common.web.component.search.SearchComponentState;
import ru.complitex.common.web.component.search.WiQuerySearchComponent;
import ru.complitex.pspoffice.importing.legacy.entity.ImportMessage;
import ru.complitex.pspoffice.importing.legacy.entity.ImportStatus;
import ru.complitex.pspoffice.importing.legacy.entity.LegacyDataImportFile;
import ru.complitex.pspoffice.importing.legacy.entity.ProcessItem;
import ru.complitex.pspoffice.importing.legacy.service.LegacyDataImportService;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.complitex.pspoffice.importing.legacy.entity.LegacyDataImportConfig.DEFAULT_LEGACY_IMPORT_FILE_DIR;
import static ru.complitex.pspoffice.importing.legacy.entity.LegacyDataImportConfig.DEFAULT_LEGACY_IMPORT_FILE_ERRORS_DIR;

/**
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class LegacyDataImportPage extends TemplatePage {

    private final Logger log = LoggerFactory.getLogger(LegacyDataImportPage.class);

    @EJB
    private LegacyDataImportService importService;

    @EJB(name = IOrganizationStrategy.BEAN_NAME)
    private IOrganizationStrategy organizationStrategy;

    @EJB
    private ConfigBean configBean;
    private int stopTimer = 0;
    private final IModel<List<ImportMessage>> messagesModel;
    private final List<DomainObject> selectedOrganizations;
    private final SearchComponentState citySearchComponentState;
    private final IModel<String> importDirectoryModel;
    private final IModel<String> errorsDirectoryModel;

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(
                LegacyDataImportPage.class, LegacyDataImportPage.class.getSimpleName() + ".css")));
    }

    public LegacyDataImportPage() {
        add(new Label("title", getString("title")));

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        Form<Void> dataForm = new Form<Void>("dataForm");
        add(dataForm);

        importDirectoryModel = new Model<String>(configBean.getString(DEFAULT_LEGACY_IMPORT_FILE_DIR, true));
        dataForm.add(new TextField<String>("importDirectory", importDirectoryModel).setRequired(true));

        errorsDirectoryModel = new Model<String>(configBean.getString(DEFAULT_LEGACY_IMPORT_FILE_ERRORS_DIR, true));
        dataForm.add(new TextField<String>("errorsDirectory", errorsDirectoryModel).setRequired(true));

        citySearchComponentState = new SearchComponentState();
        dataForm.add(new WiQuerySearchComponent("citySearchComponent", citySearchComponentState,
                ImmutableList.of("city"), null, ShowMode.ACTIVE, true));

        final List<? extends DomainObject> userOrganizations = organizationStrategy.getUserOrganizations(getLocale());

        selectedOrganizations = Lists.newArrayList();
        selectedOrganizations.add(null);

        final WebMarkupContainer organizationsContainer = new WebMarkupContainer("organizationsContainer");
        organizationsContainer.setOutputMarkupId(true);
        dataForm.add(organizationsContainer);

        ListView<DomainObject> organizations = new AjaxRemovableListView<DomainObject>("organizations", selectedOrganizations) {

            @Override
            protected void populateItem(ListItem<DomainObject> item) {
                final WebMarkupContainer fakeContainer = new WebMarkupContainer("fakeContainer");
                item.add(fakeContainer);

                IModel<Long> organizationModel = new Model<Long>() {

                    @Override
                    public Long getObject() {
                        int index = getCurrentIndex(fakeContainer);
                        DomainObject organization = selectedOrganizations.get(index);
                        return organization != null ? organization.getObjectId() : null;
                    }

                    @Override
                    public void setObject(final Long organizationId) {
                        int index = getCurrentIndex(fakeContainer);
                        DomainObject organization = null;
                        if (organizationId != null) {
                            organization = Iterables.find(userOrganizations, new Predicate<DomainObject>() {

                                @Override
                                public boolean apply(DomainObject input) {
                                    return organizationId.equals(input.getObjectId());
                                }
                            });
                        }
                        selectedOrganizations.set(index, organization);
                    }
                };
                organizationModel.setObject(item.getModelObject() != null ? item.getModelObject().getObjectId() : null);
                item.add(new UserOrganizationPicker("organizationPicker", organizationModel, true));

                addRemoveLink("removeOrganization", item, null, organizationsContainer);
            }
        };
        organizationsContainer.add(organizations);
        AjaxLink<Void> addOrganization = new AjaxLink<Void>("addOrganization") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                DomainObject newOrganization = null;
                selectedOrganizations.add(newOrganization);
                target.add(organizationsContainer);
            }
        };
        dataForm.add(addOrganization);

        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        messagesModel = new ListModel<ImportMessage>(new LinkedList<ImportMessage>());

        container.add(new ListView<ImportMessage>("messages", messagesModel) {

            @Override
            protected void populateItem(ListItem<ImportMessage> item) {
                final ImportMessage message = item.getModelObject();
                Label messageLabel = new Label("message", message.getMessage());
                messageLabel.add(new CssAttributeBehavior(message.getLevel().name()));
                item.add(messageLabel);
            }
        }.setReuseItems(true));

        container.add(new ListView<LegacyDataImportFile>("files", Lists.newArrayList(LegacyDataImportFile.values())) {

            @Override
            protected void populateItem(ListItem<LegacyDataImportFile> item) {
                final LegacyDataImportFile importFile = item.getModelObject();
                item.add(new Label("file", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return getString(importFile.name()) + " ("
                                + importFile.getFileName() + ") "
                                + displayLoadingStatus(importService.getLoadingStatus(importFile));
                    }
                }));
            }

            private String displayLoadingStatus(ImportStatus status) {
                if (status != null) {
                    if (status.isFinished()) {
                        return " - " + getStringFormat("finish_loading", status.getIndex());
                    } else if (importService.isProcessing()) {
                        return " - " + getStringFormat("loading_continue", status.getIndex());
                    } else {
                        return " - " + getStringOrKey("error");
                    }
                }
                return "";
            }
        }.setReuseItems(true));

        container.add(new ListView<ProcessItem>("processingItems", Lists.newArrayList(ProcessItem.values())) {

            @Override
            protected void populateItem(ListItem<ProcessItem> item) {
                final ProcessItem processItem = item.getModelObject();
                item.add(new Label("processingItem", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return getString(processItem.name())
                                + displayProcessingStatus(importService.getProcessingStatus(processItem));
                    }

                    private String displayProcessingStatus(ImportStatus status) {
                        if (status != null) {
                            if (status.isFinished()) {
                                return " - " + getStringFormat("finish_processing", status.getIndex());
                            } else if (importService.isProcessing()) {
                                return " - " + getStringFormat("processing_continue", status.getIndex());
                            } else {
                                return " - " + getStringOrKey("error");
                            }
                        }
                        return "";
                    }
                }));
            }
        }.setReuseItems(true));

        container.add(new AjaxSubmitLink("startImport", dataForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (!importService.isProcessing()) {
                    if (LegacyDataImportPage.this.validate()) {
                        Map<String, Long> organizationMap = getSelectedOrganizations();
                        long cityId = citySearchComponentState.get("city").getObjectId();

                        messagesModel.getObject().clear();
                        importService.startImport(cityId, organizationMap, importDirectoryModel.getObject(),
                                errorsDirectoryModel.getObject(), getLocale());

                        container.add(newTimer());
                        target.add(container);
                        target.appendJavaScript("(function(){$('.import_indicator').css('visibility','visible');})()");
                    }
                    target.add(messages);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }

            @Override
            public boolean isVisible() {
                return !importService.isProcessing();
            }
        });
        container.add(new IndicatingAjaxLink<Void>("cleanData") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                messagesModel.getObject().clear();
                target.add(container);

                Map<String, Long> organizationMap = getSelectedOrganizations();
                if (organizationMap.isEmpty()) {
                    error(getString("clean_data_organization_required"));
                } else {
                    try {
                        importService.cleanData(organizationMap.keySet());
                        info(getString("clean_data_successful"));
                    } catch (Exception e) {
                        log.error("", e);
                        error(getString("db_error"));
                    }
                }
                target.add(messages);
            }

            @Override
            public boolean isVisible() {
                return !importService.isProcessing();
            }
        });

        add(new StaticImage("importIndicatorImage", AbstractDefaultAjaxBehavior.INDICATOR));
    }

    private Map<String, Long> getSelectedOrganizations() {
        Map<String, Long> organizationMap = Maps.newHashMap();
        for (DomainObject o : selectedOrganizations) {
            if (o != null && o.getObjectId() != null && o.getObjectId() > 0) {
                organizationMap.put(organizationStrategy.getCode(o), o.getObjectId());
            }
        }
        return organizationMap;
    }

    private boolean validateFiles() {
        boolean valid = true;
        File importDirectory = new File(importDirectoryModel.getObject());
        if (!importDirectory.exists()) {
            valid = false;
            error(getString("import_directory_does_not_exists"));
        } else if (!importDirectory.isDirectory()) {
            valid = false;
            error(getString("import_directory_is_not_directory"));
        } else {
            Set<String> notFoundFiles = Sets.newLinkedHashSet();
            Set<String> notFiles = Sets.newLinkedHashSet();

            for (LegacyDataImportFile importFile : LegacyDataImportFile.values()) {
                final File file = new File(importDirectory, importFile.getFileName());
                if (!file.exists()) {
                    notFoundFiles.add(importFile.getFileName());
                } else if (!file.isFile()) {
                    notFiles.add(importFile.getFileName());
                }
            }

            if (!notFoundFiles.isEmpty()) {
                valid = false;
                final StringBuilder files = new StringBuilder();
                boolean first = true;
                for (String fileName : notFoundFiles) {
                    files.append(!first ? ", " : "").append(fileName);
                    if (first) {
                        first = false;
                    }
                }
                error(getStringFormat("import_file_does_not_exists", files));
            }
            if (!notFiles.isEmpty()) {
                valid = false;
                final StringBuilder files = new StringBuilder();
                boolean first = true;
                for (String fileName : notFoundFiles) {
                    files.append(!first ? ", " : "").append(fileName);
                    if (first) {
                        first = false;
                    }
                }
                error(getStringFormat("import_file_is_not_file", files));
            }
        }

        File errorsDirectory = new File(errorsDirectoryModel.getObject());
        if (!errorsDirectory.exists()) {
            valid = false;
            error(getString("errors_directory_does_not_exists"));
        } else if (!errorsDirectory.isDirectory()) {
            valid = false;
            error(getString("errors_directory_is_not_directory"));
        }
        return valid;
    }

    private boolean validate() {
        boolean valid = true;

        valid = validateFiles();

        DomainObject city = citySearchComponentState.get("city");
        if (city == null || city.getObjectId() == null || city.getObjectId() <= 0) {
            error(getString("unselected_city"));
            valid = false;
        }

        if (selectedOrganizations.isEmpty()) {
            error(getString("organization_required"));
            valid = false;
        } else {
            for (DomainObject selectedOrganization : selectedOrganizations) {
                if (selectedOrganization == null || selectedOrganization.getObjectId() == null
                        || selectedOrganization.getObjectId() <= 0) {
                    error(getString("unselected_organization"));
                    valid = false;
                    break;
                }
            }
        }

        return valid;
    }

    private AjaxSelfUpdatingTimerBehavior newTimer() {
        stopTimer = 0;

        return new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)) {

            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                ImportMessage importMessage = null;
                while ((importMessage = importService.getNextMessage()) != null) {
                    messagesModel.getObject().add(importMessage);
                }

                if (!importService.isProcessing()) {
                    target.appendJavaScript("(function(){$('.import_indicator').css('visibility','hidden');})()");
                    stopTimer++;
                }
                if (stopTimer > 2) {
                    stop(target);
                }
            }
        };
    }
}
