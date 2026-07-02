package com.zrlog.plugin.render;

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
            appendPluginData(dataMap, plugin);
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

    private void appendPluginData(Map<String, Object> dataMap, Plugin plugin) {
        dataMap.put("_plugin.id", plugin.getId());
        dataMap.put("_plugin.version", plugin.getVersion());
        dataMap.put("_plugin.name", plugin.getName());
        dataMap.put("_plugin.paths", plugin.getPaths());
        dataMap.put("_plugin.actions", plugin.getActions());
        dataMap.put("_plugin.desc", plugin.getDesc());
        dataMap.put("_plugin.author", plugin.getAuthor());
        dataMap.put("_plugin.shortName", plugin.getShortName());
        dataMap.put("_plugin.indexPage", plugin.getIndexPage());
        dataMap.put("_plugin.previewImageBase64", plugin.getPreviewImageBase64());
        dataMap.put("_plugin.services", plugin.getServices());
        dataMap.put("_plugin.dependentService", plugin.getDependentService());
        dataMap.put("_plugin.cacheableStaticPaths", plugin.getCacheableStaticPaths());
        dataMap.put("_plugin.capabilities", plugin.getCapabilities());
    }
}
