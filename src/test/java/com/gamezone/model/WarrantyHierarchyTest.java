package com.gamezone.model;

import com.gamezone.testutil.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Warranty hierarchy tests")
class WarrantyHierarchyTest {

    @Test
    @DisplayName("BasicWarranty endDate is startDate plus 6 months")
    void basicWarrantyEndDate() {
        LocalDate today = LocalDate.now();
        BasicWarranty warranty = new BasicWarranty("WAR001", sampleConsole(), sampleSale(), today);

        assertThat(warranty.getEndDate()).isEqualTo(today.plusMonths(6));
    }

    @Test
    @DisplayName("BasicWarranty duration, type, and cost")
    void basicWarrantyDetails() {
        BasicWarranty warranty = new BasicWarranty("WAR001", sampleConsole(), sampleSale(), LocalDate.now());

        assertThat(warranty.getDurationInMonths()).isEqualTo(6);
        assertThat(warranty.getWarrantyType()).isEqualTo("Garantia Basica");
        assertThat(warranty.getAdditionalCost()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("ExtendedWarranty endDate is startDate plus 12 months")
    void extendedWarrantyEndDate() {
        LocalDate today = LocalDate.now();
        ExtendedWarranty warranty = new ExtendedWarranty("WAR002", sampleConsole(), sampleSale(), today);

        assertThat(warranty.getEndDate()).isEqualTo(today.plusMonths(12));
    }

    @Test
    @DisplayName("ExtendedWarranty duration, type, and cost (10 percent of product price)")
    void extendedWarrantyDetails() {
        Console console = sampleConsole();
        console.setPrice(2000000);
        ExtendedWarranty warranty = new ExtendedWarranty("WAR002", console, sampleSale(), LocalDate.now());

        assertThat(warranty.getDurationInMonths()).isEqualTo(12);
        assertThat(warranty.getWarrantyType()).isEqualTo("Garantia Extendida");
        assertThat(warranty.getAdditionalCost()).isEqualTo(200000);
    }

    @Test
    @DisplayName("isActive: BasicWarranty starting today is active today")
    void basicWarrantyActiveToday() {
        BasicWarranty warranty = new BasicWarranty("WAR001", sampleConsole(), sampleSale(), LocalDate.now());

        assertThat(warranty.isActive(LocalDate.now())).isTrue();
    }

    @Test
    @DisplayName("isActive: BasicWarranty starting 3 months ago is still active today")
    void basicWarrantyActiveThreeMonthsIn() {
        BasicWarranty warranty = new BasicWarranty("WAR001", sampleConsole(), sampleSale(), LocalDate.now().minusMonths(3));

        assertThat(warranty.isActive(LocalDate.now())).isTrue();
    }

    @Test
    @DisplayName("isActive: BasicWarranty starting 7 months ago has expired")
    void basicWarrantyExpiredAfterSevenMonths() {
        BasicWarranty warranty = new BasicWarranty("WAR001", sampleConsole(), sampleSale(), LocalDate.now().minusMonths(7));

        assertThat(warranty.isActive(LocalDate.now())).isFalse();
    }

    private Console sampleConsole() {
        return TestDataFactory.sampleConsole("CN001");
    }

    private Sale sampleSale() {
        Customer customer = TestDataFactory.sampleCustomer("C001");
        Seller seller = TestDataFactory.sampleSeller("S001");
        return TestDataFactory.sampleSale("SALE001", customer, seller,
            List.of(sampleConsole()), LocalDate.now());
    }
}
