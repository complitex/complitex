package ru.complitex.osznconnection.file.web.component.process;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import ru.complitex.common.entity.Log;
import ru.complitex.common.service.LogBean;
import ru.complitex.osznconnection.file.Module;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.service.RequestFileBean;

import javax.ejb.EJB;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Artem
 */
public abstract class RequestFileDeleteButton extends DeleteButton {

    @EJB
    private RequestFileBean requestFileBean;
    @EJB
    private LogBean logBean;
    private final SelectManager selectManager;
    private final Collection<Component> updateComponents;

    public RequestFileDeleteButton(String id, SelectManager selectManager) {
        this(id, selectManager, new Component[0]);
    }

    public RequestFileDeleteButton(String id, SelectManager selectManager, Component... updateComponents) {
        super(id);
        this.selectManager = selectManager;
        this.updateComponents = new ArrayList<>(ImmutableList.copyOf(updateComponents));
    }

    public RequestFileDeleteButton addUpdateComponent(Component component) {
        this.updateComponents.add(component);
        return this;
    }

    @Override
    public void onSubmit(AjaxRequestTarget target, Form form) {
        for (long requestFileId : selectManager.getSelectedFileIds()) {
            RequestFile requestFile = requestFileBean.getRequestFile(requestFileId);
            try {
                requestFileBean.delete(requestFile);

                selectManager.remove(requestFileId);

                logSuccess(requestFile);
                info(MessageFormat.format(getString("info.deleted"), requestFile.getFullName()));
                logBean.info(Module.NAME, getLoggerControllerClass(), RequestFile.class,
                        null, requestFile.getId(), Log.EVENT.REMOVE, requestFile.getLogChangeList(),
                        getString("info.requestFileDeleted"), requestFile.getLogObjectName());
            } catch (Exception e) {
                logError(requestFile, e);
                error(MessageFormat.format("error.delete", requestFile.getFullName()));
                logBean.error(Module.NAME, getLoggerControllerClass(), RequestFile.class,
                        null, requestFile.getId(), Log.EVENT.REMOVE, requestFile.getLogChangeList(),
                        getString("error.requestFileDeleted"), requestFile.getLogObjectName());
                break;
            }
        }

        if (!updateComponents.isEmpty()) {
            for (Component c : updateComponents) {
                target.add(c);
            }
        }
    }

    protected abstract Class<?> getLoggerControllerClass();

    protected void logSuccess(RequestFile requestFile) {
    }

    protected void logError(RequestFile requestFile, Exception e) {
    }
}
