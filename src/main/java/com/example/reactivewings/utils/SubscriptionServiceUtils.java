package com.example.reactivewings.utils;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SubscriptionServiceUtils
{
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
                    .append(oldVal)
                    .append(" \u2192 ")
                    .append(newVal)
                    .append("<br>");
            setter.accept(newVal);
            return true;
        }

        return false;
    }
}
