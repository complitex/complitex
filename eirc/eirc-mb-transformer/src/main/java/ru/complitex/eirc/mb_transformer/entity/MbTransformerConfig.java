package ru.complitex.eirc.mb_transformer.entity;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.IComponentConfig;
import ru.complitex.common.web.component.type.InputPanel;
import ru.complitex.common.web.model.LongModel;
import ru.complitex.eirc.dictionary.entity.OrganizationType;
import ru.complitex.eirc.mb_transformer.web.component.OrganizationPicker;

/**
 * @author Pavel Sknar
 */
public enum MbTransformerConfig implements IComponentConfig {

    EIRC_ORGANIZATION_ID("general"),
    MB_ORGANIZATION_ID("general"),

    WORK_DIR("general"),
    TMP_DIR("general"),

    EIRC_DATA_SOURCE("general");

    private String groupKey;

    MbTransformerConfig(String groupKey) {
        this.groupKey = groupKey;
    }

    @Override
    public String getGroupKey() {
        return groupKey;
    }

    @Override
    public WebMarkupContainer getComponent(String id, IModel<String> model) {
        if (this.equals(EIRC_ORGANIZATION_ID)) {
            return new OrganizationPicker(id, new LongModel(model), (long)OrganizationType.USER_ORGANIZATION.getId());
        } if (this.equals(MB_ORGANIZATION_ID)) {
            return new OrganizationPicker(id, new LongModel(model), (long)OrganizationType.PAYMENT_COLLECTOR.getId());
        } else {
            return new InputPanel<>("config", model, String.class, false, null, true, 40);
        }
    }


}
