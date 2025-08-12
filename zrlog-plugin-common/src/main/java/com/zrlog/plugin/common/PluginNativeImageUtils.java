package com.zrlog.plugin.common;

import com.google.gson.Gson;
import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.common.bucket.BucketVO;
import com.zrlog.plugin.common.model.*;
import com.zrlog.plugin.common.response.UploadFileResponseEntry;
import com.zrlog.plugin.data.codec.*;
import com.zrlog.plugin.message.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

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
                        method.invoke(constructor.newInstance(null, null, null));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void usedGsonObject() {
        new Gson().toJson(new FileDesc());
        new Gson().toJson(new HashMap<>());
        new Gson().toJson(new TreeMap<>());
        new Gson().fromJson("{}", Map.class);
        new Gson().toJson(new HttpRequestInfo());
        new Gson().toJson(new BaseHttpRequestInfo());
        new Gson().toJson(new HttpResponseInfo());
        new Gson().toJson(new Plugin());
        new Gson().toJson(new BlogRunTime());
        new Gson().toJson(new Comment());
        new Gson().toJson(new CreateArticleRequest());
        new Gson().toJson(new PublicInfo());
        new Gson().toJson(new TemplatePath());
        new Gson().toJson(new UploadFileResponseEntry());
        new Gson().toJson(new User());
        new Gson().toJson(new BucketVO("", "", "", ""));
        //new Gson().toJson(new UploadFile());
    }
}
