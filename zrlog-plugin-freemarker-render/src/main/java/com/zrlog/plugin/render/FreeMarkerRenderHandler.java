package com.zrlog.plugin.render;

import com.zrlog.plugin.message.Plugin;

import java.io.InputStream;
import java.util.Map;

public class FreeMarkerRenderHandler implements IRenderHandler {

    public FreeMarkerRenderHandler() {
        FreeMarkerUtil.initClassTemplate("/");
    }

    @Override
    public String render(String templatePath, Plugin plugin, Map<String, Object> map) {
        map.put("_plugin", plugin);
        try {
            return FreeMarkerUtil.renderToFMByModel(templatePath, map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String render(InputStream inputStream, Plugin plugin, Map<String, Object> map) {
        throw new RuntimeException("not support this method");
    }
}
