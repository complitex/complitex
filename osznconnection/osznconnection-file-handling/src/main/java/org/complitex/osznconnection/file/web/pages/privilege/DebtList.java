package org.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.util.StringUtil;
import org.complitex.osznconnection.file.entity.privilege.Debt;
import org.complitex.osznconnection.file.entity.privilege.DebtDBF;
import org.complitex.osznconnection.file.service.privilege.DebtBean;
import org.complitex.osznconnection.file.web.AuthorizedListPage;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Anatoly Ivanov
 * 16.09.2020 20:38
 */
@AuthorizeInstantiation("PRIVILEGE_LOCAL")
public class DebtList extends AuthorizedListPage<Debt> {
    @EJB
    private DebtBean debtBean;

    public DebtList(PageParameters pageParameters) {
        super(pageParameters, "dbfFields.", DebtFileList.class);
    }

    @Override
    protected Debt newFilterObject(PageParameters pageParameters) {
        return new Debt(pageParameters.get("request_file_id").toLongObject());
    }

    @Override
    protected List<Debt> getList(FilterWrapper<Debt> filterWrapper) {
        return debtBean.getDebts(filterWrapper);
    }

    @Override
    protected Long getCount(FilterWrapper<Debt> filterWrapper) {
        return debtBean.getDebtsCount(filterWrapper);
    }

    @Override
    protected List<String> getProperties() {
        return StringUtil.asList(DebtDBF.class);
    }
}
