package com.zrlog.plugin.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Capability {

    String key();

    String type() default "service";

    String label() default "";

    String description() default "";

    String[] exposure() default {"internal"};

    String riskLevel() default "low";

    boolean readOnly() default false;

    boolean requiresConfirmation() default false;

    int timeoutSeconds() default 30;

    int concurrency() default 1;

    String channel() default "";
}
