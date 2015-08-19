package org.complitex.address.web.sync;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.complitex.address.entity.*;
import org.complitex.address.service.AddressSyncBean;
import org.complitex.address.service.AddressSyncService;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.util.ExceptionUtil;
import org.complitex.common.util.ResourceUtil;
import org.complitex.common.web.component.ajax.AjaxLinkLabel;
import org.complitex.common.web.component.datatable.Action;
import org.complitex.common.web.component.datatable.FilteredDataTable;
import org.complitex.common.web.component.datatable.column.EnumColumn;
import org.complitex.common.web.component.search.SearchComponentState;
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

        Label processed = new Label("processed", Model.of(""));
        processed.setOutputMarkupId(true);
        add(processed);

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

        FilteredDataTable<AddressSync> table;

        add(table = new FilteredDataTable<AddressSync>("table", AddressSync.class, columnMap, actions, FIELDS) {
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

        add(new ListView<AddressSyncFilter>("filters", new LoadableDetachableModel<List<? extends AddressSyncFilter>>() {
            @Override
            protected List<? extends AddressSyncFilter> load() {
                return addressSyncBean.getAddressSyncFilters(addressEntity);
            }
        }) {
            @Override
            protected void populateItem(ListItem<AddressSyncFilter> item) {
                AddressSyncFilter filter = item.getModelObject();
                String status = ResourceUtil.getString(AddressSyncStatus.class, filter.getStatus().name(), getLocale());

                item.add(new AjaxLinkLabel("filter", Model.of(status + " (" + filter.getCount() +")")) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        table.getFilterWrapper().getObject().setStatus(filter.getStatus());
                        target.add(table);
                    }
                });
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

                setVisible(false);

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
            private Long lastProcessed = System.currentTimeMillis();

            @Override
            protected void onBroadcast(WebSocketRequestHandler handler, String key, Object payload) {
                if ("begin".equals(key)){
                    SyncBeginMessage begin = (SyncBeginMessage) payload;

                    getSession().info(String.format(getString(begin.getAddressEntity().name() + ".onBegin"),
                            Objects.toString(begin.getParentName(), ""), begin.getCount()));
                    onUpdate(handler);
                }else if ("done".equals(key)){
                    getSession().info(getString(payload + ".onDone"));
                    onUpdate(handler);
                    handler.add(AddressSyncPanel.this);
                }else if ("error".equals(key)){
                    getSession().error(Objects.toString(payload));
                    onUpdate(handler);
                    handler.add(AddressSyncPanel.this);
                }

                if ("processed".equals(key) && System.currentTimeMillis() - lastProcessed > 100) {
                    //noinspection ConstantConditions
                    processed.setDefaultModelObject(((AddressSync)payload).getName());
                    handler.add(processed);

                    lastProcessed = System.currentTimeMillis();
                }else {
                    processed.setDefaultModelObject("");
                    handler.add(processed);
                }

                if ("add_all".equals(key)){
                    //noinspection ConstantConditions
                    AddressSync a = (AddressSync) payload;
                    String message = String.format(getString(a.getType().name() + ".added"), a.getName());
                    processed.setDefaultModelObject(message);
                    handler.add(processed);

                    getSession().info(message);
                }if ("update_new_name_all".equals(key)){
                    //noinspection ConstantConditions
                    AddressSync a = (AddressSync) payload;
                    String message = String.format(getString(a.getType().name() + ".new_named"), a.getName());
                    processed.setDefaultModelObject(message);
                    handler.add(processed);

                    getSession().info(message);
                }if ("update_duplicate_all".equals(key)){
                    //noinspection ConstantConditions
                    AddressSync a = (AddressSync) payload;
                    String message = String.format(getString(a.getType().name() + ".duplicated"), a.getName());
                    processed.setDefaultModelObject(message);
                    handler.add(processed);

                    getSession().info(message);
                }if ("delete_all".equals(key)){
                    //noinspection ConstantConditions
                    AddressSync a = (AddressSync) payload;
                    String message = String.format(getString(a.getType().name() + ".removed"), a.getName());
                    processed.setDefaultModelObject(message);
                    handler.add(processed);

                    getSession().info(message);
                }else if ("add_all_complete".equals(key)){
                    processed.setDefaultModelObject("");
                    handler.add(AddressSyncPanel.this);
                    onUpdate(handler);
                }
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

        AddressSyncDialog addressSyncDialog = new AddressSyncDialog("dialog", addressEntity){
            @Override
            public void onAdd(AjaxRequestTarget target, SearchComponentState state) {
                switch (addressEntity){
                    case STREET:
                        addressSyncService.addAll(state.getId("city"), addressEntity, getLocale());
                        break;
                    case BUILDING:
                        addressSyncService.addAll(state.getId("street"), addressEntity, getLocale());
                        break;
                    default:
                        addressSyncService.addAll(null, addressEntity, getLocale());
                }

                target.add(AddressSyncPanel.this);
            }

            @Override
            public void onUpdate(AjaxRequestTarget target, SearchComponentState state) {
                switch (addressEntity){
                    case STREET:
                        addressSyncService.updateAll(state.getId("city"), addressEntity, getLocale());
                        break;
                    case BUILDING:
                        addressSyncService.updateAll(state.getId("street"), addressEntity, getLocale());
                        break;
                    default:
                        addressSyncService.updateAll(null, addressEntity, getLocale());
                }

                target.add(AddressSyncPanel.this);
            }

            @Override
            public void onDelete(AjaxRequestTarget target, SearchComponentState state) {
                switch (addressEntity){
                    case STREET:
                        addressSyncService.deleteAll(state.getId("city"), addressEntity);
                        break;
                    case BUILDING:
                        addressSyncService.deleteAll(state.getId("street"), addressEntity);
                        break;
                    default:
                        addressSyncService.deleteAll(null, addressEntity);
                }

                target.add(AddressSyncPanel.this);
            }
        };
        add(addressSyncDialog);

        add(new AjaxLink("add_all") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                addressSyncDialog.open(target);
            }

            @Override
            public boolean isVisible() {
                return !addressSyncService.isLockSync();
            }
        });
    }

    protected void onUpdate(AjaxRequestTarget target){
    }
}
