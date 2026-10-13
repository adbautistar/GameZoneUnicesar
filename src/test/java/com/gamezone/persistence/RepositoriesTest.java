package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.BasicWarranty;
import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.Cable;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.Console;
import com.gamezone.model.Controller;
import com.gamezone.model.Customer;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Memory;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.VideoGame;
import com.gamezone.testutil.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Repository round-trip tests")
class RepositoriesTest {

    @Nested
    @DisplayName("ProductRepository")
    class ProductRepositoryTests {

        @Test
        @DisplayName("loadAll on non-existent file returns empty list")
        void loadAllNonExistentFile(@TempDir Path tempDir) {
            ProductRepository repository = new ProductRepository(tempDir.resolve("missing.csv").toString());

            assertThat(repository.loadAll()).isEmpty();
        }

        @Test
        @DisplayName("saveAll then loadAll round-trips products with discriminator preserved")
        void saveAndLoadRoundTrip(@TempDir Path tempDir) {
            ProductRepository repository = new ProductRepository(tempDir.resolve("products.csv").toString());
            VideoGame videoGame = TestDataFactory.sampleVideoGame("VG001");
            Console console = TestDataFactory.sampleConsole("CN001");

            repository.saveAll(List.of(videoGame, console));
            List<Product> loaded = repository.loadAll();

            assertThat(loaded).hasSize(2);
            assertThat(loaded.get(0)).isInstanceOf(VideoGame.class);
            assertThat(loaded.get(1)).isInstanceOf(Console.class);
            assertThat(loaded.get(0).getId()).isEqualTo("VG001");
            assertThat(loaded.get(1).getId()).isEqualTo("CN001");
        }
    }

    @Nested
    @DisplayName("AccessoryRepository")
    class AccessoryRepositoryTests {

        @Test
        @DisplayName("loadAll on non-existent file returns empty list")
        void loadAllNonExistentFile(@TempDir Path tempDir) {
            AccessoryRepository repository = new AccessoryRepository(tempDir.resolve("missing.csv").toString());

            assertThat(repository.loadAll()).isEmpty();
        }

        @Test
        @DisplayName("saveAll then loadAll round-trips accessories with discriminator preserved (CONTROLLER/CABLE/MEMORY)")
        void saveAndLoadRoundTrip(@TempDir Path tempDir) {
            AccessoryRepository repository = new AccessoryRepository(tempDir.resolve("accessories.csv").toString());
            Controller controller = TestDataFactory.sampleController("ACC001", "CN001");
            Cable cable = TestDataFactory.sampleCable("ACC002", "CN001");
            Memory memory = TestDataFactory.sampleMemory("ACC003", "CN001");

            repository.saveAll(List.of(controller, cable, memory));
            List<Accessory> loaded = repository.loadAll();

            assertThat(loaded).hasSize(3);
            assertThat(loaded.get(0)).isInstanceOf(Controller.class);
            assertThat(loaded.get(1)).isInstanceOf(Cable.class);
            assertThat(loaded.get(2)).isInstanceOf(Memory.class);
        }
    }

    @Nested
    @DisplayName("PromotionRepository")
    class PromotionRepositoryTests {

        @Test
        @DisplayName("loadAll on non-existent file returns empty list")
        void loadAllNonExistentFile(@TempDir Path tempDir) {
            PromotionRepository repository = new PromotionRepository(tempDir.resolve("missing.csv").toString());

            assertThat(repository.loadAll()).isEmpty();
        }

        @Test
        @DisplayName("saveAll then loadAll round-trips promotions with discriminator preserved (PERCENTAGE/CATEGORY/BULK)")
        void saveAndLoadRoundTrip(@TempDir Path tempDir) {
            PromotionRepository repository = new PromotionRepository(tempDir.resolve("promotions.csv").toString());
            PercentageDiscount percentage = TestDataFactory.samplePercentageDiscount("PR001", 15);
            CategoryDiscount category = TestDataFactory.sampleCategoryDiscount("PR002", 20, "VIDEOGAME");
            BulkPurchaseDiscount bulk = TestDataFactory.sampleBulkDiscount("PR003", 3, 10);

            repository.saveAll(List.of(percentage, category, bulk));
            List<Promotion> loaded = repository.loadAll();

            assertThat(loaded).hasSize(3);
            assertThat(loaded.get(0)).isInstanceOf(PercentageDiscount.class);
            assertThat(loaded.get(1)).isInstanceOf(CategoryDiscount.class);
            assertThat(loaded.get(2)).isInstanceOf(BulkPurchaseDiscount.class);
        }
    }

    @Nested
    @DisplayName("ReturnRepository")
    class ReturnRepositoryTests {

        @Test
        @DisplayName("loadAll on non-existent file returns empty list")
        void loadAllNonExistentFile(@TempDir Path tempDir) {
            ReturnRepository repository = new ReturnRepository(tempDir.resolve("missing.csv").toString());

            assertThat(repository.loadAll()).isEmpty();
        }

        @Test
        @DisplayName("saveAll then loadAll round-trips a return, keeping sale and products as ids")
        void saveAndLoadRoundTrip(@TempDir Path tempDir) {
            Customer customer = TestDataFactory.sampleCustomer("C001");
            Seller seller = TestDataFactory.sampleSeller("S001");
            VideoGame product = TestDataFactory.sampleVideoGame("VG001");
            Sale sale = TestDataFactory.sampleSale("SALE001", customer, seller, List.of(product), LocalDate.now());

            ReturnRepository repository = new ReturnRepository(tempDir.resolve("returns.csv").toString());
            Return returnItem = new Return("RET001", LocalDate.now(), sale, List.of(product), "Defective");

            repository.saveAll(List.of(returnItem));
            List<ReturnRecord> loaded = repository.loadAll();

            assertThat(loaded).hasSize(1);
            assertThat(loaded.get(0).getId()).isEqualTo("RET001");
            assertThat(loaded.get(0).getSaleId()).isEqualTo("SALE001");
            assertThat(loaded.get(0).getProductIds()).containsExactly("VG001");
            assertThat(loaded.get(0).getReason()).isEqualTo("Defective");
        }
    }

    @Nested
    @DisplayName("WarrantyRepository")
    class WarrantyRepositoryTests {

        @Test
        @DisplayName("loadAll on non-existent file returns empty list")
        void loadAllNonExistentFile(@TempDir Path tempDir) {
            WarrantyRepository repository = new WarrantyRepository(tempDir.resolve("missing.csv").toString());

            assertThat(repository.loadAll()).isEmpty();
        }

        @Test
        @DisplayName("saveAll then loadAll round-trips warranties with discriminator preserved (BASIC/EXTENDED)")
        void saveAndLoadRoundTrip(@TempDir Path tempDir) {
            Customer customer = TestDataFactory.sampleCustomer("C001");
            Seller seller = TestDataFactory.sampleSeller("S001");
            Console console = TestDataFactory.sampleConsole("CN001");
            Sale sale = TestDataFactory.sampleSale("SALE001", customer, seller, List.of(console), LocalDate.now());

            WarrantyRepository repository = new WarrantyRepository(tempDir.resolve("warranties.csv").toString());
            BasicWarranty basic = new BasicWarranty("WAR001", console, sale, LocalDate.now());
            ExtendedWarranty extended = new ExtendedWarranty("WAR002", console, sale, LocalDate.now());

            repository.saveAll(List.of(basic, extended));
            List<WarrantyRecord> loaded = repository.loadAll();

            assertThat(loaded).hasSize(2);
            assertThat(loaded.get(0).getType()).isEqualTo("BASIC");
            assertThat(loaded.get(1).getType()).isEqualTo("EXTENDED");
            assertThat(loaded).allSatisfy(record -> {
                assertThat(record.getProductId()).isEqualTo("CN001");
                assertThat(record.getSaleId()).isEqualTo("SALE001");
                assertThat(record.getStartDate()).isEqualTo(LocalDate.now());
            });
        }
    }
}
