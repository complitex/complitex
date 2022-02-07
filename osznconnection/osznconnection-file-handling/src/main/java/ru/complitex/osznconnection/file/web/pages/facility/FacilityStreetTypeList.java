package ru.complitex.osznconnection.file.web.pages.facility;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.StringUtil;
import ru.complitex.osznconnection.file.entity.privilege.FacilityStreetType;
import ru.complitex.osznconnection.file.entity.privilege.FacilityStreetTypeDBF;
import ru.complitex.osznconnection.file.service.privilege.FacilityReferenceBookBean;
import ru.complitex.osznconnection.file.web.AuthorizedListPage;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Anatoly Ivanov java@inheaven.ru
 * Date: 03.04.13 11:04
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class FacilityStreetTypeList extends AuthorizedListPage<FacilityStreetType> {
    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    public FacilityStreetTypeList(PageParameters pageParameters) {
        super(pageParameters, "dbfFields.", FacilityStreetTypeFileList.class);
    }

    @Override
    protected FacilityStreetType newFilterObject(PageParameters pageParameters) {
        return new FacilityStreetType(pageParameters.get("request_file_id").toLongObject());
    }

    @Override
    protected List<FacilityStreetType> getList(FilterWrapper<FacilityStreetType> filterWrapper) {
        return facilityReferenceBookBean.getFacilityStreetTypes(filterWrapper);
    }

    @Override
    protected Long getCount(FilterWrapper<FacilityStreetType> filterWrapper) {
        return facilityReferenceBookBean.getFacilityStreetTypesCount(filterWrapper);
    }

    @Override
    protected List<String> getProperties() {
        return StringUtil.asList(FacilityStreetTypeDBF.class);
    }
}
