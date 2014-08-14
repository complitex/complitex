/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.address.street;

import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.address.strategy.building_address.BuildingAddressStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.service.SessionBean;

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
    public List<DomainObject> find(DomainObjectExample example) {
        if (example.getId() != null && example.getId() <= 0) {
            return Collections.emptyList();
        }

        example.setTable(getEntityTable());
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
    public int count(DomainObjectExample example) {
        if (example.getId() != null && example.getId() <= 0) {
            return 0;
        }

        example.setTable(getEntityTable());
        prepareExampleForPermissionCheck(example);
        prepareExampleForBuildingCheck(example);

        return (Integer) sqlSession().selectOne(PSP_STREET_NAMESPACE + "." + COUNT_OPERATION, example);
    }

    private void prepareExampleForBuildingCheck(DomainObjectExample example) {
        if (!example.isAdmin()) {
            example.addAdditionalParam("building_address_permission_string",
                    sessionBean.getPermissionString(buildingAddressStrategy.getEntityTable()));
        }
    }
}
