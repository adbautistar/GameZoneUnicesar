package com.gamezone.model;

import com.gamezone.testutil.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Return tests")
class ReturnTest {

    @Test
    @DisplayName("constructor calculates refundAmount from the returned products")
    void constructorCalculatesRefundAmount() {
        Sale sale = sampleSale();
        VideoGame product = TestDataFactory.sampleVideoGame("VG001");
        product.setPrice(50000);

        Return returnItem = new Return("RET001", LocalDate.now(), sale, List.of(product), "Defective");

        assertThat(returnItem.getRefundAmount()).isEqualTo(50000);
    }

    @Test
    @DisplayName("calculateRefundAmount returns the sum of returned product prices")
    void calculateRefundAmountSumsPrices() {
        Sale sale = sampleSale();
        VideoGame p1 = TestDataFactory.sampleVideoGame("VG001");
        p1.setPrice(30000);
        Console p2 = TestDataFactory.sampleConsole("CN001");
        p2.setPrice(70000);

        Return returnItem = new Return("RET001", LocalDate.now(), sale, List.of(p1, p2), "Defective");

        assertThat(returnItem.calculateRefundAmount()).isEqualTo(100000);
    }

    @Test
    @DisplayName("generateReturnReceipt contains sale id, product titles, reason, and refund amount")
    void generateReturnReceiptContainsDetails() {
        Sale sale = sampleSale();
        VideoGame product = TestDataFactory.sampleVideoGame("VG001");

        Return returnItem = new Return("RET001", LocalDate.now(), sale, List.of(product), "Producto defectuoso");

        assertThat(returnItem.generateReturnReceipt())
            .contains(sale.getId())
            .contains(product.getTitle())
            .contains("Producto defectuoso")
            .contains(String.format("%.2f", returnItem.getRefundAmount()));
    }

    @Test
    @DisplayName("Return has no setter for originalSale or date")
    void returnHasNoSettersForImmutableFields() {
        assertThatThrownBy(() -> Return.class.getMethod("setOriginalSale", Sale.class))
            .isInstanceOf(NoSuchMethodException.class);
        assertThatThrownBy(() -> Return.class.getMethod("setDate", LocalDate.class))
            .isInstanceOf(NoSuchMethodException.class);
    }

    private Sale sampleSale() {
        Customer customer = TestDataFactory.sampleCustomer("C001");
        Seller seller = TestDataFactory.sampleSeller("S001");
        return TestDataFactory.sampleSale("SALE001", customer, seller,
            List.of(TestDataFactory.sampleVideoGame("VG999")), LocalDate.now());
    }
}
