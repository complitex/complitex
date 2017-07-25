package org.complitex.pspoffice.frontend.web.person;

import org.apache.wicket.cdi.NonContextual;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.complitex.pspoffice.frontend.service.PspOfficeClient;
import org.complitex.ui.wicket.datatable.TableDataProvider;
import ru.complitex.pspoffice.api.model.PersonObject;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 05.07.2017 16:15
 */
public class PersonDataProvider extends TableDataProvider<PersonObject>{
    @Inject
    private PspOfficeClient pspOfficeClient;

    private PersonObject personObject;

    PersonDataProvider() {
        NonContextual.of(PersonDataProvider.class).inject(this);
    }

    @Override
    public PersonObject getFilterState() {
        return personObject;
    }

    @Override
    public void setFilterState(PersonObject state) {
        personObject = state;
    }

    @Override
    public Iterator<? extends PersonObject> iterator(long first, long count) {
        return pspOfficeClient.request("person").get(new GenericType<List<PersonObject>>(){}).iterator();
    }

    @Override
    public long size() {
        return pspOfficeClient.request("person/size").get(Long.class);
    }

    @Override
    public IModel<PersonObject> model(PersonObject object) {
        return new CompoundPropertyModel<>(object);
    }
}
