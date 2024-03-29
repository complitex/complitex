package ru.complitex.pspoffice.frontend.web.address.country;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.pspoffice.frontend.service.PspOfficeClient;
import ru.complitex.pspoffice.frontend.web.FormPage;
import ru.complitex.pspoffice.frontend.web.address.AddressListPage;
import ru.complitex.pspoffice.api.model.AddressModel;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.HashMap;

/**
 * @author Anatoly A. Ivanov
 * 09.08.2017 18:40
 */
public class CountryPage extends FormPage{
    @Inject
    private PspOfficeClient pspOfficeClient;

    private IModel<AddressModel> addressModel;

    public CountryPage(PageParameters pageParameters) {
        Long addressObjectId = pageParameters.get("id").toOptionalLong();

        addressModel = Model.of(addressObjectId != null ? getAddressObject(addressObjectId) : newAddressObject());

        getForm().add(new TextField<String>("nameRu", new PropertyModel<>(addressModel, "name.ru")).setRequired(true));
        getForm().add(new TextField<String>("nameUk", new PropertyModel<>(addressModel, "name.uk")));

        setReturnPage(AddressListPage.class, new PageParameters().add("entity", "country"));
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel(addressModel.getObject().getId() == null ? "titleNew" : "titleEdit");
    }

    private AddressModel newAddressObject() {
        AddressModel addressModel = new AddressModel();

        addressModel.setName(new HashMap<>());

        return addressModel;
    }

    private AddressModel getAddressObject(Long addressObjectId) {
        return pspOfficeClient.request("address/country/" + addressObjectId).get(AddressModel.class);
    }

    @Override
    protected Response put() {
        return pspOfficeClient.request("address/country").put(Entity.json(addressModel.getObject()));
    }
}
