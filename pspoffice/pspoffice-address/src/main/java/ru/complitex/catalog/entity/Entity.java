package ru.complitex.catalog.entity;

import ru.complitex.catalog.util.Strings;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Ivanov Anatoliy
 */
public class Entity implements Serializable {
    private Long id;
    private LocalDateTime begin;
    private LocalDateTime end;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getBegin() {
        return begin;
    }

    public void setBegin(LocalDateTime begin) {
        this.begin = begin;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return Strings.toString(this);
    }
}
