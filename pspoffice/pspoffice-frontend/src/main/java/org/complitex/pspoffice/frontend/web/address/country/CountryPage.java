package org.complitex.pspoffice.frontend.web.address.country;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.pspoffice.frontend.service.PspOfficeClient;
import org.complitex.pspoffice.frontend.web.BasePage;
import org.complitex.pspoffice.frontend.web.address.AddressListPage;
import ru.complitex.pspoffice.api.model.AddressObject;

import javax.inject.Inject;
import java.util.HashMap;

/**
 * @author Anatoly A. Ivanov
 * 09.08.2017 18:40
 */
public class CountryPage extends BasePage{
    @Inject
    private PspOfficeClient pspOfficeClient;

    private IModel<AddressObject> addressModel;

    private Component feedback;

    public CountryPage(PageParameters pageParameters) {
        Long addressObjectId = pageParameters.get("id").toOptionalLong();

        addressModel = Model.of(addressObjectId != null ? getAddressObject(addressObjectId) : newAddressObject());

        add(feedback = new NotificationPanel("feedback").setOutputMarkupId(true));

        Form<AddressObject> form = new Form<>("form");
        form.setOutputMarkupId(true);
        add(form);

        form.add(new TextField<String>("nameRu", new PropertyModel<>(addressModel, "name.ru")).setRequired(true));
        form.add(new TextField<String>("nameUk", new PropertyModel<>(addressModel, "name.uk")));

        form.add(new AjaxSubmitLink("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                getSession().warn("todo extract validate");

                setResponsePage(AddressListPage.class, new PageParameters().add("entity", "country"));
            }

            @Override
            protected void onError(AjaxRequestTarget target) {

            }
        });

        form.add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                setResponsePage(AddressListPage.class, new PageParameters().add("entity", "country"));
            }
        });

    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel(addressModel.getObject().getId() == null ? "titleNew" : "titleEdit");
    }

    private AddressObject newAddressObject() {
        AddressObject addressObject = new AddressObject();

        addressObject.setName(new HashMap<>());

        return addressObject;
    }

    private AddressObject getAddressObject(Long addressObjectId) {
        return pspOfficeClient.request("address/country/" + addressObjectId).get(AddressObject.class);
    }


}
