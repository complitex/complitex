/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.history.registration;

import java.util.Date;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.person.strategy.web.edit.registration.RegistrationEdit;
import org.complitex.pspoffice.person.strategy.web.history.AbstractHistoryPage;

/**
 *
 * @author Artem
 */
public class RegistrationHistoryPage extends AbstractHistoryPage {

    @EJB
    private RegistrationStrategy registrationStrategy;
    private final ApartmentCard apartmentCard;
    private final Registration registration;

    public RegistrationHistoryPage(ApartmentCard apartmentCard, Registration registration) {
        super(registration.getId(), new StringResourceModel("title", null, new Object[]{registration.getId()}),
                new ResourceModel("object_link_message"));
        this.apartmentCard = apartmentCard;
        this.registration = registration;
    }

    @Override
    protected Date getPreviousModificationDate(long objectId, Date currentEndDate) {
        return registrationStrategy.getPreviousModificationDate(objectId, currentEndDate);
    }

    @Override
    protected Date getNextModificationDate(long objectId, Date currentEndDate) {
        return registrationStrategy.getNextModificationDate(objectId, currentEndDate);
    }

    @Override
    protected Component newHistoryContent(String id, long objectId, Date currentEndDate) {
        return new RegistrationHistoryPanel(id, objectId, ApartmentCardStrategy.getAddressEntity(apartmentCard),
                apartmentCard.getAddressId(), currentEndDate);
    }

    @Override
    protected void returnBackToObject(long objectId) {
        setResponsePage(new RegistrationEdit(apartmentCard, ApartmentCardStrategy.getAddressEntity(apartmentCard),
                apartmentCard.getAddressId(), registration));
    }
}
