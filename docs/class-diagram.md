# Full Class Diagram — GameZone Unicesar

```mermaid
classDiagram

    %% ===== MODEL LAYER =====
    class Product {
        <<abstract>>
        -id: String
        -title: String
        -price: double
        -stock: int
        +getDescription() String*
        +updateStock(quantity int) void
    }
    class VideoGame {
        -platform: String
        -genre: String
        -ageRating: String
        +getDescription() String
    }
    class Console {
        -brand: String
        -model: String
        -generation: String
        +getDescription() String
    }
    class Person {
        <<abstract>>
        -id: String
        -firstName: String
        -lastName: String
        -phone: String
        +getFullName() String
    }
    class Customer {
        -email: String
        -purchaseHistory: List~Sale~
    }
    class Seller {
        -employeeCode: String
        -shift: String
    }
    class Sale {
        -id: String
        -date: LocalDate
        -customer: Customer
        -seller: Seller
        -products: List~Product~
        -totalAmount: double
        +calculateTotal() double
        +generateReceipt() String
    }

    Product <|-- VideoGame
    Product <|-- Console
    Person <|-- Customer
    Person <|-- Seller
    Sale "1" --> "1" Customer
    Sale "1" --> "1" Seller
    Sale "1" o-- "1..*" Product
    Customer "1" o-- "0..*" Sale : purchaseHistory

    %% ===== PERSISTENCE LAYER =====
    class ProductStore {
        <<interface>>
        +saveAll(products List~Product~) void
        +loadAll() List~Product~
    }
    class PersonStore {
        <<interface>>
        +saveAllCustomers(customers List~Customer~) void
        +loadAllCustomers() List~Customer~
        +saveAllSellers(sellers List~Seller~) void
        +loadAllSellers() List~Seller~
    }
    class SaleStore {
        <<interface>>
        +saveAll(sales List~Sale~) void
        +loadAll() List~SaleRecord~
    }
    class ProductRepository
    class PersonRepository
    class SaleRepository
    class SaleRecord {
        -id: String
        -customerId: String
        -sellerId: String
        -productIds: List~String~
        -appliedPromotionName: String
        -discountAmount: double
    }

    ProductStore <|.. ProductRepository
    PersonStore <|.. PersonRepository
    SaleStore <|.. SaleRepository
    ProductRepository ..> Product
    PersonRepository ..> Customer
    PersonRepository ..> Seller
    SaleRepository ..> Sale
    SaleRepository ..> SaleRecord

    %% ===== SERVICE LAYER =====
    class ItemSource {
        <<interface>>
        +findItem(id String) Product
        +decreaseStock(id String) void
        +restoreStock(id String) void
    }
    class ItemLookup {
        <<interface>>
        +findItem(id String) Product
    }
    class PersonLookup {
        <<interface>>
        +findCustomerById(id String) Customer
        +findSellerById(id String) Seller
    }
    class SaleLookup {
        <<interface>>
        +findById(id String) Sale
        +viewAllSales() List~Sale~
    }
    class ProductService {
        -repository: ProductStore
        +registerVideoGame(...) void
        +registerConsole(...) void
        +listAllProducts() List~Product~
        +updateStock(productId String, quantity int) void
        +findById(id String) Product
    }
    class PersonService {
        -repository: PersonStore
        +registerCustomer(...) void
        +findCustomerById(id String) Customer
        +findSellerById(id String) Seller
        +listAllCustomers() List~Customer~
        +listAllSellers() List~Seller~
    }
    class ItemCatalog {
        -sources: List~ItemSource~
        +findItem(id String) Product
        +decreaseStock(item Product) void
        +restoreStock(item Product) void
    }
    class SaleService {
        -repository: SaleStore
        -personLookup: PersonLookup
        -itemLookup: ItemLookup
        +recordSale(sale Sale) void
        +viewAllSales() List~Sale~
        +viewSalesByCustomer(customerId String) List~Sale~
        +viewSalesBySeller(sellerId String) List~Sale~
        +findById(id String) Sale
    }
    class SaleRegistrationService {
        -saleService: SaleService
        -personLookup: PersonLookup
        -itemCatalog: ItemCatalog
        -promotionSelector: PromotionSelector
        -warrantyAssigner: WarrantyAssigner
        +registerSale(customerId, sellerId, productIds, extendedWarrantyIds) Sale
        +findWarrantyEligibleItems(productIds) List~Product~
    }

    ItemSource <|.. ProductService
    ItemLookup <|.. ItemCatalog
    PersonLookup <|.. PersonService
    SaleLookup <|.. SaleService
    ProductService "1" --> "1" ProductStore
    ProductService ..> Product
    PersonService "1" --> "1" PersonStore
    ItemCatalog "1" --> "1..*" ItemSource
    SaleService "1" --> "1" SaleStore
    SaleService "1" --> "1" PersonLookup
    SaleService "1" --> "1" ItemLookup
    SaleService ..> Sale
    SaleRegistrationService "1" --> "1" SaleService
    SaleRegistrationService "1" --> "1" PersonLookup
    SaleRegistrationService "1" --> "1" ItemCatalog

    %% ===== UI LAYER =====
    class MenuOption {
        <<interface>>
        +getLabel() String
        +run() void
    }
    class ConsoleMenu {
        -options: List~MenuOption~
        +start() void
    }
    class ProductMenu {
        +show() void
    }
    class PersonMenu {
        +show() void
    }
    class SaleMenu {
        +show() void
    }

    ConsoleMenu "1" --> "1..*" MenuOption
    MenuOption <|.. ProductMenu
    MenuOption <|.. PersonMenu
    MenuOption <|.. SaleMenu
    ProductMenu "1" --> "1" ProductService
    PersonMenu "1" --> "1" PersonService
    SaleMenu "1" --> "1" SaleService
    SaleMenu "1" --> "1" SaleRegistrationService

    %% ===== ENTRY POINT =====
    class Main {
        +main(args String[]) void$
    }

    Main ..> ConsoleMenu
    Main ..> ProductMenu
    Main ..> PersonMenu
    Main ..> SaleMenu
    Main ..> ProductRepository
    Main ..> PersonRepository
    Main ..> SaleRepository
    Main ..> ProductService
    Main ..> PersonService
    Main ..> SaleService
    Main ..> ItemCatalog
    Main ..> SaleRegistrationService
```

**Scope of this diagram:** the classes of Taller 1 (products, people, sales, UI and entry point) as they stand after the GRASP correction of Requerimiento 7 (v1.5.0) and the SOLID correction of Requerimiento 8 (v1.6.0), including the classes and interfaces that replaced parts of them. `ItemCatalog` also receives `AccessoryService` as an `ItemSource`; `SaleRegistrationService` also depends on `PromotionSelector` and `WarrantyAssigner`; `Sale.generateReceipt()` delegates to `SaleReceipt`; the other modules (Requerimientos 1-4) and the other five menu options are shown in [grasp-class-diagram.md](grasp-class-diagram.md) and [solid-class-diagram.md](solid-class-diagram.md).

**No layering exceptions remain.** Until v1.4.1, `SaleRepository` depended on `ProductService` and `PersonService` to resolve references while loading `data/sales.csv`, a reverse dependency that the GRASP audit classified as a Low Coupling violation (V-11). Since v1.5.0 the repository loads `SaleRecord`s that keep only ids, and `SaleService` resolves them, so persistence depends only on the model.

**Where the abstractions live.** The store interfaces (`ProductStore`, `PersonStore`, `SaleStore`, …) sit in the persistence package next to the repositories that implement them, so that services depend on persistence and never the reverse (Taller 1 layer rule). The other interfaces sit next to the clients that need them: `ItemLookup`, `ItemSource`, `PersonLookup`, `SaleLookup`, `PromotionSelector` and `WarrantyAssigner` in the service package, `MenuOption` in the UI package.
