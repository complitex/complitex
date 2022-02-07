package ru.complitex.common.web.component.datatable;

import ru.complitex.common.entity.FilterWrapper;

import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly Ivanov
 *         Date: 001 01.07.14 19:57
 */
public interface IFilterBean<T extends Serializable> {
    List<T> getList(FilterWrapper<T> filterWrapper);

    Long getCount(FilterWrapper<T> filterWrapper);
}
