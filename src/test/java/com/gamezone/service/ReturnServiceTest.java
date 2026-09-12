package com.gamezone.service;

import com.gamezone.model.Accessory;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReturnService tests")
class ReturnServiceTest {

    @Mock
    private ReturnRepository repository;
    @Mock
    private SaleService saleService;
    @Mock
    private ProductService productService;
    @Mock
    private AccessoryService accessoryService;

    private ReturnService service;

    @BeforeEach
    void setUp() {
        when(repository.loadAll()).thenReturn(new ArrayList<>());
        service = new ReturnService(repository, saleService, productService, accessoryService);
    }

    @Test
    @DisplayName("registerReturn happy path: valid sale, valid products, restores stock")
    void registerReturnHappyPath() {
        VideoGame product = TestDataFactory.sampleVideoGame("VG001");
        Sale sale = sale(product, LocalDate.now());
        when(saleService.findById("SALE001")).thenReturn(sale);

        Return result = service.registerReturn("SALE001", List.of("VG001"), "Defective");

        assertThat(result).isNotNull();
        assertThat(result.getReturnedProducts()).containsExactly(product);
        verify(productService).restoreStock("VG001", 1);
        verify(repository).saveAll(anyList());
    }

    @Test
    @DisplayName("registerReturn rejects a sale that cannot be found")
    void registerReturnSaleNotFoundThrows() {
        when(saleService.findById("UNKNOWN")).thenReturn(null);

        assertThatThrownBy(() -> service.registerReturn("UNKNOWN", List.of("VG001"), "Defective"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Venta no encontrada");
    }

    @Test
    @DisplayName("registerReturn rejects a sale older than 30 days")
    void registerReturnSaleTooOldThrows() {
        VideoGame product = TestDataFactory.sampleVideoGame("VG001");
        Sale sale = sale(product, LocalDate.now().minusDays(45));
        when(saleService.findById("SALE001")).thenReturn(sale);

        assertThatThrownBy(() -> service.registerReturn("SALE001", List.of("VG001"), "Defective"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("30 dias");
    }

    @Test
    @DisplayName("registerReturn rejects a product not in the original sale")
    void registerReturnProductNotInSaleThrows() {
        VideoGame product = TestDataFactory.sampleVideoGame("VG001");
        Sale sale = sale(product, LocalDate.now());
        when(saleService.findById("SALE001")).thenReturn(sale);

        assertThatThrownBy(() -> service.registerReturn("SALE001", List.of("VG999"), "Defective"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("VG999");
    }

    @Test
    @DisplayName("registerReturn restores accessory stock via AccessoryService")
    void registerReturnRestoresAccessoryStock() {
        Accessory accessory = TestDataFactory.sampleController("ACC001");
        Sale sale = sale(accessory, LocalDate.now());
        when(saleService.findById("SALE001")).thenReturn(sale);

        service.registerReturn("SALE001", List.of("ACC001"), "Defective");

        verify(accessoryService).updateStock("ACC001", 1);
    }

    @Test
    @DisplayName("generateMonthlyBalance computes sales minus returns for the given month")
    void generateMonthlyBalanceComputesDifference() {
        LocalDate today = LocalDate.now();
        VideoGame soldProduct = TestDataFactory.sampleVideoGame("VG001");
        Sale sale = sale(soldProduct, today);
        when(saleService.viewAllSales()).thenReturn(List.of(sale));
        when(saleService.findById("SALE001")).thenReturn(sale);

        Return returnItem = service.registerReturn("SALE001", List.of("VG001"), "Defective");

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
