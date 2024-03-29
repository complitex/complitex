/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.document.strategy.entity;

import ru.complitex.common.entity.Attribute;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.pspoffice.document.strategy.DocumentStrategy;
import ru.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;

import java.util.Date;

import static ru.complitex.common.util.AttributeUtil.getDateValue;
import static ru.complitex.pspoffice.document.strategy.DocumentStrategy.DOCUMENT_TYPE;

/**
 *
 * @author Artem
 */
public class Document extends DomainObject {

    private DomainObject documentType;

    public Document() {
    }

    public Document(DomainObject object) {
        super(object);
    }

    public Long getDocumentTypeId() {
        Attribute documentTypeAttribute = getAttribute(DOCUMENT_TYPE);
        return documentTypeAttribute != null ? documentTypeAttribute.getValueId() : null;
    }

    public DomainObject getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DomainObject documentType) {
        this.documentType = documentType;
    }

    public boolean isKidDocument() {
        return !DocumentTypeStrategy.RESERVED_INSTANCE_IDS.contains(getDocumentTypeId())
                || getDocumentTypeId() == DocumentTypeStrategy.BIRTH_CERTIFICATE;
    }

    public boolean isAdultDocument() {
        return true;
    }

    public String getSeries() {
        return getStringValue(DocumentStrategy.DOCUMENT_SERIES);
    }

    public String getNumber() {
        return getStringValue(DocumentStrategy.DOCUMENT_NUMBER);
    }

    public String getOrganizationIssued() {
        return getStringValue(DocumentStrategy.ORGANIZATION_ISSUED);
    }

    public Date getDateIssued() {
        return getDateValue(this, DocumentStrategy.DATE_ISSUED);
    }
}
