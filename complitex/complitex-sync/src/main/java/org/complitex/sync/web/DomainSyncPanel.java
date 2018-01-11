package org.complitex.sync.web;


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
import org.complitex.address.entity.AddressEntity;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.util.ResourceUtil;
import org.complitex.common.web.component.ajax.AjaxLinkLabel;
import org.complitex.common.web.component.datatable.Action;
import org.complitex.common.web.component.datatable.FilteredDataTable;
import org.complitex.common.web.component.datatable.column.EnumColumn;
import org.complitex.common.web.component.search.SearchComponentState;
import org.complitex.common.wicket.BroadcastBehavior;
import org.complitex.sync.entity.*;
import org.complitex.sync.service.DomainSyncBean;
import org.complitex.sync.service.DomainSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.*;

import static org.complitex.sync.entity.SyncEntity.BUILDING;


/**
 * @author Anatoly Ivanov
 *         Date: 024 24.06.14 17:57
 */
public class DomainSyncPanel extends Panel {
    private Logger log = LoggerFactory.getLogger(DomainSyncPanel.class);

    private final static String[] FIELDS = {"name", "additionalName", "altName", "altAdditionalName",
            "objectId", "parentObjectId", "externalId", "additionalExternalId", "type", "status", "date"};

    @EJB
    private DomainSyncBean domainSyncBean;

    @EJB
    private DomainSyncService domainSyncService;

    @EJB
    private StreetStrategy streetStrategy;

    public DomainSyncPanel(String id, SyncEntity syncEntity) {
        super(id);

        setOutputMarkupId(true);

        Label processed = new Label("processed", Model.of(""));
        processed.setOutputMarkupId(true);
        add(processed);

        //actions
        List<Action<DomainSync>> actions = new ArrayList<>();


        actions.add(new Action<DomainSync>("update", "object.duplicate") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<DomainSync> model) {
//                domainSyncService.update(model.getObject());

                getSession().info(String.format(getString(model.getObject().getType().name() + ".duplicated"),
                        model.getObject().getName()));
                target.add(DomainSyncPanel.this);
                onUpdate(target);
            }

            @Override
            public boolean isVisible(IModel<DomainSync> model) {
                return false; //todo
            }
        });


        Map<String, IColumn<DomainSync, String>> columnMap = new HashMap<>();

        columnMap.put("objectId", new DomainSyncObjectColumn("objectId"));
        columnMap.put("parentObjectId", new DomainSyncParentColumn("parentObjectId"));
        columnMap.put("type", new EnumColumn<>("type", AddressEntity.class, getLocale()));
        columnMap.put("status", new EnumColumn<>("status", DomainSyncStatus.class, getLocale()));

        FilteredDataTable<DomainSync> table;

        add(table = new FilteredDataTable<DomainSync>("table", DomainSync.class, columnMap, actions, FIELDS) {
            @Override
            public List<DomainSync> getList(FilterWrapper<DomainSync> filterWrapper) {
                return domainSyncBean.getList(filterWrapper);
            }

            @Override
            public Long getCount(FilterWrapper<DomainSync> filterWrapper) {
                return domainSyncBean.getCount(filterWrapper);
            }

            @Override
            protected void onInit(FilterWrapper<DomainSync> filterWrapper) {
                filterWrapper.getObject().setType(syncEntity);
            }

            @Override
            protected boolean isVisible(String field) {
                return !(field.equals("type")) && super.isVisible(field);
            }
        });

        add(new ListView<DomainSyncFilter>("filters", new LoadableDetachableModel<List<DomainSyncFilter>>() {
            @Override
            protected List<DomainSyncFilter> load() {
                return domainSyncBean.getDomainSyncFilters(syncEntity);
            }
        }) {
            @Override
            protected void populateItem(ListItem<DomainSyncFilter> item) {
                DomainSyncFilter filter = item.getModelObject();
                String status = ResourceUtil.getString(DomainSyncStatus.class, filter.getStatus().name(), getLocale());

                item.add(new AjaxLinkLabel("filter", Model.of(status + " (" + filter.getCount() +")")) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        table.getFilterWrapper().getObject().setStatus(filter.getStatus());
                        target.add(table);
                    }
                });
            }
        });



        AjaxLink buildingLoadLink = new AjaxLink("building_load") {
            @Override
            public boolean isVisible() {
                return BUILDING.equals(syncEntity) && !domainSyncService.getProcessing();
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
                if (domainSyncService.getProcessing()){
                    return;
                }

                getSession().info(getString("object.start"));

                buildingLoadLink.setVisible(false);

                target.add(DomainSyncPanel.this);
                onUpdate(target);

                domainSyncService.load(syncEntity, map);
            }
        };
        buildingLoadDialog.setVisible(BUILDING.equals(syncEntity));
        add(buildingLoadDialog);

        DomainSyncDialog domainSyncDialog = new DomainSyncDialog("dialog", syncEntity){
            @Override
            public void onUpdate(AjaxRequestTarget target, SearchComponentState state) {
                switch (syncEntity){
                    case STREET:
                        domainSyncService.sync(state.getId("city"), syncEntity);
                        break;
                    case BUILDING:
                        domainSyncService.sync(state.getId("street"), syncEntity);
                        break;
                    default:
                        domainSyncService.sync(null, syncEntity);
                }

                target.add(DomainSyncPanel.this);
            }
        };
        add(domainSyncDialog);


        add(new AjaxLink("load") {
            @Override
            public boolean isVisible() {
                return !BUILDING.equals(syncEntity) && !domainSyncService.getProcessing();
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
                if (domainSyncService.getProcessing()){
                    return;
                }

                getSession().info(getString("object.start"));

                setVisible(false);

                target.add(DomainSyncPanel.this);
                onUpdate(target);

                domainSyncService.load(syncEntity, null);
            }
        });

        add(new AjaxLink("sync") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                switch (syncEntity){
                    case STREET:
                    case BUILDING:
                        domainSyncDialog.open(target);
                        break;
                    default:
                        domainSyncService.sync(null, syncEntity);
                }
            }

            @Override
            public boolean isVisible() {
                return !domainSyncService.getProcessing();
            }
        });

        add(new AjaxLink("cancel") {
            @Override
            public boolean isVisible() {
                return domainSyncService.getProcessing();
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                domainSyncService.cancelSync();
            }
        });

        add(new BroadcastBehavior(DomainSyncService.class) {
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

                    handler.add(DomainSyncPanel.this);
                    handler.add(processed);
                }else if ("error".equals(key)){
                    getSession().error(Objects.toString(payload));
                    onUpdate(handler);
                    handler.add(DomainSyncPanel.this);
                }

                if (key.equals("add_all_complete")){
                    processed.setDefaultModelObject("");

                    handler.add(DomainSyncPanel.this);
                    handler.add(processed);
                    onUpdate(handler);

                    return;
                }

                if (payload instanceof DomainSync && System.currentTimeMillis() - lastProcessed > 100) {
                    lastProcessed = System.currentTimeMillis();

                    DomainSync domainSync = (DomainSync) payload;

                    String name;

                    if (syncEntity.equals(BUILDING)){
                        name = Objects.toString(streetStrategy.getFullName(domainSync.getParentId()) +", ", "") +
                                domainSync.getName();
                    }else {
                        name = domainSync.getName();
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



    }

    protected void onUpdate(IPartialPageRequestHandler target){
    }
}
