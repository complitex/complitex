/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.strategy.web.history.apartment_card;

import org.apache.wicket.Component;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import ru.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import ru.complitex.pspoffice.person.strategy.web.edit.apartment_card.ApartmentCardEdit;
import ru.complitex.pspoffice.person.strategy.web.history.AbstractHistoryPage;

import javax.ejb.EJB;
import java.util.Date;

/**
 *
 * @author Artem
 */
public final class ApartmentCardHistoryPage extends AbstractHistoryPage {

    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;

    public ApartmentCardHistoryPage(long apartmentCardId) {
        super(apartmentCardId, new StringResourceModel("title", null, Model.of(new Object[]{apartmentCardId})),
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
