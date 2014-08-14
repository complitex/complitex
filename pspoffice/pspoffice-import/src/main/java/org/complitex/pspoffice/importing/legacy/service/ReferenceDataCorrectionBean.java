/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.service;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.AttributeExample;
import org.complitex.dictionary.entity.example.ComparisonType;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.pspoffice.importing.legacy.entity.ReferenceDataCorrection;
import org.complitex.pspoffice.importing.legacy.service.exception.TooManyResultsException;

/**
 *
 * @author Artem
 */
@Stateless
public class ReferenceDataCorrectionBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = ReferenceDataCorrectionBean.class.getName();
    /**
     * Owner relationships consts
     */
    public static final long DAUGHTER = 3;
    public static final long SON = 4;
    /**
     * Registration types consts
     */
    public static final long PERMANENT = 1;
    /**
     * Document types consts
     */
    public static final long PASSPORT = 7;
    public static final long BIRTH_CERTIFICATE = 10;
    /**
     * Owner type consts
     */
    public static final long OWNER_TYPE = 1;
    @EJB
    private StrategyFactory strategyFactory;

    public void insert(ReferenceDataCorrection referenceDataCorrection) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insert", referenceDataCorrection);
    }

    public void update(ReferenceDataCorrection referenceDataCorrection) {
        sqlSession().update(MAPPING_NAMESPACE + ".update", referenceDataCorrection);
    }

    public boolean exists(String entity, String idjek) {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".exists",
                ImmutableMap.of("entity", entity, "idjek", idjek)) > 0;
    }

    public void cleanData(String entity, Set<String> jekIds) {
        sqlSession().delete(MAPPING_NAMESPACE + ".delete", ImmutableMap.of("entity", entity, "jekIds", jekIds));
    }

    public int countForProcessing(String entity, Set<String> jekIds) {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".countForProcessingForJeks",
                ImmutableMap.of("entity", entity, "jekIds", jekIds));
    }

    public int countForProcessing(String entity, String idjek) {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".countForProcessing",
                ImmutableMap.of("entity", entity, "idjek", idjek));
    }

    public List<ReferenceDataCorrection> findForProcessing(String entity, String idjek, int size) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findForProcessing",
                ImmutableMap.of("entity", entity, "idjek", idjek, "size", size));
    }

    public void clearProcessingStatus(String entity, Set<String> jekIds) {
        sqlSession().update(MAPPING_NAMESPACE + ".clearProcessingStatus",
                ImmutableMap.of("entity", entity, "jekIds", jekIds));
    }

    public Long findSystemObject(String entity, String nkod) throws TooManyResultsException {
        IStrategy strategy = strategyFactory.getStrategy(entity);
        long nameAttributeTypeId = strategy.getEntity().getId();

        DomainObjectExample example = new DomainObjectExample();
        example.setAdmin(true);
        AttributeExample nameExample = new AttributeExample(nameAttributeTypeId);
        nameExample.setValue(nkod);
        example.addAttributeExample(nameExample);
        example.setComparisonType(ComparisonType.EQUALITY.name());

        List<? extends DomainObject> objects = strategy.find(example);
        if (objects.size() == 1) {
            return objects.get(0).getId();
        } else if (objects.isEmpty()) {
            return null;
        } else {
            throw new TooManyResultsException();
        }
    }

    public static class OwnerRelationshipsNotResolved extends Exception {

        private final boolean daughterResolved;
        private final boolean sonResolved;

        public OwnerRelationshipsNotResolved(boolean daughterResolved, boolean sonResolved) {
            this.daughterResolved = daughterResolved;
            this.sonResolved = sonResolved;
        }

        public boolean isDaughterResolved() {
            return daughterResolved;
        }

        public boolean isSonResolved() {
            return sonResolved;
        }
    }

    public void checkReservedOwnerRelationships(Set<String> jekIds) throws OwnerRelationshipsNotResolved {
        final String entity = "owner_relationship";
        final boolean daughterResolved = isReservedObjectResolved(entity, DAUGHTER, jekIds);
        final boolean sonResolved = isReservedObjectResolved(entity, SON, jekIds);
        if (!daughterResolved || !sonResolved) {
            throw new OwnerRelationshipsNotResolved(daughterResolved, sonResolved);
        }
    }

    private boolean isReservedObjectResolved(String entity, long objectId, Set<String> jekIds) {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".isReservedObjectResolved",
                ImmutableMap.of("entity", entity, "objectId", objectId, "jekIds", jekIds)) == 1;
    }

    public static class RegistrationTypeNotResolved extends Exception {
    }

    public void checkReservedRegistrationTypes(Set<String> jekIds) throws RegistrationTypeNotResolved {
        if (!isReservedObjectResolved("registration_type", PERMANENT, jekIds)) {
            throw new RegistrationTypeNotResolved();
        }
    }

    public static class DocumentTypesNotResolved extends Exception {

        private final boolean passportResolved;
        private final boolean birthCertificateResolved;

        public DocumentTypesNotResolved(boolean passportResolved, boolean birthCertificateResolved) {
            this.passportResolved = passportResolved;
            this.birthCertificateResolved = birthCertificateResolved;
        }

        public boolean isBirthCertificateResolved() {
            return birthCertificateResolved;
        }

        public boolean isPassportResolved() {
            return passportResolved;
        }
    }

    public void checkReservedDocumentTypes(Set<String> jekIds) throws DocumentTypesNotResolved {
        final String entity = "document_type";
        final boolean passportResolved = isReservedObjectResolved(entity, PASSPORT, jekIds);
        final boolean birthCertificateResolved = isReservedObjectResolved(entity, BIRTH_CERTIFICATE, jekIds);
        if (!passportResolved || !birthCertificateResolved) {
            throw new DocumentTypesNotResolved(passportResolved, birthCertificateResolved);
        }
    }

    public String getReservedOwnerType(Set<String> jekIds) {
        List<String> ownerTypes = sqlSession().selectList(MAPPING_NAMESPACE + ".getReservedOwnerType",
                ImmutableMap.of("OWNER_TYPE", OWNER_TYPE, "jekIds", jekIds));
        return ownerTypes.size() == 1 ? ownerTypes.get(0) : null;
    }

    public ReferenceDataCorrection getById(String entity, String id, String idjek) {
        if (!Strings.isEmpty(id)) {
            return (ReferenceDataCorrection) sqlSession().selectOne(MAPPING_NAMESPACE + ".getByIdAndJek",
                    ImmutableMap.of("entity", entity, "id", id, "idjek", idjek));
        } else {
            return null;
        }
    }
}
