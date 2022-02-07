package ru.complitex.osznconnection.file.web.pages.facility;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.osznconnection.file.entity.privilege.FacilityStreet;
import ru.complitex.osznconnection.file.entity.privilege.FacilityStreetDBF;
import ru.complitex.osznconnection.file.service.privilege.FacilityReferenceBookBean;
import ru.complitex.osznconnection.file.web.AuthorizedListPage;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.List;

import static ru.complitex.common.util.StringUtil.asList;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.04.13 16:29
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class FacilityStreetList extends AuthorizedListPage<FacilityStreet> {
    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    public FacilityStreetList(PageParameters pageParameters) {
        super(pageParameters, "dbfFields.", FacilityStreetFileList.class);
    }

    @Override
    protected FacilityStreet newFilterObject(PageParameters pageParameters) {
        return new FacilityStreet(pageParameters.get("request_file_id").toLongObject());
    }

    @Override
    protected List<FacilityStreet> getList(FilterWrapper<FacilityStreet> filterWrapper) {
        return facilityReferenceBookBean.getFacilityStreets(filterWrapper);
    }

    @Override
    protected Long getCount(FilterWrapper<FacilityStreet> filterWrapper) {
        return facilityReferenceBookBean.getFacilityStreetsCount(filterWrapper);
    }

    @Override
    protected List<String> getProperties() {
        return asList(FacilityStreetDBF.class);
    }
}
