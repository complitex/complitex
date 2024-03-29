package ru.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import ru.complitex.osznconnection.file.service.process.ProcessManagerService;
import ru.complitex.osznconnection.file.web.AbstractFileList;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

import static ru.complitex.osznconnection.file.entity.RequestFileType.DEBT;
import static ru.complitex.osznconnection.file.service.process.ProcessType.*;
import static ru.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy.PRIVILEGE_DEPARTMENT_TYPE;

/**
 * @author Anatoly Ivanov
 * 16.09.2020 20:26
 */
@AuthorizeInstantiation("PRIVILEGE_DEBT")
public class DebtFileList extends AbstractFileList {
    @EJB
    private ProcessManagerService processManagerService;

    public DebtFileList() {
        super(DEBT, null, LOAD_DEBT, BIND_DEBT, FILL_DEBT, SAVE_DEBT, new Long[]{PRIVILEGE_DEPARTMENT_TYPE});
    }

    @Override
    protected Class<? extends WebPage> getItemListPageClass() {
        return DebtList.class;
    }

    @Override
    protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
        processManagerService.loadDebt(serviceProviderId, userOrganizationId, organizationId, year, monthFrom);
    }

    @Override
    protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
        processManagerService.saveDebt(selectedFileIds, parameters);
    }

    @Override
    protected boolean isBindVisible() {
        return true;
    }

    @Override
    protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
        processManagerService.bindDebt(selectedFileIds, parameters);
    }

    @Override
    protected boolean isFillVisible() {
        return true;
    }

    @Override
    protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
        processManagerService.fillDebt(selectedFileIds, parameters);
    }
}
