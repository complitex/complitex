package org.complitex.common.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.complitex.common.entity.Entity;
import org.complitex.common.entity.EntityAttribute;
import org.complitex.common.service.AbstractBean;
import org.complitex.common.util.StringValueUtil;

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
    private StringValueBean stringBean;

    @EJB
    private StrategyFactory strategyFactory;

    private Map<String, Entity> entityMap = new ConcurrentHashMap<>();
    private Map<Long, EntityAttribute> attributeTypeMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        List<String> entities = getEntityNames();

        for (String entityName : entities){
            Entity entity = loadFromDb(null, entityName);
            entityMap.put(entityName, entity);

            for (EntityAttribute entityAttribute : entity.getAttributes()){
                attributeTypeMap.put(entityAttribute.getId(), entityAttribute);
            }
        }
    }

    public Entity getEntity(String dataSource, String entity) {
        return dataSource != null ? loadFromDb(dataSource, entity): getEntity(entity);
    }

    public Entity getEntity(String entityName) {
        return entityMap.get(entityName);
    }

    public List<EntityAttribute> getAttributeTypes(List<Long> entityAttributeIds){
        List<EntityAttribute> entityAttributes = new ArrayList<>(entityAttributeIds.size());

        for (Long entityAttributeId : entityAttributeIds){
            entityAttributes.add(attributeTypeMap.get(entityAttributeId));
        }

        return entityAttributes;
    }

    public EntityAttribute getAttributeType(Long entityAttributeId){
        return attributeTypeMap.get(entityAttributeId);
    }

    private Entity loadFromDb(String dataSource, String entity) {
        return (Entity) (dataSource == null ? sqlSession() : sqlSession(dataSource))
                .selectOne(NS + ".load", ImmutableMap.of("entity", entity));
    }

    private void updateCache(String entity) {
        entityMap.put(entity, loadFromDb(null, entity));
    }

    public String getAttributeLabel(String entityName, long entityAttributeId, Locale locale) {
        return getEntity(entityName).getAttribute(entityAttributeId).getName(locale);
    }

    public EntityAttribute newAttributeType() {
        EntityAttribute entityAttribute = new EntityAttribute();

        entityAttribute.setNames(StringValueUtil.newStringValues());

        return entityAttribute;
    }

    public void save(Entity oldEntity, Entity newEntity) {
        Date updateDate = new Date();

        boolean changed = false;

        //attributes
        Set<Long> toDeleteAttributeIds = Sets.newHashSet();

        for (EntityAttribute oldEntityAttribute : oldEntity.getAttributes()) {
            boolean removed = true;
            for (EntityAttribute newEntityAttribute : newEntity.getAttributes()) {
                if (oldEntityAttribute.getId().equals(newEntityAttribute.getId())) {
                    removed = false;
                    break;
                }
            }
            if (removed) {
                changed = true;
                toDeleteAttributeIds.add(oldEntityAttribute.getId());
            }
        }
        removeAttributeTypes(oldEntity.getEntity(), toDeleteAttributeIds, updateDate);

        for (EntityAttribute entityAttribute : newEntity.getAttributes()) {
            if (entityAttribute.getId() == null) {
                changed = true;
                insertAttributeType(entityAttribute, newEntity.getId(), updateDate);
            }
        }

        if (changed) {
            updateCache(oldEntity.getEntity());
        }
    }

    private void insertAttributeType(EntityAttribute entityAttribute, long entityId, Date startDate) {
        entityAttribute.setStartDate(startDate);
        entityAttribute.setEntityId(entityId);

        Long stringId = stringBean.save(entityAttribute.getNames(), null);

        entityAttribute.setNameId(stringId);

        sqlSession().insert(NS + ".insertAttributeType", entityAttribute);
    }

    private void removeAttributeTypes(String entityName, Collection<Long> entityAttributeIds, Date endDate) {
        if (entityAttributeIds != null && !entityAttributeIds.isEmpty()) {
            Map<String, Object> params = ImmutableMap.<String, Object>builder().
                    put("endDate", endDate).
                    put("attributeTypeIds", entityAttributeIds).
                    build();
            sqlSession().update(NS + ".removeAttributeTypes", params);

            strategyFactory.getStrategy(entityName).archiveAttributes(entityAttributeIds, endDate);
        }
    }

    public List<String> getEntityNames() {
        return sqlSession().selectList(NS + ".allEntities");
    }
}
