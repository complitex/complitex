package ru.complitex.osznconnection.file.web.component.process;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.SharedResourceReference;
import ru.complitex.common.entity.IExecutorObject;
import ru.complitex.common.web.component.css.CssAttributeBehavior;
import ru.complitex.common.web.component.image.StaticImage;

/**
 *
 * @author Artem
 */
public final class ItemCheckBoxPanel<M extends IExecutorObject> extends Panel {

    private final static String IMAGE_AJAX_LOADER = "images/ajax-loader2.gif";
    private final static String IMAGE_AJAX_WAITING = "images/ajax-waiting.gif";

    private final SelectManager selectManager;

    private IModel<? extends IExecutorObject> model;

    public ItemCheckBoxPanel(String id, SelectManager selectManager, IModel<? extends IExecutorObject> model) {
        super(id);

        this.selectManager = selectManager;

        this.model = model;

        setOutputMarkupId(true);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        //Выбор файлов
        CheckBox checkBox = new CheckBox("selected", selectManager.newSelectCheckboxModel(model.getObject().getId())) {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                boolean visible = !model.getObject().isProcessing() && !model.getObject().isWaiting();

                tag.getAttributes().put("style", "display: " + (visible ? "block" : "none"));
            }
        };

        checkBox.add(new AjaxFormComponentUpdatingBehavior("change") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        checkBox.add(new CssAttributeBehavior("processable-list-panel-select"));
        add(checkBox);

        //Анимация в обработке
        add(new StaticImage("processing", new SharedResourceReference(IMAGE_AJAX_LOADER)) {

            @Override
            public boolean isVisible() {
                return model.getObject().isProcessing();
            }
        });

        //Анимация ожидание
        add(new StaticImage("waiting", new SharedResourceReference(IMAGE_AJAX_WAITING)) {

            @Override
            public boolean isVisible() {
                return model.getObject().isWaiting() && !model.getObject().isProcessing();
            }
        });
    }
}
