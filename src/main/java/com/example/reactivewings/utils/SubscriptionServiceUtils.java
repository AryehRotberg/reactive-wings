package com.example.reactivewings.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SubscriptionServiceUtils {
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static boolean updateField(String fieldName,
            Supplier<String> oldValSupplier,
            Supplier<String> newValSupplier,
            Consumer<String> setter,
            StringBuilder changeLog) {
        String oldVal = oldValSupplier.get();
        String newVal = newValSupplier.get();

        if (!Objects.equals(oldVal, newVal)) {
            changeLog.append(fieldName)
                    .append(": ")
                    .append(formatValue(oldVal))
                    .append(" \u2192 ")
                    .append(formatValue(newVal))
                    .append("<br>");
            setter.accept(newVal);
            return true;
        }

        return false;
    }

    private static String formatValue(String value) {
        if (value == null)
            return "לא זמין";

        try {
            LocalDateTime dateTime = LocalDateTime.parse(value);
            return dateTime.format(DISPLAY_FORMATTER);
        } catch (DateTimeParseException e) {
            return value;
        }
    }
}
