package com.zrlog.plugin.common;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class BasicCronParser {

    private static final int MAX_SEARCH_MINUTES = 366 * 24 * 60;

    public ZonedDateTime nextRunAt(String expression, ZoneId zoneId, ZonedDateTime now) {
        if (zoneId == null) {
            zoneId = ZoneId.systemDefault();
        }
        if (now == null) {
            now = ZonedDateTime.now(zoneId);
        } else {
            now = now.withZoneSameInstant(zoneId);
        }
        CronFields fields = parse(expression);
        ZonedDateTime candidate = now.withSecond(0).withNano(0).plusMinutes(1);
        for (int i = 0; i < MAX_SEARCH_MINUTES; i++) {
            if (matches(fields, candidate)) {
                return candidate;
            }
            candidate = candidate.plusMinutes(1);
        }
        throw new CronParseException("No next run time found within one year");
    }

    public CronFields parse(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new CronParseException("Cron expression is empty");
        }
        String[] parts = expression.trim().split("\\s+");
        if (parts.length != 5) {
            throw new CronParseException("Only 5-field cron is supported");
        }
        CronFields fields = new CronFields();
        fields.minute = parseField(parts[0], 0, 59, "minute");
        fields.hour = parseField(parts[1], 0, 23, "hour");
        fields.dayOfMonth = parseField(parts[2], 1, 31, "dayOfMonth");
        fields.month = parseField(parts[3], 1, 12, "month");
        fields.dayOfWeek = parseField(parts[4], 0, 7, "dayOfWeek");
        return fields;
    }

    private boolean matches(CronFields fields, ZonedDateTime candidate) {
        return fields.minute.matches(candidate.getMinute())
                && fields.hour.matches(candidate.getHour())
                && fields.dayOfMonth.matches(candidate.getDayOfMonth())
                && fields.month.matches(candidate.getMonthValue())
                && matchesDayOfWeek(fields.dayOfWeek, candidate.getDayOfWeek());
    }

    private boolean matchesDayOfWeek(CronField field, DayOfWeek dayOfWeek) {
        if (field.any) {
            return true;
        }
        int value = dayOfWeek.getValue();
        if (value == 7 && field.matches(0)) {
            return true;
        }
        return field.matches(value);
    }

    private CronField parseField(String raw, int min, int max, String name) {
        if ("*".equals(raw)) {
            return CronField.any();
        }
        if (raw.startsWith("*/")) {
            int step = parseInt(raw.substring(2), name);
            if (step <= 0) {
                throw new CronParseException(name + " step must be greater than 0");
            }
            return CronField.step(step);
        }
        if (raw.contains(",") || raw.contains("-") || raw.contains("L") || raw.contains("W") || raw.contains("#")) {
            throw new CronParseException(name + " only supports *, */n, or a single number");
        }
        int value = parseInt(raw, name);
        if (value < min || value > max) {
            throw new CronParseException(name + " out of range");
        }
        return CronField.value(value);
    }

    private int parseInt(String text, String name) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new CronParseException(name + " is not a number");
        }
    }

    public static class CronFields {
        private CronField minute;
        private CronField hour;
        private CronField dayOfMonth;
        private CronField month;
        private CronField dayOfWeek;
    }

    private static class CronField {
        private boolean any;
        private Integer step;
        private Integer value;

        private static CronField any() {
            CronField field = new CronField();
            field.any = true;
            return field;
        }

        private static CronField step(int step) {
            CronField field = new CronField();
            field.step = step;
            return field;
        }

        private static CronField value(int value) {
            CronField field = new CronField();
            field.value = value;
            return field;
        }

        private boolean matches(int candidate) {
            if (any) {
                return true;
            }
            if (step != null) {
                return candidate % step == 0;
            }
            return value != null && value == candidate;
        }
    }
}
