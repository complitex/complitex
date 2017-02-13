package org.complitex.osznconnection.file.web.component.process;

import com.google.common.base.Function;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.common.entity.IExecutorObject;

import java.io.Serializable;
import java.util.*;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;

/**
 *
 * @author Artem
 */
public class SelectManager implements Serializable {

    private static class SelectModelValueWithId implements Serializable {

        SelectModelValue selectModelValue;
        long objectId;

        SelectModelValueWithId(long objectId, SelectModelValue selectModelValue) {
            this.selectModelValue = selectModelValue;
            this.objectId = objectId;
        }
    }

    private static class SelectModelValue implements Serializable {
        private volatile boolean selected;
        private int sortId;

        void toggle() {
            selected = !selected;
        }

        void clearSelect() {
            selected = false;
        }

        public boolean isSelected() {
            return selected;
        }
    }

    private static class SelectValueComparator implements Comparator<SelectModelValueWithId> {

        @Override
        public int compare(SelectModelValueWithId o1, SelectModelValueWithId o2) {
            return Integer.compare(o1.selectModelValue.sortId, o2.selectModelValue.sortId);
        }
    }
    private final Map<Long, SelectModelValue> selectModels;

    public SelectManager() {
        this.selectModels = new HashMap<>();
    }

    public List<Long> getSelectedFileIds() {
        SortedSet<SelectModelValueWithId> selected = new TreeSet<>(new SelectValueComparator());
        for (long objectId : selectModels.keySet()) {
            SelectModelValue selectModelValue = selectModels.get(objectId);
            if (selectModelValue.isSelected()) {
                selected.add(new SelectModelValueWithId(objectId, selectModelValue));
            }
        }

        return newArrayList(transform(selected, new Function<SelectModelValueWithId, Long>() {

            @Override
            public Long apply(SelectModelValueWithId s) {
                return s.objectId;
            }
        }));
    }

    public void clearSelection() {
        selectModels.values().forEach(SelectModelValue::clearSelect);
    }

    public void initializeSelectModels(List<? extends IExecutorObject> objects) {
        for (int i = 0; i < objects.size(); i++) {
            final IExecutorObject object = objects.get(i);
            SelectModelValue selectModelValue = selectModels.get(object.getId());
            if (selectModelValue == null) {
                selectModelValue = new SelectModelValue();
                selectModels.put(object.getId(), selectModelValue);
            }
            selectModelValue.sortId = i;
        }
    }

    IModel<Boolean> newSelectCheckboxModel(Long objectId) {
        return new Model<Boolean>() {

            @Override
            public Boolean getObject() {
                return selectModels.get(objectId).isSelected();
            }

            @Override
            public void setObject(Boolean object) {
                if (object != null) {
                    selectModels.get(objectId).toggle();
                }
            }
        };
    }

    public void remove(long objectId) {
        selectModels.remove(objectId);
    }
}
