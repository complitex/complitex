package org.complitex.common.web;

import org.apache.wicket.Component;

/**
 *
 * @author Artem
 */
public interface IWebComponentResolver {

    Class<? extends Component> getComponentClass(String componentName);
}
