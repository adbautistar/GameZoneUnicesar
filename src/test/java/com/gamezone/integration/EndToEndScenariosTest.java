package com.gamezone.integration;

import com.gamezone.model.Customer;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.PromotionRepository;
import com.gamezone.persistence.ReturnRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.persistence.WarrantyRepository;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;
import com.gamezone.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Wires up all real services, backed by temporary files (via the file-path
 * constructor overload added to every Repository), to exercise the actual
 * crossings between Sale, Product, Accessory, Promotion, Return, and
 * Warranty end to end.
 */
@DisplayName("End-to-end integration scenarios")
class EndToEndScenariosTest {

    @TempDir
    Path tempDir;

    private ProductService productService;
    private PromotionService promotionService;
    private SaleService saleService;
    private ReturnService returnService;
    private WarrantyService warrantyService;

    private Customer customer;
    private Seller seller;

    @BeforeEach
    void setUp() {
        ProductRepository productRepository = new ProductRepository(tempDir.resolve("products.csv").toString());
        PersonRepository personRepository = new PersonRepository(
            tempDir.resolve("customers.csv").toString(), tempDir.resolve("sellers.csv").toString());
        personRepository.saveAllSellers(List.of(TestDataFactory.sampleSeller("S001")));

        AccessoryRepository accessoryRepository = new AccessoryRepository(tempDir.resolve("accessories.csv").toString());
        PromotionRepository promotionRepository = new PromotionRepository(tempDir.resolve("promotions.csv").toString());

        productService = new ProductService(productRepository);
        PersonService personService = new PersonService(personRepository);
        AccessoryService accessoryService = new AccessoryService(accessoryRepository);
        promotionService = new PromotionService(promotionRepository);

        SaleRepository saleRepository = new SaleRepository(productService, personService, accessoryService,
            tempDir.resolve("sales.csv").toString());
        saleService = new SaleService(saleRepository, productService, personService, accessoryService, promotionService);

        ReturnRepository returnRepository = new ReturnRepository(saleService, productService, accessoryService,
            tempDir.resolve("returns.csv").toString());
        returnService = new ReturnService(returnRepository, saleService, productService, accessoryService);

        WarrantyRepository warrantyRepository = new WarrantyRepository(saleService, productService, accessoryService,
            tempDir.resolve("warranties.csv").toString());
        warrantyService = new WarrantyService(warrantyRepository);
        saleService.setWarrantyService(warrantyService);

        personService.registerCustomer("C001", "Test", "Customer", "3000000000", "test@example.com");
        customer = personService.findCustomerById("C001");
        seller = personService.findSellerById("S001");
    }

    @Test
    @DisplayName("Scenario 1: a sale with one console gets an automatic basic warranty and no discount when none is active")
    void scenario1_consoleSaleGetsBasicWarrantyNoDiscount() {
        productService.registerConsole("CN001", "PS5", 2000000, 5, "Sony", "PS5", "9th");

        Sale sale = saleService.registerSale(customer.getId(), seller.getId(), List.of("CN001"), List.of());

        assertThat(sale.getDiscountAmount()).isEqualTo(0.0);
        assertThat(sale.getTotalAmount()).isEqualTo(2000000);
        Warranty warranty = warrantyService.findWarrantyByProduct("CN001", sale.getId());
        assertThat(warranty).isNotNull();
        assertThat(warranty.getWarrantyType()).isEqualTo("Garantia Basica");
    }

    @Test
    @DisplayName("Scenario 2: a bulk purchase discount applies when a sale meets the minimum quantity")
    void scenario2_bulkDiscountAppliesForThreeVideogames() {
        productService.registerVideoGame("VG001", "Game A", 50000, 10, "PS5", "Action", "T");
        productService.registerVideoGame("VG002", "Game B", 50000, 10, "PS5", "Action", "T");
        productService.registerVideoGame("VG003", "Game C", 50000, 10, "PS5", "Action", "T");
        promotionService.registerBulkPurchaseDiscount("PR001", "Bulk 10%", LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1), 3, 10);

        Sale sale = saleService.registerSale(customer.getId(), seller.getId(),
            List.of("VG001", "VG002", "VG003"), List.of());

        assertThat(sale.getDiscountAmount()).isGreaterThan(0.0);
        assertThat(sale.getAppliedPromotionName()).isEqualTo("Bulk 10%");
    }

    @Test
    @DisplayName("Scenario 3: opting into an extended warranty adds both warranties and the 10 percent surcharge")
    void scenario3_extendedWarrantyOptInAddsSurcharge() {
        productService.registerConsole("CN001", "PS5", 2000000, 5, "Sony", "PS5", "9th");

        Sale sale = saleService.registerSale(customer.getId(), seller.getId(), List.of("CN001"), List.of("CN001"));

        assertThat(sale.getTotalAmount()).isEqualTo(2000000 + 200000);
        List<Warranty> warranties = warrantyService.listAllWarranties();
        assertThat(warranties).hasSize(2);
        assertThat(warranties).anyMatch(warranty -> warranty.getWarrantyType().equals("Garantia Basica"));
        assertThat(warranties).anyMatch(warranty -> warranty.getWarrantyType().equals("Garantia Extendida"));
    }

    @Test
    @DisplayName("Scenario 4: registering a return the same day restores stock")
    void scenario4_sameDayReturnRestoresStock() {
        productService.registerVideoGame("VG001", "Game A", 50000, 10, "PS5", "Action", "T");
        Sale sale = saleService.registerSale(customer.getId(), seller.getId(), List.of("VG001"), List.of());

        Return returnItem = returnService.registerReturn(sale.getId(), List.of("VG001"), "Defective");

        assertThat(returnItem).isNotNull();
        assertThat(productService.findById("VG001").getStock()).isEqualTo(10);
    }

    @Test
    @DisplayName("Scenario 5: monthly balance equals sale total minus refund for the same month")
    void scenario5_monthlyBalanceReflectsSaleAndReturn() {
        productService.registerVideoGame("VG001", "Game A", 50000, 10, "PS5", "Action", "T");
        Sale sale = saleService.registerSale(customer.getId(), seller.getId(), List.of("VG001"), List.of());
        Return returnItem = returnService.registerReturn(sale.getId(), List.of("VG001"), "Defective");

        LocalDate today = LocalDate.now();
        double balance = returnService.generateMonthlyBalance(today.getMonthValue(), today.getYear());

        assertThat(balance).isEqualTo(sale.getTotalAmount() - returnItem.getRefundAmount());
    }

    @Test
    @DisplayName("Scenario 6: a category discount applies only to the matching product's price")
    void scenario6_categoryDiscountAppliesOnlyToMatchingCategory() {
        productService.registerVideoGame("VG001", "Game A", 50000, 10, "PS5", "Action", "T");
        productService.registerConsole("CN001", "PS5", 200000, 5, "Sony", "PS5", "9th");
        promotionService.registerCategoryDiscount("PR002", "20% Videogames", LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1), 20, "VIDEOGAME");

        Sale sale = saleService.registerSale(customer.getId(), seller.getId(), List.of("VG001", "CN001"), List.of());

        assertThat(sale.getDiscountAmount()).isEqualTo(50000 * 0.20);
    }
}
