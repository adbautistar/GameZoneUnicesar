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
    class ProductRepository {
        +saveAll(products List~Product~) void
        +loadAll() List~Product~
    }
    class PersonRepository {
        +saveAllCustomers(customers List~Customer~) void
        +loadAllCustomers() List~Customer~
        +saveAllSellers(sellers List~Seller~) void
        +loadAllSellers() List~Seller~
    }
    class SaleRecord {
        -id: String
        -customerId: String
        -sellerId: String
        -productIds: List~String~
        -appliedPromotionName: String
        -discountAmount: double
    }
    class SaleRepository {
        +saveAll(sales List~Sale~) void
        +loadAll() List~SaleRecord~
    }

    ProductRepository ..> Product
    PersonRepository ..> Customer
    PersonRepository ..> Seller
    SaleRepository ..> Sale
    SaleRepository ..> SaleRecord

    %% ===== SERVICE LAYER =====
    class ProductService {
        -repository: ProductRepository
        +registerVideoGame(...) void
        +registerConsole(...) void
        +listAllProducts() List~Product~
        +updateStock(productId String, quantity int) void
        +findById(id String) Product
    }
    class PersonService {
        -repository: PersonRepository
        +registerCustomer(...) void
        +findCustomerById(id String) Customer
        +findSellerById(id String) Seller
        +listAllCustomers() List~Customer~
        +listAllSellers() List~Seller~
    }
    class ItemCatalog {
        -productService: ProductService
        -accessoryService: AccessoryService
        +findItem(id String) Product
        +decreaseStock(item Product) void
        +restoreStock(item Product) void
    }
    class SaleService {
        -repository: SaleRepository
        -personService: PersonService
        -itemCatalog: ItemCatalog
        +recordSale(sale Sale) void
        +viewAllSales() List~Sale~
        +viewSalesByCustomer(customerId String) List~Sale~
        +viewSalesBySeller(sellerId String) List~Sale~
        +findById(id String) Sale
    }
    class SaleRegistrationService {
        -saleService: SaleService
        -personService: PersonService
        -itemCatalog: ItemCatalog
        -promotionService: PromotionService
        -warrantyService: WarrantyService
        +registerSale(customerId String, sellerId String, productIds List~String~, extendedWarrantyIds List~String~) Sale
        +findWarrantyEligibleItems(productIds List~String~) List~Product~
    }

    ProductService "1" --> "1" ProductRepository
    ProductService ..> Product
    PersonService "1" --> "1" PersonRepository
    ItemCatalog "1" --> "1" ProductService
    SaleService "1" --> "1" SaleRepository
    SaleService "1" --> "1" PersonService
    SaleService "1" --> "1" ItemCatalog
    SaleService ..> Sale
    SaleRegistrationService "1" --> "1" SaleService
    SaleRegistrationService "1" --> "1" PersonService
    SaleRegistrationService "1" --> "1" ItemCatalog

    %% ===== UI LAYER =====
    class ConsoleMenu {
        -productMenu: ProductMenu
        -personMenu: PersonMenu
        -saleMenu: SaleMenu
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

    ConsoleMenu "1" --> "1" ProductMenu
    ConsoleMenu "1" --> "1" PersonMenu
    ConsoleMenu "1" --> "1" SaleMenu
    ProductMenu "1" --> "1" ProductService
    PersonMenu "1" --> "1" PersonService
    SaleMenu "1" --> "1" SaleService
    SaleMenu "1" --> "1" SaleRegistrationService

    %% ===== ENTRY POINT =====
    class Main {
        +main(args String[]) void$
    }

    Main ..> ConsoleMenu
    Main ..> ProductRepository
    Main ..> PersonRepository
    Main ..> SaleRepository
    Main ..> ProductService
    Main ..> PersonService
    Main ..> SaleService
    Main ..> ItemCatalog
    Main ..> SaleRegistrationService
```

**Scope of this diagram:** the classes of Taller 1 (products, people, sales, UI and entry point) as they stand after the GRASP correction of Requerimiento 7 (v1.5.0), including the new classes that replaced parts of them (`SaleRecord`, `ItemCatalog`, `SaleRegistrationService`, and the product, person and sale submenus). `ItemCatalog` also depends on `AccessoryService`, and `SaleRegistrationService` on `PromotionService` and `WarrantyService`; those modules (Requerimientos 1-4) and the other four submenus are shown in [grasp-class-diagram.md](grasp-class-diagram.md).

**No layering exceptions remain.** Until v1.4.1, `SaleRepository` depended on `ProductService` and `PersonService` to resolve references while loading `data/sales.csv`, a reverse dependency that the GRASP audit classified as a Low Coupling violation (V-11). Since v1.5.0 the repository loads `SaleRecord`s that keep only ids, and `SaleService` resolves them, so persistence depends only on the model.
