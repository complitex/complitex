package org.complitex.address.web.sync;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.complitex.address.entity.AddressEntity;
import org.complitex.address.entity.AddressSync;
import org.complitex.address.entity.AddressSyncStatus;
import org.complitex.address.entity.SyncBeginMessage;
import org.complitex.address.service.AddressSyncBean;
import org.complitex.address.service.AddressSyncService;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.util.ExceptionUtil;
import org.complitex.common.web.component.datatable.Action;
import org.complitex.common.web.component.datatable.FilteredDataTable;
import org.complitex.common.web.component.datatable.column.EnumColumn;
import org.complitex.common.wicket.BroadcastBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.*;

/**
 * @author Anatoly Ivanov
 *         Date: 024 24.06.14 17:57
 */
public class AddressSyncPanel extends Panel {
    private Logger log = LoggerFactory.getLogger(AddressSyncPanel.class);

    private final static String[] FIELDS = {"name", "additionalName", "objectId", "parentObjectId", "externalId",
            "additionalExternalId", "type", "status", "date"};

    @EJB
    private AddressSyncBean addressSyncBean;

    @EJB
    private AddressSyncService addressSyncService;

    public AddressSyncPanel(String id, AddressEntity addressEntity) {
        super(id);

        setOutputMarkupId(true);

        //actions
        List<Action<AddressSync>> actions = new ArrayList<>();
        actions.add(new Action<AddressSync>("add", "object.add") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<AddressSync> model) {
                try {
                    addressSyncService.insert(model.getObject(), getLocale());

                    getSession().info(String.format(getString(model.getObject().getType().name() + ".added"),
                            model.getObject().getName()));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);

                    getSession().error(ExceptionUtil.getCauseMessage(e, true));
                }

                target.add(AddressSyncPanel.this);
                onUpdate(target);
            }

            @Override
            public boolean isVisible(IModel<AddressSync> model) {
                return AddressSyncStatus.NEW.equals(model.getObject().getStatus());
            }
        });

        actions.add(new Action<AddressSync>("update", "object.duplicate") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<AddressSync> model) {
                addressSyncService.update(model.getObject(), getLocale());

                getSession().info(String.format(getString(model.getObject().getType().name() + ".duplicated"),
                        model.getObject().getName()));
                target.add(AddressSyncPanel.this);
                onUpdate(target);
            }

            @Override
            public boolean isVisible(IModel<AddressSync> model) {
                return AddressSyncStatus.DUPLICATE.equals(model.getObject().getStatus());
            }
        });

        actions.add(new Action<AddressSync>("update", "object.new_name") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<AddressSync> model) {
                addressSyncService.update(model.getObject(), getLocale());

                getSession().info(String.format(getString(model.getObject().getType().name() + ".new_named"),
                        model.getObject().getName()));
                target.add(AddressSyncPanel.this);
                onUpdate(target);
            }

            @Override
            public boolean isVisible(IModel<AddressSync> model) {
                return AddressSyncStatus.NEW_NAME.equals(model.getObject().getStatus());
            }
        });

        actions.add(new Action<AddressSync>("archive", "object.archive") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<AddressSync> model) {
                addressSyncService.archive(model.getObject());

                getSession().info(String.format(getString(model.getObject().getType().name() + ".archived"),
                        model.getObject().getName()));
                target.add(AddressSyncPanel.this);
                onUpdate(target);
            }

            @Override
            public boolean isVisible(IModel<AddressSync> model) {
                return AddressSyncStatus.ARCHIVAL.equals(model.getObject().getStatus());
            }
        });

        actions.add(new Action<AddressSync>("remove", "object.remove") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<AddressSync> model) {
                addressSyncBean.delete(model.getObject().getId());

                getSession().info(String.format(getString(model.getObject().getType().name() + ".removed"),
                        model.getObject().getName()));
                target.add(AddressSyncPanel.this);
                onUpdate(target);
            }

            @Override
            public boolean isVisible(IModel<AddressSync> model) {
                return true;
            }
        });

        Map<String, IColumn<AddressSync, String>> columnMap = new HashMap<>();

        columnMap.put("objectId", new AddressSyncObjectColumn("objectId"));
        columnMap.put("parentObjectId", new AddressSyncParentColumn("parentObjectId"));
        columnMap.put("type", new EnumColumn<>("type", AddressEntity.class, getLocale()));
        columnMap.put("status", new EnumColumn<>("status", AddressSyncStatus.class, getLocale()));

        add(new FilteredDataTable<AddressSync>("table", AddressSync.class, columnMap, actions, FIELDS) {
            @Override
            public List<AddressSync> getList(FilterWrapper<AddressSync> filterWrapper) {
                return addressSyncBean.getList(filterWrapper);
            }

            @Override
            public Long getCount(FilterWrapper<AddressSync> filterWrapper) {
                return addressSyncBean.getCount(filterWrapper);
            }

            @Override
            protected void onInit(FilterWrapper<AddressSync> filterWrapper) {
                filterWrapper.getObject().setType(addressEntity);
            }

            @Override
            protected boolean isVisible(String field) {
                return !(addressEntity != null && field.equals("type")) && super.isVisible(field);
            }
        });

        add(new AjaxLink("sync") {
            @Override
            public boolean isVisible() {
                return !addressSyncService.isLockSync();
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
                if (addressSyncService.isLockSync()){
                    return;
                }

                getSession().info(getString("object.start"));

                target.add(AddressSyncPanel.this);
                onUpdate(target);

                if (addressEntity != null){
                    addressSyncService.sync(addressEntity);
                }else{
                    addressSyncService.syncAll();
                }
            }
        });

        add(new BroadcastBehavior(AddressSyncService.class) {
            @Override
            protected void onBroadcast(WebSocketRequestHandler handler, String key, Object payload) {
                if ("begin".equals(key)){
                    SyncBeginMessage begin = (SyncBeginMessage) payload;

                    getSession().info(String.format(getString(begin.getAddressEntity().name() + ".onBegin"),
                            begin.getParentName(), begin.getCount()));
                }else if ("processed".equals(key)){
                    //todo add stats
                }else if ("done".equals(key)){
                    getSession().info(getString(payload + ".onDone"));
                }else if ("error".equals(key)){
                    getSession().error(Objects.toString(payload));
                }

                onUpdate(handler);
            }
        });

        add(new AjaxLink("cancel") {
            @Override
            public boolean isVisible() {
                return addressSyncService.isLockSync();
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                addressSyncService.cancelSync();
            }
        });
    }

    protected void onUpdate(AjaxRequestTarget target){
    }
}
