package com.gamezone.service;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.Console;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.WarrantyRepository;
import com.gamezone.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("WarrantyService tests")
class WarrantyServiceTest {

    @Mock
    private WarrantyRepository repository;

    private WarrantyService service;

    @BeforeEach
    void setUp() {
        when(repository.loadAll()).thenReturn(new ArrayList<>());
        service = new WarrantyService(repository);
    }

    @Test
    @DisplayName("assignBasicWarranty creates a BasicWarranty with correct product, sale, and dates, and persists")
    void assignBasicWarrantyCreatesAndPersists() {
        Console console = TestDataFactory.sampleConsole("CN001");
        Sale sale = sampleSale(console);
        LocalDate startDate = LocalDate.now();

        BasicWarranty warranty = service.assignBasicWarranty(console, sale, startDate);

        assertThat(warranty.getProduct()).isEqualTo(console);
        assertThat(warranty.getSale()).isEqualTo(sale);
        assertThat(warranty.getStartDate()).isEqualTo(startDate);
        assertThat(warranty.getEndDate()).isEqualTo(startDate.plusMonths(6));
        assertThat(service.listAllWarranties()).contains(warranty);
    }

    @Test
    @DisplayName("assignExtendedWarranty creates an ExtendedWarranty with correct duration and additional cost")
    void assignExtendedWarrantyCreatesWithCorrectCost() {
        Console console = TestDataFactory.sampleConsole("CN001");
        console.setPrice(2000000);
        Sale sale = sampleSale(console);
        LocalDate startDate = LocalDate.now();

        ExtendedWarranty warranty = service.assignExtendedWarranty(console, sale, startDate);

        assertThat(warranty.getEndDate()).isEqualTo(startDate.plusMonths(12));
        assertThat(warranty.getAdditionalCost()).isEqualTo(200000);
    }

    @Test
    @DisplayName("findWarrantyByProduct returns the matching warranty, or null when not found")
    void findWarrantyByProductReturnsMatchOrNull() {
        Console console = TestDataFactory.sampleConsole("CN001");
        Sale sale = sampleSale(console);
        service.assignBasicWarranty(console, sale, LocalDate.now());

        assertThat(service.findWarrantyByProduct("CN001", sale.getId())).isNotNull();
        assertThat(service.findWarrantyByProduct("UNKNOWN", sale.getId())).isNull();
    }

    @Test
    @DisplayName("listActiveWarranties filters by today's date")
    void listActiveWarrantiesFiltersByToday() {
        Console console = TestDataFactory.sampleConsole("CN001");
        Sale sale = sampleSale(console);
        service.assignBasicWarranty(console, sale, LocalDate.now().minusMonths(7));
        service.assignBasicWarranty(console, sale, LocalDate.now());

        List<Warranty> active = service.listActiveWarranties();

        assertThat(active).hasSize(1);
    }

    @Test
    @DisplayName("listWarrantiesExpiringSoon includes only warranties ending within the window")
    void listWarrantiesExpiringSoonFiltersByWindow() {
        Console console = TestDataFactory.sampleConsole("CN001");
        Sale sale = sampleSale(console);
        // Basic warranty (6 months) starting today ends in ~180 days: excluded from a 30-day window.
        service.assignBasicWarranty(console, sale, LocalDate.now());
        // Basic warranty starting 5 months and 15 days ago ends in ~15 days: included in a 30-day window.
        service.assignBasicWarranty(console, sale, LocalDate.now().minusMonths(5).minusDays(15));
        // Basic warranty that already ended: excluded.
        service.assignBasicWarranty(console, sale, LocalDate.now().minusMonths(7));

        List<Warranty> expiringSoon = service.listWarrantiesExpiringSoon(30);

        assertThat(expiringSoon).hasSize(1);
    }

    private Sale sampleSale(Console console) {
        return TestDataFactory.sampleSale("SALE001", TestDataFactory.sampleCustomer("C001"),
            TestDataFactory.sampleSeller("S001"), List.of(console), LocalDate.now());
    }
}
