package ru.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.StringUtil;
import ru.complitex.osznconnection.file.entity.privilege.FacilityForm2;
import ru.complitex.osznconnection.file.entity.privilege.FacilityForm2DBF;
import ru.complitex.osznconnection.file.service.privilege.FacilityForm2Bean;
import ru.complitex.osznconnection.file.web.AuthorizedListPage;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

/**
 * @author inheaven on 021 21.11.16.
 */
@AuthorizeInstantiation("PRIVILEGE_FORM_2")
public class FacilityForm2List extends AuthorizedListPage<FacilityForm2> {
    @EJB
    private FacilityForm2Bean facilityForm2Bean;

    public FacilityForm2List(PageParameters pageParameters) {
        super(pageParameters, "dbfFields.", FacilityForm2FileList.class);
    }

    @Override
    protected FacilityForm2 newFilterObject(PageParameters pageParameters) {
        return new FacilityForm2(pageParameters.get("request_file_id").toLongObject());
    }

    @Override
    protected List<FacilityForm2> getList(FilterWrapper<FacilityForm2> filterWrapper) {
        return facilityForm2Bean.getFacilityForm2List(filterWrapper);
    }

    @Override
    protected Long getCount(FilterWrapper<FacilityForm2> filterWrapper) {
        return facilityForm2Bean.getFacilityForm2ListCount(filterWrapper);
    }

    @Override
    protected List<String> getProperties() {
        List<String> list = new ArrayList<>();

        list.add("DEPART");
        list.addAll(StringUtil.asList(FacilityForm2DBF.class));

        return list;
    }
}
