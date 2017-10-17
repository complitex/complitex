package ru.complitex.pspoffice.backend.adapter;

import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.Status;
import org.complitex.common.entity.StringValue;
import ru.complitex.pspoffice.api.model.DomainAttributeModel;
import ru.complitex.pspoffice.api.model.DomainModel;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        m.setSubjectIds(domainObject.getSubjectIds());

        if (domainObject.getAttributes() != null) {
            m.setAttributes(domainObject.getAttributes().stream().map(DomainAdapter::adapt).collect(Collectors.toList()));
        }

        return m;
    }

    public static DomainAttributeModel adapt(Attribute attribute){
        DomainAttributeModel m = new DomainAttributeModel();

        m.setAttributeId(attribute.getAttributeId());
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

    public static DomainObject adapt(DomainModel domainModel){
        DomainObject o = new DomainObject();

        o.setObjectId(domainModel.getId());
        o.setParentEntityId(domainModel.getParentEntityId());
        o.setParentId(domainModel.getParentId());
        o.setExternalId(domainModel.getExternalId());
        o.setStartDate(domainModel.getStartDate());
        o.setEndDate(domainModel.getEndDate());
        o.setStatus(Stream.of(Status.values())
                .filter(s -> s.getId().equals(domainModel.getStatusId())).findAny().orElse(null));
        o.setSubjectIds(domainModel.getSubjectIds());

        if (domainModel.getAttributes() != null){
            o.setAttributes(domainModel.getAttributes().stream().map(DomainAdapter::adapt).collect(Collectors.toList()));
        }

        return o;
    }

    public static Attribute adapt(DomainAttributeModel domainAttributeModel){
        Attribute a = new Attribute();

        a.setAttributeId(domainAttributeModel.getAttributeId());
        a.setEntityAttributeId(domainAttributeModel.getEntityAttributeId());
        a.setValueId(domainAttributeModel.getValueId());
        a.setStartDate(domainAttributeModel.getStartDate());
        a.setEndDate(domainAttributeModel.getEndDate());
        a.setStatus(Stream.of(Status.values())
                .filter(s -> s.getId().equals(domainAttributeModel.getStatusId())).findAny().orElse(null));

        if (domainAttributeModel.getValues() != null){
            a.setStringValues(domainAttributeModel.getValues().entrySet().stream()
                    .map(e -> new StringValue(Long.valueOf(e.getKey()), e.getValue()))
                    .collect(Collectors.toList()));
        }

        return a;
    }
}
