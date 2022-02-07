package ru.complitex.common.web.component.back;

import org.apache.wicket.Component;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public abstract class BackInfo implements Serializable {

    public abstract void back(Component pageComponent);
}
