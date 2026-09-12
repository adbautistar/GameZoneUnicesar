package com.gamezone.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Product hierarchy tests")
class ProductHierarchyTest {

    @Test
    @DisplayName("VideoGame constructor sets all attributes")
    void videoGameConstructorSetsAttributes() {
        VideoGame videoGame = new VideoGame("VG001", "Zelda", 100000, 10, "Switch", "Adventure", "E");

        assertThat(videoGame.getId()).isEqualTo("VG001");
        assertThat(videoGame.getTitle()).isEqualTo("Zelda");
        assertThat(videoGame.getPrice()).isEqualTo(100000);
        assertThat(videoGame.getStock()).isEqualTo(10);
        assertThat(videoGame.getPlatform()).isEqualTo("Switch");
        assertThat(videoGame.getGenre()).isEqualTo("Adventure");
        assertThat(videoGame.getAgeRating()).isEqualTo("E");
    }

    @Test
    @DisplayName("VideoGame getDescription includes title and genre")
    void videoGameDescriptionIncludesTitleAndGenre() {
        VideoGame videoGame = new VideoGame("VG001", "Zelda", 100000, 10, "Switch", "Adventure", "E");

        assertThat(videoGame.getDescription())
            .contains("Zelda")
            .contains("Adventure");
    }

    @Test
    @DisplayName("VideoGame is a Product")
    void videoGameIsAProduct() {
        VideoGame videoGame = new VideoGame("VG001", "Zelda", 100000, 10, "Switch", "Adventure", "E");

        assertThat(videoGame).isInstanceOf(Product.class);
    }

    @Test
    @DisplayName("Console constructor sets all attributes")
    void consoleConstructorSetsAttributes() {
        Console console = new Console("CN001", "PS5", 2000000, 5, "Sony", "PS5", "9th");

        assertThat(console.getId()).isEqualTo("CN001");
        assertThat(console.getTitle()).isEqualTo("PS5");
        assertThat(console.getPrice()).isEqualTo(2000000);
        assertThat(console.getStock()).isEqualTo(5);
        assertThat(console.getBrand()).isEqualTo("Sony");
        assertThat(console.getModel()).isEqualTo("PS5");
        assertThat(console.getGeneration()).isEqualTo("9th");
    }

    @Test
    @DisplayName("Console getDescription includes brand and model")
    void consoleDescriptionIncludesBrandAndModel() {
        Console console = new Console("CN001", "PS5", 2000000, 5, "Sony", "PS5", "9th");

        assertThat(console.getDescription())
            .contains("Sony")
            .contains("PS5");
    }

    @Test
    @DisplayName("Console is a Product")
    void consoleIsAProduct() {
        Console console = new Console("CN001", "PS5", 2000000, 5, "Sony", "PS5", "9th");

        assertThat(console).isInstanceOf(Product.class);
    }
}
