package com.core.payment_gateway_service.DTO;

import com.core.payment_gateway_service.enums.SearchOperator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SearchParameter {
    SearchOperator operator() default SearchOperator.EQUAL_TO;

    String attributeName() default "";

    String parameterName() default "";
}