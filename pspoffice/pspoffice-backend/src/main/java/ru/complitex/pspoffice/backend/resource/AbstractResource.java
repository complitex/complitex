package ru.complitex.pspoffice.backend.resource;

import org.complitex.common.entity.DomainObject;
import ru.complitex.pspoffice.api.model.Name;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 *         25.05.2017 17:28
 */
abstract class AbstractResource {
    List<Name> getNames(DomainObject domainObject, Long attributeTypeId){
        if (domainObject.getAttribute(attributeTypeId) == null ||
                domainObject.getAttribute(attributeTypeId).getStringValues().isEmpty()){
            return null;
        }

        return domainObject.getAttribute(attributeTypeId).getStringValues().stream()
                .filter(s -> s.getValue() != null)
                .map(s -> new Name(s.getLocaleId(), s.getValue()))
                .collect(Collectors.toList());
    }
}
