package ru.flexpay.eirc.dictionary.strategy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.*;
import org.complitex.common.strategy.StringCultureBean;
import org.complitex.common.util.ResourceUtil;
import org.complitex.common.util.StringCultures;
import org.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import org.complitex.common.web.component.domain.DomainObjectListPanel;
import org.complitex.common.web.component.search.ISearchCallback;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.pages.DomainObjectEdit;
import ru.flexpay.eirc.dictionary.strategy.resource.EircResources;
import ru.flexpay.eirc.dictionary.web.ModuleInstancePrivateKeyPanel;
import ru.flexpay.eirc.dictionary.web.security.SecurityRole;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Pavel Sknar
 */
@Stateless
public class ModuleInstanceStrategy extends TemplateStrategy {

    @EJB
    private StringCultureBean stringBean;

    /*
     * Attribute type ids
     */
    public static final long NAME = 1010L;
    public static final long PRIVATE_KEY = 1011L;
    public static final long UNIQUE_INDEX = 1012L;
    public static final long ORGANIZATION = 1013L;
    public static final long MODULE_INSTANCE_TYPE = 1014L;

    /**
     * Filter parameter to filter out module instances by types.
     */
    public static final String MODULE_INSTANCE_TYPE_PARAMETER = "moduleInstanceTypeIds";

    public static final List<Long> CUSTOM_ATTRIBUTES = ImmutableList.of(PRIVATE_KEY, ORGANIZATION);

    private static final String MODULE_INSTANCE_NS = ModuleInstanceStrategy.class.getName();

    @Override
    public List<Long> getColumnAttributeTypeIds() {
        return Lists.newArrayList(NAME, UNIQUE_INDEX);
    }

    @Override
    public String getEntityName() {
        return "module_instance";
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return object.getStringValue(NAME, locale);
    }

    @Override
    public ISearchCallback getSearchCallback() {
        return new SearchCallback();
    }

    private void configureExampleImpl(DomainObjectFilter example, Map<String, Long> ids, String searchTextInput) {
        if (!Strings.isEmpty(searchTextInput)) {
            AttributeFilter attrExample = example.getAttributeExample(NAME);
            if (attrExample == null) {
                attrExample = new AttributeFilter(NAME);
                example.addAttributeFilter(attrExample);
            }
            attrExample.setValue(searchTextInput);
        }
    }

    @Override
    public void configureExample(DomainObjectFilter example, Map<String, Long> ids, String searchTextInput) {
        configureExampleImpl(example, ids, searchTextInput);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.MODULE_INSTANCE_VIEW, SecurityRole.MODULE_INSTANCE_EDIT};
    }

    private class SearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
            DomainObjectListPanel list = component.findParent(DomainObjectListPanel.class);
            configureExampleImpl(list.getExample(), ids, null);
            list.refreshContent(target);
        }
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(EircResources.class.getName(), getEntityName(), locale);
    }

    @Override
    public boolean allowProceedNextSearchFilter() {
        return true;
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.MODULE_INSTANCE_VIEW};
    }

    @Override
    public Class<? extends WebPage> getEditPage() {
        return DomainObjectEdit.class;
    }

    @Override
    protected void extendOrderBy(DomainObjectFilter example) {
        if (example.getOrderByAttributeTypeId() != null
                && example.getOrderByAttributeTypeId().equals(NAME)) {
            example.setOrderByNumber(true);
        }
    }

    /**
     * Найти модуль по уникальному индексу.
     */
    public Long getModuleInstanceObjectId(String uniqueIndex) {
        Map<String, Object> params = Maps.newHashMap();

        params.put("uniqueIndex", uniqueIndex);

        return sqlSession().selectOne(MODULE_INSTANCE_NS + ".selectModuleInstanceId", params);
    }

    @Override
    public List<? extends DomainObject> getList(DomainObjectFilter example) {
        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return Collections.emptyList();
        }

        example.setEntityName(getEntityName());
        if (!example.isAdmin()) {
            prepareExampleForPermissionCheck(example);
        }
        extendOrderBy(example);

        List<DomainObject> organizations = sqlSession().selectList(MODULE_INSTANCE_NS + ".selectDomainObject", example);

        for (DomainObject object : organizations) {
            loadAttributes(object);
            //load subject ids
            object.setSubjectIds(loadSubjects(object.getPermissionId()));
        }

        return organizations;
    }

    @Override
    public boolean isSimpleAttributeType(AttributeType attributeType) {
        return !CUSTOM_ATTRIBUTES.contains(attributeType.getId()) && attributeType.getId() != MODULE_INSTANCE_TYPE &&
                super.isSimpleAttributeType(attributeType);
    }

    @Override
    protected void fillAttributes(String dataSource, DomainObject object) {
        super.fillAttributes(dataSource, object);

        for (long attributeTypeId : CUSTOM_ATTRIBUTES) {
            if (object.getAttribute(attributeTypeId).getStringCultures() == null) {
                object.getAttribute(attributeTypeId).setStringCultures(StringCultures.newStringCultures());
            }
        }
    }

    @Override
    protected void loadStringCultures(List<Attribute> attributes) {
        super.loadStringCultures(attributes);

        for (Attribute attribute : attributes) {
            if (CUSTOM_ATTRIBUTES.contains(attribute.getAttributeTypeId())) {
                if (attribute.getValueId() != null) {
                    loadStringCultures(attribute);
                } else {
                    attribute.setStringCultures(StringCultures.newStringCultures());
                }
            }
        }
    }

    @Override
    protected Long insertStrings(long attributeTypeId, List<StringCulture> strings) {
        /* if it's data source or one of load/save request file directory attributes
         * or root directory for loading and saving request files
         * then string value should be inserted as is and not upper cased. */
        return CUSTOM_ATTRIBUTES.contains(attributeTypeId)
                ? stringBean.save(strings, getEntityName(), false)
                : super.insertStrings(attributeTypeId, strings);
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass() {
        return ModuleInstancePrivateKeyPanel.class;
    }
}
