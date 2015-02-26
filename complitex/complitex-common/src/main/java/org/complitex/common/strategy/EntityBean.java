package org.complitex.common.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.complitex.common.entity.AttributeType;
import org.complitex.common.entity.AttributeValueType;
import org.complitex.common.entity.Entity;
import org.complitex.common.service.AbstractBean;
import org.complitex.common.util.StringCultures;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class EntityBean extends AbstractBean {
    private static final String NS = EntityBean.class.getName();

    @EJB
    private StringCultureBean stringBean;

    @EJB
    private StrategyFactory strategyFactory;

    private Map<String, Entity> entityMap = new ConcurrentHashMap<>();
    private Map<Long, AttributeType> attributeTypeMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        List<String> entities = getEntityNames();

        for (String entityName : entities){
            Entity entity = loadFromDb(null, entityName);
            entityMap.put(entityName, entity);

            for (AttributeType attributeType : entity.getAttributeTypes()){
                attributeTypeMap.put(attributeType.getId(), attributeType);
            }
        }
    }

    public Entity getEntity(String dataSource, String entity) {
        return loadFromDb(dataSource, entity);
    }

    public Entity getEntity(String entityName) {
        return entityMap.get(entityName);
    }

    public List<AttributeType> getAttributeTypes(List<Long> attributeTypeIds){
        List<AttributeType> attributeTypes = new ArrayList<>(attributeTypeIds.size());

        for (Long attributeTypeId : attributeTypeIds){
            attributeTypes.add(attributeTypeMap.get(attributeTypeId));
        }

        return attributeTypes;
    }

    public AttributeType getAttributeType(Long attributeTypeId){
        return attributeTypeMap.get(attributeTypeId);
    }

    private Entity loadFromDb(String dataSource, String entity) {
        return (Entity) (dataSource == null ? sqlSession() : sqlSession(dataSource))
                .selectOne(NS + ".load", ImmutableMap.of("entity", entity));
    }

    private void updateCache(String entity) {
        entityMap.put(entity, loadFromDb(null, entity));
    }

    public String getAttributeLabel(String entityName, long attributeTypeId, Locale locale) {
        return getEntity(entityName).getAttributeType(attributeTypeId).getAttributeName(locale);
    }

    public AttributeType newAttributeType() {
        AttributeType attributeType = new AttributeType();

        attributeType.setAttributeNames(StringCultures.newStringCultures());
        attributeType.setAttributeValueTypes(new ArrayList<AttributeValueType>());

        return attributeType;
    }

    public void save(Entity oldEntity, Entity newEntity) {
        Date updateDate = new Date();

        boolean changed = false;

        //attributes
        Set<Long> toDeleteAttributeIds = Sets.newHashSet();

        for (AttributeType oldAttributeType : oldEntity.getAttributeTypes()) {
            boolean removed = true;
            for (AttributeType newAttributeType : newEntity.getAttributeTypes()) {
                if (oldAttributeType.getId().equals(newAttributeType.getId())) {
                    removed = false;
                    break;
                }
            }
            if (removed) {
                changed = true;
                toDeleteAttributeIds.add(oldAttributeType.getId());
            }
        }
        removeAttributeTypes(oldEntity.getEntityName(), toDeleteAttributeIds, updateDate);

        for (AttributeType attributeType : newEntity.getAttributeTypes()) {
            if (attributeType.getId() == null) {
                changed = true;
                insertAttributeType(attributeType, newEntity.getId(), updateDate);
            }
        }

        if (changed) {
            updateCache(oldEntity.getEntityName());
        }
    }

    private void insertAttributeType(AttributeType attributeType, long entityId, Date startDate) {
        attributeType.setStartDate(startDate);
        attributeType.setEntityId(entityId);

        Long stringId = stringBean.save(attributeType.getAttributeNames(), null);

        attributeType.setAttributeNameId(stringId);

        sqlSession().insert(NS + ".insertAttributeType", attributeType);

        AttributeValueType valueType = attributeType.getAttributeValueTypes().get(0);
        valueType.setAttributeTypeId(attributeType.getId());

        sqlSession().insert(NS + ".insertValueType", valueType);
    }

    private void removeAttributeTypes(String entityName, Collection<Long> attributeTypeIds, Date endDate) {
        if (attributeTypeIds != null && !attributeTypeIds.isEmpty()) {
            Map<String, Object> params = ImmutableMap.<String, Object>builder().
                    put("endDate", endDate).
                    put("attributeTypeIds", attributeTypeIds).
                    build();
            sqlSession().update(NS + ".removeAttributeTypes", params);

            strategyFactory.getStrategy(entityName).archiveAttributes(attributeTypeIds, endDate);
        }
    }

    public List<String> getEntityNames() {
        return sqlSession().selectList(NS + ".allEntities");
    }
}
