package ru.complitex.common.util;

import ru.complitex.common.entity.DomainObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 06.02.14 2:37
 */
public class DomainObjectUtil {
    public static List<Long> getIds(List<DomainObject> objects){
        List<Long> ids = new ArrayList<>(objects.size());

        for (DomainObject o : objects){
            ids.add(o.getObjectId());
        }

        return ids;
    }
}
