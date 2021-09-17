package ru.complitex.pspoffice.address.sync.page.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

/**
 * @author Ivanov Anatoliy
 */
public abstract class SyncPanel extends Panel {
    private final IModel<Integer> addedModel = new Model<>(0);
    private final IModel<Integer> updatedModel = new Model<>(0);
    private final IModel<Integer> addedCorrectionModel = new Model<>(0);
    private final IModel<Integer> updatedCorrectionModel = new Model<>(0);
    private final IModel<Integer> loadedModel = new Model<>(0);
    private final IModel<Integer> errorsModel = new Model<>(0);

    private static abstract class SizeModel implements IModel<Integer> {
        private Integer size = null;

        protected abstract Integer get();

        @Override
        public Integer getObject() {
            if (size == null) {
                size = get();
            }

            return size;
        }

        public void init() {
            size = get();
        }

        public void add() {
            size++;
        }
    }

    private final SizeModel sizeModel = new SizeModel() {
        @Override
        protected Integer get() {
            return getSize();
        }
    };

    private final SizeModel correctionSizeModel = new SizeModel() {
        @Override
        protected Integer get() {
            return getCorrectionSize();
        }
    };

    private final SizeModel syncedCorrectionModel = new SizeModel() {
        @Override
        protected Integer get() {
            return getSyncedCorrection();
        }
    };

    public SyncPanel(String id) {
        super(id);

        setOutputMarkupId(true);

        add(new Label("catalog", new ResourceModel(id)));
        add(new Label("size", sizeModel));
        add(new Label("added", addedModel));
        add(new Label("updated", updatedModel));
        add(new Label("correctionSize", correctionSizeModel));
        add(new Label("addedCorrection", addedCorrectionModel));
        add(new Label("updatedCorrection", updatedCorrectionModel));
        add(new Label("syncedCorrection", syncedCorrectionModel));
        add(new Label("loaded", loadedModel));
        add(new Label("errors", errorsModel));
    }

    public void init() {
        addedModel.setObject(0);
        updatedModel.setObject(0);
        addedCorrectionModel.setObject(0);
        updatedCorrectionModel.setObject(0);
        loadedModel.setObject(0);
        errorsModel.setObject(0);

        sizeModel.init();
        correctionSizeModel.init();
        syncedCorrectionModel.init();
    }

    public void onAdd() {
        addedModel.setObject(addedModel.getObject() + 1);

        sizeModel.add();
    }

    public void onUpdate() {
        updatedModel.setObject(updatedModel.getObject() + 1);
    }

    public void onAddCorrection() {
        addedCorrectionModel.setObject(addedCorrectionModel.getObject() + 1);

        correctionSizeModel.add();

        syncedCorrectionModel.add();
    }

    public void onUpdateCorrection() {
        updatedCorrectionModel.setObject(updatedCorrectionModel.getObject() + 1);
    }

    public void onLoad() {
        loadedModel.setObject(loadedModel.getObject() + 1);
    }

    public void onError() {
        errorsModel.setObject(errorsModel.getObject() + 1);
    }

    protected abstract Integer getSize();

    protected abstract Integer getCorrectionSize();

    protected abstract Integer getSyncedCorrection();
}

