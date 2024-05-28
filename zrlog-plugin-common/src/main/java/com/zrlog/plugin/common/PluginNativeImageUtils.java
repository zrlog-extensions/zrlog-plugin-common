package com.zrlog.plugin.common;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.data.codec.HttpRequestInfo;
import com.zrlog.plugin.data.codec.MsgPacket;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class PluginNativeImageUtils {

    public static void doLoopResourceLoad(File[] files, String basePath, String uriStart) {
        if (Objects.isNull(files)) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                doLoopResourceLoad(file.listFiles(), basePath, uriStart);
            } else {
                String binPath = file.toString().substring(basePath.length());
                String rFileName = uriStart + binPath.replace("\\", "/");
                try (InputStream inputStream = PluginNativeImageUtils.class.getResourceAsStream(rFileName)) {
                    if (Objects.nonNull(inputStream)) {
                        LoggerUtil.getLogger(PluginNativeImageUtils.class).info("Native image add filename " + rFileName);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void exposeController(List<Class<?>> classList) {
        classList.forEach(e -> {
            Constructor constructor;
            try {
                constructor = e.getConstructor(IOSession.class, MsgPacket.class, HttpRequestInfo.class);
                for (Method method : e.getMethods()) {
                    try {
                        method.invoke(constructor);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
