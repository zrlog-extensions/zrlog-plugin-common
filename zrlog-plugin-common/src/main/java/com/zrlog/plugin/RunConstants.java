package com.zrlog.plugin;

import com.zrlog.plugin.type.RunType;

import java.util.Objects;

public class RunConstants {
    public static RunType runType = Objects.equals(System.getenv("PLUGIN_RUN_TYPE"),"DEV") ? RunType.DEV : RunType.BLOG;
}
