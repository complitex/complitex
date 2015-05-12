package org.complitex.common.strategy;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.*;
import org.complitex.common.exception.DeleteException;
import org.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import org.complitex.common.web.component.domain.validate.IValidator;
import org.complitex.common.web.component.search.ISearchCallback;
import org.complitex.common.web.component.search.SearchComponentState;

import java.util.*;

public interface IStrategy {
    
    void archive(DomainObject object, Date endDate);
    
    void archiveAttributes(Collection<Long> attributeTypeIds, Date endDate);

    void configureFilter(DomainObjectFilter filter, Map<String, Long> ids, String searchTextInput);
    
    Long getCount(DomainObjectFilter example);

    List<? extends DomainObject> getList(DomainObjectFilter example);

    void disable(DomainObject object);

    String displayDomainObject(DomainObject object, Locale locale);

    String displayDomainObject(Long objectId, Locale locale);

    String displayAttribute(Attribute attribute, Locale locale);

    void enable(DomainObject object);
    
    DomainObject getDomainObject(Long id, boolean runAsAdmin);
    
    DomainObject getDomainObject(String dataSource, Long id, boolean runAsAdmin);

    public Long getObjectId(String externalId);
    
    DomainObject getHistoryObject(long objectId, Date date);

    EntityObjectInfo findParentInSearchComponent(long id, Date date);

    String getAttributeLabel(Attribute attribute, Locale locale);

    String[] getRealChildren();

    String[] getLogicalChildren();

    Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelBeforeClass();

    Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass();

    long getDefaultOrderByAttributeId();

    Class<? extends WebPage> getEditPage();

    PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity);

    Entity getEntity(String dataSource);

    Entity getEntity();

    String getEntityName();
    
    List<History> getHistory(long objectId);
    
    TreeSet<Date> getHistoryDates(long objectId);

    Class<? extends WebPage> getHistoryPage();

    PageParameters getHistoryPageParams(long objectId);

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
    
    void insert(DomainObject object, Date insertDate);

    boolean isSimpleAttribute(final Attribute attribute);

    boolean isSimpleAttributeType(AttributeType attributeType);

    DomainObject newInstance();
    
    Long performDefaultValidation(DomainObject object, Locale locale);
    
    void update(DomainObject oldObject, DomainObject newObject, Date updateDate);
    
    void updateAndPropagate(DomainObject oldObject, DomainObject newObject, Date updateDate);
    
    void replacePermissions(PermissionInfo objectPermissionInfo, Set<Long> subjectIds);

    boolean isNeedToChangePermission(Set<Long> oldSubjectIds, Set<Long> newSubjectIds);

    String[] getListRoles();

    String[] getEditRoles();
    
    void changePermissions(PermissionInfo objectPermissionInfo, Set<Long> addSubjectIds, Set<Long> removeSubjectIds);

    void changePermissionsInDistinctThread(long objectId, long permissionId, Set<Long> addSubjectIds, Set<Long> removeSubjectIds);
    
    void changeChildrenActivity(long parentId, boolean enable);

    boolean canPropagatePermissions(DomainObject object);
    
    void delete(long objectId, Locale locale) throws DeleteException;

    String[] getDescriptionRoles();

    Page getObjectNotFoundPage();
}
