package com.shadow.gmall.myAnnotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SSOAnnotation {
    boolean isNeedSuccess() default false;
}
