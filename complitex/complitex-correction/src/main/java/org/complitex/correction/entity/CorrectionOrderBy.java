package org.complitex.correction.entity;

/**
 * @author inheaven on 007 07.04.15 14:59
 */
public enum CorrectionOrderBy {
    CORRECTION("correction"),
    EXTERNAL_ID("external_id"),
    ORGANIZATION("organization"),
    MODULE("module"),
    OBJECT("object"),
    USER_ORGANIZATION("userOrganization");

    private String orderBy;

    CorrectionOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderBy() {
        return orderBy;
    }
}
