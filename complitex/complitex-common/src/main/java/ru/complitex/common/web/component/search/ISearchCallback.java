package ru.complitex.common.web.component.search;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;
import java.util.Map;

public interface ISearchCallback extends Serializable{

    void found(Component component, Map<String, Long> ids, AjaxRequestTarget target);
}
