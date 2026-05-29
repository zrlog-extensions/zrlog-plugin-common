package com.zrlog.plugin.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationRequest {

    private String sourcePluginId;
    private String sourcePluginName;
    private String sourceCapabilityKey;
    private String eventType;
    private String notificationType;
    private List<String> channels = new ArrayList<>();
    private String title;
    private String content;
    private String level;
    private String requestId;
    private String traceId;
    private Map<String, Object> payload = new HashMap<>();

    public String getSourcePluginId() {
        return sourcePluginId;
    }

    public void setSourcePluginId(String sourcePluginId) {
        this.sourcePluginId = sourcePluginId;
    }

    public String getSourcePluginName() {
        return sourcePluginName;
    }

    public void setSourcePluginName(String sourcePluginName) {
        this.sourcePluginName = sourcePluginName;
    }

    public String getSourceCapabilityKey() {
        return sourceCapabilityKey;
    }

    public void setSourceCapabilityKey(String sourceCapabilityKey) {
        this.sourceCapabilityKey = sourceCapabilityKey;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
