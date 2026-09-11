package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AccessoryRepository {

    private static final String FILE_PATH = "data/accessories.csv";
    private static final String CONTROLLER_TYPE = "CONTROLLER";
    private static final String CABLE_TYPE = "CABLE";
    private static final String MEMORY_TYPE = "MEMORY";

    public void saveAll(List<Accessory> accessories) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Accessory accessory : accessories) {
                writer.write(toCsvLine(accessory));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save accessories to " + FILE_PATH, e);
        }
    }

    private String toCsvLine(Accessory accessory) {
        String compatibleConsoleIds = String.join("|", accessory.getCompatibleConsoleIds());
        if (accessory instanceof Controller controller) {
            return String.join(",",
                CONTROLLER_TYPE,
                controller.getId(),
                controller.getTitle(),
                String.valueOf(controller.getPrice()),
                String.valueOf(controller.getStock()),
                compatibleConsoleIds,
                controller.getConnectionType());
        }
        if (accessory instanceof Cable cable) {
            return String.join(",",
                CABLE_TYPE,
                cable.getId(),
                cable.getTitle(),
                String.valueOf(cable.getPrice()),
                String.valueOf(cable.getStock()),
                compatibleConsoleIds,
                String.valueOf(cable.getLengthInMeters()),
                cable.getConnectorType());
        }
        if (accessory instanceof Memory memory) {
            return String.join(",",
                MEMORY_TYPE,
                memory.getId(),
                memory.getTitle(),
                String.valueOf(memory.getPrice()),
                String.valueOf(memory.getStock()),
                compatibleConsoleIds,
                String.valueOf(memory.getCapacityInGB()),
                memory.getMemoryType());
        }
        throw new IllegalArgumentException("Unsupported accessory type: " + accessory.getClass());
    }

    private List<String> parseCompatibleConsoleIds(String field) {
        return field.isEmpty() ? List.of() : Arrays.asList(field.split("\\|"));
    }
}
