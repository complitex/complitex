package ru.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.correction.web.AbstractCorrectionList;
import ru.complitex.osznconnection.file.service.privilege.PrivilegeCorrectionBean;

import javax.ejb.EJB;

/**
 * Список коррекций привилегий
 */
public class PrivilegeCorrectionList extends AbstractCorrectionList {
    @EJB
    private PrivilegeCorrectionBean privilegeCorrectionBean;


    public PrivilegeCorrectionList() {
        super("privilege");
    }

    @Override
    protected Class<? extends WebPage> getEditPage() {
        return PrivilegeCorrectionEdit.class;
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("title", this, null);
    }

    @Override
    protected PageParameters getEditPageParams(Long objectCorrectionId) {
        PageParameters parameters = new PageParameters();
        if (objectCorrectionId != null) {
            parameters.set(PrivilegeCorrectionEdit.CORRECTION_ID, objectCorrectionId);
        }
        return parameters;
    }
}
