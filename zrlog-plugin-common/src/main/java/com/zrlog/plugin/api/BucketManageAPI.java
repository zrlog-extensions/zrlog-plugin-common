package com.zrlog.plugin.api;

import java.io.File;

public interface BucketManageAPI extends AutoCloseable {

    String create(File file, String key, boolean deleteRepeat, boolean supportHttps) throws Exception;

}