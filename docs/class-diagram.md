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
        -generation: int
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
        +saveCustomers(customers List~Customer~) void
        +loadCustomers() List~Customer~
        +saveSellers(sellers List~Seller~) void
        +loadSellers() List~Seller~
    }
    class SaleRepository {
        +saveAll(sales List~Sale~) void
        +loadAll() List~Sale~
    }

    ProductRepository ..> Product
    PersonRepository ..> Customer
    PersonRepository ..> Seller
    SaleRepository ..> Sale

    %% ===== SERVICE LAYER =====
    class ProductService {
        -repository: ProductRepository
        +registerVideoGame(...) VideoGame
        +registerConsole(...) Console
        +listAllProducts() List~Product~
        +updateStock(id String, quantity int) void
    }
    class PersonService {
        -repository: PersonRepository
        +registerCustomer(...) Customer
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
