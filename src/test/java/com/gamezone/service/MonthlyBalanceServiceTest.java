package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.VideoGame;
import com.gamezone.persistence.ReturnRepository;
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

/**
 * Tests for the monthly balance, moved from ReturnServiceTest together with
 * the responsibility (same scenarios and assertions).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MonthlyBalanceService tests")
class MonthlyBalanceServiceTest {

    @Mock
    private ReturnRepository repository;
    @Mock
    private SaleService saleService;
    @Mock
    private ProductService productService;
    @Mock
    private AccessoryService accessoryService;

    private ReturnService returnService;
    private MonthlyBalanceService service;

    @BeforeEach
    void setUp() {
        when(repository.loadAll()).thenReturn(new ArrayList<>());
        returnService = new ReturnService(repository, saleService, new ItemCatalog(productService, accessoryService));
        service = new MonthlyBalanceService(saleService, returnService);
    }

    @Test
    @DisplayName("generateMonthlyBalance computes sales minus returns for the given month")
    void generateMonthlyBalanceComputesDifference() {
        LocalDate today = LocalDate.now();
        VideoGame soldProduct = TestDataFactory.sampleVideoGame("VG001");
        Sale sale = sale(soldProduct, today);
        when(saleService.viewAllSales()).thenReturn(List.of(sale));
        when(saleService.findById("SALE001")).thenReturn(sale);

        Return returnItem = returnService.registerReturn("SALE001", List.of("VG001"), "Defective");

        double balance = service.generateMonthlyBalance(today.getMonthValue(), today.getYear());

        assertThat(balance).isEqualTo(sale.getTotalAmount() - returnItem.getRefundAmount());
    }

    @Test
    @DisplayName("generateMonthlyBalance returns 0.0 for a month with no sales or returns")
    void generateMonthlyBalanceEmptyMonthReturnsZero() {
        when(saleService.viewAllSales()).thenReturn(List.of());

        double balance = service.generateMonthlyBalance(1, 2000);

        assertThat(balance).isEqualTo(0.0);
    }

    private Sale sale(Product product, LocalDate date) {
        return TestDataFactory.sampleSale("SALE001", TestDataFactory.sampleCustomer("C001"),
            TestDataFactory.sampleSeller("S001"), List.of(product), date);
    }
}
