package org.complitex.pspoffice.frontend.web.person;

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.complitex.ui.wicket.datatable.TableDataProvider;
import org.glassfish.jersey.jackson.JacksonFeature;
import ru.complitex.pspoffice.api.json.ObjectMapperProvider;
import ru.complitex.pspoffice.api.model.PersonObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 05.07.2017 16:15
 */
public class PersonDataProvider extends TableDataProvider<PersonObject>{
    private static final String API_URI = "http://localhost:8080/pspoffice-backend/api"; //todo server config

    private PersonObject personObject;

    private Client client(){
        return ClientBuilder.newClient()
                .register(JacksonFeature.class)
                .register(ObjectMapperProvider.class);
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
        return client().target(API_URI)
                .path("person")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(new GenericType<List<PersonObject>>(){})
                .iterator();
    }

    @Override
    public long size() {
        return client().target(API_URI)
                .path("person/size")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(Long.class);
    }

    @Override
    public IModel<PersonObject> model(PersonObject object) {
        return new CompoundPropertyModel<>(object);
    }
}
