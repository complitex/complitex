package ru.complitex.pspoffice.address.sync.page.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ivanov Anatoliy
 */
public abstract class SyncPanel extends Panel {
    private final IModel<AtomicInteger> added = new Model<>(new AtomicInteger(0));
    private final IModel<AtomicInteger> updated = new Model<>(new AtomicInteger(0));
    private final IModel<AtomicInteger> addedCorrection = new Model<>(new AtomicInteger(0));
    private final IModel<AtomicInteger> updatedCorrection = new Model<>(new AtomicInteger(0));
    private final IModel<AtomicInteger> loaded = new Model<>(new AtomicInteger(0));
    private final IModel<AtomicInteger> errors = new Model<>(new AtomicInteger(0));

    public SyncPanel(String id) {
        super(id);

        setOutputMarkupId(true);

        add(new Label("catalog", new ResourceModel(id)));
        add(new Label("size", LoadableDetachableModel.of(this::getSize)));
        add(new Label("added", added));
        add(new Label("updated", updated));
        add(new Label("correctionSize", LoadableDetachableModel.of(this::getCorrectionSize)));
        add(new Label("addedCorrection", addedCorrection));
        add(new Label("updatedCorrection", updatedCorrection));
        add(new Label("syncedCorrection", LoadableDetachableModel.of(this::getSyncedCorrection)));
        add(new Label("loaded", loaded));
        add(new Label("errors", errors));
    }

    public void init() {
        added.getObject().set(0);
        updated.getObject().set(0);
        addedCorrection.getObject().set(0);
        updatedCorrection.getObject().set(0);
        loaded.getObject().set(0);
        errors.getObject().set(0);
    }

    public void onAdd() {
        added.getObject().incrementAndGet();
    }

    public void onUpdate() {
        updated.getObject().incrementAndGet();
    }

    public void onAddCorrection() {
        addedCorrection.getObject().incrementAndGet();
    }

    public void onUpdateCorrection() {
        updatedCorrection.getObject().incrementAndGet();
    }

    public void onLoad() {
        loaded.getObject().incrementAndGet();
    }

    public void onError() {
        errors.getObject().incrementAndGet();
    }

    protected abstract Long getSize();

    protected abstract Long getCorrectionSize();

    protected abstract Long getSyncedCorrection();
}

