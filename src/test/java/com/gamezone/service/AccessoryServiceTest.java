package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.gamezone.persistence.AccessoryRepository;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("AccessoryService tests")
class AccessoryServiceTest {

    @Mock
    private AccessoryRepository repository;

    private AccessoryService service;

    @BeforeEach
    void setUp() {
        when(repository.loadAll()).thenReturn(new ArrayList<>());
        service = new AccessoryService(repository);
    }

    @Test
    @DisplayName("registerController persists and appears in list")
    void registerControllerPersists() {
        service.registerController("ACC001", "DualSense", 300000, 10, List.of("CN001"), "WIRELESS");

        assertThat(service.listAllAccessories()).hasSize(1);
        assertThat(service.listAllAccessories().get(0)).isInstanceOf(Controller.class);
    }

    @Test
    @DisplayName("registerCable persists and appears in list")
    void registerCablePersists() {
        service.registerCable("ACC002", "HDMI Cable", 40000, 20, List.of("CN001"), 2.0, "HDMI");

        assertThat(service.listAllAccessories()).hasSize(1);
        assertThat(service.listAllAccessories().get(0)).isInstanceOf(Cable.class);
    }

    @Test
    @DisplayName("registerMemory persists and appears in list")
    void registerMemoryPersists() {
        service.registerMemory("ACC003", "microSD", 180000, 15, List.of("CN003"), 256, "MICROSD");

        assertThat(service.listAllAccessories()).hasSize(1);
        assertThat(service.listAllAccessories().get(0)).isInstanceOf(Memory.class);
    }

    @Test
    @DisplayName("listAccessoriesByType filters correctly via instanceof")
    void listAccessoriesByTypeFilters() {
        service.registerController("ACC001", "DualSense", 300000, 10, List.of("CN001"), "WIRELESS");
        service.registerCable("ACC002", "HDMI Cable", 40000, 20, List.of("CN001"), 2.0, "HDMI");
        service.registerMemory("ACC003", "microSD", 180000, 15, List.of("CN003"), 256, "MICROSD");

        assertThat(service.listAccessoriesByType("CONTROLLER")).hasSize(1)
            .allMatch(accessory -> accessory instanceof Controller);
        assertThat(service.listAccessoriesByType("CABLE")).hasSize(1)
            .allMatch(accessory -> accessory instanceof Cable);
        assertThat(service.listAccessoriesByType("MEMORY")).hasSize(1)
            .allMatch(accessory -> accessory instanceof Memory);
    }

    @Test
    @DisplayName("findAccessoriesCompatibleWith returns only compatible accessories")
    void findAccessoriesCompatibleWithFilters() {
        service.registerController("ACC001", "DualSense", 300000, 10, List.of("CN001", "CN002"), "WIRELESS");
        service.registerCable("ACC002", "HDMI Cable", 40000, 20, List.of("CN003"), 2.0, "HDMI");

        List<Accessory> compatible = service.findAccessoriesCompatibleWith("CN001");

        assertThat(compatible).hasSize(1);
        assertThat(compatible.get(0).getId()).isEqualTo("ACC001");
    }

    @Test
    @DisplayName("updateStock adjusts the accessory's stock")
    void updateStockAdjustsStock() {
        service.registerController("ACC001", "DualSense", 300000, 10, List.of("CN001"), "WIRELESS");

        service.updateStock("ACC001", -2);

        assertThat(service.findById("ACC001").getStock()).isEqualTo(8);
    }
}
