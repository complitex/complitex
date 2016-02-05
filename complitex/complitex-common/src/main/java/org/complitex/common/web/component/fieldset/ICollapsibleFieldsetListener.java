package org.complitex.common.web.component.fieldset;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public interface ICollapsibleFieldsetListener extends Serializable {

    void onExpand(AjaxRequestTarget target);
}
