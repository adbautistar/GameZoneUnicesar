package com.gamezone.service;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.WarrantyRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WarrantyService {

    private final WarrantyRepository repository;
    private final List<Warranty> warranties;

    public WarrantyService(WarrantyRepository repository) {
        this.repository = repository;
        this.warranties = new ArrayList<>(repository.loadAll());
    }

    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        String id = "WAR-" + System.currentTimeMillis();
        BasicWarranty warranty = new BasicWarranty(id, product, sale, startDate);
        warranties.add(warranty);
        repository.saveAll(warranties);
        return warranty;
    }

    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        String id = "WAR-" + System.currentTimeMillis();
        ExtendedWarranty warranty = new ExtendedWarranty(id, product, sale, startDate);
        warranties.add(warranty);
        repository.saveAll(warranties);
        return warranty;
    }
}
