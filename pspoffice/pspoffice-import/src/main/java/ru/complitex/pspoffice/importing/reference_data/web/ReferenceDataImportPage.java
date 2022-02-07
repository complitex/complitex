/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.importing.reference_data.web;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.time.Duration;
import ru.complitex.address.entity.AddressImportFile;
import ru.complitex.common.entity.IImportFile;
import ru.complitex.common.entity.ImportMessage;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.pspoffice.departure_reason.entity.DepartureReasonImportFile;
import ru.complitex.pspoffice.document_type.entity.DocumentTypeImportFile;
import ru.complitex.pspoffice.housing_rights.entity.HousingRightsImportFile;
import ru.complitex.pspoffice.importing.reference_data.service.ReferenceDataImportService;
import ru.complitex.pspoffice.military.entity.MilitaryServiceRelationImportFile;
import ru.complitex.pspoffice.ownerrelationship.entity.OwnerRelationshipImportFile;
import ru.complitex.pspoffice.ownership.entity.OwnershipFormImportFile;
import ru.complitex.pspoffice.registration_type.entity.RegistrationTypeImportFile;
import ru.complitex.template.web.component.LocalePicker;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public final class ReferenceDataImportPage extends TemplatePage {

    @EJB
    private ReferenceDataImportService importService;

    @EJB
    private StringLocaleBean stringLocaleBean;

    private final IModel<List<IImportFile>> addressDataModel;
    private final IModel<List<IImportFile>> referenceDataModel;
    private final IModel<Locale> localeModel;

    public ReferenceDataImportPage() {
        add(new Label("title", new ResourceModel("title")));

        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupPlaceholderTag(true);
        add(container);

        addressDataModel = new ListModel<>();
        referenceDataModel = new ListModel<>();

        container.add(new FeedbackPanel("messages"));

        Form<Void> form = new Form<Void>("form");
        container.add(form);

        //Справочники
        //Адреса
        final List<IImportFile> addressDataList = Lists.newArrayList();
        Collections.addAll(addressDataList, AddressImportFile.values());
        //Прочие
        final List<IImportFile> referenceDataList = Lists.newArrayList();
        Collections.addAll(referenceDataList, OwnerRelationshipImportFile.values());
        Collections.addAll(referenceDataList, OwnershipFormImportFile.values());
        Collections.addAll(referenceDataList, RegistrationTypeImportFile.values());
        Collections.addAll(referenceDataList, DocumentTypeImportFile.values());
        Collections.addAll(referenceDataList, MilitaryServiceRelationImportFile.values());
        Collections.addAll(referenceDataList, DepartureReasonImportFile.values());
        Collections.addAll(referenceDataList, HousingRightsImportFile.values());

        final IChoiceRenderer<IImportFile> renderer = new IChoiceRenderer<IImportFile>() {

            @Override
            public Object getDisplayValue(IImportFile importFile) {
                return importFile.getFileName() + getStatus(importService.getReferenceDataMessage(importFile));
            }

            @Override
            public String getIdValue(IImportFile importFile, int index) {
                return importFile.name();
            }

            @Override
            public IImportFile getObject(String id, IModel<? extends List<? extends IImportFile>> choices) {
                return choices.getObject().stream().filter(c -> id.equals(c.name())).findAny().get();
            }
        };

        form.add(new CheckBoxMultipleChoice<>("addressData", addressDataModel, addressDataList, renderer));

        localeModel = new Model<>(stringLocaleBean.getSystemLocale());
        form.add(new LocalePicker("localePicker", localeModel, false));

        form.add(new CheckBoxMultipleChoice<>("referenceData", referenceDataModel, referenceDataList, renderer));

        //Кнопка импортировать
        Button process = new Button("process") {

            @Override
            public void onSubmit() {
                if (!importService.isProcessing()) {
                    final List<IImportFile> allReferenceData = ImmutableList.<IImportFile>builder().
                            addAll(addressDataModel.getObject()).addAll(referenceDataModel.getObject()).build();
                    importService.process(allReferenceData, localeModel.getObject());
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
        return new AjaxSelfUpdatingTimerBehavior(Duration.seconds(3)) {

            long stopTimer = 0;

            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                if (!importService.isProcessing()) {

                    addressDataModel.setObject(null);
                    referenceDataModel.setObject(null);

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
            if (!im.isCompleted() && !importService.isProcessing()) {
                return " - " + getStringOrKey("error");
            } else if (im.isCompleted()) {
                return " - " + getStringFormat("complete", im.getIndex());
            } else {
                return " - " + getStringFormat("processing", im.getIndex(), im.getCount());
            }
        }
        return "";
    }
}
