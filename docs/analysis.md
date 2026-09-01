# Analysis — GameZone Unicesar

## People

### Q1: What attributes are common to all people who interact with the store, and which are specific to each type of person? How is this distinction reflected in a class hierarchy?

All people share `id`, `firstName`, `lastName`, and `phone`, since every actor in the store needs to be identified and contacted regardless of role. `Customer` adds `email` and `purchaseHistory`, which only make sense for someone buying products, while `Seller` adds `employeeCode` and `shift`, which only make sense for an employee. This distinction is reflected by placing the shared attributes in an abstract `Person` class and the role-specific attributes in `Customer` and `Seller`, which extend it.

### Q2: Should there be a class representing a "generic person" without specifying a role? Why or why not? What implication does this decision have on the possibility of instantiating this class?

Yes, a generic `Person` class is useful to hold the attributes and behavior common to both roles and avoid duplicating them, but it should never represent a real, standalone actor in the store — every person interacting with GameZone is either a customer or a seller, never an unqualified "person". This is why `Person` is declared `abstract`: it cannot be instantiated directly, and the compiler enforces that only its concrete subclasses (`Customer`, `Seller`) can be created.

## Products

### Q3: What characteristics do all products sold by the store share, regardless of type? Which characteristics are specific to each product type?

Every product, whether a video game or a console, shares `id`, `title`, `price`, and `stock`, since these attributes are required to catalog, price, and track inventory for any item sold. `VideoGame` adds `platform`, `genre`, and `ageRating`, while `Console` adds `brand`, `model`, and `generation` — attributes that only make sense for their respective product type.

### Q4: Each type of product must be able to present a description that integrates its particular characteristics. How should this behavior be declared in the base class to guarantee that all subclasses implement it in their own way? What object-oriented programming mechanism enables this?

The base `Product` class declares `getDescription()` as an abstract method, which forces every concrete subclass to provide its own implementation that incorporates its specific attributes (for example, platform and genre for `VideoGame`, or brand and generation for `Console`). This is enabled by the OOP mechanism of abstraction combined with polymorphism: callers can invoke `getDescription()` on any `Product` reference without knowing its concrete type, and the correct subclass implementation runs at execution time.

## Sales and relationships

### Q5: A sale involves a customer, a seller, and one or more products. What kinds of relationships exist between the class representing the sale and the other classes of the system? Are these relationships of inheritance, association, composition, or another type? Justify.

`Sale` is related to `Customer` and `Seller` through association: a `Sale` references one `Customer` and one `Seller`, but neither the customer nor the seller depend on the sale to exist, and the same customer or seller participates in many independent sales. `Sale` is related to `Product` through aggregation: a sale holds a list of one or more products, but those products exist in the inventory independently of any particular sale and are not destroyed when the sale is removed. None of these are inheritance relationships, since a sale is not a kind of customer, seller, or product.

### Q6: Should the sale be responsible for calculating its own total, or should this responsibility fall on another class? Justify your decision.

The sale should calculate its own total through a `calculateTotal()` method, because the total is derived directly from data the `Sale` already owns — its list of products — and keeping the calculation there follows the single responsibility principle by cohering the sale's own state and behavior in one place. `SaleService` is still responsible for orchestrating the registration process (validating stock, updating inventory), but the arithmetic of summing the sale's own products belongs on `Sale` itself.

## Business constraints

### Q7: How does the design guarantee that a sale cannot be registered without at least one product? At what point in the system should this rule be validated?

The rule is validated in `SaleService.registerSale(Customer, Seller, List<Product>)`, before a `Sale` object is constructed or persisted: the service checks that the incoming product list is neither null nor empty and rejects the operation otherwise. Validating at the service layer, rather than inside the `Sale` model or the UI, keeps the business rule in one place, independent of how the UI collected the input and reusable by any future caller of the service.

### Q8: How does the design reflect the automatic update of inventory when a sale is registered? Which classes are involved in this operation?

When `SaleService.registerSale(...)` runs, it validates that each requested product has enough stock, then calls `Product.updateStock(int)` (via `ProductService`) to decrement the inventory for every product included in the sale, before finally saving the new `Sale` through `SaleRepository`. This operation involves `SaleService` as the orchestrator, `ProductService` and `Product` for the stock update itself, and `SaleRepository`/`ProductRepository` to persist the resulting state.

## Layered organization

### Q9: The system must be organized into four layers: model, persistence, services, and user interface. What type of classes belong in each layer? What criterion allows one to decide in which layer a class should be placed?

The `model` layer holds pure domain classes (`Product`, `Person`, `Sale`, and their subclasses) with no I/O or UI references; `persistence` holds classes that read and write model objects to files (`ProductRepository`, `PersonRepository`, `SaleRepository`); `service` holds the business rules that operate on model objects through the repositories (`ProductService`, `PersonService`, `SaleService`); and `ui` holds the console menu that talks only to services. The criterion for placement is the class's responsibility: does it represent business data (model), does it perform I/O (persistence), does it enforce business rules and coordinate persistence (service), or does it interact with the end user (ui)?

### Q10: Why should the logic for saving and retrieving data from files not be inside the domain classes? What problems arise when these responsibilities are mixed?

Domain classes should represent business concepts and behavior only; mixing in file I/O would violate the single responsibility principle and couple the domain model to a specific storage format. If persistence logic lived inside `Product` or `Sale`, changing the storage format (e.g., from plain text to CSV) would require modifying the domain classes themselves, and testing the domain logic would require a file system, making the design harder to maintain, test, and extend.

### Q11: What dependencies are allowed between the layers, and which are forbidden? Justify the meaning of the allowed dependencies.

The allowed dependencies are `ui → service`, `service → persistence`, `service → model`, and `persistence → model`; `model` depends on nothing. Any dependency in the opposite direction — `model` depending on `persistence`, `persistence` depending on `service`, or `service`/`persistence`/`model` depending on `ui` — is forbidden. This unidirectional flow keeps the domain model stable and reusable at the core of the application, while outer layers (persistence, service, ui) depend inward on it rather than the reverse, preventing circular dependencies and letting each layer be replaced or tested independently.
