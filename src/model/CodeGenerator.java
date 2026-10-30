package model;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Generates random six-digit codes that are unique across model entities.
 */
public final class CodeGenerator {
    private static final int FIRST_CODE = 100000;
    private static final int CODE_COUNT = 900000;
    private static final Random RANDOM = new Random();
    private static final Set<Integer> GENERATED_CODES = new HashSet<Integer>();

    private CodeGenerator() {
    }

    public static synchronized String generateUniqueCode() {
        while (GENERATED_CODES.size() < CODE_COUNT) {
            int code = RANDOM.nextInt(CODE_COUNT) + FIRST_CODE;
            if (GENERATED_CODES.add(code)) {
                return String.valueOf(code);
            }
        }
        throw new IllegalStateException("No unique six-digit codes remain.");
    }

    public static synchronized void reserveCode(String value) {
        if (value != null) {
            try {
                int code = Integer.parseInt(value);
                if (code >= FIRST_CODE && code < FIRST_CODE + CODE_COUNT) {
                    GENERATED_CODES.add(code);
                }
            } catch (NumberFormatException exception) {
                // Non-numeric identifiers do not participate in numeric code allocation.
            }
        }
    }
}
