package ru.complitex.osznconnection.file.web.component.process;

import org.apache.wicket.Component;
import ru.complitex.common.service.SessionBean;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.common.web.DictionaryFwSession;

import java.io.Serializable;


/**
 *
 * @author Artem
 */
public final class ModificationManager implements Serializable {
    private static final String RESOURCE_BUNDLE = ModificationManager.class.getName();
    private final boolean modificationsAllowed;
    private final boolean hasFieldDescription;
    private final Component component;

    public ModificationManager(Component component, boolean hasFieldDescription) {
        this.component = component;
        this.hasFieldDescription = hasFieldDescription;

        SessionBean osznSessionBean = sessionBean();

        this.modificationsAllowed =
                //- только пользователи, принадлежащие организации или администраторы могут обрабатывать файлы.
                (osznSessionBean.getCurrentUserOrganizationId(getSession()) != null || osznSessionBean.isAdmin())
                && //можно обрабатывать файлы, только если в базу загружены описания структур для файлов запросов.
                hasFieldDescription;
    }

    private DictionaryFwSession getSession() {
        return (DictionaryFwSession) component.getSession();
    }

    private SessionBean sessionBean() {
        return EjbBeanLocator.getBean(SessionBean.class);
    }

    public void reportErrorIfNecessary() {
        //Если описания структуры для файлов запросов не загружены в базу, сообщить об этом пользователю.
        if (!hasFieldDescription) {
            component.error(ResourceUtil.getString(RESOURCE_BUNDLE, "file_description_missing", component.getLocale()));
        }
    }

    public boolean isModificationsAllowed() {
        return modificationsAllowed;
    }
}
