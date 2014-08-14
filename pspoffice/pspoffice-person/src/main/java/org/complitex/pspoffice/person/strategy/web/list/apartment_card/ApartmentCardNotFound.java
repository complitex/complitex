/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.list.apartment_card;

import javax.ejb.EJB;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.pspoffice.person.strategy.web.edit.apartment_card.ApartmentCardEdit;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

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
        add(new Label("label", new StringResourceModel("label", null, new Object[]{
                    addressRendererBean.displayAddress("apartment", apartmentId, getLocale())
                })));
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
