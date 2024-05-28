package com.zrlog.plugin;

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
            Set<Integer> integerSet = new HashSet<>();
            for (Map.Entry<Integer, PipeInfo> entry : pipeMap.entrySet()) {
                long activeTime = System.currentTimeMillis() - entry.getValue().getCratedAt();
                if (activeTime > 60000 * 10) {
                    integerSet.add(entry.getKey());
                }
            }
            for (Integer i : integerSet) {
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
        pipeMaps.remove(pipeMap);
    }
}
