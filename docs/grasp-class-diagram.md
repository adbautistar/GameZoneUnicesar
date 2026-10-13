# GRASP Correction — Class Diagrams (Before and After)

Classes involved in the violations of `docs/grasp-audit/consolidated-audit.md`, with the attributes, methods and relationships relevant to them. Notes reference the violation ids (`V-XX`).

## Before (develop `7bf4931`, start of Phase 2)

```mermaid
classDiagram
    direction LR

    class Product {
        <<abstract>>
        -String id
        -double price
        -int stock
        +updateStock(int quantity) void
        +getDescription()* String
    }
    class VideoGame
    class Console
    class Accessory {
        <<abstract>>
        -List~String~ compatibleConsoleIds
        +isCompatibleWith(String consoleId) boolean
    }
    class Controller
    class Cable
    class Memory
    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory

    class Sale {
        -double totalAmount
        -String appliedPromotionName
        -double discountAmount
        +calculateTotal() double
        +setTotalAmount(double) void
        +setDiscountAmount(double) void
        +setAppliedPromotionName(String) void
        +canBeReturned() boolean
        +generateReceipt() String
    }
    class CategoryDiscount {
        -String targetCategory
        +calculateDiscount(Sale sale) double
    }
    class Return {
        -Sale originalSale
        -List~Product~ returnedProducts
        +calculateRefundAmount() double
    }
    class Warranty {
        <<abstract>>
        -Product product
        -Sale sale
        +isActive(LocalDate) boolean
    }
    CategoryDiscount ..> VideoGame : instanceof
    CategoryDiscount ..> Console : instanceof

    class SaleRepository {
        -ProductService productService
        -PersonService personService
        -AccessoryService accessoryService
        +saveAll(List~Sale~) void
        +loadAll() List~Sale~
    }
    class ReturnRepository {
        -SaleService saleService
        -ProductService productService
        -AccessoryService accessoryService
        +loadAll() List~Return~
    }
    class WarrantyRepository {
        -SaleService saleService
        -ProductService productService
        -AccessoryService accessoryService
        +loadAll() List~Warranty~
    }

    class ProductService
    class AccessoryService {
        +listAccessoriesByType(String type) List~Accessory~
        +updateStock(String id, int quantity) void
    }
    class SaleService {
        -WarrantyService warrantyService
        +setWarrantyService(WarrantyService) void
        +registerSale(customerId, sellerId, productIds, extendedWarrantyIds) Sale
        +viewSalesByCustomer(String) List~Sale~
        +viewSalesBySeller(String) List~Sale~
    }
    class ReturnService {
        +registerReturn(saleId, productIds, reason) Return
        +viewReturnsByCustomer(String) List~Return~
        +generateMonthlyBalance(int month, int year) double
    }
    class WarrantyService {
        +assignBasicWarranty(Product, Sale, LocalDate) BasicWarranty
        +assignExtendedWarranty(Product, Sale, LocalDate) ExtendedWarranty
        +findWarrantyByProduct(productId, saleId) Warranty
    }
    class ConsoleMenu {
        +start() void
        -registerSale() void
        -showProductMenu() void
        -showWarrantyMenu() void
    }
    class Main {
        +main(String[] args)$ void
    }

    SaleRepository ..> ProductService
    SaleRepository ..> PersonService
    SaleRepository ..> AccessoryService
    ReturnRepository ..> SaleService
    ReturnRepository ..> ProductService
    ReturnRepository ..> AccessoryService
    WarrantyRepository ..> SaleService
    WarrantyRepository ..> ProductService
    WarrantyRepository ..> AccessoryService
    SaleService --> SaleRepository
    SaleService --> ProductService
    SaleService --> AccessoryService
    SaleService --> WarrantyService : setter
    WarrantyService --> WarrantyRepository
    ReturnService --> ReturnRepository
    ReturnService --> SaleService
    ReturnService --> ProductService
    ReturnService --> AccessoryService
    ConsoleMenu --> SaleService
    ConsoleMenu --> ProductService
    Main ..> SaleService : setWarrantyService

    note for CategoryDiscount "V-01: category strings + instanceof"
    note for AccessoryService "V-02: type strings + instanceof"
    note for ReturnRepository "V-03: persistence depends on services; V-05: duplicated lookup"
    note for WarrantyRepository "V-04: persistence depends on services; V-05: duplicated lookup"
    note for ReturnService "V-05: instanceof Accessory; V-06/V-07: searches and getter chains; V-09: inline RET- ids"
    note for WarrantyService "V-08: getter chains; V-09: inline WAR- ids"
    note for Sale "V-10: total adjusted from outside via setters"
    note for SaleRepository "V-11: depends on services, rebuilds purchase history"
    note for SaleService "V-12: whole use case + cycle with WarrantyService; V-13: instanceof Console; V-16: getter chains"
    note for ConsoleMenu "V-14: decides warranty eligibility; V-15: 863 lines, every module"
```

## After (end of Phase 2)

New classes: `ItemCatalog`, `IdGenerator`, `SaleRegistrationService` (service); `SaleRecord`, `ReturnRecord`, `WarrantyRecord` (persistence); `ProductMenu`, `PersonMenu`, `SaleMenu`, `AccessoryMenu`, `PromotionMenu`, `ReturnMenu`, `WarrantyMenu` (ui). No persistence class depends on a service any more, and the service graph has no cycle.

```mermaid
classDiagram
    direction LR

    class Product {
        <<abstract>>
        +updateStock(int quantity) void
        +getDescription()* String
        +belongsToCategory(String category)* boolean
        +isWarrantyEligible() boolean
    }
    class VideoGame {
        +belongsToCategory(String) boolean
    }
    class Console {
        +belongsToCategory(String) boolean
        +isWarrantyEligible() boolean
    }
    class Accessory {
        <<abstract>>
        +isCompatibleWith(String consoleId) boolean
        +belongsToCategory(String) boolean
        +getAccessoryType()* String
    }
    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory

    class Sale {
        +calculateTotal() double
        +applyPromotion(String name, double discount) void
        +addCharge(double amount) void
        +findProduct(String productId) Product
        +wasMadeIn(int month, int year) boolean
        +wasMadeBy(String customerId) boolean
        +wasHandledBy(String sellerId) boolean
        +canBeReturned() boolean
    }
    class CategoryDiscount {
        +calculateDiscount(Sale sale) double
    }
    class Return {
        +belongsToCustomer(String) boolean
        +belongsToSale(String) boolean
        +wasRegisteredIn(int month, int year) boolean
    }
    class Warranty {
        <<abstract>>
        +covers(String productId, String saleId) boolean
    }
    CategoryDiscount ..> Product : belongsToCategory

    class SaleRecord
    class ReturnRecord
    class WarrantyRecord
    class SaleRepository {
        +saveAll(List~Sale~) void
        +loadAll() List~SaleRecord~
    }
    class ReturnRepository {
        +saveAll(List~Return~) void
        +loadAll() List~ReturnRecord~
    }
    class WarrantyRepository {
        +saveAll(List~Warranty~) void
        +loadAll() List~WarrantyRecord~
    }
    SaleRepository ..> SaleRecord
    ReturnRepository ..> ReturnRecord
    WarrantyRepository ..> WarrantyRecord

    class IdGenerator {
        +next(String prefix)$ String
    }
    class ItemCatalog {
        +findItem(String id) Product
        +decreaseStock(Product item) void
        +restoreStock(Product item) void
    }
    class SaleService {
        +recordSale(Sale sale) void
        +viewAllSales() List~Sale~
        +viewSalesByCustomer(String) List~Sale~
        +viewSalesBySeller(String) List~Sale~
        +findById(String) Sale
    }
    class SaleRegistrationService {
        +registerSale(customerId, sellerId, productIds, extendedWarrantyIds) Sale
        +findWarrantyEligibleItems(List~String~ productIds) List~Product~
    }
    class AccessoryService {
        +listAccessoriesByType(String type) List~Accessory~
    }
    class ReturnService
    class WarrantyService

    ItemCatalog --> ProductService
    ItemCatalog --> AccessoryService
    SaleService --> SaleRepository
    SaleService --> PersonService
    SaleService --> ItemCatalog
    SaleRegistrationService --> SaleService
    SaleRegistrationService --> PersonService
    SaleRegistrationService --> ItemCatalog
    SaleRegistrationService --> PromotionService
    SaleRegistrationService --> WarrantyService
    SaleRegistrationService ..> IdGenerator
    ReturnService --> ReturnRepository
    ReturnService --> SaleService
    ReturnService --> ItemCatalog
    ReturnService ..> IdGenerator
    WarrantyService --> WarrantyRepository
    WarrantyService ..> SaleService : resolves on load
    WarrantyService ..> ItemCatalog : resolves on load
    WarrantyService ..> IdGenerator

    class ConsoleMenu {
        +start() void
    }
    ConsoleMenu --> ProductMenu
    ConsoleMenu --> PersonMenu
    ConsoleMenu --> SaleMenu
    ConsoleMenu --> AccessoryMenu
    ConsoleMenu --> PromotionMenu
    ConsoleMenu --> ReturnMenu
    ConsoleMenu --> WarrantyMenu
    SaleMenu --> SaleRegistrationService
    SaleMenu --> SaleService

    note for CategoryDiscount "V-01 fixed: products answer belongsToCategory"
    note for AccessoryService "V-02 fixed: filters with getAccessoryType"
    note for ReturnRepository "V-03 fixed: ids only, no service dependency"
    note for WarrantyRepository "V-04 fixed: ids only, no service dependency"
    note for ItemCatalog "V-05: single intermediary for sellable items (keeps the only type check)"
    note for Return "V-06/V-07: experts answer queries (Sale.findProduct, Return queries)"
    note for Warranty "V-08: covers(productId, saleId)"
    note for IdGenerator "V-09: one fabrication for identifiers"
    note for Sale "V-10 / V-16: total and participants handled by the expert"
    note for SaleRepository "V-11 fixed: ids only; SaleService resolves and records history"
    note for SaleRegistrationService "V-12 / V-13 / V-14: use-case controller, polymorphic eligibility"
    note for ConsoleMenu "V-15: main loop only (109 lines); one submenu per module"
```
