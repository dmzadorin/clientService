package ru.dmzadorin.clientservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds the value of a HTTP xml request extra field to a resource method parameter
 * Name of the parameter should be specified in name attribute
 * Created by Dmitry Zadorin on 02.03.2018
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    String name();
}
