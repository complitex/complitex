package ru.complitex.osznconnection.file.update;

import ru.complitex.common.service.AbstractBean;
import ru.complitex.osznconnection.file.entity.RequestFileFilter;
import ru.complitex.osznconnection.file.entity.RequestFileGroupType;
import ru.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;
import ru.complitex.osznconnection.file.entity.subsidy.RequestFileGroup;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.subsidy.RequestFileGroupBean;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 *         27.01.2017 19:28
 */
@Singleton
@Startup
public class UpdateService extends AbstractBean {

    @PostConstruct
    public void init() {
        RequestFileFilter requestFileFilter = new RequestFileFilter();
        requestFileFilter.setAdmin(true);
        requestFileFilter.setSortProperty("loaded");
        requestFileFilter.setAscending(true);

        List<PrivilegeFileGroup> list = sqlSession().selectList("selectPrivilegeFilesGroups", requestFileFilter);

        for (PrivilegeFileGroup g : list){
            if (g.getId() != null && g.getId() > 1000000){
                RequestFileGroup requestFileGroup = new RequestFileGroup();
                requestFileGroup.setGroupType(RequestFileGroupType.PRIVILEGE_GROUP);

                sqlSession().insert(RequestFileGroupBean.NS + ".insertRequestFileGroup", requestFileGroup);

                if (requestFileGroup.getId() == null){
                    throw new RuntimeException("update service error. request file group id is null");
                }

                if (g.getDwellingCharacteristicsRequestFile() != null){
                    g.getDwellingCharacteristicsRequestFile().setGroupId(requestFileGroup.getId());

                    sqlSession().update(RequestFileBean.NS + ".updateRequestFile", g.getDwellingCharacteristicsRequestFile());
                }

                if (g.getFacilityServiceTypeRequestFile() != null){
                    g.getFacilityServiceTypeRequestFile().setGroupId(requestFileGroup.getId());

                    sqlSession().update(RequestFileBean.NS + ".updateRequestFile", g.getFacilityServiceTypeRequestFile());
                }
            }
        }

    }
}
