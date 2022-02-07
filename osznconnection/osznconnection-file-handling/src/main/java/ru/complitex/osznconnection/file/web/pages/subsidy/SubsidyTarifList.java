package ru.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.StringUtil;
import ru.complitex.osznconnection.file.entity.subsidy.SubsidyTarif;
import ru.complitex.osznconnection.file.entity.subsidy.SubsidyTarifDBF;
import ru.complitex.osznconnection.file.service.subsidy.SubsidyTarifBean;
import ru.complitex.osznconnection.file.web.AuthorizedListPage;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 *         01.02.2017 18:09
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class SubsidyTarifList extends AuthorizedListPage<SubsidyTarif>{
    @EJB
    private SubsidyTarifBean subsidyTarifBean;

    public SubsidyTarifList(PageParameters pageParameters) {
        super(pageParameters, "dbfFields.", SubsidyTarifFileList.class);
    }

    @Override
    protected SubsidyTarif newFilterObject(PageParameters pageParameters) {
        return new SubsidyTarif(pageParameters.get("request_file_id").toLongObject());
    }

    @Override
    protected List<SubsidyTarif> getList(FilterWrapper<SubsidyTarif> filterWrapper) {
        return subsidyTarifBean.getSubsidyTarifs(filterWrapper);
    }

    @Override
    protected Long getCount(FilterWrapper<SubsidyTarif> filterWrapper) {
        return subsidyTarifBean.getSubsidyTarifsCount(filterWrapper);
    }

    @Override
    protected List<String> getProperties() {
        return StringUtil.asList(SubsidyTarifDBF.class);
    }
}
