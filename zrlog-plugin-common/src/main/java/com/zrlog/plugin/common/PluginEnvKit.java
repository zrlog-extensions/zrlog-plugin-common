package com.zrlog.plugin.common;

import java.util.Objects;

public class PluginEnvKit {

    public static boolean isLambda() {
        String value = System.getenv("AWS_LAMBDA_FUNCTION_NAME");
        return Objects.nonNull(value);
    }
}
