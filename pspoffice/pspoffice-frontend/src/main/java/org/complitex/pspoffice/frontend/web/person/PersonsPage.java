package org.complitex.pspoffice.frontend.web.person;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.table.toolbars.BootstrapNavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.complitex.pspoffice.frontend.service.PersonService;
import org.complitex.pspoffice.frontend.web.BasePage;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 27.06.2017 16:26
 */
public class PersonsPage extends BasePage{
    @Inject
    private PersonService personService;

    public PersonsPage() {
        List<IColumn<String, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredPropertyColumn<>(Model.of("column 1"), "trim"));
        columns.add(new TextFilteredPropertyColumn<>(Model.of("column 2"), "getBytes"));

        IDataProvider<String> dataProvider = new ListDataProvider<>(Arrays.asList("1", "2", "3", "4", "5"));

        FilterForm<String> filterForm = new FilterForm<>("form", new IFilterStateLocator<String>() {
            @Override
            public String getFilterState() {
                return null;
            }

            @Override
            public void setFilterState(String state) {

            }
        });
        add(filterForm);

        DataTable<String, String> dataTable = new DataTable<>("test", columns, dataProvider, 2);

        dataTable.addTopToolbar(new HeadersToolbar<>(dataTable, null));
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));

        dataTable.addBottomToolbar(new BootstrapNavigationToolbar(dataTable));

        filterForm.add(dataTable);

    }
}
