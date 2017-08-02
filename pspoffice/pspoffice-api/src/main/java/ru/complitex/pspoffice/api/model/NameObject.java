package ru.complitex.pspoffice.api.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 02.08.2017 17:20
 */
public class NameObject implements Serializable{
    private Long objectId;

    private List<Name> names;
}
