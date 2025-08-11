package com.zrlog.plugin.data.codec;

public enum ContentType {
    JSON((byte) 0), FILE((byte) 1), BYTE((byte) -1), SQL((byte) 2), HTML((byte) 3), XML((byte) 4), IMAGE_SVG_XML((byte) 5);
    byte type;

    ContentType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public static ContentType getContentType(byte type) {
        ContentType[] contentTypes = ContentType.values();
        for (ContentType contentType : contentTypes) {
            if (contentType.type == type) {
                return contentType;
            }
        }
        return ContentType.BYTE;
    }
}
