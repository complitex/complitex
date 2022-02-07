package ru.complitex.pspoffice.frontend.web.address;


import ru.complitex.pspoffice.frontend.service.PspOfficeClient;
import ru.complitex.ui.wicket.datatable.TableDataProvider;
import ru.complitex.pspoffice.api.model.AddressModel;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import java.util.Iterator;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * @author Anatoly A. Ivanov
 * 20.07.2017 14:36
 */
public class AddressDataProvider extends TableDataProvider<AddressModel> {
    @Inject
    private PspOfficeClient pspOfficeClient;

    private String entity;

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    @Override
    public Iterator<? extends AddressModel> iterator(long first, long count) {
        return pspOfficeClient.target()
                .path("address")
                .path(entity)
                .queryParam("offset", first)
                .queryParam("limit", count)
                .request(APPLICATION_JSON_TYPE)
                .get(new GenericType<List<AddressModel>>(){}).iterator();
    }

    @Override
    public long size() {
        return pspOfficeClient.target()
                .path("address")
                .path(entity)
                .path("size")
                .request(APPLICATION_JSON_TYPE)
                .get(Long.class);
    }
}
