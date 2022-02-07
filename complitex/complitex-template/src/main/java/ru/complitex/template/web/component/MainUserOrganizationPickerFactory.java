package ru.complitex.template.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.web.component.factory.WebComponentFactoryUtil;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public class MainUserOrganizationPickerFactory {

    public static final String WEB_COMPONENT_NAME = "MainUserOrganizationPickerComponent";

    public static Component create(String id, IModel<DomainObject> model) {
        Class<? extends Component> mainUserOrganizationPickerClass =
                WebComponentFactoryUtil.getComponentClass(WEB_COMPONENT_NAME);
        try {
            return mainUserOrganizationPickerClass.getConstructor(String.class, IModel.class).newInstance(id, model);
        } catch (Exception e) {
            LoggerFactory.getLogger(MainUserOrganizationPickerFactory.class)
                    .warn("Couldn't to instantiate main user organization picker component. Default one will be used.", e);
            return new MainUserOrganizationPicker(id, model);
        }
    }

    private MainUserOrganizationPickerFactory() {
    }
}
