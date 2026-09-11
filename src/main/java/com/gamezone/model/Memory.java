package com.gamezone.model;

import java.util.List;

/**
 * Represents a memory accessory, with capacity and memory type in addition
 * to the attributes shared with every {@link Accessory}.
 */
public class Memory extends Accessory {

    private int capacityInGB;
    private String memoryType;

    /**
     * Creates a new memory accessory with the given shared and specific attributes.
     *
     * @param id                   the unique identifier of the product
     * @param title                the product title
     * @param price                the unit price of the product
     * @param stock                the initial stock quantity
     * @param compatibleConsoleIds the ids of the consoles this memory is compatible with
     * @param capacityInGB         the storage capacity, in gigabytes
     * @param memoryType           the memory type ("SD", "MICROSD", or "INTERNAL")
     */
    public Memory(String id, String title, double price, int stock,
                  List<String> compatibleConsoleIds, int capacityInGB, String memoryType) {
        super(id, title, price, stock, compatibleConsoleIds);
        this.capacityInGB = capacityInGB;
        this.memoryType = memoryType;
    }

    /**
     * Returns the storage capacity of this memory, in gigabytes.
     *
     * @return the capacity in gigabytes
     */
    public int getCapacityInGB() {
        return capacityInGB;
    }

    /**
     * Sets the storage capacity of this memory, in gigabytes.
     *
     * @param capacityInGB the new capacity in gigabytes
     */
    public void setCapacityInGB(int capacityInGB) {
        this.capacityInGB = capacityInGB;
    }

    /**
     * Returns the memory type of this accessory.
     *
     * @return the memory type
     */
    public String getMemoryType() {
        return memoryType;
    }

    /**
     * Sets the memory type of this accessory.
     *
     * @param memoryType the new memory type
     */
    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

    /**
     * Returns a description of this memory accessory that includes its
     * shared and type-specific attributes.
     *
     * @return a human-readable description of the memory accessory
     */
    @Override
    public String getDescription() {
        return String.format(
            "[%s] %s - Capacity: %dGB, Type: %s, Price: %.2f, Compatible consoles: %s",
            getId(), getTitle(), capacityInGB, memoryType, getPrice(), getCompatibleConsoleIds());
    }
}
