package org.complitex.osznconnection.organization.strategy.web.edit;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.AttributeType;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.StringCultureBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.StringCultures;
import org.complitex.common.web.component.DomainObjectComponentUtil;
import org.complitex.organization.strategy.web.edit.OrganizationEditComponent;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;

import javax.ejb.EJB;

public class OsznOrganizationEditComponent extends OrganizationEditComponent {
    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private OsznOrganizationStrategy osznOrganizationStrategy;

    @EJB
    private StringCultureBean stringBean;

    private WebMarkupContainer loadSaveDirsSubsidyContainer;
    private WebMarkupContainer loadSaveDirsPrivilegesContainer;
    private WebMarkupContainer edrpouContainer;
    private WebMarkupContainer rootDirectoryContainer;
    private WebMarkupContainer rootExportDirectoryContainer;

    public OsznOrganizationEditComponent(String id, boolean disabled) {
        super(id, disabled);
    }


    @Override
    protected void init() {
        super.init();

        final DomainObject organization = getDomainObject();

        //Subsicy
        {
            loadSaveDirsSubsidyContainer = new WebMarkupContainer("loadSaveDirsSubsidyContainer");
            loadSaveDirsSubsidyContainer.setOutputMarkupPlaceholderTag(true);
            loadSaveDirsSubsidyContainer.setVisible(isSubsidyDepartment());
            add(loadSaveDirsSubsidyContainer);

            loadSaveDirsSubsidyContainer.add(new ListView<Long>("dir", OsznOrganizationStrategy.LOAD_SAVE_FILE_DIR_SUBSIDY_ATTRIBUTES) {

                @Override
                protected void populateItem(ListItem<Long> item) {
                    final long attributeTypeId = item.getModelObject();
                    final AttributeType attributeType =
                            osznOrganizationStrategy.getEntity().getAttributeType(attributeTypeId);
                    item.add(new Label("label",
                            DomainObjectComponentUtil.labelModel(attributeType.getAttributeNames(), getLocale())));
                    item.add(new WebMarkupContainer("required").setVisible(attributeType.isMandatory()));

                    Attribute attribute = organization.getAttribute(attributeTypeId);
                    if (attribute == null) {
                        attribute = new Attribute();
                        attribute.setAttributeTypeId(attributeTypeId);
                        attribute.setObjectId(organization.getObjectId());
                        attribute.setAttributeId(1L);
                        attribute.setStringCultures(StringCultures.newStringCultures());
                    }
                    item.add(DomainObjectComponentUtil.newInputComponent("organization", getStrategyName(), organization,
                            attribute, getLocale(), isDisabled()));
                }
            });

        }

        //Privileges
        {
            loadSaveDirsPrivilegesContainer = new WebMarkupContainer("loadSaveDirsPrivilegesContainer");
            loadSaveDirsPrivilegesContainer.setOutputMarkupPlaceholderTag(true);
            loadSaveDirsPrivilegesContainer.setVisible(isPrivilegesDepartment());
            add(loadSaveDirsPrivilegesContainer);

            loadSaveDirsPrivilegesContainer.add(new ListView<Long>("dir", OsznOrganizationStrategy.LOAD_SAVE_FILE_DIR_PRIVILEGES_ATTRIBUTES) {

                @Override
                protected void populateItem(ListItem<Long> item) {
                    final long attributeTypeId = item.getModelObject();
                    final AttributeType attributeType =
                            osznOrganizationStrategy.getEntity().getAttributeType(attributeTypeId);
                    item.add(new Label("label",
                            DomainObjectComponentUtil.labelModel(attributeType.getAttributeNames(), getLocale())));
                    item.add(new WebMarkupContainer("required").setVisible(attributeType.isMandatory()));

                    Attribute attribute = organization.getAttribute(attributeTypeId);
                    if (attribute == null) {
                        attribute = new Attribute();
                        attribute.setAttributeTypeId(attributeTypeId);
                        attribute.setObjectId(organization.getObjectId());
                        attribute.setAttributeId(1L);
                        attribute.setStringCultures(StringCultures.newStringCultures());
                    }
                    item.add(DomainObjectComponentUtil.newInputComponent("organization", getStrategyName(), organization,
                            attribute, getLocale(), isDisabled()));
                }
            });
        }

        //EDRPOU. It is user organization only attribute.
        {
            edrpouContainer = new WebMarkupContainer("edrpouContainer");
            edrpouContainer.setOutputMarkupPlaceholderTag(true);
            add(edrpouContainer);

            Attribute attribute = organization.getAttribute(OsznOrganizationStrategy.EDRPOU);

            if (attribute == null) {
                attribute = new Attribute();
                attribute.setAttributeTypeId(OsznOrganizationStrategy.EDRPOU);
                attribute.setObjectId(organization.getObjectId());
                attribute.setAttributeId(1L);
                attribute.setStringCultures(StringCultures.newStringCultures());
            }
            final AttributeType attributeType =
                    osznOrganizationStrategy.getEntity().getAttributeType(OsznOrganizationStrategy.EDRPOU);
            edrpouContainer.add(new Label("label",
                    DomainObjectComponentUtil.labelModel(attributeType.getAttributeNames(), getLocale())));
            edrpouContainer.add(new WebMarkupContainer("required").setVisible(attributeType.isMandatory()));

            edrpouContainer.add(DomainObjectComponentUtil.newInputComponent("organization", getStrategyName(),
                    organization, attribute, getLocale(), isDisabled()));

            //initial visibility
            edrpouContainer.setVisible(isUserOrganization());
        }

        //Root directory for loading and saving request files. It is user organization only attribute.
        {
            rootDirectoryContainer = new WebMarkupContainer("rootDirectoryContainer");
            rootDirectoryContainer.setOutputMarkupPlaceholderTag(true);
            add(rootDirectoryContainer);

            final long attributeTypeId = OsznOrganizationStrategy.ROOT_REQUEST_FILE_DIRECTORY;
            Attribute attribute = organization.getAttribute(attributeTypeId);
            if (attribute == null) {
                attribute = new Attribute();
                attribute.setAttributeTypeId(attributeTypeId);
                attribute.setObjectId(organization.getObjectId());
                attribute.setAttributeId(1L);
                attribute.setStringCultures(StringCultures.newStringCultures());
            }
            final AttributeType attributeType =
                    osznOrganizationStrategy.getEntity().getAttributeType(attributeTypeId);
            rootDirectoryContainer.add(new Label("label",
                    DomainObjectComponentUtil.labelModel(attributeType.getAttributeNames(), getLocale())));
            rootDirectoryContainer.add(new WebMarkupContainer("required").setVisible(attributeType.isMandatory()));

            rootDirectoryContainer.add(DomainObjectComponentUtil.newInputComponent("organization", getStrategyName(),
                    organization, attribute, getLocale(), isDisabled()));

            //initial visibility
            rootDirectoryContainer.setVisible(isUserOrganization());
        }

        //Root Export directory for loading and saving request files. It is user organization only attribute.
        {
            rootExportDirectoryContainer = new WebMarkupContainer("rootExportDirectoryContainer");
            rootExportDirectoryContainer.setOutputMarkupPlaceholderTag(true);
            add(rootExportDirectoryContainer);

            final long attributeTypeId = OsznOrganizationStrategy.ROOT_EXPORT_DIRECTORY;
            Attribute attribute = organization.getAttribute(attributeTypeId);
            if (attribute == null) {
                attribute = new Attribute();
                attribute.setAttributeTypeId(attributeTypeId);
                attribute.setObjectId(organization.getObjectId());
                attribute.setAttributeId(1L);
                attribute.setStringCultures(StringCultures.newStringCultures());
            }
            final AttributeType attributeType =
                    osznOrganizationStrategy.getEntity().getAttributeType(attributeTypeId);
            rootExportDirectoryContainer.add(new Label("label",
                    DomainObjectComponentUtil.labelModel(attributeType.getAttributeNames(), getLocale())));
            rootExportDirectoryContainer.add(new WebMarkupContainer("required").setVisible(attributeType.isMandatory()));

            rootExportDirectoryContainer.add(DomainObjectComponentUtil.newInputComponent("organization", getStrategyName(),
                    organization, attribute, getLocale(), isDisabled()));

            //initial visibility
            rootExportDirectoryContainer.setVisible(isUserOrganization());
        }
    }

    @Override
    protected void onOrganizationTypeChanged(AjaxRequestTarget target) {
        super.onOrganizationTypeChanged(target);

        //subsidy
        {
            boolean loadSaveDirsContainerWasVisible = loadSaveDirsSubsidyContainer.isVisible();
            loadSaveDirsSubsidyContainer.setVisible(isSubsidyDepartment());
            boolean loadSaveDirsContainerVisibleNow = loadSaveDirsSubsidyContainer.isVisible();
            if (loadSaveDirsContainerWasVisible ^ loadSaveDirsContainerVisibleNow) {
                target.add(loadSaveDirsSubsidyContainer);
            }
        }

        //privileges
        {
            boolean loadSaveDirsContainerWasVisible = loadSaveDirsPrivilegesContainer.isVisible();
            loadSaveDirsPrivilegesContainer.setVisible(isPrivilegesDepartment());
            boolean loadSaveDirsContainerVisibleNow = loadSaveDirsPrivilegesContainer.isVisible();
            if (loadSaveDirsContainerWasVisible ^ loadSaveDirsContainerVisibleNow) {
                target.add(loadSaveDirsPrivilegesContainer);
            }
        }

        //edrpou.
        {
            boolean edrpouContainerWasVisible = edrpouContainer.isVisible();
            edrpouContainer.setVisible(isUserOrganization());
            boolean edrpouContainerVisibleNow = edrpouContainer.isVisible();
            if (edrpouContainerWasVisible ^ edrpouContainerVisibleNow) {
                target.add(edrpouContainer);
            }
        }

        //root directory.
        {
            boolean rootDirectoryContainerWasVisible = rootDirectoryContainer.isVisible();
            rootDirectoryContainer.setVisible(isUserOrganization());
            boolean rootDirectoryContainerVisibleNow = rootDirectoryContainer.isVisible();
            if (rootDirectoryContainerWasVisible ^ rootDirectoryContainerVisibleNow) {
                target.add(rootDirectoryContainer);
            }
        }

        //root export directory.
        {
            boolean rootExportDirectoryContainerWasVisible = rootDirectoryContainer.isVisible();
            rootExportDirectoryContainer.setVisible(isUserOrganization());
            boolean rootExportDirectoryContainerVisibleNow = rootDirectoryContainer.isVisible();
            if (rootExportDirectoryContainerWasVisible ^ rootExportDirectoryContainerVisibleNow) {
                target.add(rootExportDirectoryContainer);
            }
        }
    }

    public boolean isSubsidyDepartment() {
        for (DomainObject organizationType : getOrganizationTypesModel().getObject()) {
            if (organizationType.getObjectId().equals(OsznOrganizationTypeStrategy.SUBSIDY_DEPARTMENT_TYPE)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPrivilegesDepartment() {
        for (DomainObject organizationType : getOrganizationTypesModel().getObject()) {
            if (organizationType.getObjectId().equals(OsznOrganizationTypeStrategy.PRIVILEGE_DEPARTMENT_TYPE)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUserOrganization() {
        return super.isUserOrganization();
    }

    @Override
    protected boolean isDistrictRequired() {
        return isSubsidyDepartment() || isPrivilegesDepartment();
    }

    @Override
    protected boolean isDistrictVisible() {
        return super.isDistrictVisible() || isSubsidyDepartment() || isPrivilegesDepartment();
    }

    @Override
    protected void onPersist() {
        super.onPersist();

        final DomainObject organization = getDomainObject();

        if (!isSubsidyDepartment()) {
            //load/save request file dirs
            for (long attributeTypeId : OsznOrganizationStrategy.LOAD_SAVE_FILE_DIR_SUBSIDY_ATTRIBUTES) {
                organization.removeAttribute(attributeTypeId);
            }
        }

        if (!isPrivilegesDepartment()) {
            //load/save request file dirs
            for (long attributeTypeId : OsznOrganizationStrategy.LOAD_SAVE_FILE_DIR_PRIVILEGES_ATTRIBUTES) {
                organization.removeAttribute(attributeTypeId);
            }
        }

        if (!isUserOrganization()) {
            //edrpou
            organization.removeAttribute(OsznOrganizationStrategy.EDRPOU);

            //root directory
            organization.removeAttribute(OsznOrganizationStrategy.ROOT_REQUEST_FILE_DIRECTORY);
            organization.removeAttribute(OsznOrganizationStrategy.ROOT_EXPORT_DIRECTORY);
        }
    }

    @Override
    protected String getStrategyName() {
        return OsznOrganizationStrategy.OSZN_ORGANIZATION_STRATEGY_NAME;
    }
}
