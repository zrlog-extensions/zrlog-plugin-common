package com.zrlog.plugin.message;

public class PluginProcessInfo {

    private String pluginId;
    private String pluginShortName;
    private String pluginName;
    private String instanceId;
    private String runtimeMode;
    private String status;
    private String effectiveStatus;
    private Long processId;
    private Boolean local;
    private Boolean alive;
    private Long startedAt;
    private Long readyAt;
    private Long lastActiveAt;
    private Long heartbeatAt;
    private Long leaseExpiresAt;
    private Integer activeInvocationCount;
    private Long sampledAt;
    private Long totalCpuDurationMillis;
    private Long residentMemoryBytes;
    private Long virtualMemoryBytes;
    private Integer threadCount;
    private Long heapUsedBytes;
    private Long heapCommittedBytes;
    private Long heapMaxBytes;
    private String errorMessage;

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getPluginShortName() {
        return pluginShortName;
    }

    public void setPluginShortName(String pluginShortName) {
        this.pluginShortName = pluginShortName;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getRuntimeMode() {
        return runtimeMode;
    }

    public void setRuntimeMode(String runtimeMode) {
        this.runtimeMode = runtimeMode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEffectiveStatus() {
        return effectiveStatus;
    }

    public void setEffectiveStatus(String effectiveStatus) {
        this.effectiveStatus = effectiveStatus;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Boolean getLocal() {
        return local;
    }

    public void setLocal(Boolean local) {
        this.local = local;
    }

    public Boolean getAlive() {
        return alive;
    }

    public void setAlive(Boolean alive) {
        this.alive = alive;
    }

    public Long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Long startedAt) {
        this.startedAt = startedAt;
    }

    public Long getReadyAt() {
        return readyAt;
    }

    public void setReadyAt(Long readyAt) {
        this.readyAt = readyAt;
    }

    public Long getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(Long lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public Long getHeartbeatAt() {
        return heartbeatAt;
    }

    public void setHeartbeatAt(Long heartbeatAt) {
        this.heartbeatAt = heartbeatAt;
    }

    public Long getLeaseExpiresAt() {
        return leaseExpiresAt;
    }

    public void setLeaseExpiresAt(Long leaseExpiresAt) {
        this.leaseExpiresAt = leaseExpiresAt;
    }

    public Integer getActiveInvocationCount() {
        return activeInvocationCount;
    }

    public void setActiveInvocationCount(Integer activeInvocationCount) {
        this.activeInvocationCount = activeInvocationCount;
    }

    public Long getSampledAt() {
        return sampledAt;
    }

    public void setSampledAt(Long sampledAt) {
        this.sampledAt = sampledAt;
    }

    public Long getTotalCpuDurationMillis() {
        return totalCpuDurationMillis;
    }

    public void setTotalCpuDurationMillis(Long totalCpuDurationMillis) {
        this.totalCpuDurationMillis = totalCpuDurationMillis;
    }

    public Long getResidentMemoryBytes() {
        return residentMemoryBytes;
    }

    public void setResidentMemoryBytes(Long residentMemoryBytes) {
        this.residentMemoryBytes = residentMemoryBytes;
    }

    public Long getVirtualMemoryBytes() {
        return virtualMemoryBytes;
    }

    public void setVirtualMemoryBytes(Long virtualMemoryBytes) {
        this.virtualMemoryBytes = virtualMemoryBytes;
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public Long getHeapUsedBytes() {
        return heapUsedBytes;
    }

    public void setHeapUsedBytes(Long heapUsedBytes) {
        this.heapUsedBytes = heapUsedBytes;
    }

    public Long getHeapCommittedBytes() {
        return heapCommittedBytes;
    }

    public void setHeapCommittedBytes(Long heapCommittedBytes) {
        this.heapCommittedBytes = heapCommittedBytes;
    }

    public Long getHeapMaxBytes() {
        return heapMaxBytes;
    }

    public void setHeapMaxBytes(Long heapMaxBytes) {
        this.heapMaxBytes = heapMaxBytes;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
