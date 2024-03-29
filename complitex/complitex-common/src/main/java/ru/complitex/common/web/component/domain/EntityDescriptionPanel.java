package ru.complitex.common.web.component.domain;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.Entity;
import ru.complitex.common.entity.EntityAttribute;
import ru.complitex.common.entity.StringValue;
import ru.complitex.common.entity.ValueType;
import ru.complitex.common.strategy.EntityBean;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.strategy.StringValueBean;
import ru.complitex.common.util.CloneUtil;
import ru.complitex.common.web.component.list.AjaxRemovableListView;
import ru.complitex.common.web.component.type.StringValuePanel;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Artem
 */

//todo value type refactoring
public class EntityDescriptionPanel extends Panel {
    private static final String DATE_FORMAT = "HH:mm dd.MM.yyyy";

    @EJB
    private StrategyFactory strategyFactory;

    @EJB
    private StringValueBean stringBean;

    @EJB
    private EntityBean entityBean;

    public EntityDescriptionPanel(String id, String entity, PageParameters pageParameters) {
        super(id);
        init(entity, pageParameters);
    }

    private void init(final String entity, final PageParameters params) {
        final Entity oldEntity = entityBean.getEntity(entity);
        final Entity description = CloneUtil.cloneObject(oldEntity);

        IModel<String> entityLabelModel = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return description.getName(getLocale());
            }
        };
        IModel<String> labelModel = new StringResourceModel("label", null, entityLabelModel);
        add(new Label("title", labelModel));
        add(new Label("label", labelModel));

        add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        add(form);

        //attributes
        final WebMarkupContainer attributesContainer = new WebMarkupContainer("attributesContainer");
        attributesContainer.setOutputMarkupId(true);
        form.add(attributesContainer);

        final List<ValueType> supportedValueTypes = Arrays.asList(ValueType.STRING_VALUE, ValueType.STRING,
                ValueType.BOOLEAN, ValueType.DATE, ValueType.DECIMAL, ValueType.INTEGER);

        ListView<EntityAttribute> attributes = new AjaxRemovableListView<EntityAttribute>("attributes", description.getAttributes()) {

            @Override
            protected void populateItem(ListItem<EntityAttribute> item) {
                final EntityAttribute entityAttribute = item.getModelObject();

                WebMarkupContainer valueTypesContainer = new WebMarkupContainer("valueTypesContainer");
                item.add(valueTypesContainer);
                RepeatingView valueTypeItem = new RepeatingView("valueTypeItem");
                valueTypesContainer.add(valueTypeItem);
                Label valueType = new Label("valueType", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return displayValueType(entityAttribute.getValueType());
                    }
                });
                item.add(valueType);

                DropDownChoice<ValueType> valueTypeSelect = new DropDownChoice<>("valueTypeSelect",
                        new PropertyModel<>(entityAttribute.getValueType(), "valueType"), supportedValueTypes);
                valueTypeSelect.setRequired(true);
                valueTypeSelect.setLabel(new ResourceModel("attribute_value_type"));
                valueTypeSelect.add(new AjaxFormComponentUpdatingBehavior("change") {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        //update model
                    }
                });
                item.add(valueTypeSelect);

                Label mandatoryLabel = new Label("mandatoryLabel", new ResourceModel(entityAttribute.isRequired() ? "yes" : "no"));
                item.add(mandatoryLabel);

                CheckBox mandatoryInput = new CheckBox("mandatoryInput", new PropertyModel<Boolean>(entityAttribute, "mandatory"));
                mandatoryInput.add(new AjaxFormComponentUpdatingBehavior("change") {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        //update model
                    }
                });
                item.add(mandatoryInput);

                item.add(new Label("startDate", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        if (entityAttribute.isSystem()) {
                            return getString("built-in");
                        } else if (entityAttribute.getId() != null) {
                            return new SimpleDateFormat(DATE_FORMAT, getLocale()).format(entityAttribute.getStartDate());
                        } else {
                            return null;
                        }
                    }
                }));
                item.add(new Label("endDate", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        if (entityAttribute.getEndDate() == null) {
                            return null;
                        } else {
                            return new SimpleDateFormat(DATE_FORMAT, getLocale()).format(entityAttribute.getEndDate());
                        }
                    }
                }));

                if (entityAttribute.getId() != null) { // old attribute
                    item.add(new Label("name", new AbstractReadOnlyModel<String>() {

                        @Override
                        public String getObject() {
                            return entityAttribute.getName(getLocale());
                        }
                    }));

                    valueTypesContainer.setVisible(false);
                    valueTypeSelect.setVisible(false);
                    mandatoryInput.setVisible(false);
                } else {
                    //new attribute
                    item.add(new StringValuePanel("name", new PropertyModel<List<StringValue>>(entityAttribute, "attributeNames"), true,
                            new ResourceModel("attribute_name"), true, new MarkupContainer[0]));

                    valueType.setVisible(false);
                    valueTypesContainer.setVisible(false);
                    mandatoryLabel.setVisible(false);
                }

                addRemoveLink("remove", item, null, attributesContainer).setVisible(!entityAttribute.isSystem() && (entityAttribute.getEndDate() == null));
            }
        };
        attributes.setReuseItems(true);
        attributesContainer.add(attributes);

        AjaxLink addAttribute = new AjaxLink("addAttribute") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                EntityAttribute entityAttribute = entityBean.newAttributeType();
                description.getAttributes().add(entityAttribute);
                target.add(attributesContainer);
            }
        };
        form.add(addAttribute);

        Button submit = new Button("submit") {

            @Override
            public void onSubmit() {
                save(oldEntity, description);
                setResponsePage(getPage().getClass(), params);
            }
        };
        form.add(submit);

        final String[] parents = strategyFactory.getStrategy(entity).getParents();
        WebMarkupContainer parentsContainer = new WebMarkupContainer("parentsContainer");
        Label parentsInfo = new Label("parentsInfo", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if ((parents != null) && (parents.length > 0)) {
                    return displayParents(parents);
                }
                return null;
            }
        });
        parentsContainer.add(parentsInfo);
        add(parentsContainer);
        if (parents == null || parents.length == 0) {
            parentsContainer.setVisible(false);
        }
    }

    private void save(Entity oldEntity, Entity newEntity) {
        entityBean.save(oldEntity, newEntity);
    }

    private String displayParents(String[] parents) {
        StringBuilder parentsLabel = new StringBuilder();

        for (int i = 0; i < parents.length; i++) {
            IStrategy parentStrategy = strategyFactory.getStrategy(parents[i]);
            parentsLabel.append("'").
                    append(parentStrategy.getEntity().getName(getLocale())).
                    append("'");
            if (i < parents.length - 1) {
                parentsLabel.append(", ");
            }
        }
        return parentsLabel.toString();
    }

    private String displayValueType(ValueType valueType) {
        if (valueType.isSimple()) {
            return valueType.name();
        } else {
            return "[entity]";

//            IStrategy referenceEntityStrategy = strategyFactory.getStrategy(valueType, true);
//            if (referenceEntityStrategy == null) {
//                return new StringResourceModel("reference_table", this, Model.of(valueType.toUpperCase())).getObject();
//            } else {
//                return new StringResourceModel("reference", this, Model.of(referenceEntityStrategy.getEntityName()
//                        .getName(getLocale()))).getObject();
//            }
        }
    }
}
