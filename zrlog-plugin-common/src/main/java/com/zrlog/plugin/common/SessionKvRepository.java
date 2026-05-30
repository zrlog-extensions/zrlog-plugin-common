package com.zrlog.plugin.common;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.data.codec.ContentType;
import com.zrlog.plugin.type.ActionType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class SessionKvRepository implements KvRepository {

    private final IOSession session;

    public SessionKvRepository(IOSession session) {
        this.session = Objects.requireNonNull(session, "session");
    }

    public static SessionKvRepository of(IOSession session) {
        return new SessionKvRepository(session);
    }

    @Override
    public Optional<String> get(String key) {
        Object value = read(key).get(key);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(String.valueOf(value));
    }

    @Override
    public void put(String key, String value) {
        Map<String, String> values = new HashMap<>();
        values.put(key, value);
        write(values);
    }

    public Map<String, Object> read(String... keys) {
        if (keys == null || keys.length == 0) {
            return Collections.emptyMap();
        }
        StringBuilder keyBuilder = new StringBuilder();
        for (String key : keys) {
            if (key == null || key.trim().isEmpty()) {
                continue;
            }
            if (keyBuilder.length() > 0) {
                keyBuilder.append(",");
            }
            keyBuilder.append(key.trim());
        }
        if (keyBuilder.length() == 0) {
            return Collections.emptyMap();
        }
        return readByKeyExpression(keyBuilder.toString());
    }

    public Map<String, Object> read(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }
        return read(keys.toArray(new String[0]));
    }

    public void write(Map<String, ?> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        session.getResponseSync(ContentType.JSON, values, ActionType.SET_WEBSITE, Map.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Map<String, Object> readByKeyExpression(String keyExpression) {
        Map<String, String> request = new HashMap<>();
        request.put("key", keyExpression);
        Map response = session.getResponseSync(ContentType.JSON, request, ActionType.GET_WEBSITE, Map.class);
        if (response == null || response.isEmpty()) {
            return Collections.emptyMap();
        }
        return new HashMap<String, Object>(response);
    }
}
