package com.gamezone.model;

import com.gamezone.testutil.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Promotion hierarchy tests")
class PromotionHierarchyTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 12, 31);

    @Test
    @DisplayName("isActive returns true for a date within range")
    void isActiveWithinRange() {
        PercentageDiscount promotion = new PercentageDiscount("PR001", "Test", START, END, 10);

        assertThat(promotion.isActive(LocalDate.of(2026, 6, 15))).isTrue();
    }

    @Test
    @DisplayName("isActive returns false before startDate")
    void isActiveBeforeStart() {
        PercentageDiscount promotion = new PercentageDiscount("PR001", "Test", START, END, 10);

        assertThat(promotion.isActive(START.minusDays(1))).isFalse();
    }

    @Test
    @DisplayName("isActive returns false after endDate")
    void isActiveAfterEnd() {
        PercentageDiscount promotion = new PercentageDiscount("PR001", "Test", START, END, 10);

        assertThat(promotion.isActive(END.plusDays(1))).isFalse();
    }

    @Test
    @DisplayName("isActive is inclusive of startDate and endDate")
    void isActiveInclusiveBoundaries() {
        PercentageDiscount promotion = new PercentageDiscount("PR001", "Test", START, END, 10);

        assertThat(promotion.isActive(START)).isTrue();
        assertThat(promotion.isActive(END)).isTrue();
    }

    @Test
    @DisplayName("PercentageDiscount calculates a flat percentage of the sale total")
    void percentageDiscountCalculatesFlatPercentage() {
        PercentageDiscount promotion = new PercentageDiscount("PR001", "Test", START, END, 15);
        Sale sale = saleWithTotal(100000);

        assertThat(promotion.calculateDiscount(sale)).isEqualTo(15000);
    }

    @Test
    @DisplayName("CategoryDiscount only discounts matching-category products")
    void categoryDiscountOnlyMatchingCategory() {
        CategoryDiscount promotion = new CategoryDiscount("PR002", "Test", START, END, 20, "VIDEOGAME");
        VideoGame videoGame = TestDataFactory.sampleVideoGame("VG001");
        videoGame.setPrice(50000);
        Console console = TestDataFactory.sampleConsole("CN001");
        console.setPrice(200000);
        Sale sale = sale(videoGame, console);

        assertThat(promotion.calculateDiscount(sale)).isEqualTo(10000);
    }

    @Test
    @DisplayName("CategoryDiscount returns 0 when no product matches")
    void categoryDiscountNoMatch() {
        CategoryDiscount promotion = new CategoryDiscount("PR002", "Test", START, END, 20, "VIDEOGAME");
        Sale sale = sale(TestDataFactory.sampleConsole("CN001"));

        assertThat(promotion.calculateDiscount(sale)).isEqualTo(0.0);
    }

    @Test
    @DisplayName("BulkPurchaseDiscount applies when quantity meets the minimum")
    void bulkDiscountAppliesAtMinimum() {
        BulkPurchaseDiscount promotion = new BulkPurchaseDiscount("PR003", "Test", START, END, 3, 10);
        Sale sale = sale(TestDataFactory.sampleVideoGame("VG001"), TestDataFactory.sampleVideoGame("VG002"),
            TestDataFactory.sampleVideoGame("VG003"));

        assertThat(promotion.calculateDiscount(sale)).isEqualTo(sale.getTotalAmount() * 0.10);
        assertThat(sale.getTotalAmount()).isEqualTo(300000);
    }

    @Test
    @DisplayName("BulkPurchaseDiscount returns 0 below the minimum quantity")
    void bulkDiscountBelowMinimum() {
        BulkPurchaseDiscount promotion = new BulkPurchaseDiscount("PR003", "Test", START, END, 3, 10);
        Sale sale = sale(TestDataFactory.sampleVideoGame("VG001"), TestDataFactory.sampleVideoGame("VG002"));

        assertThat(promotion.calculateDiscount(sale)).isEqualTo(0.0);
    }

    @Test
    @DisplayName("BulkPurchaseDiscount boundary: exactly the minimum quantity applies")
    void bulkDiscountExactMinimumBoundary() {
        BulkPurchaseDiscount promotion = new BulkPurchaseDiscount("PR003", "Test", START, END, 2, 10);
        Sale sale = sale(TestDataFactory.sampleVideoGame("VG001"), TestDataFactory.sampleVideoGame("VG002"));

        assertThat(promotion.calculateDiscount(sale)).isEqualTo(20000);
    }

    private Sale saleWithTotal(double total) {
        Sale sale = sale(TestDataFactory.sampleVideoGame("VG999"));
        sale.setTotalAmount(total);
        return sale;
    }

    private Sale sale(Product... products) {
        Customer customer = TestDataFactory.sampleCustomer("C001");
        Seller seller = TestDataFactory.sampleSeller("S001");
        return TestDataFactory.sampleSale("SALE001", customer, seller, List.of(products), LocalDate.now());
    }
}
