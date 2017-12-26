package org.complitex.osznconnection.organization.strategy.web.edit;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.EntityAttribute;
import org.complitex.common.strategy.StringValueBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.StringValueUtil;
import org.complitex.common.web.component.DomainObjectComponentUtil;
import org.complitex.organization.strategy.web.edit.OrganizationEditComponent;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;

import javax.ejb.EJB;

public class OsznOrganizationEditComponent extends OrganizationEditComponent {
    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private OsznOrganizationStrategy osznOrganizationStrategy;

    @EJB
    private StringValueBean stringBean;

    private WebMarkupContainer loadSaveDirsSubsidyContainer;
    private WebMarkupContainer loadSaveDirsPrivilegesContainer;
    private WebMarkupContainer edrpouContainer;
    private WebMarkupContainer rootDirectoryContainer;
    private WebMarkupContainer rootExportDirectoryContainer;
    private WebMarkupContainer referencesDirectoryContainer;

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
                    final long entityAttributeId = item.getModelObject();
                    final EntityAttribute entityAttribute =
                            osznOrganizationStrategy.getEntity().getAttribute(entityAttributeId);
                    item.add(new Label("label",
                            DomainObjectComponentUtil.labelModel(entityAttribute.getNames(), getLocale())));
                    item.add(new WebMarkupContainer("required").setVisible(entityAttribute.isRequired()));

                    Attribute attribute = organization.getAttribute(entityAttributeId);
                    if (attribute == null) {
                        attribute = new Attribute();
                        attribute.setEntityAttributeId(entityAttributeId);
                        attribute.setObjectId(organization.getObjectId());
                        attribute.setAttributeId(1L);
                        attribute.setStringValues(StringValueUtil.newStringValues());
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
                    final long entityAttributeId = item.getModelObject();
                    final EntityAttribute entityAttribute =
                            osznOrganizationStrategy.getEntity().getAttribute(entityAttributeId);
                    item.add(new Label("label",
                            DomainObjectComponentUtil.labelModel(entityAttribute.getNames(), getLocale())));
                    item.add(new WebMarkupContainer("required").setVisible(entityAttribute.isRequired()));

                    Attribute attribute = organization.getAttribute(entityAttributeId);
                    if (attribute == null) {
                        attribute = new Attribute();
                        attribute.setEntityAttributeId(entityAttributeId);
                        attribute.setObjectId(organization.getObjectId());
                        attribute.setAttributeId(1L);
                        attribute.setStringValues(StringValueUtil.newStringValues());
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
                attribute.setEntityAttributeId(OsznOrganizationStrategy.EDRPOU);
                attribute.setObjectId(organization.getObjectId());
                attribute.setAttributeId(1L);
                attribute.setStringValues(StringValueUtil.newStringValues());
            }
            final EntityAttribute entityAttribute =
                    osznOrganizationStrategy.getEntity().getAttribute(OsznOrganizationStrategy.EDRPOU);
            edrpouContainer.add(new Label("label",
                    DomainObjectComponentUtil.labelModel(entityAttribute.getNames(), getLocale())));
            edrpouContainer.add(new WebMarkupContainer("required").setVisible(entityAttribute.isRequired()));

            edrpouContainer.add(DomainObjectComponentUtil.newInputComponent("organization", getStrategyName(),
                    organization, attribute, getLocale(), isDisabled()));

            //initial visibility
            edrpouContainer.setVisible(isServiceProvider() || isSubsidyDepartment() || isPrivilegesDepartment());
        }

        //Root directory for loading and saving request files. It is user organization only attribute.
        {
            rootDirectoryContainer = new WebMarkupContainer("rootDirectoryContainer");
            rootDirectoryContainer.setOutputMarkupPlaceholderTag(true);
            add(rootDirectoryContainer);

            final long entityAttributeId = OsznOrganizationStrategy.ROOT_REQUEST_FILE_DIRECTORY;
            Attribute attribute = organization.getAttribute(entityAttributeId);
            if (attribute == null) {
                attribute = new Attribute();
                attribute.setEntityAttributeId(entityAttributeId);
                attribute.setObjectId(organization.getObjectId());
                attribute.setAttributeId(1L);
                attribute.setStringValues(StringValueUtil.newStringValues());
            }
            final EntityAttribute entityAttribute =
                    osznOrganizationStrategy.getEntity().getAttribute(entityAttributeId);
            rootDirectoryContainer.add(new Label("label",
                    DomainObjectComponentUtil.labelModel(entityAttribute.getNames(), getLocale())));
            rootDirectoryContainer.add(new WebMarkupContainer("required").setVisible(entityAttribute.isRequired()));

            rootDirectoryContainer.add(DomainObjectComponentUtil.newInputComponent("organization", getStrategyName(),
                    organization, attribute, getLocale(), isDisabled()));

            //initial visibility
            rootDirectoryContainer.setVisible(isSubsidyDepartment() || isPrivilegesDepartment());
        }

        //Root Export directory for loading and saving request files. It is user organization only attribute.
        {
            rootExportDirectoryContainer = new WebMarkupContainer("rootExportDirectoryContainer");
            rootExportDirectoryContainer.setOutputMarkupPlaceholderTag(true);
            add(rootExportDirectoryContainer);

            final long entityAttributeId = OsznOrganizationStrategy.ROOT_EXPORT_DIRECTORY;
            Attribute attribute = organization.getAttribute(entityAttributeId);
            if (attribute == null) {
                attribute = new Attribute();
                attribute.setEntityAttributeId(entityAttributeId);
                attribute.setObjectId(organization.getObjectId());
                attribute.setAttributeId(1L);
                attribute.setStringValues(StringValueUtil.newStringValues());
            }
            final EntityAttribute entityAttribute =
                    osznOrganizationStrategy.getEntity().getAttribute(entityAttributeId);
            rootExportDirectoryContainer.add(new Label("label",
                    DomainObjectComponentUtil.labelModel(entityAttribute.getNames(), getLocale())));
            rootExportDirectoryContainer.add(new WebMarkupContainer("required").setVisible(entityAttribute.isRequired()));

            rootExportDirectoryContainer.add(DomainObjectComponentUtil.newInputComponent("organization", getStrategyName(),
                    organization, attribute, getLocale(), isDisabled()));

            //initial visibility
            rootExportDirectoryContainer.setVisible(isSubsidyDepartment() || isPrivilegesDepartment());
        }

        //referencesDirectoryContainer
        {
            referencesDirectoryContainer = new WebMarkupContainer("referencesDirectoryContainer");
            referencesDirectoryContainer.setOutputMarkupPlaceholderTag(true);
            add(referencesDirectoryContainer);

            long entityAttributeId = OsznOrganizationStrategy.REFERENCES_DIR;
            Attribute attribute = organization.getAttribute(entityAttributeId);
            if (attribute == null) {
                attribute = new Attribute();
                attribute.setEntityAttributeId(entityAttributeId);
                attribute.setObjectId(organization.getObjectId());
                attribute.setAttributeId(1L);
                attribute.setStringValues(StringValueUtil.newStringValues());
            }
            EntityAttribute entityAttribute = osznOrganizationStrategy.getEntity().getAttribute(entityAttributeId);
            referencesDirectoryContainer.add(new Label("label", DomainObjectComponentUtil.labelModel(
                    entityAttribute.getNames(), getLocale())));
            referencesDirectoryContainer.add(new WebMarkupContainer("required").setVisible(entityAttribute.isRequired()));

            referencesDirectoryContainer.add(DomainObjectComponentUtil.newInputComponent("organization", getStrategyName(),
                    organization, attribute, getLocale(), isDisabled()));

            referencesDirectoryContainer.setVisible(isPrivilegesDepartment() || isSubsidyDepartment());
        }
    }

    @Override
    protected void onOrganizationTypeChanged(AjaxRequestTarget target) {
        super.onOrganizationTypeChanged(target);

        //subsidy
        {
            loadSaveDirsSubsidyContainer.setVisible(isSubsidyDepartment());
            target.add(loadSaveDirsSubsidyContainer);
        }

        //privileges
        {
            loadSaveDirsPrivilegesContainer.setVisible(isPrivilegesDepartment());
            target.add(loadSaveDirsPrivilegesContainer);
        }

        //edrpou.
        {
            edrpouContainer.setVisible(isSubsidyDepartment() || isPrivilegesDepartment() || isServiceProvider());
            target.add(edrpouContainer);
        }

        //root directory.
        {
            rootDirectoryContainer.setVisible(isSubsidyDepartment() || isPrivilegesDepartment());
            target.add(rootDirectoryContainer);
        }

        //root export directory.
        {
            rootExportDirectoryContainer.setVisible(isSubsidyDepartment() || isPrivilegesDepartment());
            target.add(rootExportDirectoryContainer);
        }

        //referencesDirectoryContainer
        {
            referencesDirectoryContainer.setVisible(isPrivilegesDepartment() || isSubsidyDepartment());
            target.add(referencesDirectoryContainer);
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
            for (long entityAttributeId : OsznOrganizationStrategy.LOAD_SAVE_FILE_DIR_SUBSIDY_ATTRIBUTES) {
                organization.removeAttribute(entityAttributeId);
            }
        }

        if (!isPrivilegesDepartment()) {
            //load/save request file dirs
            for (long entityAttributeId : OsznOrganizationStrategy.LOAD_SAVE_FILE_DIR_PRIVILEGES_ATTRIBUTES) {
                organization.removeAttribute(entityAttributeId);
            }
        }

        if (!isSubsidyDepartment() && !isPrivilegesDepartment()){
            organization.removeAttribute(OsznOrganizationStrategy.REFERENCES_DIR);
        }

        if (!isUserOrganization() && !isServiceProvider()){
            //edrpou
            organization.removeAttribute(OsznOrganizationStrategy.EDRPOU);
        }

        if (!isUserOrganization()) {
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
