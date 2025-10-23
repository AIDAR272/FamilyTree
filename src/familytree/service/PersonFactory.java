package familytree.service;

import familytree.model.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple factory to create Adult or Minor based on deterministic CURRENT_YEAR and age threshold 18.
 * Also generates deterministic IDs P001, P002 ... (useful for tests).
 */
public final class PersonFactory {
    public static final int CURRENT_YEAR = 2025;
    private static final int ADULT_AGE = 18;
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    public static String nextId() {
        int n = COUNTER.incrementAndGet();
        return String.format("P%03d", n);
    }

    public static Person create(String fullName, Gender gender, int birthYear, Integer deathYear) {
        String id = nextId();
        int age = CURRENT_YEAR - birthYear;
        if (age >= ADULT_AGE) {
            return new Adult(id, fullName, gender, birthYear, deathYear);
        } else {
            return new Minor(id, fullName, gender, birthYear, deathYear);
        }
    }
}
