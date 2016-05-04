package org.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileFilter;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;
import org.complitex.osznconnection.file.service.privilege.PrivilegeFileGroupBean;
import org.complitex.osznconnection.file.service.process.ProcessManagerBean;
import org.complitex.osznconnection.file.web.AbstractProcessableListPanel;
import org.complitex.osznconnection.file.web.component.load.DateParameter;
import org.complitex.osznconnection.file.web.component.load.RequestFileLoadPanel;
import org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.service.process.ProcessType.*;

/**
 * inheaven on 05.04.2016.
 */
public class PrivilegeFileGroupListPanel extends AbstractProcessableListPanel<PrivilegeFileGroup, RequestFileFilter>{
    @EJB
    private ProcessManagerBean processManagerBean;

    @EJB
    private PrivilegeFileGroupBean privilegeFileGroupBean;


    public PrivilegeFileGroupListPanel(String id) {
        super(id, LOAD_PRIVILEGE_GROUP, BIND_PRIVILEGE_GROUP, FILL_PRIVILEGE_GROUP, SAVE_PRIVILEGE_GROUP);

        add(new Label("title", new ResourceModel("title")));

        //Файл характеристик жилья
        addColumn(new Column() {
            @Override
            public Component head(ISortStateLocator stateLocator, DataView<?> dataView, Component refresh) {
                return new ArrowOrderByBorder("header.firstName", "firstName", stateLocator, dataView, refresh);
            }

            @Override
            public Component filter() {
                return new TextField<String>("firstName");
            }

            @Override
            public Component field(final Item<PrivilegeFileGroup> item) {
                RequestFile requestFile = item.getModelObject().getDwellingCharacteristicsRequestFile();

                if (requestFile != null) {
                    return new BookmarkablePageLinkPanel<RequestFile>("firstName",
                            requestFile.getName(), DwellingCharacteristicsList.class,
                            new PageParameters().set("request_file_id", requestFile.getId()));
                }else {
                    return new Label("firstName", "");
                }
            }
        });

        //Файл льгот
        addColumn(new Column() {
            @Override
            public Component head(ISortStateLocator stateLocator, DataView<?> dataView, Component refresh) {
                return new ArrowOrderByBorder("header.secondName", "secondName", stateLocator, dataView, refresh);
            }

            @Override
            public Component filter() {
                return new TextField<String>("secondName");
            }

            @Override
            public Component field(final Item<PrivilegeFileGroup> item) {
                RequestFile requestFile = item.getModelObject().getFacilityServiceTypeRequestFile();

                if (requestFile != null) {
                    return new BookmarkablePageLinkPanel<RequestFile>("secondName",
                            requestFile.getName(), FacilityServiceTypeList.class,
                            new PageParameters().set("request_file_id", requestFile.getId()));
                }else {
                    return new Label("secondName", "");
                }
            }
        });
    }

    @Override
    protected void load(long userOrganizationId, long osznId, DateParameter dateParameter) {
        processManagerBean.loadPrivilegeGroup(userOrganizationId, osznId, dateParameter.getMonth(), dateParameter.getYear());
    }

    @Override
    protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
        processManagerBean.bindPrivilegeGroup(selectedFileIds, commandParameters);
    }

    @Override
    protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
        processManagerBean.fillPrivilegeGroup(selectedFileIds, commandParameters);
    }

    @Override
    protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
        processManagerBean.savePrivilegeGroup(selectedFileIds, commandParameters);
    }

    @Override
    protected RequestFileLoadPanel.MonthParameterViewMode getLoadMonthParameterViewMode() {
        return RequestFileLoadPanel.MonthParameterViewMode.EXACT;
    }

    @Override
    protected String getPreferencePage() {
        return null;
    }

    @Override
    protected Long getCount(RequestFileFilter filter) {
        return privilegeFileGroupBean.getPrivilegeFileGroupsCount(filter);
    }

    @Override
    protected List<PrivilegeFileGroup> getObjects(RequestFileFilter filter) {
        return privilegeFileGroupBean.getPrivilegeFileGroups(filter);
    }

    @Override
    protected PrivilegeFileGroup getObject(long id) {
        return privilegeFileGroupBean.getPrivilegeFileGroup(id);
    }

    @Override
    protected void delete(PrivilegeFileGroup object) {
        privilegeFileGroupBean.delete(object);
    }

    @Override
    protected RequestFile getRequestFile(PrivilegeFileGroup object) {
        return object.getDwellingCharacteristicsRequestFile();
    }

    @Override
    protected Long[] getOsznOrganizationTypes() {
        return new Long[]{OsznOrganizationTypeStrategy.PRIVILEGE_DEPARTMENT_TYPE};
    }
}