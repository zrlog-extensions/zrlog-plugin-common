package com.zrlog.plugin.render;

import com.google.gson.Gson;
import com.zrlog.plugin.common.IOUtil;
import com.zrlog.plugin.message.Plugin;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SimpleTemplateRender implements IRenderHandler {

    @Override
    public String render(String s, Plugin plugin, Map<String, Object> map) {
        return render(SimpleTemplateRender.class.getResourceAsStream(s + ".html"), plugin, map);
    }

    @Override
    public String render(InputStream inputStream, Plugin plugin, Map<String, Object> map) {
        Map<String, Object> dataMap = new HashMap<>(map);
        if (Objects.nonNull(plugin)) {
            Map<String, Object> pluginMap = new Gson().fromJson(new Gson().toJson(plugin), Map.class);
            pluginMap.forEach((k, v) -> {
                dataMap.put("_plugin." + k, v);
            });
        }
        String renderResult = IOUtil.getStringInputStream(inputStream);
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            if (entry.getValue() != null) {
                renderResult = renderResult.replace("${" + entry.getKey() + "}", entry.getValue().toString());
            } else {
                renderResult = renderResult.replace("${" + entry.getKey() + "}", "");
            }
        }
        return renderResult;
    }
}