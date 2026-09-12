package com.gamezone.testutil;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.Cable;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.Console;
import com.gamezone.model.Controller;
import com.gamezone.model.Customer;
import com.gamezone.model.Memory;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.VideoGame;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Provides factory methods that build fully-populated domain objects with
 * reasonable defaults, for use across the automated test suite.
 */
public final class TestDataFactory {

    private TestDataFactory() {
    }

    /**
     * Creates a sample video game with the given id and reasonable defaults.
     *
     * @param id the product id
     * @return a new video game
     */
    public static VideoGame sampleVideoGame(String id) {
        return new VideoGame(id, "Sample Video Game " + id, 100000, 10, "PS5", "Action", "T");
    }

    /**
     * Creates a sample console with the given id and reasonable defaults.
     *
     * @param id the product id
     * @return a new console
     */
    public static Console sampleConsole(String id) {
        return new Console(id, "Sample Console " + id, 2000000, 5, "Sony", "PS5", "9th");
    }

    /**
     * Creates a sample customer with the given id and reasonable defaults.
     *
     * @param id the person id
     * @return a new customer
     */
    public static Customer sampleCustomer(String id) {
        return new Customer(id, "Sample", "Customer-" + id, "3000000000", "customer" + id + "@example.com");
    }

    /**
     * Creates a sample seller with the given id and reasonable defaults.
     *
     * @param id the person id
     * @return a new seller
     */
    public static Seller sampleSeller(String id) {
        return new Seller(id, "Sample", "Seller-" + id, "3000000001", "EMP" + id, "Manana");
    }

    /**
     * Creates a sample controller with the given id and compatible consoles.
     *
     * @param id                  the product id
     * @param compatibleConsoles the ids of consoles this controller is compatible with
     * @return a new controller
     */
    public static Controller sampleController(String id, String... compatibleConsoles) {
        return new Controller(id, "Sample Controller " + id, 200000, 10,
            Arrays.asList(compatibleConsoles), "WIRELESS");
    }

    /**
     * Creates a sample cable with the given id and compatible consoles.
     *
     * @param id                  the product id
     * @param compatibleConsoles the ids of consoles this cable is compatible with
     * @return a new cable
     */
    public static Cable sampleCable(String id, String... compatibleConsoles) {
        return new Cable(id, "Sample Cable " + id, 30000, 20,
            Arrays.asList(compatibleConsoles), 2.0, "HDMI");
    }

    /**
     * Creates a sample memory accessory with the given id and compatible consoles.
     *
     * @param id                  the product id
     * @param compatibleConsoles the ids of consoles this memory is compatible with
     * @return a new memory accessory
     */
    public static Memory sampleMemory(String id, String... compatibleConsoles) {
        return new Memory(id, "Sample Memory " + id, 150000, 15,
            Arrays.asList(compatibleConsoles), 256, "MICROSD");
    }

    /**
     * Creates a sample percentage discount active from 30 days ago to 30 days
     * from now, so it always covers today.
     *
     * @param id         the promotion id
     * @param percentage the discount percentage
     * @return a new percentage discount
     */
    public static PercentageDiscount samplePercentageDiscount(String id, double percentage) {
        LocalDate today = LocalDate.now();
        return new PercentageDiscount(id, "Sample Percentage Discount " + id,
            today.minusDays(30), today.plusDays(30), percentage);
    }

    /**
     * Creates a sample category discount active from 30 days ago to 30 days
     * from now, so it always covers today.
     *
     * @param id             the promotion id
     * @param percentage     the discount percentage
     * @param targetCategory the category this discount targets ("VIDEOGAME" or "CONSOLE")
     * @return a new category discount
     */
    public static CategoryDiscount sampleCategoryDiscount(String id, double percentage, String targetCategory) {
        LocalDate today = LocalDate.now();
        return new CategoryDiscount(id, "Sample Category Discount " + id,
            today.minusDays(30), today.plusDays(30), percentage, targetCategory);
    }

    /**
     * Creates a sample bulk purchase discount active from 30 days ago to 30
     * days from now, so it always covers today.
     *
     * @param id         the promotion id
     * @param minQty     the minimum quantity of products required
     * @param percentage the discount percentage
     * @return a new bulk purchase discount
     */
    public static BulkPurchaseDiscount sampleBulkDiscount(String id, int minQty, double percentage) {
        LocalDate today = LocalDate.now();
        return new BulkPurchaseDiscount(id, "Sample Bulk Discount " + id,
            today.minusDays(30), today.plusDays(30), minQty, percentage);
    }

    /**
     * Creates a sample sale with the given participants, products, and date,
     * with its total already calculated.
     *
     * @param id       the sale id
     * @param customer the customer making the purchase
     * @param seller   the seller handling the sale
     * @param products the products included in the sale
     * @param date     the date the sale was made
     * @return a new sale with {@code calculateTotal()} already invoked
     */
    public static Sale sampleSale(String id, Customer customer, Seller seller, List<Product> products, LocalDate date) {
        Sale sale = new Sale(id, date, customer, seller, products);
        sale.calculateTotal();
        return sale;
    }
}
