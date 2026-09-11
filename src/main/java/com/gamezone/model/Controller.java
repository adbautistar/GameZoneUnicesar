package com.gamezone.model;

import java.util.List;

/**
 * Represents a game controller accessory, with a connection type in addition
 * to the attributes shared with every {@link Accessory}.
 */
public class Controller extends Accessory {

    private String connectionType;

    /**
     * Creates a new controller with the given shared and specific attributes.
     *
     * @param id                   the unique identifier of the product
     * @param title                the product title
     * @param price                the unit price of the product
     * @param stock                the initial stock quantity
     * @param compatibleConsoleIds the ids of the consoles this controller is compatible with
     * @param connectionType       the connection type ("WIRELESS" or "WIRED")
     */
    public Controller(String id, String title, double price, int stock,
                       List<String> compatibleConsoleIds, String connectionType) {
        super(id, title, price, stock, compatibleConsoleIds);
        this.connectionType = connectionType;
    }

    /**
     * Returns the connection type of this controller.
     *
     * @return the connection type
     */
    public String getConnectionType() {
        return connectionType;
    }

    /**
     * Sets the connection type of this controller.
     *
     * @param connectionType the new connection type
     */
    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    /**
     * Returns a description of this controller that includes its shared
     * and connection-specific attributes.
     *
     * @return a human-readable description of the controller
     */
    @Override
    public String getDescription() {
        return String.format(
            "[%s] %s - Connection: %s, Price: %.2f, Compatible consoles: %s",
            getId(), getTitle(), connectionType, getPrice(), getCompatibleConsoleIds());
    }
}
