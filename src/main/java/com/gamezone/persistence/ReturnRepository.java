package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReturnRepository {

    private static final String FILE_PATH = "data/returns.csv";

    private final SaleService saleService;
    private final ProductService productService;
    private final AccessoryService accessoryService;

    public ReturnRepository(SaleService saleService, ProductService productService,
                             AccessoryService accessoryService) {
        this.saleService = saleService;
        this.productService = productService;
        this.accessoryService = accessoryService;
    }

    public void saveAll(List<Return> returns) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Return returnItem : returns) {
                writer.write(toCsvLine(returnItem));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save returns to " + FILE_PATH, e);
        }
    }

    private String toCsvLine(Return returnItem) {
        StringBuilder productIds = new StringBuilder();
        List<Product> returnedProducts = returnItem.getReturnedProducts();
        for (int i = 0; i < returnedProducts.size(); i++) {
            if (i > 0) {
                productIds.append("|");
            }
            productIds.append(returnedProducts.get(i).getId());
        }
        return String.join(",",
            returnItem.getId(),
            returnItem.getDate().toString(),
            returnItem.getOriginalSale().getId(),
            productIds.toString(),
            returnItem.getReason(),
            String.valueOf(returnItem.getRefundAmount()));
    }
}
