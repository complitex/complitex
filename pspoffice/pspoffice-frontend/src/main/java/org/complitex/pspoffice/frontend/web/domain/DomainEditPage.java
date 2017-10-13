package org.complitex.pspoffice.frontend.web.domain;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.pspoffice.frontend.service.PspOfficeClient;
import org.complitex.pspoffice.frontend.web.FormPage;
import ru.complitex.pspoffice.api.model.DomainAttributeModel;
import ru.complitex.pspoffice.api.model.DomainModel;
import ru.complitex.pspoffice.api.model.EntityAttributeModel;
import ru.complitex.pspoffice.api.model.EntityModel;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 13.10.2017 12:56
 */
public class DomainEditPage extends FormPage{
    @Inject
    private PspOfficeClient pspOfficeClient;

    private IModel<DomainModel> domainModel;

    public DomainEditPage(PageParameters pageParameters) {
        String entity = pageParameters.get("entity").toString();
        Long id = pageParameters.get("id").toLongObject();

        domainModel = Model.of(getDomainModel(entity, id));

        getForm().add(new ListView<EntityAttributeModel>("attributes", getEntityModel(entity).getAttributes()) {
            @Override
            protected void populateItem(ListItem<EntityAttributeModel> item) {
                EntityAttributeModel entityAttributeModel = item.getModelObject();
                String id = "attribute" + item.getMarkupId();

                IModel<DomainAttributeModel> domainAttributeModel = domainModel.getObject().getAttributes().stream()
                        .filter(a -> a.getEntityAttributeId().equals(entityAttributeModel.getId()))
                        .findAny()
                        .map(Model::of)
                        .orElse(null);

                Fragment attribute = new Fragment("attribute", "attribute", DomainEditPage.this);
                attribute.add(new Label("label", entityAttributeModel.getNames().get("1"))
                        .add(new AttributeModifier("for", id)));
                attribute.add(new TextField<String>("attribute", new PropertyModel<>(domainAttributeModel, "values.1"))
                        .add(new AttributeModifier("id", id)));

                //todo add locale
                //todo add value types

                item.add(attribute);
            }
        });

        setReturnPage(DomainListPage.class, new PageParameters().add("entity", entity));
    }

    private EntityModel getEntityModel(String entity){
        return pspOfficeClient.request("entity/" + entity).get(EntityModel.class);
    }

    private DomainModel getDomainModel(String entity, Long id) {
        return pspOfficeClient.request("domain/" + entity + "/" + id).get(DomainModel.class);
    }
}
