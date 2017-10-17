package org.complitex.pspoffice.frontend.web.domain;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 13.10.2017 12:56
 */
public class DomainPage extends FormPage{
    @Inject
    private PspOfficeClient pspOfficeClient;

    private IModel<DomainModel> domainModel;

    private String entity;

    public DomainPage(PageParameters pageParameters) {
        entity = pageParameters.get("entity").toString();
        Long id = pageParameters.get("id").toLongObject();

        EntityModel entityModel = getEntityModel(entity);
        domainModel = Model.of(getDomainModel(entity, id));

        getForm().add(new ListView<EntityAttributeModel>("attributes", entityModel.getAttributes()) {
            @Override
            protected void populateItem(ListItem<EntityAttributeModel> item) {
                EntityAttributeModel entityAttributeModel = item.getModelObject();
                String id = "attribute" + item.getMarkupId();

                IModel<DomainAttributeModel> domainAttributeModel = domainModel.getObject().getAttributes().stream()
                        .filter(a -> a.getEntityAttributeId().equals(entityAttributeModel.getId()))
                        .findAny()
                        .map(Model::of)
                        .orElse(null);

                switch (entityAttributeModel.getValueTypeId()){
                    case 0:
                        item.add(new Fragment("attribute", "stringValue", DomainPage.this)
                                .add(new Label("label1", entityAttributeModel.getNames().get("1"))
                                        .add(new AttributeModifier("for", id + "1")))
                                .add(new TextField<String>("input1", new PropertyModel<>(domainAttributeModel, "values.1"))
                                        .setRequired(entityAttributeModel.getRequired())
                                        .add(new AttributeModifier("id", id + "1")))
                                .add(new Label("label2", entityAttributeModel.getNames().get("2"))
                                        .add(new AttributeModifier("for", id + "2")))
                                .add(new TextField<String>("input2", new PropertyModel<>(domainAttributeModel, "values.2"))
                                        .add(new AttributeModifier("id", id + "2"))));
                        break;
                    case 1:
                    case 3:
                        item.add(new Fragment("attribute", "value", DomainPage.this)
                                .add(new Label("label", entityAttributeModel.getNames().get("1"))
                                        .add(new AttributeModifier("for", id)))
                                .add(new TextField<String>("input", new PropertyModel<>(domainAttributeModel, "values.1"))
                                        .setRequired(entityAttributeModel.getRequired())
                                        .add(new AttributeModifier("id", id))));
                        break;
                    case 2:
                        item.add(new Fragment("attribute", "value", DomainPage.this)
                                .add(new Label("label", entityAttributeModel.getNames().get("1"))
                                        .add(new AttributeModifier("for", id)))
                                .add(new DropDownChoice<>("input", new PropertyModel<>(domainAttributeModel, "values.1"),
                                        Arrays.asList(true, false), new IChoiceRenderer<Boolean>() {
                                    @Override
                                    public Object getDisplayValue(Boolean object) {
                                        return object ? "Да" : "Нет";
                                    }

                                    @Override
                                    public String getIdValue(Boolean object, int index) {
                                        return object.toString();
                                    }

                                    @Override
                                    public Boolean getObject(String id, IModel<? extends List<? extends Boolean>> choices) {
                                        return "true".equals(id);
                                    }
                                })
                                        .setRequired(entityAttributeModel.getRequired())
                                        .add(new AttributeModifier("id", id))));
                        break;
                    case 5:
                        item.add(new Fragment("attribute", "value", DomainPage.this)
                                .add(new Label("label", entityAttributeModel.getNames().get("1"))
                                        .add(new AttributeModifier("for", id)))
                                .add(new DateTextField("input", new PropertyModel<>(domainAttributeModel, "value.1"), "dd.MM.yyyy")
                                        .setRequired(entityAttributeModel.getRequired())
                                        .add(new AttributeModifier("id", id))));
                        break;
                    default:
                        item.add(new Fragment("attribute", "value", DomainPage.this)
                                .add(new Label("label", entityAttributeModel.getNames().get("1") + "[" + entityAttributeModel.getValueTypeId() + "]")
                                        .add(new AttributeModifier("for", id)))
                                .add(new TextField<String>("input", new PropertyModel<>(domainAttributeModel, "valueId"))
                                        .setRequired(entityAttributeModel.getRequired())
                                        .add(new AttributeModifier("id", id))));
                }
            }
        });

        setReturnPage(DomainListPage.class, new PageParameters().add("entity", entity));
    }

    private EntityModel getEntityModel(String entity){
        return pspOfficeClient.request("entity/" + entity).get(EntityModel.class);
    }

    private DomainModel getDomainModel(String entity, Long id) {
        DomainModel domainModel =  pspOfficeClient.request("domain/" + entity + "/" + id).get(DomainModel.class);

        domainModel.getAttributes().stream()
                .filter(a -> a.getValues() == null)
                .forEach(a -> a.setValues(new HashMap<>()));

        return domainModel;
    }

    @Override
    protected Response put() {
        return pspOfficeClient.request("domain/" + entity).put(Entity.json(domainModel.getObject()));
    }
}
