package ru.complitex.organization.service;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Strings;
import ru.complitex.common.entity.Attribute;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.exception.ImportFileNotFoundException;
import ru.complitex.common.exception.ImportFileReadException;
import ru.complitex.common.service.AbstractImportService;
import ru.complitex.common.service.IImportListener;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.util.CloneUtil;
import ru.complitex.organization.entity.OrganizationImport;
import ru.complitex.organization.exception.RootOrganizationNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.*;

import static ru.complitex.common.strategy.organization.IOrganizationStrategy.*;
import static ru.complitex.organization.entity.OrganizationImportFile.ORGANIZATION;
import static ru.complitex.organization_type.strategy.OrganizationTypeStrategy.SERVICING_ORGANIZATION_TYPE;

@Stateless
public class OrganizationImportService extends AbstractImportService {

    private final Logger log = LoggerFactory.getLogger(OrganizationImportService.class);

    @EJB
    private OrganizationImportBean organizationImportBean;

    @EJB(lookup = IOrganizationStrategy.BEAN_LOOKUP)
    private IOrganizationStrategy organizationStrategy;

    @EJB
    private StringLocaleBean stringLocaleBean;

    /**
     * ID CODE SHORT_NAME NAME HLEVEL
     */
    public void process(IImportListener listener, Locale locale, Date beginDate)
            throws ImportFileNotFoundException, ImportFileReadException, RootOrganizationNotFound {

        organizationImportBean.delete();

        listener.beginImport(ORGANIZATION, getRecordCount(ORGANIZATION));

        CSVReader reader = getCsvReader(ORGANIZATION);

        int recordIndex = 0;

        try {
            String[] line;
            while ((line = reader.readNext()) != null) {
                final String organizationId = line[0].trim();
                final String code = line[1].trim();
                final String shortName = line[2].trim();
                final String fullName = line[3].trim();
                final String hlevelString = line[4];
                final Long hlevel = Strings.isNullOrEmpty(hlevelString) ? null : Long.parseLong(hlevelString);
                organizationImportBean.importOrganization(new OrganizationImport(organizationId, code, shortName, fullName, hlevel));
                recordIndex++;
            }
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, ORGANIZATION.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }

        final long systemLocaleId = stringLocaleBean.getSystemStringLocale().getId();
        recordIndex = 0;

        final Queue<String> workQueue = new LinkedList<>();

        //find root of organization tree
        List<OrganizationImport> rootOrgs = organizationImportBean.find(null);
        if (rootOrgs == null || rootOrgs.isEmpty()) {
            throw new RootOrganizationNotFound();
        }

        for (OrganizationImport root : rootOrgs) {
            workQueue.add(root.getOrganizationId());
        }

        while (!workQueue.isEmpty()) {
            recordIndex++;

            final String externalOrganizationId = workQueue.poll();

            //put children in work queue
            List<OrganizationImport> orgs = organizationImportBean.find(Long.valueOf(externalOrganizationId));
            if (orgs != null && !orgs.isEmpty()) {
                for (OrganizationImport oi : orgs) {
                    workQueue.add(oi.getOrganizationId());
                }
            }

            //handle organization
            final OrganizationImport organization = organizationImportBean.findById(Long.valueOf(externalOrganizationId));

            // Ищем по organization id в базе.
            DomainObject newObject = null;
            DomainObject oldObject = null;

            final Long objectId = null;//organizationStrategy.getObjectId(externalOrganizationId);
            if (objectId != null) {
                oldObject = organizationStrategy.getDomainObject(objectId, true);
                if (oldObject != null) { // нашли
                    newObject = CloneUtil.cloneObject(oldObject);
                }
            }
            if (newObject == null) {
                newObject = organizationStrategy.newInstance();
            }

            //code
            newObject.setStringValue(CODE, organization.getCode().toUpperCase(), locale);


            //short name
            newObject.setStringValue(SHORT_NAME, organization.getShortName().toUpperCase(), locale);

            //full name
            newObject.setStringValue(NAME, organization.getFullName().toUpperCase(), locale);


            //parent
            Long parentId = organization.getHlevel();
            if (parentId != null) {
                Long parentObjectId = null;//organizationStrategy.getObjectId(parentId.toString());
                newObject.getAttribute(USER_ORGANIZATION_PARENT).
                        setValueId(parentObjectId);
            }

            //type
            addOrganizationTypes(newObject);

            if (oldObject == null) {
                organizationStrategy.insert(newObject, beginDate);
            } else {
                organizationStrategy.update(oldObject, newObject, beginDate);
            }

            listener.recordProcessed(ORGANIZATION, recordIndex);
        }

        listener.completeImport(ORGANIZATION, recordIndex);
    }

    private void addOrganizationTypes(DomainObject organization) {
        organization.removeAttribute(ORGANIZATION_TYPE);
        organization.addAttribute(newOrganizationTypeAttribute(1L, SERVICING_ORGANIZATION_TYPE));
        //organization.addAttribute(newOrganizationTypeAttribute(2L, USER_ORGANIZATION_TYPE));
    }

    private Attribute newOrganizationTypeAttribute(long attributeId, long organizationTypeId) {
        Attribute a = new Attribute();
        a.setAttributeId(attributeId);
        a.setEntityAttributeId(ORGANIZATION_TYPE);
        a.setValueId(organizationTypeId);
        return a;
    }
}
