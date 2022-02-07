package ru.complitex.correction.service;

import com.google.common.collect.ImmutableMap;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.IEntityName;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.common.service.ModuleBean;
import ru.complitex.common.util.DateUtil;
import ru.complitex.correction.entity.Correction;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author inheaven on 002 02.02.15 18:11
 */
@Stateless
public class CorrectionBean extends AbstractBean{
    public static final String CORRECTION_NS = CorrectionBean.class.getName();

    @EJB
    private ModuleBean moduleBean;

    public void save(Correction correction) {
        if (correction.getId() == null){
            if (correction.getStartDate() == null) {
                correction.setStartDate(DateUtil.getCurrentDate());
            }
            if (correction.getEndDate() == null) {
                correction.setEndDate(DateUtil.MAX_END_DATE);
            }
            if (correction.getModuleId() == null) {
                correction.setModuleId(moduleBean.getModuleId());
            }

            sqlSession().insert(CORRECTION_NS + ".insertCorrection", correction);
        }else {
            sqlSession().update(CORRECTION_NS + ".updateCorrection", correction);
        }
    }

    public void delete(Correction correction){
        sqlSession().delete(CORRECTION_NS + ".deleteCorrection", correction);
    }

    public Correction getCorrection(String entityName, Long id){
        return sqlSession().selectOne(CORRECTION_NS + ".selectCorrection", new Correction(entityName, id));
    }

    public List<Correction> getCorrections(FilterWrapper<Correction> filterWrapper){
        return sqlSession().selectList(CORRECTION_NS + ".selectCorrections", filterWrapper);
    }

    public Long getCorrectionsCount(FilterWrapper<Correction> filterWrapper){
        return sqlSession().selectOne(CORRECTION_NS + ".selectCorrectionsCount", filterWrapper);
    }

    //todo filter
    public List<Long> getObjectIds(IEntityName entityName, String correction, Long entityAttributeId){
        return sqlSession().selectList(CORRECTION_NS + ".selectObjectIds",
                ImmutableMap.of("entityName", entityName.getEntityName(), "correction", correction,
                        "entityAttributeId", entityAttributeId));
    }

    public List<Long> getObjectIds(IEntityName entityName, String correction, Long entityAttributeId1, Long entityAttributeId2){
        Set<Long> set = new HashSet<>();

        set.addAll(getObjectIds(entityName, correction, entityAttributeId1));
        set.addAll(getObjectIds(entityName, correction, entityAttributeId2));

        return new ArrayList<>(set);
    }

    public List<Correction> getCorrections(IEntityName entityName,  String correction, Long organizationId, Long userOrganizationId){
        return getCorrections(FilterWrapper.of(new Correction(entityName.getEntityName(), null, null, null,
                correction, organizationId, userOrganizationId)));
    }

    public List<Correction> getCorrections(IEntityName entityName, Long parentId, Long additionalParentId, String correction,
                                           Long organizationId, Long userOrganizationId){
        return getCorrections(FilterWrapper.of(new Correction(entityName.getEntityName(), parentId, additionalParentId, null, null,
                correction, null, organizationId, userOrganizationId)));
    }

    public List<Correction> getCorrections(IEntityName entityName, Long parentId, String correction, String additionalCorrection,
                                           Long organizationId, Long userOrganizationId){
        return getCorrections(FilterWrapper.of(new Correction(entityName.getEntityName(), parentId, null, null, null,
                correction, additionalCorrection, organizationId, userOrganizationId)));
    }

    public List<Correction> getCorrections(IEntityName entityName, Long parentId, String correction,
                                           Long organizationId, Long userOrganizationId){
        return getCorrections(FilterWrapper.of(new Correction(entityName.getEntityName(), parentId, null, null, null,
                correction, null, organizationId, userOrganizationId)));
    }

    public List<Correction> getCorrectionsByParentId(IEntityName entityName, Long parentId, Long organizationId, Long userOrganizationId){
        return getCorrections(FilterWrapper.of(new Correction(entityName.getEntityName(), parentId, null, null,
                null, null, null, organizationId, userOrganizationId)));
    }

    public List<Correction> getCorrectionsByExternalId(IEntityName entityName, Long externalId, Long organizationId, Long userOrganizationId){
        return getCorrections(FilterWrapper.of(new Correction(entityName.getEntityName(), null, null, externalId,
                null, null, null, organizationId, userOrganizationId)));
    }

    public List<Correction> getCorrectionsByObjectId(IEntityName entityName, Long objectId, Long organizationId, Long userOrganizationId){
        return getCorrections(FilterWrapper.of(new Correction(entityName.getEntityName(), null, null, null,
                objectId, null, null, organizationId, userOrganizationId)));
    }

    public String getCorrectionByExternalId(IEntityName entityName, Long externalId, Long organizationId, Long userOrganizationId){
        List<Correction> corrections =  getCorrections(FilterWrapper.of(new Correction(entityName.getEntityName(), null, null, externalId,
                null, null, null, organizationId, userOrganizationId)));

        if (!corrections.isEmpty()){
            return corrections.get(0).getCorrection();
        }

        return null;
    }


}
