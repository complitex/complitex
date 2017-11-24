package org.complitex.pspoffice.frontend.web.card;

import org.complitex.pspoffice.frontend.service.PspOfficeClient;
import org.complitex.ui.wicket.datatable.TableDataProvider;
import ru.complitex.pspoffice.api.model.ApartmentCardModel;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import java.util.Iterator;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * @author Anatoly A. Ivanov
 * 03.11.2017 16:09
 */
public class ApartmentCardDataProvider extends TableDataProvider<ApartmentCardModel> {
    @Inject
    private PspOfficeClient pspOfficeClient;

    @Override
    public Iterator<? extends ApartmentCardModel> iterator(long first, long count) {
        return pspOfficeClient.target()
                .path("apartment_card")
                .queryParam("offset", first)
                .queryParam("limit", count)
                .request(APPLICATION_JSON_TYPE)
                .get(new GenericType<List<ApartmentCardModel>>(){})
                .iterator();
    }

    @Override
    public long size() {
        return pspOfficeClient.request("apartment_card/size").get(Long.class);
    }
}
