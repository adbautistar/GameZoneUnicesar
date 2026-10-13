package com.gamezone.service;

/**
 * Generates identifiers for the entities created by the services (sales,
 * returns, warranties). A pure fabrication: identifier generation is a
 * technical responsibility that belongs to no domain class, so it lives in
 * one place instead of being repeated in every service.
 */
public final class IdGenerator {

    private IdGenerator() {
    }

    /**
     * Returns a new identifier made of the given prefix, a dash and the
     * current time in milliseconds (for example, {@code RET-1760000000000}).
     *
     * @param prefix the entity prefix, such as "SALE", "RET" or "WAR"
     * @return the generated identifier
     */
    public static String next(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }
}
