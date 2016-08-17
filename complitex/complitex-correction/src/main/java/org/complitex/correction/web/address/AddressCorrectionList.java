package org.complitex.correction.web.address;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.web.AbstractCorrectionList;

/**
 * Страница для списка коррекций элементов адреса(город, улица).
  */
public abstract class AddressCorrectionList<T extends Correction> extends AbstractCorrectionList<T> {
    public AddressCorrectionList(String entity) {
        super(entity);

        add(new AjaxLink("sync"){
            @Override
            public void onClick(AjaxRequestTarget target) {
                onSync(target);
            }
        }.setVisible(isSyncVisible()));
    }

    @Override
    protected Class<? extends WebPage> getEditPage() {
        return AddressCorrectionEdit.class;
    }

    @Override
    protected PageParameters getEditPageParams(Long objectCorrectionId) {
        PageParameters parameters = new PageParameters();
        parameters.set(AddressCorrectionEdit.CORRECTED_ENTITY, getEntity());
        if (objectCorrectionId != null) {
            parameters.set(AddressCorrectionEdit.CORRECTION_ID, objectCorrectionId);
        }
        return parameters;
    }

    protected boolean isSyncVisible() {
        return false;
    }

    protected void onSync(AjaxRequestTarget target){
    }
}
