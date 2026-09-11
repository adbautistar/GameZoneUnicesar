package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.gamezone.persistence.AccessoryRepository;

import java.util.ArrayList;
import java.util.List;

public class AccessoryService {

    private final AccessoryRepository repository;
    private final List<Accessory> accessories;

    public AccessoryService(AccessoryRepository repository) {
        this.repository = repository;
        this.accessories = new ArrayList<>(repository.loadAll());
    }

    public void registerController(String id, String title, double price, int stock,
                                    List<String> compatibleConsoleIds, String connectionType) {
        Controller controller = new Controller(id, title, price, stock, compatibleConsoleIds, connectionType);
        accessories.add(controller);
        repository.saveAll(accessories);
    }

    public void registerCable(String id, String title, double price, int stock,
                               List<String> compatibleConsoleIds, double lengthInMeters, String connectorType) {
        Cable cable = new Cable(id, title, price, stock, compatibleConsoleIds, lengthInMeters, connectorType);
        accessories.add(cable);
        repository.saveAll(accessories);
    }

    public void registerMemory(String id, String title, double price, int stock,
                                List<String> compatibleConsoleIds, int capacityInGB, String memoryType) {
        Memory memory = new Memory(id, title, price, stock, compatibleConsoleIds, capacityInGB, memoryType);
        accessories.add(memory);
        repository.saveAll(accessories);
    }
}
