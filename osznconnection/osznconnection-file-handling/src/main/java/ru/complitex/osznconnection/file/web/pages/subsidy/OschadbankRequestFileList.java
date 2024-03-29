package ru.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.service.process.ProcessManagerService;
import ru.complitex.osznconnection.file.service.subsidy.OschadbankRequestBean;
import ru.complitex.osznconnection.file.service.subsidy.OschadbankRequestFileBean;
import ru.complitex.osznconnection.file.web.AbstractFileList;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

import static ru.complitex.osznconnection.file.entity.RequestFileType.OSCHADBANK_REQUEST;
import static ru.complitex.osznconnection.file.service.process.ProcessType.*;
import static ru.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy.SUBSIDY_DEPARTMENT_TYPE;

/**
 * @author Anatoly A. Ivanov
 * 26.03.2019 15:05
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class OschadbankRequestFileList extends AbstractFileList {
    @EJB
    private ProcessManagerService processManagerService;

    @EJB
    private OschadbankRequestBean oschadbankRequestBean;

    @EJB
    private OschadbankRequestFileBean oschadbankRequestFileBean;

    public OschadbankRequestFileList() {
        super(OSCHADBANK_REQUEST, null, LOAD_OSCHADBANK_REQUEST, null,
                FILL_OSCHADBANK_REQUEST, SAVE_OSCHADBANK_REQUEST, new Long[]{SUBSIDY_DEPARTMENT_TYPE});
    }

    @Override
    protected Class<? extends WebPage> getItemListPageClass() {
        return OschadbankRequestList.class;
    }

    @Override
    protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
        processManagerService.loadOschadbankRequest(serviceProviderId, userOrganizationId, organizationId, year, monthFrom);
    }

    @Override
    protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
        processManagerService.fillOschadbankRequest(selectedFileIds, parameters);
    }

    @Override
    protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
        processManagerService.saveOschadbankRequest(selectedFileIds, parameters);
    }

    @Override
    protected void delete(RequestFile requestFile) {
        oschadbankRequestBean.delete(requestFile.getId());
        oschadbankRequestFileBean.delete(requestFile.getId());

        super.delete(requestFile);
    }

    @Override
    protected boolean isFillVisible() {
        return true;
    }
}
