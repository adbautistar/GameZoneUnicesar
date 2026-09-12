package com.gamezone.model;

import com.gamezone.testutil.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Accessory hierarchy tests")
class AccessoryHierarchyTest {

    @Test
    @DisplayName("Controller constructor sets inherited and own attributes")
    void controllerConstructorSetsAttributes() {
        Controller controller = new Controller("ACC001", "DualSense", 300000, 10, List.of("CN001"), "WIRELESS");

        assertThat(controller.getId()).isEqualTo("ACC001");
        assertThat(controller.getTitle()).isEqualTo("DualSense");
        assertThat(controller.getPrice()).isEqualTo(300000);
        assertThat(controller.getStock()).isEqualTo(10);
        assertThat(controller.getConnectionType()).isEqualTo("WIRELESS");
        assertThat(controller.getCompatibleConsoleIds()).containsExactly("CN001");
    }

    @Test
    @DisplayName("Cable constructor sets inherited and own attributes")
    void cableConstructorSetsAttributes() {
        Cable cable = new Cable("ACC002", "HDMI Cable", 40000, 20, List.of("CN001", "CN002"), 2.0, "HDMI");

        assertThat(cable.getLengthInMeters()).isEqualTo(2.0);
        assertThat(cable.getConnectorType()).isEqualTo("HDMI");
        assertThat(cable.getCompatibleConsoleIds()).containsExactly("CN001", "CN002");
    }

    @Test
    @DisplayName("Memory constructor sets inherited and own attributes")
    void memoryConstructorSetsAttributes() {
        Memory memory = new Memory("ACC003", "microSD", 180000, 15, List.of("CN003"), 256, "MICROSD");

        assertThat(memory.getCapacityInGB()).isEqualTo(256);
        assertThat(memory.getMemoryType()).isEqualTo("MICROSD");
    }

    @Test
    @DisplayName("addCompatibleConsole adds to the internal list")
    void addCompatibleConsoleAddsToList() {
        Controller controller = TestDataFactory.sampleController("ACC010");

        controller.addCompatibleConsole("CN099");

        assertThat(controller.getCompatibleConsoleIds()).contains("CN099");
    }

    @Test
    @DisplayName("isCompatibleWith returns true for added consoles, false otherwise")
    void isCompatibleWithChecksCompatibility() {
        Controller controller = TestDataFactory.sampleController("ACC011", "CN001", "CN002");

        assertThat(controller.isCompatibleWith("CN001")).isTrue();
        assertThat(controller.isCompatibleWith("CN999")).isFalse();
    }

    @Test
    @DisplayName("getDescription includes type-specific attributes")
    void getDescriptionIncludesTypeSpecificAttributes() {
        Controller controller = new Controller("ACC001", "DualSense", 300000, 10, List.of("CN001"), "WIRELESS");
        Cable cable = new Cable("ACC002", "HDMI Cable", 40000, 20, List.of("CN001"), 2.0, "HDMI");
        Memory memory = new Memory("ACC003", "microSD", 180000, 15, List.of("CN003"), 256, "MICROSD");

        assertThat(controller.getDescription()).contains("WIRELESS");
        assertThat(cable.getDescription()).contains("HDMI");
        assertThat(memory.getDescription()).contains("MICROSD");
    }

    @Test
    @DisplayName("Controller is an Accessory and a Product")
    void controllerInheritanceChain() {
        Controller controller = new Controller("ACC001", "DualSense", 300000, 10, List.of("CN001"), "WIRELESS");

        assertThat(controller).isInstanceOf(Accessory.class);
        assertThat(controller).isInstanceOf(Product.class);
    }
}
