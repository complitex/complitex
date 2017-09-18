package ru.flexpay.eirc.dictionary.strategy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.*;
import org.complitex.common.strategy.StringValueBean;
import org.complitex.common.util.ResourceUtil;
import org.complitex.common.util.StringValueUtil;
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
    private StringValueBean stringBean;

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
    public void configureFilter(DomainObjectFilter filter, Map<String, Long> ids, String searchTextInput) {
        configureExampleImpl(filter, ids, searchTextInput);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.MODULE_INSTANCE_VIEW, SecurityRole.MODULE_INSTANCE_EDIT};
    }

    private class SearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
            DomainObjectListPanel list = component.findParent(DomainObjectListPanel.class);
            configureExampleImpl(list.getFilter(), ids, null);
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
            object.setSubjectIds(getSubjects(object.getPermissionId()));
        }

        return organizations;
    }

    @Override
    protected void fillAttributes(String dataSource, DomainObject object) {
        super.fillAttributes(dataSource, object);

        for (long attributeTypeId : CUSTOM_ATTRIBUTES) {
            if (object.getAttribute(attributeTypeId).getStringValues() == null) {
                object.getAttribute(attributeTypeId).setStringValues(StringValueUtil.newStringValues());
            }
        }
    }

    @Override
    protected void loadStringValues(List<Attribute> attributes) {
        super.loadStringValues(attributes);

        for (Attribute attribute : attributes) {
            if (CUSTOM_ATTRIBUTES.contains(attribute.getEntityAttributeId())) {
                if (attribute.getValueId() != null) {
                    loadStringValues(attribute);
                } else {
                    attribute.setStringValues(StringValueUtil.newStringValues());
                }
            }
        }
    }

    @Override
    protected Long insertStrings(Long attributeTypeId, List<StringValue> strings) {
        return CUSTOM_ATTRIBUTES.contains(attributeTypeId)
                ? stringBean.save(strings, getEntityName(), false)
                : super.insertStrings(attributeTypeId, strings);
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass() {
        return ModuleInstancePrivateKeyPanel.class;
    }
}
