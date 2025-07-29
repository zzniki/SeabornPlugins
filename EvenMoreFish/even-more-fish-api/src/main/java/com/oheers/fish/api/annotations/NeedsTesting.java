package com.oheers.fish.api.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NeedsTesting {
    String reason() default "";

    TestType[] testType() default {TestType.MANUAL};

    Priority priority() default Priority.MEDIUM;
}


