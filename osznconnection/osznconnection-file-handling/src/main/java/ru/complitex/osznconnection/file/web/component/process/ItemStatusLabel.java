package ru.complitex.osznconnection.file.web.component.process;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.LoadableDetachableModel;
import ru.complitex.common.entity.IExecutorObject;
import ru.complitex.common.util.StringUtil;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;

public class ItemStatusLabel extends Label {
    public ItemStatusLabel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final Item item = findParent(Item.class);

        setDefaultModel(new LoadableDetachableModel<String>() {
            private int index;

            @Override
            protected String load() {
                if (item.getModelObject() instanceof IExecutorObject) {
                    IExecutorObject object = (IExecutorObject) item.getModelObject();

                    String dots = "";
                    if (object.isProcessing()) {
                        dots += StringUtil.getDots(index++ % 4);
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
