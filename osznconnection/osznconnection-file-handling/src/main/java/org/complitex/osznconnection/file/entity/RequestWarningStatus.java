package org.complitex.osznconnection.file.entity;

import org.complitex.common.mybatis.IFixedIdType;

public enum RequestWarningStatus implements IFixedIdType {
    SUBSIDY_TARIF_NOT_FOUND(300),
    OWNERSHIP_OBJECT_NOT_FOUND(301), OWNERSHIP_CODE_NOT_FOUND(302), OWNERSHIP_CODE_INVALID(303),
    PRIVILEGE_OBJECT_NOT_FOUND(304), PRIVILEGE_CODE_NOT_FOUND(305), PRIVILEGE_CODE_INVALID(306),
    ORD_FAM_INVALID(307), PU_ACCOUNT_NUMBER_INVALID_FORMAT(308);
    
    private Integer id;

    private RequestWarningStatus(Integer id) {
        this.id = id;
    }


    @Override
    public Integer getId() {
        return id;
    }
}
