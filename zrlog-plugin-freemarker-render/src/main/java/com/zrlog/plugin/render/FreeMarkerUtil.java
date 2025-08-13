package com.zrlog.plugin.render;


import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;

public class FreeMarkerUtil {

    private static Object cfg;

    static {
        try {
            cfg = Class.forName("freemarker.template.Configuration").getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            //LOGGER.log(Level.WARNING, "load freemarker error", e);
        }
    }

    public static String renderToFMByModel(String name, Object model) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(out);
        Object template = cfg.getClass().getMethod("getTemplate", String.class).invoke(cfg, name + ".ftl");
        template.getClass().getMethod("process", Object.class, Writer.class).invoke(template, model, writer);
        writer.flush();
        return out.toString();
    }

    public static void initClassTemplate(String basePath) {
        try {
            cfg.getClass().getMethod("setClassForTemplateLoading", Class.class, String.class).invoke(cfg, FreeMarkerUtil.class, basePath);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            //LOGGER.log(Level.WARNING, "init freemarker class path error", e);
        }
    }
}
