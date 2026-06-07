package com.zrlog.plugin.common;

import com.zrlog.plugin.common.type.PluginVersion;
import com.zrlog.plugin.message.Plugin;

public class PluginVersionUtils {


    public static PluginVersion getPluginVersion(Plugin plugin) {
        if (plugin.getVersion().startsWith("1.")) {
            return PluginVersion.V1;
        }
        if (plugin.getVersion().startsWith("2.")) {
            return PluginVersion.V2;
        }
        if (plugin.getVersion().startsWith("3.")) {
            return PluginVersion.V3;
        }
        return PluginVersion.V4;
    }

    public static boolean isUpper(Plugin plugin, PluginVersion pluginVersion) {
        return getPluginVersion(plugin).getCode() >= pluginVersion.getCode();
    }
}
