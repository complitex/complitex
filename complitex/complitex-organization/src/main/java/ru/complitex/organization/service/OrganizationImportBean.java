package ru.complitex.organization.service;

import ru.complitex.common.service.AbstractBean;
import ru.complitex.organization.entity.OrganizationImport;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;


@Stateless
public class OrganizationImportBean extends AbstractBean {
    private static final String NS = OrganizationImportBean.class.getName();


    public void importOrganization(OrganizationImport importOrganization) {
        sqlSession().insert(NS + ".insertOrganizationImport", importOrganization);
    }

    public List<OrganizationImport> find(Long parentOrganizationId) {
        return sqlSession().selectList(NS + ".selectOrganizationImports", parentOrganizationId);
    }

    public OrganizationImport findById(long organizationId) {
        return sqlSession().selectOne(NS + ".selectOrganizationImport", organizationId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void delete() {
        sqlSession().delete(NS + ".deleteOrganizationImport");
    }
}
