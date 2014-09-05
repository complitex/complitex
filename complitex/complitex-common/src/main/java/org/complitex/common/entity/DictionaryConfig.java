package org.complitex.common.entity;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.complitex.common.web.component.organization.OrganizationIdPicker;
import org.complitex.common.web.component.organization.OrganizationPicker;
import org.complitex.common.web.component.type.InputPanel;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.03.11 16:31
 */
public enum DictionaryConfig implements IComponentConfig {
    IMPORT_FILE_STORAGE_DIR("c:\\storage\\import"),
    SYNC_DATA_SOURCE("2"),
    MODULE_ID("0");

    private String defaultValue;

    DictionaryConfig(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getGroupKey() {
        return "import";
    }

    @Override
    public WebMarkupContainer getComponent(String id, final IModel<String> model) {
        if (this.equals(SYNC_DATA_SOURCE)) {
            return new OrganizationIdPicker(id, new IModel<Long>() {
                @Override
                public Long getObject() {
                    try {
                        return Long.valueOf(model.getObject());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }

                @Override
                public void setObject(Long object) {
                    model.setObject(object.toString());
                }

                @Override
                public void detach() {
                    model.detach();
                }
            }, 3L);
        } else {
            return new InputPanel<>("config", model, String.class, false, null, true, 40);
        }
    }
}
