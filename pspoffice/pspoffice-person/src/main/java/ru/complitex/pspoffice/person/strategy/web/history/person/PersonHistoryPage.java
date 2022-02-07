/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.strategy.web.history.person;

import org.apache.wicket.Component;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import ru.complitex.pspoffice.person.strategy.PersonStrategy;
import ru.complitex.pspoffice.person.strategy.web.edit.person.PersonEdit;
import ru.complitex.pspoffice.person.strategy.web.history.AbstractHistoryPage;

import javax.ejb.EJB;
import java.util.Date;

/**
 *
 * @author Artem
 */
public class PersonHistoryPage extends AbstractHistoryPage {

    @EJB
    private PersonStrategy personStrategy;

    public PersonHistoryPage(long personId) {
        super(personId, new StringResourceModel("title", null, Model.of(new Object[]{personId})),
                new ResourceModel("object_link_message"));
    }

    @Override
    protected void returnBackToObject(long objectId) {
        setResponsePage(new PersonEdit(personStrategy.getEditPageParams(objectId, null, null)));
    }

    @Override
    protected Date getPreviousModificationDate(long objectId, Date currentEndDate) {
        return personStrategy.getPreviousModificationDate(objectId, currentEndDate);
    }

    @Override
    protected Date getNextModificationDate(long objectId, Date currentEndDate) {
        return personStrategy.getNextModificationDate(objectId, currentEndDate);
    }

    @Override
    protected Component newHistoryContent(String id, long objectId, Date currentEndDate) {
        return new PersonHistoryPanel(id, objectId, currentEndDate);
    }
}
