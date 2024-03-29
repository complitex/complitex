package ru.complitex.common.strategy;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.*;
import ru.complitex.common.entity.*;
import ru.complitex.common.exception.DeleteException;
import ru.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import ru.complitex.common.web.component.domain.validate.IValidator;
import ru.complitex.common.web.component.search.ISearchCallback;
import ru.complitex.common.web.component.search.SearchComponentState;

import java.util.*;

public interface IStrategy {
    
    void archive(DomainObject object, Date endDate);
    
    void archiveAttributes(Collection<Long> entityAttributeIds, Date endDate);

    void configureFilter(DomainObjectFilter filter, Map<String, Long> ids, String searchTextInput);
    
    Long getCount(DomainObjectFilter example);

    List<? extends DomainObject> getList(DomainObjectFilter example);

    void disable(DomainObject object);

    String displayDomainObject(DomainObject object, Locale locale);

    String displayDomainObject(Long objectId, Locale locale);

    String displayAttribute(Attribute attribute, Locale locale);

    void enable(DomainObject object);

    DomainObject getDomainObject(Long id, boolean runAsAdmin);

    DomainObject getDomainObject(Long id);
    
    DomainObject getDomainObject(String dataSource, Long id, boolean runAsAdmin);

    DomainObject getHistoryObject(Long objectId, Date date);

    EntityObjectInfo findParentInSearchComponent(Long id, Date date);

    String getAttributeLabel(Attribute attribute, Locale locale);

    String[] getRealChildren();

    String[] getLogicalChildren();

    Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelBeforeClass();

    Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass();

    Long getDefaultOrderByAttributeId();

    Class<? extends WebPage> getEditPage();

    PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity);

    Entity getEntity(String dataSource);

    Entity getEntity();

    String getEntityName();
    
    List<History> getHistory(Long objectId);
    
    TreeSet<Date> getHistoryDates(Long objectId);

    Class<? extends WebPage> getHistoryPage();

    PageParameters getHistoryPageParams(Long objectId);

    List<Long> getColumnAttributeTypeIds();

    Long getDefaultSortAttributeTypeId();

    Class<? extends WebPage> getListPage();

    PageParameters getListPageParams();

    ISearchCallback getParentSearchCallback();

    List<String> getParentSearchFilters();

    String[] getParents();

    String getPluralEntityLabel(Locale locale);

    ISearchCallback getSearchCallback();

    SearchComponentState getSearchComponentStateForParent(Long parentId, String parentEntity, Date date);

    List<String> getSearchFilters();

    int getSearchTextFieldSize();
    
    boolean allowProceedNextSearchFilter();

    IValidator getValidator();

    void insert(DomainObject object);

    void insert(DomainObject object, Date insertDate);

    boolean isSimpleAttributeType(EntityAttribute entityAttribute);

    DomainObject newInstance();
    
    Long performDefaultValidation(DomainObject object, Locale locale);

    void update(DomainObject domainObject);
    
    void update(DomainObject oldObject, DomainObject newObject, Date updateDate);
    
    void updateAndPropagate(DomainObject oldObject, DomainObject newObject, Date updateDate);
    
    void replacePermissions(PermissionInfo objectPermissionInfo, Set<Long> subjectIds);

    boolean isNeedToChangePermission(Set<Long> oldSubjectIds, Set<Long> newSubjectIds);

    String[] getListRoles();

    String[] getEditRoles();
    
    void changePermissions(PermissionInfo objectPermissionInfo, Set<Long> addSubjectIds, Set<Long> removeSubjectIds);

    void changePermissionsInDistinctThread(Long objectId, Long permissionId, Set<Long> addSubjectIds, Set<Long> removeSubjectIds);
    
    void changeChildrenActivity(Long parentId, boolean enable);

    boolean canPropagatePermissions(DomainObject object);

    void delete(Long objectId) throws DeleteException;
    
    void delete(Long objectId, Locale locale) throws DeleteException;

    String[] getDescriptionRoles();

    Page getObjectNotFoundPage();
}
