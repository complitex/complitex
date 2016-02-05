package org.complitex.osznconnection.file.entity;

import org.complitex.common.mybatis.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.06.13 13:02
 */
public enum RequestFileType implements IFixedIdType{
    BENEFIT(1), PAYMENT(2), SUBSIDY_TARIF(3), ACTUAL_PAYMENT(4), SUBSIDY(5),
    DWELLING_CHARACTERISTICS(6), FACILITY_SERVICE_TYPE(7), FACILITY_FORM2(8),
    FACILITY_STREET_TYPE(9), FACILITY_STREET(10), FACILITY_TARIF(11), SUBSIDY_J_FILE(12), SUBSIDY_S_FILE(13);

    private Integer id;

    RequestFileType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
