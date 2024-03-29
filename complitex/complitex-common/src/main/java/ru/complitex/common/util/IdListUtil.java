package ru.complitex.common.util;

import ru.complitex.common.entity.ILongId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.06.12 17:45
 */
public class IdListUtil {
    /**
     * @param list1 List
     * @param list2 List
     * @param <T> ILongId
     * @return Элементы из list1, которые не содержатся в list2 (list1 - list2)
     */
    public static <T extends ILongId> List<T> getDiff(List<T> list1, List<T> list2){
        List<T> list = new ArrayList<>();

        for (T o1 : list1){
            boolean found = false;

            for (T o2 : list2){
                if (o1.getId() != null && o1.getId().equals(o2.getId())){
                    found = true;

                    break;
                }
            }

            if (!found){
                list.add(o1);
            }
        }

        return list;
    }

    public static <T extends ILongId> List<Long> getIdDiff(List<Long> list1, List<T> list2){
        List<Long> list = new ArrayList<>();

        for (Long o1 : list1){
            boolean found = false;

            for (T o2 : list2){
                if (o1 != null && o1.equals(o2.getId())){
                    found = true;

                    break;
                }
            }

            if (!found){
                list.add(o1);
            }
        }

        return list;
    }

    public static <T extends ILongId> void iterateDiff(List<T> newList, List<T> oldList, IDiffFunction<T> function){
        //delete
        for (T oldObject : oldList){
            boolean has = false;

            for (T newObject : newList){
                if (newObject.getId().equals(oldObject.getId())){
                    has = true;
                    break;
                }
            }

            if (!has){
                function.onDelete(oldObject);
            }
        }

        //save
        for (T newObject : newList){
            function.onSave(newObject);
        }
    }
}
