package org.complitex.pspoffice.frontend.web.person;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.complitex.pspoffice.frontend.service.PspOfficeClient;
import org.complitex.ui.wicket.datatable.TableDataProvider;
import ru.complitex.pspoffice.api.model.PersonObject;

import javax.ws.rs.core.GenericType;
import java.util.Iterator;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * @author Anatoly A. Ivanov
 * 05.07.2017 16:15
 */
public class PersonDataProvider extends TableDataProvider<PersonObject>{
    private PersonObject personObject;

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
        return PspOfficeClient.get().target()
                .path("person")
                .queryParam("offset", first)
                .queryParam("limit", count)
                .request(APPLICATION_JSON_TYPE)
                .get(new GenericType<List<PersonObject>>(){}).iterator();
    }

    @Override
    public long size() {
        return PspOfficeClient.get().request("person/size").get(Long.class);
    }

    @Override
    public IModel<PersonObject> model(PersonObject object) {
        return new CompoundPropertyModel<>(object);
    }
}
