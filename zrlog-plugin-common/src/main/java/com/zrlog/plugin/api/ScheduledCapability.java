package com.zrlog.plugin.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduledCapability {

    String key();

    String label() default "";

    String description() default "";

    String defaultCron() default "";

    String timezone() default "system";

    int timeoutSeconds() default 60;

    int concurrency() default 1;
}
