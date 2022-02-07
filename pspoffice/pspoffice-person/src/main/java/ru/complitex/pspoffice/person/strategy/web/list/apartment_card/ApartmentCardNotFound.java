/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.strategy.web.list.apartment_card;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import ru.complitex.address.service.AddressRendererBean;
import ru.complitex.pspoffice.person.strategy.web.edit.apartment_card.ApartmentCardEdit;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class ApartmentCardNotFound extends TemplatePage {

    @EJB
    private AddressRendererBean addressRendererBean;

    public ApartmentCardNotFound(final long apartmentId) {
        add(new Label("title", new ResourceModel("title")));
        add(new Label("label", new StringResourceModel("label", null, Model.of(new Object[]{
                addressRendererBean.displayAddress("apartment", apartmentId, getLocale())
        }))));
        add(new Link<Void>("yes") {

            @Override
            public void onClick() {
                setResponsePage(new ApartmentCardEdit("apartment", apartmentId, null));
            }
        });
        add(new Link<Void>("no") {

            @Override
            public void onClick() {
                setResponsePage(ApartmentCardSearch.class);
            }
        });
    }
}
