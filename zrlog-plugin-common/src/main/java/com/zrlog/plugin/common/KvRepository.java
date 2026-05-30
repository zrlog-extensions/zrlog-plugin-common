package com.zrlog.plugin.common;

import java.util.Optional;

public interface KvRepository {

    Optional<String> get(String key);

    void put(String key, String value);

    default String getOrDefault(String key, String defaultValue) {
        Optional<String> value = get(key);
        return value.isPresent() ? value.get() : defaultValue;
    }
}
