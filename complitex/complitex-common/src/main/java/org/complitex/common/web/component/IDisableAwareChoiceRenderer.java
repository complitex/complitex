package org.complitex.common.web.component;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 *
 * @author Artem
 */
public interface IDisableAwareChoiceRenderer<T> extends IChoiceRenderer<T> {

    boolean isDisabled(T object);
}
