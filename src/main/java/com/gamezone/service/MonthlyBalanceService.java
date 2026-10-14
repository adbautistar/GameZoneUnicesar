package com.gamezone.service;

import com.gamezone.model.Return;
import com.gamezone.model.Sale;

/**
 * Computes the monthly balance of the store: the total of the sales made in a
 * month minus the refunds of the returns registered in that month. Reporting
 * is kept apart from return handling so that each can change on its own.
 */
public class MonthlyBalanceService {

    private final SaleLookup saleLookup;
    private final ReturnService returnService;

    /**
     * Creates a new balance service.
     *
     * @param saleLookup    the read-only view of the registered sales
     * @param returnService the service that holds the registered returns
     */
    public MonthlyBalanceService(SaleLookup saleLookup, ReturnService returnService) {
        this.saleLookup = saleLookup;
        this.returnService = returnService;
    }

    /**
     * Computes the balance of the given month: the total of the sales made in
     * that month minus the refunds of the returns registered in that month.
     *
     * @param month the month, from 1 to 12
     * @param year  the year
     * @return total sales minus total refunds for that month
     */
    public double generateMonthlyBalance(int month, int year) {
        double totalSales = 0.0;
        for (Sale sale : saleLookup.viewAllSales()) {
            if (sale.wasMadeIn(month, year)) {
                totalSales += sale.getTotalAmount();
            }
        }

        double totalReturns = 0.0;
        for (Return returnItem : returnService.viewAllReturns()) {
            if (returnItem.wasRegisteredIn(month, year)) {
                totalReturns += returnItem.getRefundAmount();
            }
        }

        return totalSales - totalReturns;
    }
}
