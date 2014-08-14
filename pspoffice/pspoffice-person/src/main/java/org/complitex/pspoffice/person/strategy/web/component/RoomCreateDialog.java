/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component;

import java.text.MessageFormat;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.complitex.address.strategy.room.RoomStrategy;
import org.complitex.address.strategy.room.web.edit.RoomEdit;
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
public abstract class RoomCreateDialog extends AbstractAddressCreateDialog {

    @EJB
    private LocaleBean localeBean;
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
    protected DomainObject initObject(List<StringCulture> number) {
        DomainObject room = roomStrategy.newInstance();
        room.getAttribute(RoomStrategy.NAME).setLocalizedValues(number);
        room.setParentEntityId("apartment".equals(getParentEntity()) ? 100L : 500L);
        room.setParentId(getParentObject().getId());
        return room;
    }

    @Override
    protected boolean validate(DomainObject object) {
        Long existingObjectId = roomStrategy.performDefaultValidation(object, localeBean.getSystemLocale());
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

        return roomStrategy.findById(object.getId(), true);
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
