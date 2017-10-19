package org.complitex.pspoffice.frontend.web.domain;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.complitex.pspoffice.frontend.service.PspOfficeClient;
import org.complitex.pspoffice.frontend.web.FormPage;
import ru.complitex.pspoffice.api.model.DomainAttributeModel;
import ru.complitex.pspoffice.api.model.DomainModel;
import ru.complitex.pspoffice.api.model.EntityAttributeModel;
import ru.complitex.pspoffice.api.model.EntityModel;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

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
        domainModel = Model.of(id > 0 ? getDomainModel(entity, id) : newDomainModel(entityModel));

        Long parentEntityId = domainModel.getObject().getParentEntityId();

        if (parentEntityId == null){
            List<DomainModel> list = pspOfficeClient.request("domain/" + entity, "limit", 1)
                    .get(new GenericType<List<DomainModel>>(){});

            if (!list.isEmpty()){
                parentEntityId = list.get(0).getParentEntityId();
            }
        }

        if (parentEntityId != null) {
            EntityModel parentEntityModel = getEntityModel(parentEntityId.toString());

            getForm().add(new Fragment("parent", "entity", DomainPage.this)
                    .add(new Label("label", parentEntityModel.getNames().get("1"))
                            .add(new AttributeModifier("for", "parent" + id)))
                    .add(new HiddenField<>("inputId", new PropertyModel<>(domainModel, "parentId"))
                            .setMarkupId("parentId" + id))
                    .add(new AutoCompleteTextField<DomainModel>("input",
                            Model.of(getDomainModel(parentEntityModel.getEntity(), domainModel.getObject().getParentId())),
                            new AbstractAutoCompleteTextRenderer<DomainModel>(){

                                @Override
                                protected String getTextValue(DomainModel object) {
                                    return object.getAttributes().get(0).getValues().get("1");
                                }

                                @Override
                                protected CharSequence getOnSelectJavaScriptExpression(DomainModel item) {
                                    return "$('#parentId" + id +"').val('" + item.getId() + "'); input";
                                }
                            }) {
                        @Override
                        protected Iterator<DomainModel> getChoices(String input) {
                            return pspOfficeClient.request("domain/" + parentEntityModel.getEntity(), "value", input)
                                    .get(new GenericType<List<DomainModel>>(){}).iterator();
                        }

                        @Override
                        protected IConverter<?> createConverter(Class<?> type) {
                            if (DomainModel.class.equals(type)){
                                return new IConverter<DomainModel>() {
                                    @Override
                                    public DomainModel convertToObject(String s, Locale locale) throws ConversionException {
                                        return null;
                                    }

                                    @Override
                                    public String convertToString(DomainModel c, Locale locale) {
                                        return c.getAttributes().get(0).getValues().get("1");
                                    }
                                };
                            }

                            return super.createConverter(type);
                        }

                    }.setMarkupId("parent" + id)));
        }else{
            getForm().add(new EmptyPanel("parent"));
        }

        getForm().add(new ListView<EntityAttributeModel>("attributes", entityModel.getAttributes()) {
            @Override
            protected void populateItem(ListItem<EntityAttributeModel> item) {
                EntityAttributeModel entityAttributeModel = item.getModelObject();
                String id = "attribute" + item.getMarkupId();

                IModel<DomainAttributeModel> domainAttributeModel = domainModel.getObject().getAttributes().stream()
                        .filter(a -> a.getEntityAttributeId().equals(entityAttributeModel.getId()))
                        .findAny()
                        .map(Model::of)
                        .get();

                switch (entityAttributeModel.getValueTypeId()){
                    case 0:
                        item.add(new Fragment("attribute", "stringValue", DomainPage.this)
                                .add(new Label("labelRu", entityAttributeModel.getNames().get("1"))
                                        .add(new AttributeModifier("for", id + "1")))
                                .add(new TextField<String>("inputRu", new PropertyModel<>(domainAttributeModel, "values.1"))
                                        .setRequired(entityAttributeModel.getRequired())
                                        .setMarkupId(id + "1"))
                                .add(new Label("labelUk", "")
                                        .add(new AttributeModifier("for", id + "2")))
                                .add(new TextField<String>("inputUk", new PropertyModel<>(domainAttributeModel, "values.2"))
                                        .setMarkupId(id + "2")));
                        break;
                    case 1:
                    case 3:
                        item.add(new Fragment("attribute", "value", DomainPage.this)
                                .add(new Label("label", entityAttributeModel.getNames().get("1"))
                                        .add(new AttributeModifier("for", id)))
                                .add(new TextField<String>("input", new PropertyModel<>(domainAttributeModel, "values.1"))
                                        .setRequired(entityAttributeModel.getRequired())
                                        .setMarkupId(id)));
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
                                        .setMarkupId(id)));
                        break;
                    case 5:
                        item.add(new Fragment("attribute", "value", DomainPage.this)
                                .add(new Label("label", entityAttributeModel.getNames().get("1"))
                                        .add(new AttributeModifier("for", id)))
                                .add(new DateTextField("input", new PropertyModel<>(domainAttributeModel, "value.1"), "dd.MM.yyyy")
                                        .setRequired(entityAttributeModel.getRequired())
                                        .setMarkupId(id)));
                        break;
                    case 10:
                        EntityModel reference = getEntityModel(entityAttributeModel.getReferenceId().toString());

                        item.add(new Fragment("attribute", "entity", DomainPage.this)
                            .add(new Label("label", entityAttributeModel.getNames().get("1"))
                                    .add(new AttributeModifier("for", "value" + id)))
                            .add(new HiddenField<>("inputId", new PropertyModel<>(domainAttributeModel, "valueId"))
                                    .setMarkupId("valueId" + id))
                            .add(new AutoCompleteTextField<DomainModel>("input",
                                    Model.of(getDomainModel(reference.getEntity(), domainAttributeModel.getObject().getValueId())),
                                    DomainModel.class,
                                    new AbstractAutoCompleteTextRenderer<DomainModel>(){

                                        @Override
                                        protected String getTextValue(DomainModel object) {
                                            return object.getAttributes().get(0).getValues().get("1");
                                        }

                                        @Override
                                        protected CharSequence getOnSelectJavaScriptExpression(DomainModel item) {
                                            return "$('#valueId" + id +"').val('" + item.getId() + "'); input";
                                        }
                                    }, new AutoCompleteSettings().setShowListOnEmptyInput(true)) {
                                @Override
                                protected Iterator<DomainModel> getChoices(String input) {
                                    return pspOfficeClient.request("domain/" + reference.getEntity(), "value", input)
                                            .get(new GenericType<List<DomainModel>>(){}).iterator();
                                }

                                @Override
                                protected IConverter<?> createConverter(Class<?> type) {
                                    if (DomainModel.class.equals(type)){
                                        return new IConverter<DomainModel>() {
                                            @Override
                                            public DomainModel convertToObject(String s, Locale locale) throws ConversionException {
                                                return null;
                                            }

                                            @Override
                                            public String convertToString(DomainModel c, Locale locale) {
                                                return c.getAttributes().get(0).getValues().get("1");
                                            }
                                        };
                                    }

                                    return super.createConverter(type);
                                }

                            }.setMarkupId("value" + id)));
                        break;
                    default:
                        item.add(new Fragment("attribute", "value", DomainPage.this)
                                .add(new Label("label", entityAttributeModel.getNames().get("1") + "[" + entityAttributeModel.getValueTypeId() + "]")
                                        .add(new AttributeModifier("for", id)))
                                .add(new TextField<String>("input", new PropertyModel<>(domainAttributeModel, "valueId"))
                                        .setMarkupId(id)));
                }
            }
        });

        setReturnPage(DomainListPage.class, new PageParameters().add("entity", entity));
    }

    private DomainModel newDomainModel(EntityModel entityModel) {
        DomainModel domainModel = new DomainModel();

        domainModel.setAttributes(
                entityModel.getAttributes().stream()
                        .map(ea -> {
                            DomainAttributeModel m = new DomainAttributeModel();

                            m.setEntityAttributeId(ea.getId());
                            m.setValues(new HashMap<>());

                            return m;
                        })
                        .collect(Collectors.toList()));

        return domainModel;
    }

    private EntityModel getEntityModel(String entity){
        return pspOfficeClient.request("entity/" + entity).get(EntityModel.class);
    }

    private DomainModel getDomainModel(String entity, Long id) {
        if (id == null){
            return null;
        }

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

    @Override
    protected boolean isDeleteVisible() {
        return domainModel.getObject().getId() != null && domainModel.getObject().getId() > 0;
    }

    @Override
    protected Response delete() {
        return pspOfficeClient.request("domain/" + entity + "/" + domainModel.getObject().getId()).delete();
    }
}
