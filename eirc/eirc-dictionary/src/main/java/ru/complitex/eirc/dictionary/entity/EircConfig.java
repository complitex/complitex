package ru.complitex.eirc.dictionary.entity;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.IComponentConfig;
import ru.complitex.common.web.component.type.InputPanel;
import ru.complitex.eirc.dictionary.strategy.ModuleInstanceTypeStrategy;
import ru.complitex.eirc.dictionary.web.ModuleInstancePicker;

/**
 * @author Pavel Sknar
 */
public enum EircConfig implements IComponentConfig {

    MODULE_ID("general"),

    IMPORT_FILE_STORAGE_DIR("import"),
    SYNC_DATA_SOURCE("import"),
    TMP_DIR("import"),
    NUMBER_FLUSH_REGISTRY_RECORDS("import"),
    NUMBER_READ_CHARS("import");

    private String groupKey;

    EircConfig(String groupKey) {
        this.groupKey = groupKey;
    }

    @Override
    public String getGroupKey() {
        return groupKey;
    }

    @Override
    public WebMarkupContainer getComponent(String id, IModel<String> model) {
        if (this.equals(MODULE_ID)) {
            return new ModuleInstancePicker(id, model, true, null, true, ModuleInstanceTypeStrategy.EIRC_TYPE);
        } else {
            return new InputPanel<>("config", model, String.class, false, null, true, 40);
        }
    }
}
