package com.gamezone.model;

/**
 * Represents a video game product, with platform, genre, and age rating
 * in addition to the attributes shared with every {@link Product}.
 */
public class VideoGame extends Product {

    private String platform;
    private String genre;
    private String ageRating;

    /**
     * Creates a new video game with the given shared and specific attributes.
     *
     * @param id        the unique identifier of the product
     * @param title     the product title
     * @param price     the unit price of the product
     * @param stock     the initial stock quantity
     * @param platform  the platform the game runs on
     * @param genre     the genre of the game
     * @param ageRating the age rating of the game
     */
    public VideoGame(String id, String title, double price, int stock,
                      String platform, String genre, String ageRating) {
        super(id, title, price, stock);
        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
    }

    /**
     * Returns the platform this game runs on.
     *
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Sets the platform this game runs on.
     *
     * @param platform the new platform
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * Returns the genre of this game.
     *
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets the genre of this game.
     *
     * @param genre the new genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Returns the age rating of this game.
     *
     * @return the age rating
     */
    public String getAgeRating() {
        return ageRating;
    }

    /**
     * Sets the age rating of this game.
     *
     * @param ageRating the new age rating
     */
    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    /**
     * Returns a description of this video game that includes its shared
     * and platform-specific attributes.
     *
     * @return a human-readable description of the video game
     */
    @Override
    public String getDescription() {
        return String.format(
            "[%s] %s - Platform: %s, Genre: %s, Age Rating: %s, Price: %.2f, Stock: %d",
            getId(), getTitle(), platform, genre, ageRating, getPrice(), getStock());
    }
}
