package ru.complitex.eirc.dictionary.entity;

import ru.complitex.common.entity.IFixedIdType;

/**
 * @author Pavel Sknar
 */
public enum OrganizationType implements IFixedIdType {

    USER_ORGANIZATION(1L), SERVICE_PROVIDER(2L), PAYMENT_COLLECTOR(3L);

    private DictionaryObject object = new DictionaryObject() {};

    private OrganizationType() {
    }

    private OrganizationType(Long id) {
        object.setId(id);
    }

    @Override
    public Integer getId() {
        return object.getId().intValue();
    }
}
