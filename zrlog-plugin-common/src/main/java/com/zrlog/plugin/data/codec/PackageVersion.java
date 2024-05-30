package com.zrlog.plugin.data.codec;

public enum PackageVersion {
    V1((byte) 0x5E);

    private final byte version;

    PackageVersion(byte version) {
        this.version = version;
    }

    public byte getVersion() {
        return version;
    }
}
