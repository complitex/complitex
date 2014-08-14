/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.service;

import static com.google.common.collect.ImmutableMap.*;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.pspoffice.person.strategy.entity.PersonName;
import org.complitex.pspoffice.person.strategy.entity.PersonName.PersonNameType;

/**
 *
 * @author Artem
 */
@Stateless
public class PersonNameBean extends AbstractBean {

    private final static String MAPPING_NAMESPACE = PersonNameBean.class.getName();
    @EJB
    private LocaleBean localeBean;

    public PersonName findById(PersonNameType personNameType, Long id) {
        if (id == null) {
            return null;
        }
        PersonName personName = (PersonName) sqlSession().selectOne(MAPPING_NAMESPACE + ".findById", of("personNameType",
                personNameType.name().toLowerCase(), "id", id));
        personName.setPersonNameType(personNameType);
        return personName;
    }

    public List<PersonName> find(PersonNameType personNameType, String filter, Locale locale, int size) {
        Long localeId = localeBean.convert(locale).getId();
        List<PersonName> personNames = sqlSession().selectList(MAPPING_NAMESPACE + ".find", of("personNameType", personNameType.name().toLowerCase(),
                "filter", filter, "localeId", localeId, "size", size));
        for (PersonName personName : personNames) {
            personName.setPersonNameType(personNameType);
        }
        return personNames;
    }

    @Transactional
    public PersonName findOrSave(PersonNameType personNameType, String name, Locale locale, boolean createIfNotExist) {
        if (Strings.isEmpty(name)) {
            throw new IllegalArgumentException("Name must be not null");
        }
        Long localeId = localeBean.convert(locale).getId();
        PersonName personName = (PersonName) sqlSession().selectOne(MAPPING_NAMESPACE + ".findByName",
                of("personNameType", personNameType.name().toLowerCase(), "name", name, "localeId", localeId));
        if (personName != null) {
            personName.setPersonNameType(personNameType);
        } else {
            if (createIfNotExist) {
                personName = save(personNameType, name, locale);
            }
        }
        return personName;
    }

    public PersonName saveIfNotExists(PersonNameType personNameType, String name, long localeId) {
        return findOrSave(personNameType, name, localeBean.getLocale(localeId), true);
    }

    @Transactional
    public PersonName save(PersonNameType personNameType, String name, Locale locale) {
        PersonName personName = new PersonName();
        personName.setName(normalizeName(name));
        personName.setLocaleId(localeBean.convert(locale).getId());
        sqlSession().insert(MAPPING_NAMESPACE + ".save", of("personNameType", personNameType.name().toLowerCase(),
                "personName", personName));
        return personName;
    }

    public static String normalizeName(String name) {
        if (Strings.isEmpty(name) || name.indexOf('-') > -1) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
