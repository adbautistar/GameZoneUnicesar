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
| Exceptions and validation (Requerimiento 6) | Not present in the reference repository — nothing to audit |

## Information Expert

**Verdict:** Violation — see V-04, V-05, V-07.

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

**Verdict:** Correct — each service creates the entities it registers and aggregates.

- **Persons — Correct.** `PersonService.registerCustomer` (`PersonService.java:42`) creates the `Customer` it adds to the list it aggregates; `PersonRepository` creates customers and sellers from the records it reads.

- **Accessories — Correct.** `AccessoryService.registerController/registerCable/registerMemory` (`AccessoryService.java:23-42`) create the accessories they aggregate.

- **Promotions — Correct.** `PromotionService.registerPercentageDiscount/CategoryDiscount/BulkPurchaseDiscount` create the promotions they aggregate.

- **Returns — Correct.** `ReturnService.registerReturn` creates the `Return` it records.

- **Warranties — Correct.** `WarrantyService.assignBasicWarranty/assignExtendedWarranty` create the warranties they record.

## Controller

**Verdict:** Not applicable — no class in scope receives system events from the user.

- **Persons — Not applicable.** No class in scope receives menu events; `ConsoleMenu` (Technical Lead) does.

- **Exceptions and validation — Not applicable.** The reference repository has no `exceptions` or `validation` packages.

## Low Coupling

**Verdict:** Violation — see V-02, V-06.

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

**Verdict:** Correct — each class in scope has one focused purpose.

- **Persons — Correct.** `PersonRepository` only reads and writes the two CSV files; `PersonService` only registers and looks up people.

- **Accessories — Correct.** `AccessoryRepository` only handles `data/accessories.csv`; `AccessoryService` only handles accessory rules.

- **Promotions — Correct.** `PromotionService` holds registration, validation of percentage and dates, and selection of the best promotion — all promotion rules.

- **Returns — Correct.** `ReturnService` groups return registration, return queries and the monthly balance, which the requirement defines as part of the returns module.

- **Warranties — Correct.** `WarrantyService` only assigns and queries warranties.

## Polymorphism

**Verdict:** Violation — see V-01, V-03.

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

**Verdict:** Violation — see V-09.

- **Persons — Correct.** `PersonRepository` is the fabrication that keeps file access out of `Customer` and `Seller`.

### V-09

| Field | Content |
|---|---|
| ID | V-09 |
| Pattern | Pure Fabrication |
| Module | Returns and Warranties (Requerimientos 3 and 4) |
| Evidence | `ReturnService.registerReturn` (`ReturnService.java:55`) `"RET-" + System.currentTimeMillis()`; `WarrantyService.assignBasicWarranty` and `assignExtendedWarranty` (`WarrantyService.java:26`, `:34`) `"WAR-" + System.currentTimeMillis()`. |
| Explanation | Generating identifiers is a technical responsibility that belongs to no domain concept; it is scattered across services instead of living in one fabricated class. |
| Consequence | Any change to the identifier scheme must be repeated in every service. |
| Proposed solution | Create a fabrication `IdGenerator.next(prefix)` and use it everywhere identifiers are generated. |
| Other role's code involved | Yes — the sale registration code (Technical Lead) generates `SALE-` identifiers the same way; reported for consolidation. |

## Indirection

**Verdict:** Violation — see V-08.

- **Persons — Correct.** `PersonService` mediates between the UI and `PersonRepository`.

- **Reported to the Technical Lead.** While auditing V-08 and V-09, the same duplication was observed from the calling side in the sale registration code, which belongs to the Technical Lead. It is not audited here; it was reported for the consolidation.

### V-08

| Field | Content |
|---|---|
| ID | V-08 |
| Pattern | Indirection |
| Module | Returns and Warranties (Requerimientos 3 and 4) |
| Evidence | The lookup `productService.findById(id)`, then `accessoryService.findById(id)` is written in `ReturnRepository.fromCsvLine` (`:115-117`) and `WarrantyRepository.fromCsvLine` (`:109-111`); the stock dispatch of V-03 is the same problem on the write side. |
| Explanation | Two catalogs (products and accessories) must collaborate with every client that handles a sellable item, but no intermediary exists, so each client talks to both and knows the lookup order. |
| Consequence | The lookup order and the stock dispatch are repeated in several classes; a third catalog would require editing all of them. |
| Proposed solution | Introduce an intermediary (for example an `ItemCatalog` in the service layer) with `findItem(id)`, `decreaseStock(item)` and `restoreStock(item)`, and make every client use it. |
| Other role's code involved | Yes — the same duplication exists in the sale registration code (Technical Lead); reported for consolidation. |

## Protected Variations

**Verdict:** Violation — see V-01 (same fragment).

- **Persons — Correct.** A change of file format is confined to `PersonRepository`.

- **Accessories — Violation (V-01).** The accessory type vocabulary (`"CONTROLLER"`, `"CABLE"`, `"MEMORY"`) and its mapping to classes is hard-coded in the service. Full analysis under Polymorphism.

## Summary of verdicts

| Pattern | Verdict | Violations |
|---|---|---|
| Information Expert | Violation | V-04, V-05, V-07 |
| Creator | Correct | — |
| Controller | Not applicable | — |
| Low Coupling | Violation | V-02, V-06 |
| High Cohesion | Correct | — |
| Polymorphism | Violation | V-01, V-03 |
| Pure Fabrication | Violation | V-09 |
| Indirection | Violation | V-08 |
| Protected Variations | Violation | V-01 |

## Notes

- Repository serialization branches (`AccessoryRepository`, `PromotionRepository`, and the `BASIC`/`EXTENDED` tag in `WarrantyRepository.toCsvLine`) were examined and judged correct: the repositories are Pure Fabrications that keep the CSV format out of the model.
- Findings V-04, V-05 and V-07 need model methods in Developer 1's code; V-08 and V-09 have a counterpart in the Technical Lead's code. These were reported to the Technical Lead for the consolidation.
- While auditing `WarrantyService`, it was noticed that `assignBasicWarranty` and `assignExtendedWarranty` called in the same millisecond produce the same `WAR-` id. This is a functional defect, not a GRASP violation; it was reported to the Technical Lead for the out-of-scope section.
