package org.complitex.osznconnection.file.web.component.process;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.LoadableDetachableModel;
import org.complitex.common.entity.IExecutorObject;
import org.complitex.osznconnection.file.entity.RequestFileStatus;

public class ItemStatusLabel extends Label {
    private final ProcessingManager processingManager;

    public ItemStatusLabel(String id, ProcessingManager processingManager) {
        super(id);
        this.processingManager = processingManager;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final Item item = findParent(Item.class);

        setDefaultModel(new LoadableDetachableModel<String>() {

            @Override
            protected String load() {
                if (item.getModelObject() instanceof IExecutorObject) {
                    IExecutorObject object = (IExecutorObject) item.getModelObject();

                    String dots = "";
                    if (object.isProcessing() && processingManager.isGlobalProcessing()) {
//                        dots += StringUtil.getDots(timerManager.getTimerIndex() % 5);
                    }

                    if (object.getStatus() instanceof RequestFileStatus) {
                        final RequestFileStatus status = (RequestFileStatus) object.getStatus();

                        return (status != null ? RequestFileStatusRenderer.render(status, getLocale()) : "") + dots;
                    }else {
                        return "";
                    }
                }

                return "";
            }
        });
    }
}
