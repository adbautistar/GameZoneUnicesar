# GRASP Audit — Developer 2

| Item | Value |
|---|---|
| Role | Developer 2 (simulated) |
| Requirement | Requerimiento 7 — GRASP audit and correction, Phase 1 (audit) |
| Audited commit | `8ca31bf` (`develop`, tag `v1.4.1`) |
| Date | 2026-10-13 |
| Scope rule | Only the code written by Developer 2, per the responsibility table of Requerimiento 7 |

Verdicts: **Violation** (the code breaks the pattern at one or more points), **Correct** (the pattern is applied correctly), **Not applicable** (the audited code has no point where the pattern applies).

## Scope

| Module (requirement) | Classes and methods audited |
|---|---|
| Persons (Taller 1) | `Person`, `Customer`, `Seller`, `PersonRepository`, `PersonService` |
| Accessories (Requerimiento 1) | `AccessoryRepository`, `AccessoryService` |
| Promotions (Requerimiento 2) | `PromotionRepository`, `PromotionService` |
| Returns (Requerimiento 3) | `ReturnRepository`, `ReturnService` (including `generateMonthlyBalance`) |
| Warranties (Requerimiento 4) | `WarrantyRepository`, `WarrantyService` |

## Information Expert

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `Person.getFullName()` builds the name from its own fields; `Customer.addPurchase(Sale)` updates the history it owns.

- **Accessories — Correct.** `AccessoryService.findAccessoriesCompatibleWith` (`AccessoryService.java:62`) asks each accessory `isCompatibleWith(consoleId)` instead of reading its list.

- **Promotions — Correct.** `PromotionService.listActivePromotions` asks each promotion `isActive(today)`; `findBestPromotionFor` (`PromotionService.java:76`) asks each promotion `calculateDiscount(sale)` and only compares the results.

- **Warranties — Mixed.** `listActiveWarranties` asks each warranty `isActive(today)` (correct); `findWarrantyByProduct` navigates the warranty's associations (V-07).

### V-04

| Field | Content |
|---|---|
| ID | V-04 |
| Pattern | Information Expert |
| Module | Returns (Requerimiento 3) |
| Evidence | `ReturnService.registerReturn`, `ReturnService.java:41-53`: nested loop `for (Product product : sale.getProducts()) { if (product.getId().equals(productId)) ... }` |
| Explanation | The service searches the sale's products itself, but the `Sale` holds that information and is the expert to answer which of its products has a given id. |
| Consequence | The search logic is written outside the class that owns the data and would be repeated by any other client that needs it. |
| Proposed solution | Ask the sale: `sale.findProduct(productId)`. |
| Other role's code involved | Yes — `Sale` belongs to the Technical Lead; by the precedent of Requerimiento 3 (`Sale.canBeReturned`), return-related query methods on `Sale` are model work of Developer 1. |

### V-05

| Field | Content |
|---|---|
| ID | V-05 |
| Pattern | Information Expert |
| Module | Returns (Requerimiento 3) |
| Evidence | `ReturnService.viewReturnsByCustomer` (`:79`) `returnItem.getOriginalSale().getCustomer().getId()`; `viewReturnsBySale` (`:89`) `returnItem.getOriginalSale().getId()`; `generateMonthlyBalance` (`:99`, `:106`) `sale.getDate().getMonthValue() == month && sale.getDate().getYear() == year` and the same on each return. |
| Explanation | The service navigates getter chains to reach data owned by `Return` and `Sale` instead of asking them. |
| Consequence | The service knows the internal structure of `Return` and `Sale`; any change in those associations breaks the service. |
| Proposed solution | Ask the experts: `Return.belongsToCustomer(customerId)`, `Return.belongsToSale(saleId)`, `Return.wasRegisteredIn(month, year)` and `Sale.wasMadeIn(month, year)`. |
| Other role's code involved | Yes — `Return` is Developer 1's code; `Sale.wasMadeIn` follows the same precedent as V-04. |

### V-07

| Field | Content |
|---|---|
| ID | V-07 |
| Pattern | Information Expert |
| Module | Warranties (Requerimiento 4) |
| Evidence | `WarrantyService.findWarrantyByProduct`, `WarrantyService.java:43`: `warranty.getProduct().getId().equals(productId) && warranty.getSale().getId().equals(saleId)` |
| Explanation | The service navigates the warranty's associations instead of asking the warranty whether it covers a product in a sale. |
| Consequence | The service depends on the internal structure of `Warranty`. |
| Proposed solution | Ask the warranty: `warranty.covers(productId, saleId)`. |
| Other role's code involved | Yes — `Warranty` is Developer 1's code. |

## Creator

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonService.registerCustomer` (`PersonService.java:42`) creates the `Customer` it adds to the list it aggregates; `PersonRepository` creates customers and sellers from the records it reads.

- **Accessories — Correct.** `AccessoryService.registerController/registerCable/registerMemory` (`AccessoryService.java:23-42`) create the accessories they aggregate.

- **Promotions — Correct.** `PromotionService.registerPercentageDiscount/CategoryDiscount/BulkPurchaseDiscount` create the promotions they aggregate.

- **Returns — Correct.** `ReturnService.registerReturn` creates the `Return` it records.

- **Warranties — Correct.** `WarrantyService.assignBasicWarranty/assignExtendedWarranty` create the warranties they record.

## Controller

**Verdict:** Pending — audit in progress.

- **Persons — Not applicable.** No class in scope receives menu events; `ConsoleMenu` (Technical Lead) does.

## Low Coupling

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonRepository` depends only on the model; `PersonService` depends only on `PersonRepository` and the model.

### V-02

| Field | Content |
|---|---|
| ID | V-02 |
| Pattern | Low Coupling |
| Module | Returns (Requerimiento 3) |
| Evidence | `ReturnRepository` imports and stores `SaleService`, `ProductService` and `AccessoryService` (`ReturnRepository.java:6-8`, `:25-27`, constructors `:30`, `:39`) and calls them in `fromCsvLine` (`:107`, `:115-117`). |
| Explanation | A persistence class depends on the service layer, the reverse of the allowed direction `service → persistence`. The repository both reads the file and resolves business references. |
| Consequence | Changing a service constructor or lookup breaks the repository; the repository cannot be built or tested without three services; it contributes to the construction-order problems between services. |
| Proposed solution | The repository persists and loads only references (a `ReturnRecord` with ids); `ReturnService` resolves those ids into `Sale` and `Product` objects when it loads. |
| Other role's code involved | No — both classes are Developer 2's code (the `Main` wiring that builds them belongs to the Technical Lead). |

### V-06

| Field | Content |
|---|---|
| ID | V-06 |
| Pattern | Low Coupling |
| Module | Warranties (Requerimiento 4) |
| Evidence | `WarrantyRepository` imports and stores `SaleService`, `ProductService` and `AccessoryService` (`WarrantyRepository.java:8-10`, `:31-33`, constructors `:36`, `:45`) and calls them in `fromCsvLine` (`:109-119`). |
| Explanation | Same reverse dependency as V-02. Here it is also part of a cycle: the sale registration needs the warranty service, and the warranty repository needs the sale service. |
| Consequence | The cycle forces a special construction order in `Main` (a setter instead of a constructor parameter); the repository cannot be built without three services. |
| Proposed solution | Persist and load only references (a `WarrantyRecord`); `WarrantyService` resolves them when it loads. |
| Other role's code involved | No — both classes are Developer 2's code (the `Main` wiring belongs to the Technical Lead). |

## High Cohesion

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonRepository` only reads and writes the two CSV files; `PersonService` only registers and looks up people.

- **Accessories — Correct.** `AccessoryRepository` only handles `data/accessories.csv`; `AccessoryService` only handles accessory rules.

- **Promotions — Correct.** `PromotionService` holds registration, validation of percentage and dates, and selection of the best promotion — all promotion rules.

- **Returns — Correct.** `ReturnService` groups return registration, return queries and the monthly balance, which the requirement defines as part of the returns module.

- **Warranties — Correct.** `WarrantyService` only assigns and queries warranties.

## Polymorphism

**Verdict:** Pending — audit in progress.

- **Accessories — Mixed.** `AccessoryRepository.toCsvLine`/`fromCsvLine` branch on the concrete accessory type; judged **correct** for the same reason as the other repositories (the repository is a Pure Fabrication that keeps the CSV format out of the model). `AccessoryService.listAccessoriesByType` is a violation (V-01).

- **Promotions — Correct.** `findBestPromotionFor` relies on the polymorphic `calculateDiscount` and never asks for the promotion's type; `PromotionRepository` serialization judged correct (Pure Fabrication).

### V-01

| Field | Content |
|---|---|
| ID | V-01 |
| Pattern | Polymorphism (also recorded under Protected Variations) |
| Module | Accessories (Requerimiento 1) |
| Evidence | `AccessoryService.listAccessoriesByType(String)`, `AccessoryService.java:48-60`: `if ("CONTROLLER".equals(type) && accessory instanceof Controller) ... else if ("CABLE".equals(type) && accessory instanceof Cable) ... else if ("MEMORY".equals(type) && accessory instanceof Memory)` |
| Explanation | The service asks each accessory for its concrete class and maps a type string to it. The type of an accessory is something each accessory knows about itself. |
| Consequence | A new accessory subclass is never listed by type until this method is edited; the service is coupled to the three concrete subclasses. |
| Proposed solution | Let each accessory expose its own type (`getAccessoryType()`, implemented by each subclass) and filter with `type.equals(accessory.getAccessoryType())`. |
| Other role's code involved | Yes — the accessory hierarchy is Developer 1's code; Developer 1 must add `getAccessoryType()`. |

### V-03

| Field | Content |
|---|---|
| ID | V-03 |
| Pattern | Polymorphism |
| Module | Returns (Requerimiento 3) |
| Evidence | `ReturnService.registerReturn`, `ReturnService.java:58-64`: `if (product instanceof Accessory) { accessoryService.updateStock(...) } else { productService.restoreStock(...) }` |
| Explanation | Where the stock of an item lives is decided with a type check inside the use case. |
| Consequence | Every place that adjusts stock must repeat the same check; adding a new kind of sellable item means editing every such place. |
| Proposed solution | Move the decision behind a single intermediary that adjusts stock for any sellable item (see V-08). |
| Other role's code involved | No. |

## Pure Fabrication

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonRepository` is the fabrication that keeps file access out of `Customer` and `Seller`.

## Indirection

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonService` mediates between the UI and `PersonRepository`.

## Protected Variations

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** A change of file format is confined to `PersonRepository`.

- **Accessories — Violation (V-01).** The accessory type vocabulary (`"CONTROLLER"`, `"CABLE"`, `"MEMORY"`) and its mapping to classes is hard-coded in the service. Full analysis under Polymorphism.
