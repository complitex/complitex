package ru.complitex.pspoffice.frontend.web.entity;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.pspoffice.frontend.service.PspOfficeClient;
import ru.complitex.pspoffice.frontend.web.FormPage;
import ru.complitex.ui.wicket.datatable.TableDataProvider;
import ru.complitex.ui.wicket.datatable.TablePanel;
import ru.complitex.pspoffice.api.model.EntityAttributeModel;
import ru.complitex.pspoffice.api.model.EntityModel;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Anatoly A. Ivanov
 * 22.09.2017 17:42
 */
public class EntityPage extends FormPage{
    @Inject
    private PspOfficeClient pspOfficeClient;

    private IModel<EntityModel> entityModel;

    public EntityPage(PageParameters pageParameters) {
        Long entityModelId = pageParameters.get("id").toLongObject();

        entityModel = Model.of(getEntityModel(entityModelId));

        getForm().add(new TextField<String>("entityId", new PropertyModel<>(entityModel, "id")));
        getForm().add(new TextField<String>("entity", new PropertyModel<>(entityModel, "entity")));
        getForm().add(new TextField<String>("nameRu", new PropertyModel<>(entityModel, "names.1")));
        getForm().add(new TextField<String>("nameUk", new PropertyModel<>(entityModel, "names.2")));

        getForm().add(new TablePanel<EntityAttributeModel>("entityAttributes",
                Arrays.asList("id", "names.1", "names.2", "valueTypeId"),
                new TableDataProvider<EntityAttributeModel>() {

                    @Override
                    public Iterator<? extends EntityAttributeModel> iterator(long first, long count) {
                        return entityModel.getObject().getAttributes().iterator();
                    }

                    @Override
                    public long size() {
                        return entityModel.getObject().getAttributes().size();
                    }
                }){
            @Override
            protected boolean isShowFilter() {
                return false;
            }
        });

        setReturnPage(EntityListPage.class);
    }

    private EntityModel getEntityModel(Long entityModelId) {
        return pspOfficeClient.request("entity/" + entityModelId).get(EntityModel.class);
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel("title");
    }
}
