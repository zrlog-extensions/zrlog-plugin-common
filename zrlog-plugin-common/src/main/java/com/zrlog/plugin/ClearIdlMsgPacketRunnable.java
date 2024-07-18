package com.zrlog.plugin;

import java.time.Duration;
import java.util.*;

class ClearIdlMsgPacketRunnable implements Runnable {

    private final List<Map<Integer, PipeInfo>> pipeMaps = new ArrayList<>();

    public void addTask(Map<Integer, PipeInfo> pipeMap) {
        pipeMaps.add(pipeMap);
    }

    @Override
    public void run() {
        for (Map<Integer, PipeInfo> pipeMap : pipeMaps) {
            Set<Integer> removedKeys = new HashSet<>();
            for (Map.Entry<Integer, PipeInfo> entry : pipeMap.entrySet()) {
                long activeTime = System.currentTimeMillis() - entry.getValue().getCratedAt();
                //max wait 6min
                if (activeTime >= Duration.ofMinutes(6).toMillis()) {
                    removedKeys.add(entry.getKey());
                }
            }
            for (Integer i : removedKeys) {
                pipeMap.remove(i);
            }
        }
    }

    public void removePipeMap(Map<Integer, PipeInfo> pipeMap) {
        pipeMaps.remove(pipeMap);
    }

    public void removePipeByMsgId(int msgId) {
        for (Map<Integer, PipeInfo> pipeInfoMap : pipeMaps) {
            if (pipeInfoMap == null) {
                continue;
            }
            PipeInfo pipeInfo = pipeInfoMap.get(msgId);
            if (Objects.isNull(pipeInfo)) {
                continue;
            }
            pipeInfoMap.remove(msgId);
        }
    }
}
