package com.gamezone.service;

import com.gamezone.model.Controller;
import com.gamezone.model.VideoGame;
import com.gamezone.persistence.AccessoryStore;
import com.gamezone.persistence.ProductStore;
import com.gamezone.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests ItemCatalog with real product and accessory services (over mocked
 * stores), so that the stock really moves in the catalog that owns each item.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ItemCatalog tests")
class ItemCatalogTest {

    @Mock
    private ProductStore productStore;
    @Mock
    private AccessoryStore accessoryStore;

    private VideoGame videoGame;
    private Controller controller;
    private ItemCatalog catalog;

    @BeforeEach
    void setUp() {
        videoGame = TestDataFactory.sampleVideoGame("VG001");
        controller = TestDataFactory.sampleController("ACC001", "CN001");
        when(productStore.loadAll()).thenReturn(new ArrayList<>(List.of(videoGame)));
        when(accessoryStore.loadAll()).thenReturn(new ArrayList<>(List.of(controller)));
        catalog = new ItemCatalog(new ProductService(productStore), new AccessoryService(accessoryStore));
    }

    @Test
    @DisplayName("findItem finds products and accessories, and returns null for unknown ids")
    void findItemAcrossSources() {
        assertThat(catalog.findItem("VG001")).isSameAs(videoGame);
        assertThat(catalog.findItem("ACC001")).isSameAs(controller);
        assertThat(catalog.findItem("UNKNOWN")).isNull();
    }

    @Test
    @DisplayName("decreaseStock lowers the stock of a product and of an accessory by one")
    void decreaseStockInOwningSource() {
        int productStock = videoGame.getStock();
        int accessoryStock = controller.getStock();

        catalog.decreaseStock(videoGame);
        catalog.decreaseStock(controller);

        assertThat(videoGame.getStock()).isEqualTo(productStock - 1);
        assertThat(controller.getStock()).isEqualTo(accessoryStock - 1);
    }

    @Test
    @DisplayName("restoreStock raises the stock of a product and of an accessory by one")
    void restoreStockInOwningSource() {
        int productStock = videoGame.getStock();
        int accessoryStock = controller.getStock();

        catalog.restoreStock(videoGame);
        catalog.restoreStock(controller);

        assertThat(videoGame.getStock()).isEqualTo(productStock + 1);
        assertThat(controller.getStock()).isEqualTo(accessoryStock + 1);
    }
}
