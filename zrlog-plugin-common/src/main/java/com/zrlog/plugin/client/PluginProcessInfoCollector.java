package com.zrlog.plugin.client;

import com.zrlog.plugin.message.Plugin;
import com.zrlog.plugin.message.PluginProcessInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.charset.StandardCharsets;

final class PluginProcessInfoCollector {

    private PluginProcessInfoCollector() {
    }

    static PluginProcessInfo current(Plugin plugin) {
        PluginProcessInfo info = new PluginProcessInfo();
        fillPlugin(info, plugin);
        info.setAlive(Boolean.TRUE);
        info.setLocal(Boolean.TRUE);
        info.setSampledAt(System.currentTimeMillis());
        info.setProcessId(currentProcessId());
        info.setThreadCount(ManagementFactory.getThreadMXBean().getThreadCount());
        fillHeap(info);
        fillOperatingSystem(info);
        fillProcSelfStatus(info);
        return info;
    }

    private static void fillPlugin(PluginProcessInfo info, Plugin plugin) {
        if (plugin == null) {
            return;
        }
        info.setPluginId(plugin.getId());
        info.setPluginShortName(plugin.getShortName());
        info.setPluginName(plugin.getName());
    }

    private static Long currentProcessId() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (name == null) {
            return null;
        }
        int atIndex = name.indexOf('@');
        String pid = atIndex < 0 ? name : name.substring(0, atIndex);
        try {
            return Long.parseLong(pid);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static void fillHeap(PluginProcessInfo info) {
        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        if (heap == null) {
            return;
        }
        info.setHeapUsedBytes(nonNegative(heap.getUsed()));
        info.setHeapCommittedBytes(nonNegative(heap.getCommitted()));
        info.setHeapMaxBytes(nonNegative(heap.getMax()));
    }

    private static void fillOperatingSystem(PluginProcessInfo info) {
        java.lang.management.OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
        if (!(bean instanceof com.sun.management.OperatingSystemMXBean)) {
            return;
        }
        com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) bean;
        long cpuTime = osBean.getProcessCpuTime();
        if (cpuTime >= 0) {
            info.setTotalCpuDurationMillis(cpuTime / 1000000L);
        }
        long committedVirtualMemory = osBean.getCommittedVirtualMemorySize();
        if (committedVirtualMemory >= 0 && info.getVirtualMemoryBytes() == null) {
            info.setVirtualMemoryBytes(committedVirtualMemory);
        }
    }

    private static void fillProcSelfStatus(PluginProcessInfo info) {
        File status = new File("/proc/self/status");
        if (!status.isFile()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(status), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("VmRSS:")) {
                    info.setResidentMemoryBytes(parseKbBytes(line));
                } else if (line.startsWith("VmSize:")) {
                    info.setVirtualMemoryBytes(parseKbBytes(line));
                } else if (line.startsWith("Threads:")) {
                    info.setThreadCount(parseIntegerValue(line));
                }
            }
        } catch (Exception ignored) {
            // Resource fields are best-effort and may be unavailable on restricted runtimes.
        }
    }

    private static Long parseKbBytes(String line) {
        Long value = parseLongValue(line);
        return value == null ? null : value * 1024L;
    }

    private static Long parseLongValue(String line) {
        int index = line.indexOf(':');
        String value = index < 0 ? line : line.substring(index + 1);
        String[] parts = value.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(parts[0]);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static Integer parseIntegerValue(String line) {
        Long value = parseLongValue(line);
        if (value == null || value > Integer.MAX_VALUE) {
            return null;
        }
        return value.intValue();
    }

    private static Long nonNegative(long value) {
        return value < 0 ? null : value;
    }
}
