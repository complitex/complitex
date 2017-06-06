package org.complitex.address.strategy.apartment;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.resource.CommonResources;
import org.complitex.address.strategy.apartment.web.edit.ApartmentEdit;
import org.complitex.common.entity.AttributeFilter;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.StringValueBean;
import org.complitex.common.util.ResourceUtil;
import org.complitex.common.web.component.DomainObjectInputPanel;
import org.complitex.common.web.component.domain.DomainObjectListPanel;
import org.complitex.common.web.component.search.ISearchCallback;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Artem
 */
@Stateless
public class ApartmentStrategy extends TemplateStrategy {
    public static final String APARTMENT_NS = ApartmentStrategy.class.getName();

    @EJB
    private StringValueBean stringBean;

    /*
     * Attribute type ids
     */
    public static final long NAME = 100L;
    public static final long PARENT_ENTITY_ID = 500L;


    @Override
    public List<Long> getColumnAttributeTypeIds() {
        return Lists.newArrayList(NAME);
    }

    @Override
    public String getEntityName() {
        return "apartment";
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return object.getStringValue(NAME, locale);
    }

    @Override
    public ISearchCallback getSearchCallback() {
        return new ISearchCallback(){
            @Override
            public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
                DomainObjectListPanel list = component.findParent(DomainObjectListPanel.class);
                configureExampleImpl(list.getFilter(), ids, null);
                list.refreshContent(target);
            }
        };
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
        Long buildingId = ids.get("building");
        if (buildingId != null && buildingId > 0) {
            example.setParentId(buildingId);
            example.setParentEntity("building");
        } else {
            example.setParentId(-1L);
            example.setParentEntity("");
        }
    }

    @Override
    public void configureFilter(DomainObjectFilter filter, Map<String, Long> ids, String searchTextInput) {
        configureExampleImpl(filter, ids, searchTextInput);
    }

    @Override
    public List<String> getSearchFilters() {
        return ImmutableList.of("country", "region", "city", "street", "building");
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_EDIT, SecurityRole.APARTMENT_EDIT};
    }

    @Override
    public ISearchCallback getParentSearchCallback() {
        return new ParentSearchCallback();
    }

    private static class ParentSearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
            DomainObjectInputPanel inputPanel = component.findParent(DomainObjectInputPanel.class);
            DomainObject object = inputPanel.getObject();
            Long buildingId = ids.get("building");
            if (buildingId != null && buildingId > 0) {
                object.setParentId(buildingId);
                object.setParentEntityId(500L);
            } else {
                object.setParentId(null);
                object.setParentEntityId(null);
            }
        }
    }

    @Override
    public String[] getRealChildren() {
        return new String[]{"room"};
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(CommonResources.class.getName(), getEntityName(), locale);
    }

    @Override
    public String[] getParents() {
        return new String[]{"building"};
    }

    @Override
    public int getSearchTextFieldSize() {
        return 3;
    }

    @Override
    public boolean allowProceedNextSearchFilter() {
        return true;
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_VIEW};
    }

    @Override
    public Class<? extends WebPage> getEditPage() {
        return ApartmentEdit.class;
    }

    @Override
    protected void extendOrderBy(DomainObjectFilter example) {
        if (example.getOrderByAttributeTypeId() != null
                && example.getOrderByAttributeTypeId().equals(NAME)) {
            example.setOrderByNumber(true);
        }
    }

    /**
     * Найти квартиру в локальной адресной базе.
     */
    public List<Long> getApartmentObjectIds(Long buildingId, String apartment) {
        Map<String, Object> params = Maps.newHashMap();

        params.put("buildingId", buildingId);
        params.put("number", apartment);

        return sqlSession().selectList(APARTMENT_NS + ".selectApartmentObjectIds", params);
    }
}
