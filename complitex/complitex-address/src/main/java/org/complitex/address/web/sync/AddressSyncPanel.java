package org.complitex.address.web.sync;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
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
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.common.entity.DomainObject;
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

import static org.complitex.address.entity.SyncEntity.*;
import static org.complitex.address.entity.AddressSyncStatus.*;

/**
 * @author Anatoly Ivanov
 *         Date: 024 24.06.14 17:57
 */
public class AddressSyncPanel extends Panel {
    private Logger log = LoggerFactory.getLogger(AddressSyncPanel.class);

    private final static String[] FIELDS = {"name", "additionalName", "altName", "altAdditionalName",
            "objectId", "parentObjectId", "externalId", "additionalExternalId", "type", "status", "date"};

    @EJB
    private AddressSyncBean addressSyncBean;

    @EJB
    private AddressSyncService addressSyncService;

    @EJB
    private StreetStrategy streetStrategy;

    public AddressSyncPanel(String id, SyncEntity syncEntity) {
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
                    addressSyncService.insert(model.getObject());

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
                return NEW.equals(model.getObject().getStatus());
            }
        });

        if (STREET.equals(syncEntity)) {
            actions.add(new Action<AddressSync>("street_code_add", "object.street_code_add") {
                @Override
                public void onAction(AjaxRequestTarget target, IModel<AddressSync> model) {
                    try {
                        AddressSync sync = model.getObject();

                        addressSyncService.insert(sync);

                        getSession().info(String.format(getString(sync.getType().name() + ".code_added"),
                                sync.getName(), sync.getExternalId()));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);

                        getSession().error(ExceptionUtil.getCauseMessage(e, true));
                    }

                    target.add(AddressSyncPanel.this);
                    onUpdate(target);
                }

                @Override
                public boolean isVisible(IModel<AddressSync> model) {
                    return DUPLICATE.equals(model.getObject().getStatus());
                }
            });
        }

        actions.add(new Action<AddressSync>("update", "object.duplicate") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<AddressSync> model) {
                addressSyncService.update(model.getObject());

                getSession().info(String.format(getString(model.getObject().getType().name() + ".duplicated"),
                        model.getObject().getName()));
                target.add(AddressSyncPanel.this);
                onUpdate(target);
            }

            @Override
            public boolean isVisible(IModel<AddressSync> model) {
                return DUPLICATE.equals(model.getObject().getStatus());
            }
        });

        actions.add(new Action<AddressSync>("update", "object.new_name") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<AddressSync> model) {
                addressSyncService.update(model.getObject());

                getSession().info(String.format(getString(model.getObject().getType().name() + ".new_named"),
                        model.getObject().getName()));
                target.add(AddressSyncPanel.this);
                onUpdate(target);
            }

            @Override
            public boolean isVisible(IModel<AddressSync> model) {
                return NEW_NAME.equals(model.getObject().getStatus());
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
                return ARCHIVAL.equals(model.getObject().getStatus());
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
                filterWrapper.getObject().setType(syncEntity);
            }

            @Override
            protected boolean isVisible(String field) {
                return !(field.equals("type")) && super.isVisible(field);
            }
        });

        add(new ListView<AddressSyncFilter>("filters", new LoadableDetachableModel<List<AddressSyncFilter>>() {
            @Override
            protected List<AddressSyncFilter> load() {
                return addressSyncBean.getAddressSyncFilters(syncEntity);
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
                return !BUILDING.equals(syncEntity) && !addressSyncService.isLockSync();
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

                if (syncEntity != null){
                    addressSyncService.sync(syncEntity);
                }else{
                    addressSyncService.syncAll();
                }
            }
        });

        AjaxLink buildingLoadLink = new AjaxLink("building_load") {
            @Override
            public boolean isVisible() {
                return BUILDING.equals(syncEntity) && !addressSyncService.isLockSync();
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
                ((BuildingLoadDialog)getParent().get("building_load_dialog")).open(target);
            }
        };
        add(buildingLoadLink);

        BuildingLoadDialog buildingLoadDialog = new BuildingLoadDialog("building_load_dialog"){
            @Override
            protected void onLoad(AjaxRequestTarget target, Map<String, DomainObject> map) {
                if (addressSyncService.isLockSync()){
                    return;
                }

                getSession().info(getString("object.start"));

                buildingLoadLink.setVisible(false);

                target.add(AddressSyncPanel.this);
                onUpdate(target);

                addressSyncService.sync(syncEntity, map);
            }
        };
        buildingLoadDialog.setVisible(BUILDING.equals(syncEntity));
        add(buildingLoadDialog);

        add(new BroadcastBehavior(AddressSyncService.class) {
            private Long lastProcessed = System.currentTimeMillis();

            @SuppressWarnings("Duplicates")
            @Override
            protected void onBroadcast(WebSocketRequestHandler handler, String key, Object payload) {
                if ("begin".equals(key)){
                    SyncBeginMessage begin = (SyncBeginMessage) payload;

                    getSession().info(String.format(getString(begin.getSyncEntity().name() + ".onBegin"),
                            Objects.toString(begin.getParentName(), ""), begin.getCount()));
                    onUpdate(handler);
                }else if ("done".equals(key)){
                    getSession().info(getString(payload + ".onDone"));

                    processed.setDefaultModelObject("");
                    onUpdate(handler);

                    handler.add(AddressSyncPanel.this);
                    handler.add(processed);
                }else if ("error".equals(key)){
                    getSession().error(Objects.toString(payload));
                    onUpdate(handler);
                    handler.add(AddressSyncPanel.this);
                }

                if (key.equals("add_all_complete")){
                    processed.setDefaultModelObject("");

                    handler.add(AddressSyncPanel.this);
                    handler.add(processed);
                    onUpdate(handler);

                    return;
                }

                if (payload instanceof AddressSync && System.currentTimeMillis() - lastProcessed > 100) {
                    lastProcessed = System.currentTimeMillis();

                    AddressSync addressSync = (AddressSync) payload;

                    String name;

                    if (syncEntity.equals(BUILDING)){
                        name = Objects.toString(streetStrategy.getFullName(addressSync.getParentId()) +", ", "") +
                                addressSync.getName();
                    }else {
                        name = addressSync.getName();
                    }

                    String message = "";

                    switch (key){
                        case "processed":
                            message = name;
                            break;
                        case "add_all":
                            message = String.format(getString(syncEntity.name() + ".added"), name);
                            break;
                        case "update_new_name_all":
                            message = String.format(getString(syncEntity.name() + ".new_named"), name);
                            break;
                        case "update_duplicate_all":
                            message = String.format(getString(syncEntity.name() + ".duplicated"), name);
                            break;
                        case "delete_all":
                            message = String.format(getString(syncEntity.name() + ".removed"), name);
                            break;
                    }

                    processed.setDefaultModelObject(message);
                    handler.add(processed);
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

        AddressSyncDialog addressSyncDialog = new AddressSyncDialog("dialog", syncEntity){
            @Override
            public void onAdd(AjaxRequestTarget target, SearchComponentState state) {
                switch (syncEntity){
                    case STREET:
                        addressSyncService.addAll(state.getId("city"), syncEntity);
                        break;
                    case BUILDING:
                        addressSyncService.addAll(state.getId("street"), syncEntity);
                        break;
                    default:
                        addressSyncService.addAll(null, syncEntity);
                }

                target.add(AddressSyncPanel.this);
            }

            @Override
            public void onUpdate(AjaxRequestTarget target, SearchComponentState state) {
                switch (syncEntity){
                    case STREET:
                        addressSyncService.updateAll(state.getId("city"), syncEntity);
                        break;
                    case BUILDING:
                        addressSyncService.updateAll(state.getId("street"), syncEntity);
                        break;
                    default:
                        addressSyncService.updateAll(null, syncEntity);
                }

                target.add(AddressSyncPanel.this);
            }

            @Override
            public void onDelete(AjaxRequestTarget target, SearchComponentState state) {
                switch (syncEntity){
                    case STREET:
                        addressSyncService.deleteAll(state.getId("city"), syncEntity);
                        break;
                    case BUILDING:
                        addressSyncService.deleteAll(state.getId("street"), syncEntity);
                        break;
                    default:
                        addressSyncService.deleteAll(null, syncEntity);
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

    protected void onUpdate(IPartialPageRequestHandler target){
    }
}
