/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.strategy.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import ru.complitex.address.strategy.room.RoomStrategy;
import ru.complitex.address.strategy.room.web.edit.RoomEdit;
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
public abstract class RoomCreateDialog extends AbstractAddressCreateDialog {
    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private LogBean logBean;

    @EJB
    private RoomStrategy roomStrategy;

    protected RoomCreateDialog(String id, List<Long> userOrganizationIds) {
        super(id, userOrganizationIds);
    }

    @Override
    protected String getTitle() {
        return getString("room_title");
    }

    @Override
    protected String getNumberLabel() {
        return getString("number");
    }

    @Override
    protected DomainObject initObject(List<StringValue> number) {
        DomainObject room = roomStrategy.newInstance();
        room.getAttribute(RoomStrategy.NAME).setStringValues(number);
        room.setParentEntityId("apartment".equals(getParentEntity()) ? 100L : 500L);
        room.setParentId(getParentObject().getObjectId());
        return room;
    }

    @Override
    protected boolean validate(DomainObject object) {
        Long existingObjectId = roomStrategy.performDefaultValidation(object, stringLocaleBean.getSystemLocale());
        if (existingObjectId != null) {
            error(MessageFormat.format(getString("validation_error"), existingObjectId));
        }
        return existingObjectId == null;
    }

    @Override
    protected DomainObject save(DomainObject object) {
        roomStrategy.insert(object, DateUtil.getCurrentDate());
        logBean.log(Log.STATUS.OK, Module.NAME, RoomCreateDialog.class,
                Log.EVENT.CREATE, roomStrategy, null, object, null);

        return roomStrategy.getDomainObject(object.getObjectId(), true);
    }

    @Override
    protected void bulkSave(DomainObject object) {
        roomStrategy.insert(object, DateUtil.getCurrentDate());
    }

    @Override
    protected void beforeBulkSave(String numbers) {
        RoomEdit.beforeBulkSave(Module.NAME, RoomCreateDialog.class, numbers, getLocale());
    }

    @Override
    protected void afterBulkSave(AjaxRequestTarget target, String numbers, boolean operationSuccessed) {
        RoomEdit.afterBulkSave(Module.NAME, RoomCreateDialog.class, numbers, operationSuccessed, getLocale());
    }

    @Override
    protected void onFailBulkSave(AjaxRequestTarget target, DomainObject failObject, String numbers, String failNumber) {
        RoomEdit.onFailBulkSave(Module.NAME, RoomCreateDialog.class, failObject, numbers, failNumber, getLocale());
    }

    @Override
    protected void onInvalidateBulkSave(AjaxRequestTarget target, DomainObject invalidObject, String numbers, String invalidNumber) {
        RoomEdit.onInvalidateBulkSave(Module.NAME, RoomCreateDialog.class, invalidObject, numbers,
                invalidNumber, getLocale());
    }
}
