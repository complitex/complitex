package ru.complitex.pspoffice.backend.adapter;

import org.complitex.common.entity.Entity;
import org.complitex.common.entity.StringValue;
import ru.complitex.pspoffice.api.model.EntityAttributeModel;
import ru.complitex.pspoffice.api.model.EntityModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 22.09.2017 16:52
 */
public class EntityAdapter {
    public static EntityModel adapt(Entity entity){
        EntityModel entityModel = new EntityModel();

        entityModel.setId(entity.getId());
        entityModel.setEntity(entity.getEntity());

        entityModel.setNames(entity.getNames().stream().collect(Collectors.toMap(n -> n.getLocaleId().toString(),
                StringValue::getValue)));

        entityModel.setAttributes(entity.getAttributes().stream()
                .map(a -> {
                    EntityAttributeModel m = new EntityAttributeModel();
                    m.setId(a.getId());
                    m.setStartDate(a.getStartDate());
                    m.setEndDate(a.getEndDate());
                    m.setRequired(a.isRequired());
                    m.setSystem(a.isSystem());
                    m.setValueTypeId(a.getValueType().getId());
                    m.setReferenceId(a.getReferenceId());

                    m.setNames(a.getNames().stream().collect(Collectors.toMap(n -> n.getLocaleId().toString(),
                                    StringValue::getValue)));

                    return m;
                }).collect(Collectors.toList()));
        return entityModel;
    }

    public static List<EntityModel> adapt(List<Entity> entities){
        return entities.stream().map(EntityAdapter::adapt).collect(Collectors.toList());
    }

    public static Entity adapt(EntityModel entityModel){
        Entity entity = new Entity();

        //todo adapt

        return entity;
    }
}
