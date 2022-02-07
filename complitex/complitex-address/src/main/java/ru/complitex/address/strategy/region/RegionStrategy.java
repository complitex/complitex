package ru.complitex.address.strategy.region;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.string.Strings;
import ru.complitex.address.resource.CommonResources;
import ru.complitex.common.entity.AttributeFilter;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.strategy.StringValueBean;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.common.web.component.DomainObjectInputPanel;
import ru.complitex.common.web.component.domain.DomainObjectListPanel;
import ru.complitex.common.web.component.search.ISearchCallback;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.security.SecurityRole;

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
public class RegionStrategy extends TemplateStrategy {

    @EJB
    private StringValueBean stringBean;

    /*
     * Attribute type ids
     */
    public static final long NAME = 700L;
    public static final long PARENT_ENTITY_ID = 800L;

    @Override
    public List<Long> getColumnAttributeTypeIds() {
        return Lists.newArrayList(NAME);
    }

    @Override
    public String getEntityName() {
        return "region";
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

                if (list != null) {
                    configureExampleImpl(list.getFilter(), ids, null);
                    list.refreshContent(target);
                }
            }
        };
    }

    @Override
    public void configureFilter(DomainObjectFilter filter, Map<String, Long> ids, String searchTextInput) {
        configureExampleImpl(filter, ids, searchTextInput);
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
        Long countryId = ids.get("country");
        if (countryId != null && countryId > 0) {
            example.setParentId(countryId);
            example.setParentEntity("country");
        } else {
            example.setParentId(-1L);
            example.setParentEntity("");
        }
    }

    @Override
    public List<String> getSearchFilters() {
        return ImmutableList.of("country");
    }

    @Override
    public ISearchCallback getParentSearchCallback() {
        return new ParentSearchCallback();
    }

    private static class ParentSearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
            DomainObjectInputPanel inputPanel = component.findParent(DomainObjectInputPanel.class);
            Long countryId = ids.get("country");
            if (countryId != null && countryId > 0) {
                inputPanel.getObject().setParentId(countryId);
                inputPanel.getObject().setParentEntityId(800L);
            } else {
                inputPanel.getObject().setParentId(null);
                inputPanel.getObject().setParentEntityId(null);
            }
        }
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(CommonResources.class.getName(), getEntityName(), locale);
    }

    @Override
    public String[] getRealChildren() {
        return new String[]{"city"};
    }

    @Override
    public String[] getParents() {
        return new String[]{"country"};
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_EDIT};
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_VIEW};
    }
}
