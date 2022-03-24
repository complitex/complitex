package ru.complitex.eirc.registry.entity.log;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 * @author Pavel Sknar
 */
@BaseName("ru.complitex.eirc.registry.entity.log.rollback")
@LocaleData(defaultCharset = "UTF-8", value = {@Locale("ru"), @Locale("en")})
public enum  Rollback {
    STARTING_ROLLBACK_REGISTRIES,
    REGISTRY_FAILED_STATUS,
    REGISTRY_STATUS_INNER_ERROR,
    NOT_FOUND_ROLLBACK_REGISTRY_RECORDS,
    REGISTRY_FAILED_ROLLBACK,
    REGISTRY_FINISH_ROLLBACK,
    ROLLBACK_CANCELED,
    ROLLBACK_BULK_RECORDS,

}
