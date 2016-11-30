package org.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.util.StringUtil;
import org.complitex.osznconnection.file.entity.privilege.FacilityLocal;
import org.complitex.osznconnection.file.entity.privilege.FacilityLocalDBF;
import org.complitex.osznconnection.file.service.privilege.FacilityLocalBean;
import org.complitex.template.web.template.ListTemplatePage;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author inheaven on 021 21.11.16.
 */
@AuthorizeInstantiation("PRIVILEGE_LOCAL")
public class FacilityLocalList extends ListTemplatePage<FacilityLocal>{
    @EJB
    private FacilityLocalBean facilityLocalBean;

    public FacilityLocalList(PageParameters pageParameters) {
        super(pageParameters, "dbfFields.", FacilityLocalFileList.class);
    }

    @Override
    protected FacilityLocal newFilterObject(PageParameters pageParameters) {
        return new FacilityLocal(pageParameters.get("request_file_id").toLongObject());
    }

    @Override
    protected List<FacilityLocal> getList(FilterWrapper<FacilityLocal> filterWrapper) {
        return facilityLocalBean.getFacilityLocals(filterWrapper);
    }

    @Override
    protected Long getCount(FilterWrapper<FacilityLocal> filterWrapper) {
        return facilityLocalBean.getFacilityLocalsCount(filterWrapper);
    }

    @Override
    protected List<String> getProperties() {
        return StringUtil.asList(FacilityLocalDBF.class);
    }
}
