/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.document.strategy.entity;

import java.util.Date;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.DomainObject;
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import static org.complitex.pspoffice.document.strategy.DocumentStrategy.*;
import static org.complitex.common.util.AttributeUtil.*;

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

    public long getDocumentTypeId() {
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
        return DocumentTypeStrategy.isKidDocumentType(getDocumentTypeId());
    }

    public boolean isAdultDocument() {
        return DocumentTypeStrategy.isAdultDocumentType(getDocumentTypeId());
    }

    public String getSeries() {
        return getStringValue(DocumentStrategy.DOCUMENT_SERIA);
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
