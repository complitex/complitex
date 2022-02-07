
package ru.complitex.pspoffice.importing.legacy.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.wicket.util.string.Strings;
import ru.complitex.address.strategy.building.BuildingStrategy;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.pspoffice.importing.legacy.entity.BuildingCorrection;
import ru.complitex.pspoffice.importing.legacy.service.exception.TooManyResultsException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Artem
 */
@Stateless
public class BuildingCorrectionBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = BuildingCorrectionBean.class.getName();

    @EJB
    private BuildingStrategy buildingStrategy;

    @EJB
    private StringLocaleBean stringLocaleBean;

    private long SYSTEM_LOCALE_ID;

    @PostConstruct
    private void init() {
        SYSTEM_LOCALE_ID = stringLocaleBean.getSystemStringLocale().getId();
    }

    public void insert(BuildingCorrection buildingCorrection) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insert", buildingCorrection);
    }

    public boolean exists(String idjek) {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".exists", idjek) > 0;
    }

    public void cleanData(Set<String> jekIds) {
        sqlSession().delete(MAPPING_NAMESPACE + ".delete", ImmutableMap.of("value", jekIds));
    }

    public void update(BuildingCorrection buildingCorrection) {
        sqlSession().update(MAPPING_NAMESPACE + ".update", buildingCorrection);
    }

    public void clearProcessingStatus(Set<String> jekIds) {
        sqlSession().update(MAPPING_NAMESPACE + ".clearProcessingStatus", ImmutableMap.of("value", jekIds));
    }

    public int countForProcessing(Set<String> jekIds) {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".countForProcessingForJeks",
                ImmutableMap.of("value", jekIds));
    }

    public int countForProcessing(String idjek) {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".countForProcessing", idjek);
    }

    public List<BuildingCorrection> findForProcessing(String idjek, int size) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findForProcessing",
                ImmutableMap.of("idjek", idjek, "size", size));
    }

    public Long findSystemBuilding(long streetId, String dom, String korpus) throws TooManyResultsException {
        if (Strings.isEmpty(korpus)) {
            korpus = null;
        }
        Map<String, Object> params = Maps.newHashMap(ImmutableMap.<String, Object>of(
//                "buildingBuildingAddressAT", BuildingStrategy.BUILDING_ADDRESS,
                "buildingAddressNumberAT", BuildingStrategy.NUMBER,
                "buildingAddressCorpAT", BuildingStrategy.CORP,
                "dom", dom,
                "parentEntityId", BuildingStrategy.PARENT_ENTITY_ID));
        params.put("parentId", streetId);
        params.put("korpus", korpus);

        List<Long> ids = sqlSession().selectList(MAPPING_NAMESPACE + ".findSystemBuilding", params);
        if (ids.isEmpty()) {
            return null;
        } else if (ids.size() == 1) {
            return ids.get(0);
        } else {
            throw new TooManyResultsException();
        }
    }

    public BuildingCorrection findById(String id, Set<String> jekIds) throws TooManyResultsException {
        List<BuildingCorrection> corrections = sqlSession().selectList(MAPPING_NAMESPACE + ".findById",
                ImmutableMap.of("id", id, "jekIds", jekIds));
        if (corrections.isEmpty()) {
            return null;
        } else if (corrections.size() == 1) {
            return corrections.get(0);
        } else {
            throw new TooManyResultsException();
        }
    }

    public DomainObject newBuilding(long streetId, String buildingNumber, String corp, long jekId) {
        DomainObject b = buildingStrategy.newInstance();

        b.setParentEntityId(BuildingStrategy.PARENT_ENTITY_ID);
        b.setParentId(streetId);

        Utils.setValue(b.getAttribute(BuildingStrategy.NUMBER), SYSTEM_LOCALE_ID, buildingNumber);
        Utils.setValue(b.getAttribute(BuildingStrategy.CORP), SYSTEM_LOCALE_ID, corp);

        b.setSubjectIds(Sets.newHashSet(jekId));

        return b;
    }
}
