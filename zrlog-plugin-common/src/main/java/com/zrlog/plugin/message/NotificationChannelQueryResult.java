package com.zrlog.plugin.message;

import java.util.ArrayList;
import java.util.List;

public class NotificationChannelQueryResult {

    private boolean success;
    private int code;
    private String message;
    private List<NotificationChannelProvider> items = new ArrayList<>();

    public static NotificationChannelQueryResult success(List<NotificationChannelProvider> items) {
        NotificationChannelQueryResult result = new NotificationChannelQueryResult();
        result.setSuccess(true);
        result.setCode(0);
        result.setMessage("success");
        result.setItems(items);
        return result;
    }

    public static NotificationChannelQueryResult error(String message) {
        NotificationChannelQueryResult result = new NotificationChannelQueryResult();
        result.setSuccess(false);
        result.setCode(1);
        result.setMessage(message);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<NotificationChannelProvider> getItems() {
        return items;
    }

    public void setItems(List<NotificationChannelProvider> items) {
        this.items = items == null ? new ArrayList<NotificationChannelProvider>() : items;
    }

    public boolean isOk() {
        return success && code == 0;
    }

    public void normalize() {
        if (items == null) {
            items = new ArrayList<>();
            return;
        }
        for (NotificationChannelProvider item : items) {
            if (item != null) {
                item.normalizeIcon();
            }
        }
    }
}
