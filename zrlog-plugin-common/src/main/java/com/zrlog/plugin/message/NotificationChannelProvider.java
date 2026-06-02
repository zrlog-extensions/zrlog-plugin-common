package com.zrlog.plugin.message;

public class NotificationChannelProvider {

    private String channel;
    private String channelIconBase64;
    private String providerPluginId;
    private String providerPluginName;
    private String providerPluginPreviewImageBase64;
    private String capabilityKey;
    private String capabilityLabel;
    private String providerStatus;
    private boolean selected;
    private boolean confirmed;
    private boolean reviewRequired;
    private String lastDeliveryStatus;
    private Long lastDeliveryAt;
    private String lastDeliveryError;
    private Long updatedAt;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannelIconBase64() {
        return channelIconBase64;
    }

    public void setChannelIconBase64(String channelIconBase64) {
        this.channelIconBase64 = channelIconBase64;
    }

    public String getProviderPluginId() {
        return providerPluginId;
    }

    public void setProviderPluginId(String providerPluginId) {
        this.providerPluginId = providerPluginId;
    }

    public String getProviderPluginName() {
        return providerPluginName;
    }

    public void setProviderPluginName(String providerPluginName) {
        this.providerPluginName = providerPluginName;
    }

    public String getProviderPluginPreviewImageBase64() {
        return providerPluginPreviewImageBase64;
    }

    public void setProviderPluginPreviewImageBase64(String providerPluginPreviewImageBase64) {
        this.providerPluginPreviewImageBase64 = providerPluginPreviewImageBase64;
    }

    public String getCapabilityKey() {
        return capabilityKey;
    }

    public void setCapabilityKey(String capabilityKey) {
        this.capabilityKey = capabilityKey;
    }

    public String getCapabilityLabel() {
        return capabilityLabel;
    }

    public void setCapabilityLabel(String capabilityLabel) {
        this.capabilityLabel = capabilityLabel;
    }

    public String getProviderStatus() {
        return providerStatus;
    }

    public void setProviderStatus(String providerStatus) {
        this.providerStatus = providerStatus;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isReviewRequired() {
        return reviewRequired;
    }

    public void setReviewRequired(boolean reviewRequired) {
        this.reviewRequired = reviewRequired;
    }

    public String getLastDeliveryStatus() {
        return lastDeliveryStatus;
    }

    public void setLastDeliveryStatus(String lastDeliveryStatus) {
        this.lastDeliveryStatus = lastDeliveryStatus;
    }

    public Long getLastDeliveryAt() {
        return lastDeliveryAt;
    }

    public void setLastDeliveryAt(Long lastDeliveryAt) {
        this.lastDeliveryAt = lastDeliveryAt;
    }

    public String getLastDeliveryError() {
        return lastDeliveryError;
    }

    public void setLastDeliveryError(String lastDeliveryError) {
        this.lastDeliveryError = lastDeliveryError;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void normalizeIcon() {
        if ((channelIconBase64 == null || channelIconBase64.trim().isEmpty())
                && providerPluginPreviewImageBase64 != null
                && !providerPluginPreviewImageBase64.trim().isEmpty()) {
            channelIconBase64 = providerPluginPreviewImageBase64;
        }
    }
}
