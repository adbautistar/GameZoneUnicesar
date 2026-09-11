package com.gamezone.model;

import java.util.List;

/**
 * Represents a cable accessory, with length and connector type in addition
 * to the attributes shared with every {@link Accessory}.
 */
public class Cable extends Accessory {

    private double lengthInMeters;
    private String connectorType;

    /**
     * Creates a new cable with the given shared and specific attributes.
     *
     * @param id                   the unique identifier of the product
     * @param title                the product title
     * @param price                the unit price of the product
     * @param stock                the initial stock quantity
     * @param compatibleConsoleIds the ids of the consoles this cable is compatible with
     * @param lengthInMeters       the length of the cable, in meters
     * @param connectorType        the connector type (e.g., "HDMI", "USB")
     */
    public Cable(String id, String title, double price, int stock,
                 List<String> compatibleConsoleIds, double lengthInMeters, String connectorType) {
        super(id, title, price, stock, compatibleConsoleIds);
        this.lengthInMeters = lengthInMeters;
        this.connectorType = connectorType;
    }

    /**
     * Returns the length of this cable, in meters.
     *
     * @return the length in meters
     */
    public double getLengthInMeters() {
        return lengthInMeters;
    }

    /**
     * Sets the length of this cable, in meters.
     *
     * @param lengthInMeters the new length in meters
     */
    public void setLengthInMeters(double lengthInMeters) {
        this.lengthInMeters = lengthInMeters;
    }

    /**
     * Returns the connector type of this cable.
     *
     * @return the connector type
     */
    public String getConnectorType() {
        return connectorType;
    }

    /**
     * Sets the connector type of this cable.
     *
     * @param connectorType the new connector type
     */
    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    /**
     * Returns a description of this cable that includes its shared
     * and connector-specific attributes.
     *
     * @return a human-readable description of the cable
     */
    @Override
    public String getDescription() {
        return String.format(
            "[%s] %s - Length: %.1fm, Connector: %s, Price: %.2f, Compatible consoles: %s",
            getId(), getTitle(), lengthInMeters, connectorType, getPrice(), getCompatibleConsoleIds());
    }
}
