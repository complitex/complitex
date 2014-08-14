/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component;

import java.text.MessageFormat;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.complitex.address.strategy.apartment.ApartmentStrategy;
import org.complitex.address.strategy.apartment.web.edit.ApartmentEdit;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.Log;
import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.LogBean;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.pspoffice.person.Module;

/**
 *
 * @author Artem
 */
public abstract class ApartmentCreateDialog extends AbstractAddressCreateDialog {

    @EJB
    private LocaleBean localeBean;
    @EJB
    private LogBean logBean;
    @EJB
    private ApartmentStrategy apartmentStrategy;

    protected ApartmentCreateDialog(String id, List<Long> userOrganizationIds) {
        super(id, userOrganizationIds);
    }

    public void open(AjaxRequestTarget target, String number, DomainObject building) {
        super.open(target, number, "building", building);
    }

    @Override
    protected String getTitle() {
        return getString("apartment_title");
    }

    @Override
    protected String getNumberLabel() {
        return getString("number");
    }

    @Override
    protected DomainObject initObject(List<StringCulture> number) {
        DomainObject apartment = apartmentStrategy.newInstance();
        apartment.getAttribute(ApartmentStrategy.NAME).setLocalizedValues(number);
        apartment.setParentEntityId(ApartmentStrategy.PARENT_ENTITY_ID);
        apartment.setParentId(getParentObject().getId());
        return apartment;
    }

    @Override
    protected boolean validate(DomainObject object) {
        Long existingObjectId = apartmentStrategy.performDefaultValidation(object, localeBean.getSystemLocale());
        if (existingObjectId != null) {
            error(MessageFormat.format(getString("validation_error"), existingObjectId));
        }
        return existingObjectId == null;
    }

    @Override
    protected DomainObject save(DomainObject object) {
        apartmentStrategy.insert(object, DateUtil.getCurrentDate());
        logBean.log(Log.STATUS.OK, Module.NAME, ApartmentCreateDialog.class,
                Log.EVENT.CREATE, apartmentStrategy, null, object, null);

        return apartmentStrategy.findById(object.getId(), true);
    }

    @Override
    protected void bulkSave(DomainObject object) {
        apartmentStrategy.insert(object, DateUtil.getCurrentDate());
    }

    @Override
    protected void beforeBulkSave(String numbers) {
        ApartmentEdit.beforeBulkSave(Module.NAME, ApartmentCreateDialog.class, numbers, getLocale());
    }

    @Override
    protected void afterBulkSave(AjaxRequestTarget target, String numbers, boolean operationSuccessed) {
        ApartmentEdit.afterBulkSave(Module.NAME, ApartmentCreateDialog.class, numbers, operationSuccessed, getLocale());
    }

    @Override
    protected void onFailBulkSave(AjaxRequestTarget target, DomainObject failObject, String numbers, String failNumber) {
        ApartmentEdit.onFailBulkSave(Module.NAME, ApartmentCreateDialog.class, failObject, numbers, failNumber, getLocale());
    }

    @Override
    protected void onInvalidateBulkSave(AjaxRequestTarget target, DomainObject invalidObject, String numbers, String invalidNumber) {
        ApartmentEdit.onInvalidateBulkSave(Module.NAME, ApartmentCreateDialog.class, invalidObject, numbers,
                invalidNumber, getLocale());
    }
}
