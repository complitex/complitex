package ru.complitex.osznconnection.file.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.time.Duration;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.IImportFile;
import ru.complitex.common.entity.ImportMessage;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.web.component.DisableAwareDropDownChoice;
import ru.complitex.common.web.component.DomainObjectDisableAwareRenderer;
import ru.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import ru.complitex.osznconnection.file.entity.CorrectionImportFile;
import ru.complitex.osznconnection.file.entity.privilege.PrivilegeImportFile;
import ru.complitex.osznconnection.file.service.ImportService;
import ru.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import ru.complitex.template.web.component.LocalePicker;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static ru.complitex.address.entity.AddressImportFile.*;
import static ru.complitex.organization_type.strategy.OrganizationTypeStrategy.BILLING_TYPE;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.02.11 18:46
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class ImportPage extends TemplatePage {

    @EJB(name = "OsznImportService")
    private ImportService importService;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private StringLocaleBean stringLocaleBean;

    private int stopTimer = 0;

    private final IModel<List<IImportFile>> dictionaryModel;
    private final IModel<List<IImportFile>> correctionModel;
    private final IModel<Locale> localeModel;

    public ImportPage() {
        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        dictionaryModel = new ListModel<>();
        correctionModel = new ListModel<>();

        container.add(new AjaxFeedbackPanel("messages"));

        Form form = new Form("form");
        container.add(form);

        //Справочники
        List<IImportFile> dictionaryList = new ArrayList<>();
//        Collections.addAll(dictionaryList, OrganizationImportFile.values());
//        Collections.addAll(dictionaryList, AddressImportFile.values());
        Collections.addAll(dictionaryList, PrivilegeImportFile.values());
        //Collections.addAll(dictionaryList, OwnershipImportFile.values());

        //Справочники
        form.add(new CheckBoxMultipleChoice<>("dictionary", dictionaryModel, dictionaryList,
                new IChoiceRenderer<IImportFile>() {

                    @Override
                    public Object getDisplayValue(IImportFile object) {
                        return object.getFileName() + getStatus(importService.getDictionaryMessage(object));
                    }

                    @Override
                    public String getIdValue(IImportFile object, int index) {
                        return object.name();
                    }

                    @Override
                    public IImportFile getObject(String id, IModel<? extends List<? extends IImportFile>> choices) {
                        return choices.getObject().stream().filter(c -> id.equals(c.name())).findAny().get();
                    }
                }).setSuffix("<br/>"));

        //Организация
        final IModel<DomainObject> organizationModel = new Model<>();

        final DisableAwareDropDownChoice<DomainObject> organization = new DisableAwareDropDownChoice<>("organization",
                organizationModel,
                new LoadableDetachableModel<List<? extends DomainObject>>() {

                    @Override
                    protected List<? extends DomainObject> load() {
                        return organizationStrategy.getOrganizations(BILLING_TYPE);
                    }
                }, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return organizationStrategy.displayDomainObject(object, getLocale());
            }
        });
        form.add(organization);

        //Коррекции
        List<IImportFile> correctionList = new ArrayList<>();
        Collections.addAll(correctionList, CITY, DISTRICT, STREET_TYPE, STREET, BUILDING);
        Collections.addAll(correctionList, CorrectionImportFile.values());

        form.add(new CheckBoxMultipleChoice<>("corrections", correctionModel, correctionList,
                new IChoiceRenderer<IImportFile>() {

                    @Override
                    public Object getDisplayValue(IImportFile object) {
                        return object.getFileName() + getStatus(importService.getCorrectionMessage(object));
                    }

                    @Override
                    public String getIdValue(IImportFile object, int index) {
                        return object.name();
                    }

                    @Override
                    public IImportFile getObject(String id, IModel<? extends List<? extends IImportFile>> choices) {
                        return choices.getObject().stream().filter(c -> id.equals(c.name())).findAny().get();
                    }
                }).setSuffix("<br/>"));

        localeModel = new Model<>(stringLocaleBean.getSystemLocale());
        form.add(new LocalePicker("localePicker", localeModel, false));

        //Кнопка импортировать
        Button process = new Button("process") {

            @Override
            public void onSubmit() {
                if (!correctionModel.getObject().isEmpty() && organization.getDefaultModelObject() == null) {
                    error(getStringOrKey("error_organization_required"));
                    return;
                }

                if (!importService.isProcessing()) {
                    importService.process(dictionaryModel.getObject(), correctionModel.getObject(),
                            organizationModel.getObject() != null ? organizationModel.getObject().getObjectId() : null,
                            localeModel.getObject());

                    container.add(newTimer());
                }
            }

            @Override
            public boolean isVisible() {
                return !importService.isProcessing();
            }
        };
        form.add(process);

        //Ошибки
        container.add(new Label("error", new LoadableDetachableModel<Object>() {

            @Override
            protected Object load() {
                return importService.getErrorMessage();
            }
        }) {

            @Override
            public boolean isVisible() {
                return importService.isError();
            }
        });
    }

    private AjaxSelfUpdatingTimerBehavior newTimer() {
        stopTimer = 0;

        return new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)) {

            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                String warn;
                while ((warn = importService.getWarnQueue().poll()) != null){
                    getSession().warn(warn);
                }

                if (!importService.isProcessing()) {

                    dictionaryModel.setObject(null);
                    correctionModel.setObject(null);

                    stopTimer++;
                }

                if (stopTimer > 2) {
                    if (importService.isSuccess()) {
                        info(getString("success"));
                    }
                    stop(target);
                }
            }
        };
    }

    private String getStatus(ImportMessage im) {
        if (im != null) {
            if (im.getIndex() < 1 && !importService.isProcessing()) {
                return " - " + getStringOrKey("error");
            } else if (im.getIndex() == im.getCount()) {
                return " - " + getStringFormat("complete", im.getIndex());
            } else {
                return " - " + getStringFormat("processing", im.getIndex(), im.getCount());
            }
        }

        return "";
    }
}
