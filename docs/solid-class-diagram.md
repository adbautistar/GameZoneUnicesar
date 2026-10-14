# SOLID Correction — Class Diagrams (Before and After)

Classes involved in the violations of `docs/solid-audit/consolidated-audit.md`, with the members and dependencies relevant to them. Notes reference the violation ids (`V-XX`).

## Before (develop `123ba66`, start of Phase 2)

At this point the system declares **no interfaces**: every arrow below points at a concrete class.

```mermaid
classDiagram
    direction LR

    class Sale {
        +generateReceipt() String
    }
    class Return {
        +generateReturnReceipt() String
    }
    class Warranty {
        <<abstract>>
        +generateWarrantyCertificate() String
    }

    class ProductRepository
    class PersonRepository
    class AccessoryRepository
    class PromotionRepository
    class SaleRepository
    class ReturnRepository
    class WarrantyRepository {
        -toCsvLine(Warranty) String
    }

    class ProductService
    class PersonService {
        +registerCustomer(...) void
        +findCustomerById(String) Customer
        +findSellerById(String) Seller
        +listAllCustomers() List~Customer~
        +listAllSellers() List~Seller~
    }
    class AccessoryService
    class PromotionService {
        +findBestPromotionFor(Sale) Promotion
    }
    class ItemCatalog {
        -ProductService productService
        -AccessoryService accessoryService
        +findItem(String) Product
        +decreaseStock(Product) void
        +restoreStock(Product) void
    }
    class SaleService {
        +recordSale(Sale) void
        +viewAllSales() List~Sale~
        +viewSalesByCustomer(String) List~Sale~
        +viewSalesBySeller(String) List~Sale~
        +findById(String) Sale
    }
    class SaleRegistrationService
    class ReturnService {
        +generateMonthlyBalance(int, int) double
    }
    class WarrantyService {
        -resolve(WarrantyRecord, SaleService, ItemCatalog)$ Warranty
    }
    class ConsoleMenu {
        +start() void
    }

    ProductService --> ProductRepository
    PersonService --> PersonRepository
    AccessoryService --> AccessoryRepository
    PromotionService --> PromotionRepository
    SaleService --> SaleRepository
    ReturnService --> ReturnRepository
    WarrantyService --> WarrantyRepository
    ItemCatalog --> ProductService
    ItemCatalog --> AccessoryService
    SaleService --> PersonService
    SaleService --> ItemCatalog
    SaleRegistrationService --> PersonService
    SaleRegistrationService --> PromotionService
    SaleRegistrationService --> WarrantyService
    ReturnService --> SaleService
    WarrantyService ..> SaleService
    WarrantyService ..> ItemCatalog
    ConsoleMenu --> ProductMenu
    ConsoleMenu --> SaleMenu
    ConsoleMenu --> ReturnMenu
    ConsoleMenu --> WarrantyMenu

    note for Return "V-01: receipt layout inside the domain class"
    note for Warranty "V-01: certificate layout inside the domain class"
    note for Sale "V-02: receipt layout inside the domain class"
    note for ReturnService "V-03: monthly balance (reporting); V-07: whole SaleService; V-11: concrete repository"
    note for ItemCatalog "V-04: instanceof Accessory chooses the catalog"
    note for WarrantyRepository "V-05: BASIC/EXTENDED tags here and in WarrantyService"
    note for ConsoleMenu "V-06: fixed labels and switch over concrete submenus"
    note for WarrantyService "V-07: whole SaleService; V-09: whole ItemCatalog; V-11: concrete repository"
    note for SaleService "V-08: whole PersonService; V-09: whole ItemCatalog; V-12: concrete repository"
    note for ProductService "V-10: concrete repository"
    note for PersonService "V-11: concrete repository"
    note for SaleRegistrationService "V-08: whole PersonService; V-13: concrete promotion and warranty services"
```
