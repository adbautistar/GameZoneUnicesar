package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.persistence.ReturnRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReturnService {

    private final ReturnRepository repository;
    private final SaleService saleService;
    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final List<Return> returns;

    public ReturnService(ReturnRepository repository, SaleService saleService, ProductService productService,
                          AccessoryService accessoryService) {
        this.repository = repository;
        this.saleService = saleService;
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.returns = new ArrayList<>(repository.loadAll());
    }

    public Return registerReturn(String saleId, List<String> productIds, String reason) {
        Sale sale = saleService.findById(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("Venta no encontrada.");
        }
        if (!sale.canBeReturned()) {
            throw new IllegalArgumentException("La venta excede el plazo de 30 dias para devolucion.");
        }

        List<Product> returnedProducts = new ArrayList<>();
        for (String productId : productIds) {
            Product matched = null;
            for (Product product : sale.getProducts()) {
                if (product.getId().equals(productId)) {
                    matched = product;
                    break;
                }
            }
            if (matched == null) {
                throw new IllegalArgumentException("El producto " + productId + " no pertenece a la venta original.");
            }
            returnedProducts.add(matched);
        }

        String returnId = "RET-" + System.currentTimeMillis();
        Return newReturn = new Return(returnId, LocalDate.now(), sale, returnedProducts, reason);

        for (Product product : returnedProducts) {
            if (product instanceof Accessory) {
                accessoryService.updateStock(product.getId(), 1);
            } else {
                productService.restoreStock(product.getId(), 1);
            }
        }

        returns.add(newReturn);
        repository.saveAll(returns);

        return newReturn;
    }

    public List<Return> viewAllReturns() {
        return Collections.unmodifiableList(returns);
    }

    public List<Return> viewReturnsByCustomer(String customerId) {
        List<Return> result = new ArrayList<>();
        for (Return returnItem : returns) {
            if (returnItem.getOriginalSale().getCustomer().getId().equals(customerId)) {
                result.add(returnItem);
            }
        }
        return result;
    }

    public List<Return> viewReturnsBySale(String saleId) {
        List<Return> result = new ArrayList<>();
        for (Return returnItem : returns) {
            if (returnItem.getOriginalSale().getId().equals(saleId)) {
                result.add(returnItem);
            }
        }
        return result;
    }

    public double generateMonthlyBalance(int month, int year) {
        double totalSales = 0.0;
        for (Sale sale : saleService.viewAllSales()) {
            if (sale.getDate().getMonthValue() == month && sale.getDate().getYear() == year) {
                totalSales += sale.getTotalAmount();
            }
        }

        double totalReturns = 0.0;
        for (Return returnItem : returns) {
            if (returnItem.getDate().getMonthValue() == month && returnItem.getDate().getYear() == year) {
                totalReturns += returnItem.getRefundAmount();
            }
        }

        return totalSales - totalReturns;
    }
}
