package org.complitex.ui.wicket.datatable;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.panel.Panel;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 03.07.2017 16:59
 */
public abstract class FilteredTablePanel<T extends Serializable> extends Panel {
    public FilteredTablePanel(String id) {
        super(id);


        DataTable<T, String> dataTable = new DataTable<>("dataTable", null, null, getRowsPerPage());

    }


    protected long getRowsPerPage(){
        return 10;
    }






}
