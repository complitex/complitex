package org.complitex.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.10.10 15:37
 */
public interface ILongId extends Serializable {
    @JsonIgnore
    public Long getId();
}
