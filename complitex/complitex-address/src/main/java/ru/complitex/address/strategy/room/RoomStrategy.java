package ru.complitex.address.strategy.room;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.string.Strings;
import ru.complitex.address.resource.CommonResources;
import ru.complitex.address.strategy.room.web.edit.RoomEdit;
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
public class RoomStrategy extends TemplateStrategy {
    public static final String ROOM_NS = RoomStrategy.class.getName();

    @EJB
    private StringValueBean stringBean;

    /*
     * Attribute type ids
     */
    public static final Long NAME = 200L;

    @Override
    public String getEntityName() {
        return "room";
    }

    @Override
    public List<Long> getColumnAttributeTypeIds() {
        return Lists.newArrayList(NAME);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return object.getStringValue(NAME, locale);
    }

    @Override
    public List<String> getSearchFilters() {
        return ImmutableList.of("country", "region", "city", "street", "building", "apartment");
    }

    @Override
    public ISearchCallback getSearchCallback() {
        return new ISearchCallback() {
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
        Long apartmentId = ids.get("apartment");
        if (apartmentId != null && apartmentId > 0) {
            example.setParentId(apartmentId);
            example.setParentEntity("apartment");
        } else {
            Long buildingId = ids.get("building");
            if (buildingId != null && buildingId > 0) {
                example.setParentId(buildingId);
                example.setParentEntity("building");
            } else {
                example.setParentId(-1L);
                example.setParentEntity("");
            }
        }
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
            Long apartmentId = ids.get("apartment");
            if (apartmentId != null && apartmentId > 0) {
                object.setParentId(apartmentId);
                object.setParentEntityId(100L);
            } else {
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
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(CommonResources.class.getName(), getEntityName(), locale);
    }

    @Override
    public String[] getParents() {
        return new String[]{"apartment", "building"};
    }

    @Override
    public int getSearchTextFieldSize() {
        return 5;
    }

    @Override
    public boolean allowProceedNextSearchFilter() {
        return true;
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_EDIT, SecurityRole.ROOM_EDIT};
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_VIEW};
    }

    @Override
    public Class<? extends WebPage> getEditPage() {
        return RoomEdit.class;
    }

    @Override
    protected void extendOrderBy(DomainObjectFilter example) {
        if (example.getOrderByAttributeTypeId() != null
                && example.getOrderByAttributeTypeId().equals(NAME)) {
            example.setOrderByNumber(true);
        }
    }

    /**
     * Найти комнату в локальной адресной базе.
     */
    public List<Long> getRoomObjectIds(Long buildingId, Long apartmentId, String room) {
        Map<String, Object> params = Maps.newHashMap();

        params.put("buildingId", buildingId);
        params.put("apartmentId", apartmentId);
        params.put("number", room);

        return sqlSession().selectList(ROOM_NS + ".selectRoomObjectIds", params);
    }
}
