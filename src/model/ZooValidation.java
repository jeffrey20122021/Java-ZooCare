package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Shared validation rules for zoo data entry, including dd/MM/yyyy date ordering.
 */
public final class ZooValidation {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private ZooValidation() {
    }

    public static boolean isNonBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isPositiveFinite(double value) {
        return Double.isFinite(value) && value > 0;
    }

    public static boolean isNonNegativeFinite(double value) {
        return Double.isFinite(value) && value >= 0;
    }

    public static boolean isValidTemperature(double temperature) {
        return Double.isFinite(temperature) && temperature >= -10 && temperature <= 30;
    }

    public static boolean isValidDate(String value) {
        return parseDate(value) != null;
    }

    public static boolean isDateNotAfterToday(String value) {
        LocalDate date = parseDate(value);
        return date != null && !date.isAfter(LocalDate.now());
    }

    public static boolean isDateNotBeforeToday(String value) {
        LocalDate date = parseDate(value);
        return date != null && !date.isBefore(LocalDate.now());
    }

    public static boolean isBirthBeforeEntry(String birth, String entry) {
        LocalDate birthDate = parseDate(birth);
        LocalDate entryDate = parseDate(entry);
        return birthDate != null && entryDate != null && birthDate.isBefore(entryDate);
    }

    public static boolean isWithdrawalAfterEntry(String withdrawal, String entry) {
        LocalDate withdrawalDate = parseDate(withdrawal);
        LocalDate entryDate = parseDate(entry);
        return withdrawalDate != null && entryDate != null && withdrawalDate.isAfter(entryDate);
    }

    private static LocalDate parseDate(String value) {
        if (!isNonBlank(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim(), DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }
}
