package ru.complitex.pspoffice.frontend.web.person;

import ru.complitex.pspoffice.frontend.service.PspOfficeClient;
import ru.complitex.pspoffice.frontend.web.component.datatable.TableDataProvider;
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

    public PersonDataProvider() {
        PersonModel personModel = new PersonModel();

        personModel.setLastName(new HashMap<>());
        personModel.setFirstName(new HashMap<>());
        personModel.setMiddleName(new HashMap<>());

        setFilterState(personModel);
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
}
