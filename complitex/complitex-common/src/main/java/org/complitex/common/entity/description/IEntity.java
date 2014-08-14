package org.complitex.common.entity.description;

import org.complitex.common.entity.ILongId;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.07.13 17:22
 */
public interface IEntity extends ILongId{
    String getEntityTable();
}
