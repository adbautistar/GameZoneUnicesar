package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

    public List<Accessory> loadAll() {
        List<Accessory> accessories = new ArrayList<>();
        Path path = Path.of(FILE_PATH);
        if (!Files.exists(path)) {
            return accessories;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                accessories.add(fromCsvLine(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load accessories from " + FILE_PATH, e);
        }
        return accessories;
    }

    private Accessory fromCsvLine(String line) {
        String[] fields = line.split(",", -1);
        String type = fields[0];
        String id = fields[1];
        String title = fields[2];
        double price = Double.parseDouble(fields[3]);
        int stock = Integer.parseInt(fields[4]);
        List<String> compatibleConsoleIds = parseCompatibleConsoleIds(fields[5]);
        if (CONTROLLER_TYPE.equals(type)) {
            return new Controller(id, title, price, stock, compatibleConsoleIds, fields[6]);
        }
        if (CABLE_TYPE.equals(type)) {
            return new Cable(id, title, price, stock, compatibleConsoleIds, Double.parseDouble(fields[6]), fields[7]);
        }
        if (MEMORY_TYPE.equals(type)) {
            return new Memory(id, title, price, stock, compatibleConsoleIds, Integer.parseInt(fields[6]), fields[7]);
        }
        throw new IllegalArgumentException("Unknown accessory type in CSV: " + type);
    }

    private List<String> parseCompatibleConsoleIds(String field) {
        return field.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(field.split("\\|")));
    }
}
