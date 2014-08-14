/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.history.apartment_card;

import java.util.Date;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.web.edit.apartment_card.ApartmentCardEdit;
import org.complitex.pspoffice.person.strategy.web.history.AbstractHistoryPage;

/**
 *
 * @author Artem
 */
public final class ApartmentCardHistoryPage extends AbstractHistoryPage {

    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;

    public ApartmentCardHistoryPage(long apartmentCardId) {
        super(apartmentCardId, new StringResourceModel("title", null, new Object[]{apartmentCardId}),
                new ResourceModel("object_link_message"));
    }

    @Override
    protected Date getPreviousModificationDate(long objectId, Date currentEndDate) {
        return apartmentCardStrategy.getPreviousModificationDate(objectId, currentEndDate);
    }

    @Override
    protected Date getNextModificationDate(long objectId, Date currentEndDate) {
        return apartmentCardStrategy.getNextModificationDate(objectId, currentEndDate);
    }

    @Override
    protected Component newHistoryContent(String id, long objectId, Date currentEndDate) {
        return new ApartmentCardHistoryPanel(id, objectId, currentEndDate);
    }

    @Override
    protected void returnBackToObject(long objectId) {
        setResponsePage(new ApartmentCardEdit(objectId, null));
    }
}
