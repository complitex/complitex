package ru.complitex.osznconnection.file.web.pages.facility;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.osznconnection.file.entity.privilege.FacilityTarif;
import ru.complitex.osznconnection.file.entity.privilege.FacilityTarifDBF;
import ru.complitex.osznconnection.file.service.privilege.FacilityReferenceBookBean;
import ru.complitex.osznconnection.file.web.AuthorizedListPage;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.List;

import static ru.complitex.common.util.StringUtil.asList;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.04.13 17:34
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class FacilityTarifList extends AuthorizedListPage<FacilityTarif> {
    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    public FacilityTarifList(PageParameters pageParameters) {
        super(pageParameters, "dbfFields.", FacilityTarifFileList.class);
    }

    @Override
    protected FacilityTarif newFilterObject(PageParameters pageParameters) {
        return new FacilityTarif(pageParameters.get("request_file_id").toLongObject());
    }

    @Override
    protected List<FacilityTarif> getList(FilterWrapper<FacilityTarif> filterWrapper) {
        return facilityReferenceBookBean.getFacilityTarifs(filterWrapper);
    }

    @Override
    protected Long getCount(FilterWrapper<FacilityTarif> filterWrapper) {
        return facilityReferenceBookBean.getFacilityTarifsCount(filterWrapper);
    }

    @Override
    protected List<String> getProperties() {
        return asList(FacilityTarifDBF.class);
    }
}
