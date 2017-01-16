package org.complitex.common.web.component.datatable;

import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;

/**
 * @author inheaven on 012 12.01.17.
 */
public abstract class WebsocketDataView<T> extends DataView<T>{
    protected WebsocketDataView(String id, IDataProvider<T> dataProvider) {
        super(id, dataProvider);
    }
}
