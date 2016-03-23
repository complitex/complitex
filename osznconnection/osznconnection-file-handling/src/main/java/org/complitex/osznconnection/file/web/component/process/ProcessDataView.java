package org.complitex.osznconnection.file.web.component.process;

import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.complitex.common.entity.IExecutorObject;

/**
 *
 * @author Artem
 */
public abstract class ProcessDataView<M extends IExecutorObject> extends DataView<M> {

    static final String ITEM_ID_PREFIX = "item";

    public ProcessDataView(String id, IDataProvider<M> dataProvider) {
        super(id, dataProvider);
        //setItemReuseStrategy(new ReuseIfLongIdEqualStrategy());
    }

    @Override
    protected Item<M> newItem(String id, int index, IModel<M> model) {
        Item<M> item = super.newItem(id, index, model);
        initializeItem(item, model.getObject().getId());
        return item;
    }

    private void initializeItem(Item<?> item, long objectId) {
        /* for highlighting to work properly */
        item.setOutputMarkupId(true);
        item.setMarkupId(ITEM_ID_PREFIX + objectId);
    }
}
