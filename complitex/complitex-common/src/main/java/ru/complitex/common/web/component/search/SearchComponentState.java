package ru.complitex.common.web.component.search;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.util.Numbers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SearchComponentState extends HashMap<String, DomainObject> implements Serializable {
    public static final Long NOT_SPECIFIED_ID = -1L;

    public Long getId(String key){
        DomainObject object = get(key);

        return object == null ? null : object.getObjectId();
    }

    public boolean isEmptyState() {
        boolean empty = true;
        for (Entry<String, DomainObject> entry : entrySet()) {
            DomainObject object = entry.getValue();
            if (object != null && object.getObjectId() != null && object.getObjectId() > 0) {
                empty = false;
                break;
            }
        }
        return empty;
    }

    private boolean isEqual(DomainObject o1, DomainObject o2) {
        return o1 == null && o2 == null || !(o1 == null || o2 == null) && Numbers.isEqual(o1.getObjectId(), o2.getObjectId());
    }

    public void updateState(Map<String, ? extends DomainObject> state) {
        boolean clear = false;

        for (String key : state.keySet()) {
            DomainObject that = state.get(key);
            DomainObject current = get(key);

            //clear if object changed
            clear = !isEqual(that, current);
            if (clear) {
                break;
            }
        }

        if (clear) {
            for (String k : this.keySet()) {
                put(k, new DomainObject(NOT_SPECIFIED_ID));
            }
        }

        putAll(state);
    }
}
