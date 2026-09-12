package com.gamezone.service;

import com.gamezone.model.Console;
import com.gamezone.model.VideoGame;
import com.gamezone.persistence.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService tests")
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    private ProductService service;

    @BeforeEach
    void setUp() {
        when(repository.loadAll()).thenReturn(new ArrayList<>());
        service = new ProductService(repository);
    }

    @Test
    @DisplayName("registerVideoGame persists and appears in list")
    void registerVideoGamePersistsAndAppears() {
        service.registerVideoGame("VG001", "Zelda", 100000, 10, "Switch", "Adventure", "E");

        assertThat(service.listAllProducts()).hasSize(1);
        assertThat(service.listAllProducts().get(0)).isInstanceOf(VideoGame.class);
        verify(repository).saveAll(any());
    }

    @Test
    @DisplayName("registerConsole persists and appears in list")
    void registerConsolePersistsAndAppears() {
        service.registerConsole("CN001", "PS5", 2000000, 5, "Sony", "PS5", "9th");

        assertThat(service.listAllProducts()).hasSize(1);
        assertThat(service.listAllProducts().get(0)).isInstanceOf(Console.class);
        verify(repository).saveAll(any());
    }

    @Test
    @DisplayName("updateStock with a valid product decreases stock")
    void updateStockDecreasesStock() {
        service.registerVideoGame("VG001", "Zelda", 100000, 10, "Switch", "Adventure", "E");

        service.updateStock("VG001", -3);

        assertThat(service.findById("VG001").getStock()).isEqualTo(7);
    }

    @Test
    @DisplayName("restoreStock increments stock and persists")
    void restoreStockIncrementsStock() {
        service.registerVideoGame("VG001", "Zelda", 100000, 10, "Switch", "Adventure", "E");

        service.restoreStock("VG001", 5);

        assertThat(service.findById("VG001").getStock()).isEqualTo(15);
    }

    @Test
    @DisplayName("restoreStock on an unknown id throws")
    void restoreStockUnknownIdThrows() {
        assertThatThrownBy(() -> service.restoreStock("UNKNOWN", 1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("findById returns the correct product, or null for an unknown id")
    void findByIdReturnsCorrectProductOrNull() {
        service.registerVideoGame("VG001", "Zelda", 100000, 10, "Switch", "Adventure", "E");

        assertThat(service.findById("VG001")).isNotNull();
        assertThat(service.findById("UNKNOWN")).isNull();
    }
}
