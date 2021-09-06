package ru.complitex.pspoffice.address.producer;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * @author Ivanov Anatoliy
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD, FIELD, TYPE, PARAMETER })
public @interface SyncProducer {}
