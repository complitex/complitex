package org.complitex.pspoffice.frontend.service;

import org.glassfish.jersey.jackson.JacksonFeature;
import ru.complitex.pspoffice.api.json.ObjectMapperProvider;
import ru.complitex.pspoffice.api.model.PersonObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 27.06.2017 17:01
 */
public class PersonService {
    private static final String API_URI = "http://localhost:8080/pspoffice-backend/api";

    private Client client = ClientBuilder.newClient()
            .register(JacksonFeature.class)
            .register(ObjectMapperProvider.class);

    public List<PersonObject> getPersonObjects(){
        return client.target(API_URI)
                .path("person")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(new GenericType<List<PersonObject>>(){});
    }

    public String getPersonObjectsJson(){
        return client.target(API_URI)
                .path("person")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(String.class);
    }


}
