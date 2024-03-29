package ru.complitex.common.web;

import ru.complitex.common.entity.Preference;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.11.10 17:08
 */
public interface ISessionStorage {
    public List<Preference> load();

    public void save(Preference preference);

    public Long getUserId();
}
