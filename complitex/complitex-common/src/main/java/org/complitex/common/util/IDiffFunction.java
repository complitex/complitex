package org.complitex.common.util;

/**
 * @author inheaven on 017 17.02.15 19:46
 */
public interface IDiffFunction<T> {
    public void onSave(T object);
    public void onDelete(T object);
}
