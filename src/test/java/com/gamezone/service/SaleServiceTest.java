package com.gamezone.service;

import com.gamezone.model.Console;
import com.gamezone.model.Customer;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.VideoGame;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaleService tests")
class SaleServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    private PersonService personService;
    @Mock
    private ItemCatalog itemCatalog;

    private Customer customer;
    private Seller seller;
    private VideoGame videoGame;
    private Console console;
    private Path salesFile;

    @BeforeEach
    void setUp() {
        customer = TestDataFactory.sampleCustomer("C001");
        seller = TestDataFactory.sampleSeller("S001");
        videoGame = TestDataFactory.sampleVideoGame("VG001");
        console = TestDataFactory.sampleConsole("CN001");
        salesFile = tempDir.resolve("sales.csv");
    }

    @Test
    @DisplayName("loading rebuilds each sale from its references, reapplies the stored discount and fills the purchase history")
    void loadResolvesReferencesAndPurchaseHistory() throws IOException {
        LocalDate date = LocalDate.now().minusDays(3);
        Files.writeString(salesFile,
            "SALE001," + date + ",C001,S001,VG001;CN001,9999.0,Descuento General 15%,100.0\n");
        when(personService.findCustomerById("C001")).thenReturn(customer);
        when(personService.findSellerById("S001")).thenReturn(seller);
        when(itemCatalog.findItem("VG001")).thenReturn(videoGame);
        when(itemCatalog.findItem("CN001")).thenReturn(console);

        SaleService service = new SaleService(new SaleRepository(salesFile.toString()), personService, itemCatalog);

        assertThat(service.viewAllSales()).hasSize(1);
        Sale sale = service.findById("SALE001");
        assertThat(sale.getDate()).isEqualTo(date);
        assertThat(sale.getCustomer()).isSameAs(customer);
        assertThat(sale.getSeller()).isSameAs(seller);
        assertThat(sale.getProducts()).containsExactly(videoGame, console);
        assertThat(sale.getAppliedPromotionName()).isEqualTo("Descuento General 15%");
        assertThat(sale.getDiscountAmount()).isEqualTo(100.0);
        // The total is rebuilt from the item prices minus the stored discount, ignoring the stored
        // total (out-of-scope defect OOS-01, preserved on purpose by the GRASP correction).
        assertThat(sale.getTotalAmount()).isEqualTo(videoGame.getPrice() + console.getPrice() - 100.0);
        assertThat(customer.getPurchaseHistory()).containsExactly(sale);
    }

    @Test
    @DisplayName("loading fails when a stored sale references a customer that no longer exists")
    void loadFailsOnUnknownCustomer() throws IOException {
        Files.writeString(salesFile, "SALE001," + LocalDate.now() + ",C404,S001,VG001,100000.0,,0.0\n");
        when(personService.findCustomerById("C404")).thenReturn(null);
        when(personService.findSellerById("S001")).thenReturn(seller);

        assertThatThrownBy(() -> new SaleService(new SaleRepository(salesFile.toString()), personService, itemCatalog))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to resolve customer or seller for sale SALE001");
    }

    @Test
    @DisplayName("recordSale adds the sale to the history and to the customer's purchases, and persists it")
    void recordSaleAddsAndPersists() throws IOException {
        SaleService service = new SaleService(new SaleRepository(salesFile.toString()), personService, itemCatalog);
        Sale sale = TestDataFactory.sampleSale("SALE002", customer, seller, List.of(videoGame), LocalDate.now());

        service.recordSale(sale);

        assertThat(service.viewAllSales()).containsExactly(sale);
        assertThat(customer.getPurchaseHistory()).containsExactly(sale);
        assertThat(Files.readAllLines(salesFile)).hasSize(1).first().asString().startsWith("SALE002,");
    }
}
