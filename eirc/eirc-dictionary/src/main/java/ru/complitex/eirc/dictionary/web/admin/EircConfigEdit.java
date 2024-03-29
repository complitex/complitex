package ru.complitex.eirc.dictionary.web.admin;

import ru.complitex.common.entity.IConfig;
import ru.complitex.template.web.pages.ConfigEdit;
import ru.complitex.eirc.dictionary.service.EircConfigBean;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Pavel Sknar
 */
public class EircConfigEdit extends ConfigEdit {

    @EJB(name = "EircConfigBean")
    private EircConfigBean configBean;

    @Override
    protected Map<String, List<IConfig>> getConfigGroups() {
        return configBean.getConfigGroups();
    }

    @Override
    protected Set<IConfig> getConfigs() {
        return configBean.getConfigs();
    }
}
