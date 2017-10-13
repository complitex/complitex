package ru.complitex.pspoffice.backend.adapter;

import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.StringValue;
import ru.complitex.pspoffice.api.model.DomainAttributeModel;
import ru.complitex.pspoffice.api.model.DomainModel;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 11.10.2017 13:48
 */
public class DomainAdapter {
    public static DomainModel adapt(DomainObject domainObject){
        DomainModel m = new DomainModel();

        m.setId(domainObject.getObjectId());
        m.setParentEntityId(domainObject.getParentEntityId());
        m.setParentId(domainObject.getParentId());
        m.setExternalId(domainObject.getExternalId());
        m.setStartDate(domainObject.getStartDate());
        m.setEndDate(domainObject.getEndDate());
        m.setStatusId(domainObject.getStatus().getId());

        if (domainObject.getAttributes() != null) {
            m.setAttributes(new ArrayList<>());
            domainObject.getAttributes().forEach(a -> m.getAttributes().add(adapt(a)));
        }


        return m;
    }

    public static DomainAttributeModel adapt(Attribute attribute){
        DomainAttributeModel m = new DomainAttributeModel();

        if (attribute.getAttributeId() > 1) {
            m.setAttributeId(attribute.getAttributeId());
        }
        m.setEntityAttributeId(attribute.getEntityAttributeId());
        m.setValueId(attribute.getValueId());
        m.setStartDate(attribute.getStartDate());
        m.setEndDate(attribute.getEndDate());
        m.setStatusId(attribute.getStatus().getId());

        if (attribute.getStringValues() != null) {
            m.setValues(attribute.getStringValues().stream()
                    .filter(a -> a.getValue() != null)
                    .collect(Collectors.toMap(a -> a.getLocaleId().toString(), StringValue::getValue)));
        }

        return m;
    }
}
