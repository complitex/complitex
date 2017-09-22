package org.complitex.pspoffice.frontend.web.person;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.complitex.pspoffice.frontend.service.PspOfficeClient;
import org.complitex.ui.wicket.datatable.TableDataProvider;
import ru.complitex.pspoffice.api.model.PersonModel;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * @author Anatoly A. Ivanov
 * 05.07.2017 16:15
 */
public class PersonDataProvider extends TableDataProvider<PersonModel>{
    @Inject
    private PspOfficeClient pspOfficeClient;

    private PersonModel personModel;

    public PersonDataProvider() {
        personModel = new PersonModel();

        personModel.setLastName(new HashMap<>());
        personModel.setFirstName(new HashMap<>());
        personModel.setMiddleName(new HashMap<>());
    }

    @Override
    public PersonModel getFilterState() {
        return personModel;
    }

    @Override
    public void setFilterState(PersonModel personModel) {
        personModel = personModel;
    }

    @Override
    public Iterator<? extends PersonModel> iterator(long first, long count) {
        return pspOfficeClient.target()
                .path("person")
                .queryParam("offset", first)
                .queryParam("limit", count)
                .request(APPLICATION_JSON_TYPE)
                .get(new GenericType<List<PersonModel>>(){})
                .iterator();
    }

    @Override
    public long size() {
        return pspOfficeClient.request("person/size").get(Long.class);
    }

    @Override
    public IModel<PersonModel> model(PersonModel personModel) {
        return new CompoundPropertyModel<>(personModel);
    }
}
