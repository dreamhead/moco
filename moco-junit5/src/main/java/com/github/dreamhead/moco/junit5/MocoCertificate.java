package com.github.dreamhead.moco.junit5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MocoCertificate {
    String filepath() default "";

    String classpath() default "";

    String keyStorePassword();

    String certPassword();
}
