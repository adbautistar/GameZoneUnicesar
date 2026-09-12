package com.gamezone.model;

import com.gamezone.testutil.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Sale.canBeReturned tests")
class SaleExtensionsTest {

    @Test
    @DisplayName("Sale with today's date can be returned")
    void todaysSaleCanBeReturned() {
        assertThat(saleFromDaysAgo(0).canBeReturned()).isTrue();
    }

    @Test
    @DisplayName("Sale from 15 days ago can be returned")
    void fifteenDaysAgoCanBeReturned() {
        assertThat(saleFromDaysAgo(15).canBeReturned()).isTrue();
    }

    @Test
    @DisplayName("Sale from exactly 30 days ago can still be returned (inclusive boundary)")
    void thirtyDaysAgoCanBeReturned() {
        assertThat(saleFromDaysAgo(30).canBeReturned()).isTrue();
    }

    @Test
    @DisplayName("Sale from 31 days ago can no longer be returned")
    void thirtyOneDaysAgoCannotBeReturned() {
        assertThat(saleFromDaysAgo(31).canBeReturned()).isFalse();
    }

    @Test
    @DisplayName("Sale from 45 days ago can no longer be returned")
    void fortyFiveDaysAgoCannotBeReturned() {
        assertThat(saleFromDaysAgo(45).canBeReturned()).isFalse();
    }

    private Sale saleFromDaysAgo(int days) {
        Customer customer = TestDataFactory.sampleCustomer("C001");
        Seller seller = TestDataFactory.sampleSeller("S001");
        return TestDataFactory.sampleSale("SALE001", customer, seller,
            List.of(TestDataFactory.sampleVideoGame("VG001")), LocalDate.now().minusDays(days));
    }
}
