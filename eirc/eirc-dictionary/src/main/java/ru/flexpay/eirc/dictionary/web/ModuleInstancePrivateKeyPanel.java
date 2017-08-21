package ru.flexpay.eirc.dictionary.web;

import org.apache.commons.codec.binary.Base64;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.EntityAttribute;
import org.complitex.common.strategy.StringValueBean;
import org.complitex.common.util.StringValueUtil;
import org.complitex.common.web.component.DomainObjectComponentUtil;
import org.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import org.complitex.common.web.component.organization.OrganizationIdPicker;
import org.complitex.common.web.model.AttributeStringModel;
import org.complitex.common.web.model.LongModel;
import ru.flexpay.eirc.dictionary.entity.OrganizationType;
import ru.flexpay.eirc.dictionary.strategy.ModuleInstanceStrategy;
import ru.flexpay.eirc.dictionary.strategy.ModuleInstanceTypeStrategy;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.ejb.EJB;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author Pavel Sknar
 */
public class ModuleInstancePrivateKeyPanel extends AbstractComplexAttributesPanel {

    @EJB
    private StringValueBean stringBean;

    @EJB
    private ModuleInstanceStrategy moduleInstanceStrategy;

    @EJB
    private ModuleInstanceTypeStrategy moduleInstanceTypeStrategy;

    public ModuleInstancePrivateKeyPanel(String id, boolean disabled) {
        super(id, disabled);
    }

    @Override
    protected void init() {
        DomainObject moduleInstance = getDomainObject();

        addPrivateKeyContainer(ModuleInstanceStrategy.PRIVATE_KEY, false, moduleInstance, "privateKeyContainer", new CallbackButton() {
            @Override
            public void onSubmit(AttributeStringModel attributeStringModel) {
                try {
                    //KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");
                    //SecretKey key = keyGen.generateKey();
                    // Generate a key for the HMAC-SHA1 keyed-hashing algorithm
                    KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA1");
                    SecretKey key = keyGen.generateKey();
                    attributeStringModel.setObject(new String(Base64.encodeBase64(key.getEncoded()), Charset.forName("UTF-8")));
                } catch (Exception ex) {
                    throw new RuntimeException("Inner error", ex);
                }
            }
        });

        addOrganizationContainer(moduleInstance, "organizationContainer");

        addModuleInstanceTypeContainer(moduleInstance, "typeContainer");
    }

    private void addOrganizationContainer(DomainObject moduleInstance, String name) {
        WebMarkupContainer container = new WebMarkupContainer(name);
        container.setOutputMarkupPlaceholderTag(true);
        add(container);

        Attribute attribute = moduleInstance.getAttribute(ModuleInstanceStrategy.ORGANIZATION);

        final EntityAttribute entityAttribute =
                moduleInstanceStrategy.getEntity().getAttributeType(ModuleInstanceStrategy.ORGANIZATION);

        OrganizationIdPicker picker = new OrganizationIdPicker("organization",
                new LongModel(new AttributeStringModel(attribute)),
                Long.valueOf(OrganizationType.USER_ORGANIZATION.getId()));
        //picker.setRequired(entityAttribute.isMandatory());
        //picker.setLabel(new Model<>(getString("organization")));

        container.add(picker);

        container.add(new Label("label",
                DomainObjectComponentUtil.labelModel(entityAttribute.getNames(), getLocale())));
        container.add(new WebMarkupContainer("required").setVisible(entityAttribute.isMandatory()));
    }

    private void addModuleInstanceTypeContainer(final DomainObject moduleInstance, String name) {
        WebMarkupContainer container = new WebMarkupContainer(name);
        container.setOutputMarkupPlaceholderTag(true);
        add(container);

        final Attribute attribute = moduleInstance.getAttribute(ModuleInstanceStrategy.MODULE_INSTANCE_TYPE);
        //final SimpleTypeModel<Integer> model = new SimpleTypeModel<>(attribute, new IntegerConverter());
        final Model<DomainObject> model = new Model<DomainObject>(new DomainObject(attribute.getValueId())) {
            @Override
            public void setObject(DomainObject object) {
                super.setObject(object);
                attribute.setValueId(object.getObjectId());
            }
        };
        container.add(
            new DropDownChoice<>("type",
                    model,
                    moduleInstanceTypeStrategy.getAll(),
                    new IChoiceRenderer<DomainObject>() {
                        @Override
                        public Object getDisplayValue(DomainObject type) {
                            return moduleInstanceTypeStrategy.displayDomainObject(type, getLocale());
                        }

                        @Override
                        public String getIdValue(DomainObject type, int i) {
                            return type != null && type.getObjectId() != null ? type.getObjectId().toString(): "-1";
                        }

                        @Override
                        public DomainObject getObject(String id, IModel<? extends List<? extends DomainObject>> choices) {
                            return choices.getObject().stream().filter(c -> id.equals(c.getObjectId().toString())).findAny().orElse(null);
                        }
                    }
            )
        );

        final EntityAttribute entityAttribute =
                moduleInstanceStrategy.getEntity().getAttributeType(ModuleInstanceStrategy.MODULE_INSTANCE_TYPE);
        container.add(new Label("label",
                DomainObjectComponentUtil.labelModel(entityAttribute.getNames(), getLocale())));
        container.add(new WebMarkupContainer("required").setVisible(entityAttribute.isMandatory()));
    }

    private WebMarkupContainer addPrivateKeyContainer(final long attributeTypeId, boolean disabled,
                                                      DomainObject moduleInstance, String name, final CallbackButton callbackButton) {
        WebMarkupContainer container = new WebMarkupContainer(name);
        container.setOutputMarkupPlaceholderTag(true);
        add(container);
        Attribute attribute = moduleInstance.getAttribute(attributeTypeId);
        if (attribute == null) {
            attribute = new Attribute();
            attribute.setAttributeTypeId(attributeTypeId);
            attribute.setObjectId(moduleInstance.getObjectId());
            attribute.setAttributeId(1L);
            attribute.setStringValues(StringValueUtil.newStringValues());
        }
        final AttributeStringModel attributeModel = new AttributeStringModel(attribute);
        final EntityAttribute entityAttribute =
                moduleInstanceStrategy.getEntity().getAttributeType(attributeTypeId);
        container.add(new Label("label",
                DomainObjectComponentUtil.labelModel(entityAttribute.getNames(), getLocale())));
        container.add(new WebMarkupContainer("required").setVisible(entityAttribute.isMandatory()));

        final Component key =
                DomainObjectComponentUtil.newInputComponent("module_instance", getStrategyName(),
                        moduleInstance, attribute, getLocale(), disabled);
        key.setOutputMarkupId(true);
        container.add(key);

        container.add(
                new AjaxButton("update") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        callbackButton.onSubmit(attributeModel);
                        target.add(key);
                    }
                }
        );

        return container;
    }

    protected String getStrategyName() {
        return null;
    }

    public static interface CallbackButton {
        void onSubmit(AttributeStringModel attributeStringModel);
    }
}

