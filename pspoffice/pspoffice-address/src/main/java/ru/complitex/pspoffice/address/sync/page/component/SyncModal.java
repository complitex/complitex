package ru.complitex.pspoffice.address.sync.page.component;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import ru.complitex.address.entity.*;
import ru.complitex.catalog.util.Dates;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public class SyncModal extends Modal<Void> {
    private final IModel<List<Integer>> model = new ListModel<>(new ArrayList<>());
    private final IModel<LocalDate> dateModel = new Model<>(Dates.now());

    private final WebMarkupContainer container;

    public SyncModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);
        setCloseOnEscapeKey(false);
        setHeaderVisible(false);

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        CheckGroup<Integer> choices = new CheckGroup<>("choices", model);
        container.add(choices);

        choices.add(new CheckGroupSelector("all"));
        choices.add(new Check<>("country", () -> Country.CATALOG));
        choices.add(new Check<>("region", () -> Region.CATALOG));
        choices.add(new Check<>("cityType", () -> CityType.CATALOG));
        choices.add(new Check<>("city", () -> City.CATALOG));
        choices.add(new Check<>("district", () -> District.CATALOG));
        choices.add(new Check<>("streetType", () -> StreetType.CATALOG));
        choices.add(new Check<>("street", () -> Street.CATALOG));
        choices.add(new Check<>("house", () -> House.CATALOG));
        choices.add(new Check<>("flat", () -> Flat.CATALOG));

        container.add(new LocalDateTextField("date", dateModel, "dd.MM.yyyy"));

        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Outline_Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                SyncModal.this.sync(target, model.getObject(), dateModel.getObject());

                appendCloseDialogJavaScript(target);
            }
        }.setLabel(new ResourceModel("sync")).setSize(Buttons.Size.Small));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Outline_Secondary) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                appendCloseDialogJavaScript(target);
            }
        }.setLabel(new ResourceModel("cancel")).setSize(Buttons.Size.Small));
    }

    public void open(AjaxRequestTarget target) {
        model.getObject().clear();
        dateModel.setObject(Dates.now());

        target.add(container);

        appendShowDialogJavaScript(target);
    }

    protected void sync(AjaxRequestTarget target, List<Integer> select, LocalDate localDate) {}
}
