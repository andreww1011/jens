/*
 * Copyright (c) 2018 to present, Andrew Wagner. All rights reserved.
 */
package com.jamw.jens;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Andrew
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnumeratedItem {
    String description() default "";
}
