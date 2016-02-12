package ru.flexpay.eirc.registry.entity;

import org.complitex.common.entity.ILocalizedType;
import org.complitex.common.mybatis.IFixedIdType;
import org.complitex.common.util.ResourceUtil;

import java.util.Locale;

/**
 * @author Pavel Sknar
 */
public enum RegistryType implements IFixedIdType, ILocalizedType {
    UNKNOWN(0),
    SALDO_SIMPLE(1),
    INCOME(2),
    MESSAGE(3),
    CLOSED_ACCOUNTS(4),
    INFO(5),
    CORRECTIONS(6),
    PAYMENTS(7),
    CASHLESS_PAYMENTS(8),
    REPAYMENT(9),
    ERRORS(10),
    QUITTANCE(11),
    BANK_PAYMENTS(12);

    private static final String RESOURCE_BUNDLE = RegistryType.class.getName();

    private Integer id;

    private RegistryType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public boolean isPayments() {
        return this == PAYMENTS || this == CASHLESS_PAYMENTS;
    }

    public boolean isCashPayments() {
        return this == PAYMENTS;
    }

    public boolean isCashlessPayments() {
        return this == CASHLESS_PAYMENTS;
    }

    @Override
    public String getLabel(Locale locale) {
        return ResourceUtil.getString(RESOURCE_BUNDLE, String.valueOf(getId()), locale);
    }
}
