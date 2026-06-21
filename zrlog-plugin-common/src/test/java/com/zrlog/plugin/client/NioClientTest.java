package com.zrlog.plugin.client;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NioClientTest {

    @Test
    public void shouldReadCacheableStaticPathsFromAssetManifest() {
        String manifest = "{"
                + "\"files\":{"
                + "\"main.js\":\"/static/js/main.12345678.js\","
                + "\"main.css\":\"https://cdn.example.com/plugin/static/css/main.12345678.css\","
                + "\"index.html\":\"/index.html\""
                + "},"
                + "\"entrypoints\":[\"static/js/runtime.js\",\"/static/css/main.12345678.css\"],"
                + "\"nested\":{\"logo\":\"/static/media/logo.svg?v=1\"}"
                + "}";

        Set<String> paths = NioClient.parseAssetManifestStaticPaths(manifest);

        assertTrue(paths.contains("/static/js/main.12345678.js"));
        assertTrue(paths.contains("/static/css/main.12345678.css"));
        assertTrue(paths.contains("/static/js/runtime.js"));
        assertTrue(paths.contains("/static/media/logo.svg"));
        assertFalse(paths.contains("/index.html"));
        assertEquals(4, paths.size());
    }
}
