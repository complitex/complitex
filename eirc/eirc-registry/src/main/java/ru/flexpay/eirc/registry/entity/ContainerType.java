package ru.flexpay.eirc.registry.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.complitex.common.mybatis.IFixedIdType;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static ru.flexpay.eirc.registry.entity.RegistryType.*;

/**
 * @author Pavel Sknar
 */
public enum ContainerType implements IFixedIdType {
    UNDEFINED(0, null),
    OPEN_ACCOUNT(1, INFO, SALDO_SIMPLE),
    CLOSE_ACCOUNT(2, CLOSED_ACCOUNTS),
    SET_RESPONSIBLE_PERSON(3, INFO, SALDO_SIMPLE),
    SET_NUMBER_ON_HABITANTS(4, INFO, SALDO_SIMPLE),
    SET_TOTAL_SQUARE(5, INFO),
    SET_LIVE_SQUARE(6, INFO),
    SET_WARM_SQUARE(7, INFO, SALDO_SIMPLE),
    SET_PRIVILEGE_TYPE(8, INFO),
    SET_PRIVILEGE_OWNER(9, INFO),
    SET_PRIVILEGE_PERSON(10, INFO),
    SET_PRIVILEGE_APPROVAL_DOCUMENT(11, INFO),
    SET_PRIVILEGE_PERSONS_NUMBER(12, INFO),
    OPEN_SUBACCOUNT(14, INFO),
    EXTERNAL_ORGANIZATION_ACCOUNT(15, INFO, SALDO_SIMPLE),

    CASH_PAYMENT(50, PAYMENTS),
    CASHLESS_PAYMENT(51, PAYMENTS),
    BANK_PAYMENT(52, BANK_PAYMENTS),

    BASE(100, QUITTANCE),
    CHARGE(101, SALDO_SIMPLE),
    SALDO_OUT(102, SALDO_SIMPLE),

    ADDRESS_CORRECTION(150, CORRECTIONS),


    SETUP_PAYMENT_POINT(500, null),
    REGISTRY_ANNOTATION(501, null),
    SYNC_IDENTIFIER(502, null),
    OBJECT_IDENTIFIER(503, null),
    DETAILS_PAYMENTS_DOCUMENT(504, PAYMENTS),

    SET_CALCULATION_NUMBER_TENANTS(600, INFO),
    SET_CALCULATION_NUMBER_REGISTERED(601, INFO),
    SET_CALCULATION_TOTAL_SQUARE(602, INFO),
    SET_CALCULATION_LIVE_SQUARE(603, INFO),
    SET_CALCULATION_HEATING_SQUARE(604, INFO)
    ;

    private static final Map<Integer, ContainerType> CONTAINER_TYPE_MAP;

    static {
        ImmutableMap.Builder<Integer, ContainerType> builder = ImmutableMap.builder();

        for (ContainerType containerType : ContainerType.values()) {
            builder.put(containerType.getId(), containerType);
        }

        CONTAINER_TYPE_MAP = builder.build();
    }

    private Integer id;
    private Set<RegistryType> registryTypes;

    private ContainerType(Integer id, RegistryType... registryTypes) {
        this.id = id;
        this.registryTypes = registryTypes == null? Collections.<RegistryType>emptySet(): Sets.newHashSet(registryTypes);
    }

    @Override
    public Integer getId() {
        return id;
    }

    public Set<RegistryType> getRegistryTypes() {
        return registryTypes;
    }

    public boolean isSupport(RegistryType registryType) {
        return registryTypes.contains(registryType);
    }

    public static ContainerType valueOf(Long id) {
        ContainerType containerType = CONTAINER_TYPE_MAP.get(id);
        return containerType != null? containerType : UNDEFINED;
    }
}
