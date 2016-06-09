package org.complitex.osznconnection.file.web.component.process;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.web.component.css.CssAttributeBehavior;
import org.complitex.common.web.component.image.StaticImage;

/**
 *
 * @author Artem
 */
public final class ItemCheckBoxPanel<M extends IExecutorObject> extends Panel {

    private final static String IMAGE_AJAX_LOADER = "images/ajax-loader2.gif";
    private final static String IMAGE_AJAX_WAITING = "images/ajax-waiting.gif";
    private final ProcessingManager processingManager;
    private final SelectManager selectManager;

    private IModel<? extends IExecutorObject> model;

    public ItemCheckBoxPanel(String id, ProcessingManager processingManager, SelectManager selectManager, IModel<? extends IExecutorObject> model) {
        super(id);
        this.processingManager = processingManager;
        this.selectManager = selectManager;

        this.model = model;

        setOutputMarkupId(true);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        //Выбор файлов
        CheckBox checkBox = new CheckBox("selected", selectManager.newSelectCheckboxModel(model.getObject().getId())) {

//            @Override
//            public boolean isVisible() {
//                return !model.getObject().isProcessing()
//                        && !processingManager.isGlobalWaiting(model.getObject());
//            }

            @Override
            public boolean isEnabled() {
                return !processingManager.isGlobalWaiting(model.getObject()) &&
                        !model.getObject().isProcessing()
                        && !processingManager.isGlobalWaiting(model.getObject());
            }

            @Override
            public String getInputName() {
                return "select" + model.getObject().getId();
            }
        };

        checkBox.setMarkupId("select" + model.getObject().getId());

//        checkBox.add(new AjaxFormComponentUpdatingBehavior("change") {
//
//            @Override
//            protected void onUpdate(AjaxRequestTarget target) {
//            }
//        });
        checkBox.add(new CssAttributeBehavior("processable-list-panel-select"));
        add(checkBox);

        //Анимация в обработке
        add(new StaticImage("processing", new SharedResourceReference(IMAGE_AJAX_LOADER)) {

            @Override
            public boolean isVisible() {
                return model.getObject().isProcessing();
            }
        }.setOutputMarkupId(true));

        //Анимация ожидание
        add(new StaticImage("waiting", new SharedResourceReference(IMAGE_AJAX_WAITING)) {

            @Override
            public boolean isVisible() {
                return processingManager.isGlobalWaiting(model.getObject()) && !model.getObject().isProcessing();
            }
        }.setOutputMarkupId(true));
    }
}
