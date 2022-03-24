package ru.complitex.eirc.mb_transformer.web.admin;

import ru.complitex.common.entity.IConfig;
import ru.complitex.template.web.pages.ConfigEdit;
import ru.complitex.eirc.mb_transformer.service.MbTransformerConfigBean;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Pavel Sknar
 */
public class MbTransformerConfigEdit extends ConfigEdit {

    @EJB(name = "MbTransformerConfigBean")
    private MbTransformerConfigBean configBean;

    @Override
    protected Map<String, List<IConfig>> getConfigGroups() {
        return configBean.getConfigGroups();
    }

    @Override
    protected Set<IConfig> getConfigs() {
        return configBean.getConfigs();
    }
}
