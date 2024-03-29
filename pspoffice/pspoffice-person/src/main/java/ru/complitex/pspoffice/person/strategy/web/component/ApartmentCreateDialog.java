/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.strategy.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import ru.complitex.address.strategy.apartment.ApartmentStrategy;
import ru.complitex.address.strategy.apartment.web.edit.ApartmentEdit;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.Log;
import ru.complitex.common.entity.StringValue;
import ru.complitex.common.service.LogBean;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.util.DateUtil;
import ru.complitex.pspoffice.person.Module;

import javax.ejb.EJB;
import java.text.MessageFormat;
import java.util.List;

/**
 *
 * @author Artem
 */
public abstract class ApartmentCreateDialog extends AbstractAddressCreateDialog {
    @EJB
    private StringLocaleBean stringLocaleBean;

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
    protected DomainObject initObject(List<StringValue> number) {
        DomainObject apartment = apartmentStrategy.newInstance();
        apartment.getAttribute(ApartmentStrategy.NAME).setStringValues(number);
        apartment.setParentEntityId(ApartmentStrategy.PARENT_ENTITY_ID);
        apartment.setParentId(getParentObject().getObjectId());
        return apartment;
    }

    @Override
    protected boolean validate(DomainObject object) {
        Long existingObjectId = apartmentStrategy.performDefaultValidation(object, stringLocaleBean.getSystemLocale());
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

        return apartmentStrategy.getDomainObject(object.getObjectId(), true);
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
