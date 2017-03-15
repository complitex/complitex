/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.address.street;

import org.complitex.address.strategy.building_address.BuildingAddressStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
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

    private static final String PSP_STREET_NS = StreetStrategyDelegate.class.getPackage().getName() + ".Street";
    @EJB
    private BuildingAddressStrategy buildingAddressStrategy;
    @EJB
    private SessionBean sessionBean;

    @Override
    public List<DomainObject> getList(DomainObjectFilter example) {
        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return Collections.emptyList();
        }

        example.setEntityName(getEntityName());
        prepareExampleForPermissionCheck(example);
        prepareExampleForBuildingCheck(example);

        List<DomainObject> objects = sqlSession().selectList(PSP_STREET_NS + ".selectStreets", example);
        for (DomainObject object : objects) {
            loadAttributes(object);
            //load subject ids
            //object.setSubjectIds(loadSubjects(object.getPermissionId()));
        }
        return objects;

    }

    @Override
    public Long getCount(DomainObjectFilter example) {
        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return 0L;
        }

        example.setEntityName(getEntityName());
        prepareExampleForPermissionCheck(example);
        prepareExampleForBuildingCheck(example);

        return sqlSession().selectOne(PSP_STREET_NS + ".selectStreetCount", example);
    }

    private void prepareExampleForBuildingCheck(DomainObjectFilter example) {
        if (!example.isAdmin()) {
            example.addAdditionalParam("building_address_permission_string",
                    sessionBean.getPermissionString(buildingAddressStrategy.getEntityName()));
        }
    }
}
