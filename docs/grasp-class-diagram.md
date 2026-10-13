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
