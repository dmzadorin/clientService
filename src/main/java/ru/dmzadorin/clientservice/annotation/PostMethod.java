package ru.dmzadorin.clientservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds the value of a HTTP xml request-type field to a resource method
 * Name of the method should be specified in name attribute
 * There is optional returnParamName attribute - that name would be used as a key key with returned value
 * as value field
 * Created by Dmitry Zadorin on 02.03.2018
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostMethod {
    String name();

    String returnParamName() default "returnParam";
}
