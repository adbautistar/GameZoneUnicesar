package com.gamezone.persistence;

import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Warranty;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WarrantyRepository {

    private static final String FILE_PATH = "data/warranties.csv";
    private static final String BASIC_TYPE = "BASIC";
    private static final String EXTENDED_TYPE = "EXTENDED";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final SaleService saleService;
    private final ProductService productService;
    private final AccessoryService accessoryService;

    public WarrantyRepository(SaleService saleService, ProductService productService,
                               AccessoryService accessoryService) {
        this.saleService = saleService;
        this.productService = productService;
        this.accessoryService = accessoryService;
    }

    public void saveAll(List<Warranty> warranties) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Warranty warranty : warranties) {
                writer.write(toCsvLine(warranty));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save warranties to " + FILE_PATH, e);
        }
    }

    private String toCsvLine(Warranty warranty) {
        String type = warranty instanceof ExtendedWarranty ? EXTENDED_TYPE : BASIC_TYPE;
        return String.join(",",
            type,
            warranty.getId(),
            warranty.getProduct().getId(),
            warranty.getSale().getId(),
            warranty.getStartDate().format(DATE_FORMATTER),
            warranty.getEndDate().format(DATE_FORMATTER));
    }
}
