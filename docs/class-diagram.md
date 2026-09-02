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
    class SaleRepository {
        +saveAll(sales List~Sale~) void
        +loadAll() List~Sale~
    }

    ProductRepository ..> Product
    PersonRepository ..> Customer
    PersonRepository ..> Seller
    SaleRepository ..> Sale
    SaleRepository ..> ProductService
    SaleRepository ..> PersonService

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
    class SaleService {
        -saleRepository: SaleRepository
        -productService: ProductService
        +registerSale(customer Customer, seller Seller, products List~Product~) Sale
        +viewAllSales() List~Sale~
        +viewSalesByCustomer(customer Customer) List~Sale~
        +viewSalesBySeller(seller Seller) List~Sale~
    }

    ProductService "1" --> "1" ProductRepository
    ProductService ..> Product
    PersonService "1" --> "1" PersonRepository
    SaleService "1" --> "1" SaleRepository
    SaleService "1" --> "1" ProductService
    SaleService ..> Sale

    %% ===== UI LAYER =====
    class ConsoleMenu {
        -productService: ProductService
        -personService: PersonService
        -saleService: SaleService
        +start() void
    }

    ConsoleMenu "1" --> "1" ProductService
    ConsoleMenu "1" --> "1" PersonService
    ConsoleMenu "1" --> "1" SaleService

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
```

**Known layering exception:** `SaleRepository` (persistence layer) depends on `ProductService` and `PersonService` (service layer) to resolve customer, seller, and product references while reconstructing a `Sale` from `data/sales.csv`. This is a reverse dependency relative to the strict `persistence → model` rule described in [layers-diagram.md](layers-diagram.md), required because a sale's persisted form only stores ids, not full records. It is an intentional, documented trade-off rather than an oversight.
