/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.address.street;

import org.complitex.address.strategy.building_address.BuildingAddressStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.example.DomainObjectExample;
import org.complitex.common.service.SessionBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Collections;
import java.util.List;

/**
 * Street strategy delegate.
 * <p>Implements custom logic for "found" and "count" street strategy operations.
 * Custom requirements: for not admins filters out the streets that have no one visible building.</p>
 * 
 * @see StreetStrategyInterceptor
 * @see StreetStrategy
 *
 * @author Artem
 */
@Stateless(name = "PspofficeStreetStrategy")
public class StreetStrategyDelegate extends StreetStrategy {

    private static final String PSP_STREET_NAMESPACE = StreetStrategyDelegate.class.getPackage().getName() + ".Street";
    @EJB
    private BuildingAddressStrategy buildingAddressStrategy;
    @EJB
    private SessionBean sessionBean;

    @Override
    public List<DomainObject> getList(DomainObjectExample example) {
        if (example.getId() != null && example.getId() <= 0) {
            return Collections.emptyList();
        }

        example.setEntityTable(getEntityTable());
        prepareExampleForPermissionCheck(example);
        prepareExampleForBuildingCheck(example);

        List<DomainObject> objects = sqlSession().selectList(PSP_STREET_NAMESPACE + "." + FIND_OPERATION, example);
        for (DomainObject object : objects) {
            loadAttributes(object);
            //load subject ids
            object.setSubjectIds(loadSubjects(object.getPermissionId()));
        }
        return objects;

    }

    @Override
    public Long getCount(DomainObjectExample example) {
        if (example.getId() != null && example.getId() <= 0) {
            return 0L;
        }

        example.setEntityTable(getEntityTable());
        prepareExampleForPermissionCheck(example);
        prepareExampleForBuildingCheck(example);

        return sqlSession().selectOne(PSP_STREET_NAMESPACE + "." + COUNT_OPERATION, example);
    }

    private void prepareExampleForBuildingCheck(DomainObjectExample example) {
        if (!example.isAdmin()) {
            example.addAdditionalParam("building_address_permission_string",
                    sessionBean.getPermissionString(buildingAddressStrategy.getEntityTable()));
        }
    }
}
