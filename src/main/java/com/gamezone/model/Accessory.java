package com.gamezone.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an accessory sold alongside video games and consoles.
 * Concrete accessory types must extend this class and provide their own description.
 */
public abstract class Accessory extends Product {

    private List<String> compatibleConsoleIds;

    /**
     * Creates a new accessory with the given shared and compatibility attributes.
     *
     * @param id                   the unique identifier of the product
     * @param title                the product title
     * @param price                the unit price of the product
     * @param stock                the initial stock quantity
     * @param compatibleConsoleIds the ids of the consoles this accessory is compatible with
     */
    public Accessory(String id, String title, double price, int stock, List<String> compatibleConsoleIds) {
        super(id, title, price, stock);
        this.compatibleConsoleIds = new ArrayList<>(compatibleConsoleIds);
    }

    /**
     * Returns the ids of the consoles this accessory is compatible with.
     *
     * @return the list of compatible console ids
     */
    public List<String> getCompatibleConsoleIds() {
        return compatibleConsoleIds;
    }

    /**
     * Sets the ids of the consoles this accessory is compatible with.
     *
     * @param compatibleConsoleIds the new list of compatible console ids
     */
    public void setCompatibleConsoleIds(List<String> compatibleConsoleIds) {
        this.compatibleConsoleIds = compatibleConsoleIds;
    }

    /**
     * Adds a console id to the list of consoles this accessory is compatible with.
     *
     * @param consoleId the console id to add
     */
    public void addCompatibleConsole(String consoleId) {
        compatibleConsoleIds.add(consoleId);
    }

    /**
     * Checks whether this accessory is compatible with the given console.
     *
     * @param consoleId the console id to check
     * @return {@code true} if the console id is in the compatibility list
     */
    public boolean isCompatibleWith(String consoleId) {
        return compatibleConsoleIds.contains(consoleId);
    }
}
