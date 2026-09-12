package com.gamezone.service;

import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.persistence.PromotionRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PromotionService tests")
class PromotionServiceTest {

    private static final LocalDate START = LocalDate.now().minusDays(10);
    private static final LocalDate END = LocalDate.now().plusDays(10);

    @Mock
    private PromotionRepository repository;

    private PromotionService service;

    @BeforeEach
    void setUp() {
        when(repository.loadAll()).thenReturn(new ArrayList<>());
        service = new PromotionService(repository);
    }

    @Test
    @DisplayName("registerPercentageDiscount with valid inputs persists")
    void registerPercentageDiscountPersists() {
        service.registerPercentageDiscount("PR001", "Test", START, END, 15);

        assertThat(service.listAllPromotions()).hasSize(1);
    }

    @Test
    @DisplayName("registerPercentageDiscount with percentage above 100 throws")
    void registerPercentageDiscountAbove100Throws() {
        assertThatThrownBy(() -> service.registerPercentageDiscount("PR001", "Test", START, END, 150))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("registerPercentageDiscount with endDate before startDate throws")
    void registerPercentageDiscountEndBeforeStartThrows() {
        assertThatThrownBy(() -> service.registerPercentageDiscount("PR001", "Test", END, START, 10))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("registerBulkPurchaseDiscount with minimumQuantity below 1 throws")
    void registerBulkDiscountMinQuantityBelowOneThrows() {
        assertThatThrownBy(() -> service.registerBulkPurchaseDiscount("PR003", "Test", START, END, 0, 10))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("registerCategoryDiscount with valid inputs persists")
    void registerCategoryDiscountPersists() {
        service.registerCategoryDiscount("PR002", "Test", START, END, 20, "VIDEOGAME");

        assertThat(service.listAllPromotions()).hasSize(1);
    }

    @Test
    @DisplayName("listActivePromotions filters by today's date")
    void listActivePromotionsFiltersByToday() {
        service.registerPercentageDiscount("PR001", "Active", START, END, 10);
        service.registerPercentageDiscount("PR002", "Expired", START.minusDays(20), START.minusDays(11), 10);

        List<Promotion> active = service.listActivePromotions();

        assertThat(active).hasSize(1);
        assertThat(active.get(0).getId()).isEqualTo("PR001");
    }

    @Test
    @DisplayName("findBestPromotionFor returns null when there are no promotions")
    void findBestPromotionForEmptyListReturnsNull() {
        Sale sale = sampleSale();

        assertThat(service.findBestPromotionFor(sale)).isNull();
    }

    @Test
    @DisplayName("findBestPromotionFor returns null when all promotions yield 0 discount")
    void findBestPromotionForAllZeroReturnsNull() {
        service.registerCategoryDiscount("PR002", "Test", START, END, 20, "CONSOLE");
        Sale sale = sampleSale();

        assertThat(service.findBestPromotionFor(sale)).isNull();
    }

    @Test
    @DisplayName("findBestPromotionFor returns the promotion with the maximum discount")
    void findBestPromotionForReturnsMaxDiscount() {
        service.registerPercentageDiscount("PR001", "Fifteen Percent", START, END, 15);
        service.registerBulkPurchaseDiscount("PR003", "Ten Percent Bulk", START, END, 1, 10);
        Sale sale = sampleSale();

        Promotion best = service.findBestPromotionFor(sale);

        assertThat(best).isNotNull();
        assertThat(best.getId()).isEqualTo("PR001");
    }

    private Sale sampleSale() {
        return TestDataFactory.sampleSale("SALE001", TestDataFactory.sampleCustomer("C001"),
            TestDataFactory.sampleSeller("S001"), List.of(TestDataFactory.sampleVideoGame("VG001")), LocalDate.now());
    }
}
