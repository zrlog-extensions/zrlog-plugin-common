package com.zrlog.plugin.common.type;

public enum PluginVersion {

    V1(1), V2(2), V3(3), V4(4);

    private final int code;

    PluginVersion(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
