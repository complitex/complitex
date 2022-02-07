package ru.complitex.osznconnection.file.web.pages.ownership;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.correction.web.AbstractCorrectionList;
import ru.complitex.osznconnection.file.service.privilege.OwnershipCorrectionBean;

import javax.ejb.EJB;

/**
 * Список коррекций форм власти.
 */
public class OwnershipCorrectionList extends AbstractCorrectionList {
    @EJB
    private OwnershipCorrectionBean ownershipCorrectionBean;

    public OwnershipCorrectionList() {
        super("ownership");
    }

    @Override
    protected Class<? extends WebPage> getEditPage() {
        return OwnershipCorrectionEdit.class;
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("title", this, null);
    }

    @Override
    protected PageParameters getEditPageParams(Long objectCorrectionId) {
        PageParameters parameters = new PageParameters();
        if (objectCorrectionId != null) {
            parameters.set(OwnershipCorrectionEdit.CORRECTION_ID, objectCorrectionId);
        }
        return parameters;
    }
}
