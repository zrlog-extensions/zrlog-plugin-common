package com.zrlog.plugin;

import java.io.IOException;
import java.io.PipedInputStream;
import java.util.*;

public class ClearIdlMsgPacketTimerTask implements Runnable {

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
                if (activeTime > 60000 * 10) {
                    removedKeys.add(entry.getKey());
                }
            }
            for (Integer i : removedKeys) {
                PipedInputStream pipedIn = pipeMap.get(i).getPipedIn();
                if (Objects.nonNull(pipedIn)) {
                    try {
                        pipedIn.close();
                    } catch (Exception e) {
                        //throw new RuntimeException(e);
                    }
                }
                pipeMap.remove(i);
            }
        }
    }

    public void removePipeMap(Map<Integer, PipeInfo> pipeMap) {
        pipeMap.values().forEach(pipeInfo -> {
            if (Objects.isNull(pipeInfo.getPipedIn())) {
                return;
            }
            try {
                pipeInfo.getPipedIn().close();
            } catch (IOException e) {
                //throw new RuntimeException(e);
            }
        });
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
            try {
                pipeInfo.getPipedIn().close();
                pipeInfoMap.remove(msgId);
            } catch (IOException e) {
                //throw new RuntimeException(e);
            }
        }
    }
}
