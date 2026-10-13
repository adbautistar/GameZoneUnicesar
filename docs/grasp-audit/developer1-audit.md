# GRASP Audit — Developer 1

| Item | Value |
|---|---|
| Role | Developer 1 (simulated) |
| Requirement | Requerimiento 7 — GRASP audit and correction, Phase 1 (audit) |
| Audited commit | `8ca31bf` (`develop`, tag `v1.4.1`) |
| Date | 2026-10-13 |
| Scope rule | Only the code written by Developer 1, per the responsibility table of Requerimiento 7 |

Verdicts: **Violation** (the code breaks the pattern at one or more points), **Correct** (the pattern is applied correctly), **Not applicable** (the audited code has no point where the pattern applies).

## Scope

| Module (requirement) | Classes and methods audited |
|---|---|
| Products (Taller 1) | `Product`, `VideoGame`, `Console`, `ProductRepository`, `ProductService` (every method except `restoreStock`, which belongs to the Technical Lead) |
| Accessories (Requerimiento 1) | `Accessory`, `Controller`, `Cable`, `Memory` |
| Promotions (Requerimiento 2) | `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount` |
| Returns (Requerimiento 3) | `Return`, `Sale.canBeReturned` |
| Warranties (Requerimiento 4) | `Warranty`, `BasicWarranty`, `ExtendedWarranty` |
| Exceptions and validation (Requerimiento 6) | Not present in the reference repository — nothing to audit |

## Information Expert

**Verdict:** Correct — every class in scope computes what it knows from its own data.

- **Products — Correct.** `Product.updateStock(int)` (`Product.java:108`) changes the stock it owns; `ProductService.updateStock` (`ProductService.java:84`) locates the product and delegates the change to it instead of computing the new value itself. `VideoGame.getDescription` and `Console.getDescription` build the description from their own fields.

- **Accessories — Correct.** `Accessory.isCompatibleWith(String)` (`Accessory.java:61`) answers with the list of compatible consoles it owns.

- **Promotions — Correct.** `Promotion.isActive(LocalDate)` (`Promotion.java:111`) uses its own start and end dates; each subclass computes its own discount for the sale it receives.

- **Returns — Correct.** `Return.calculateRefundAmount()` (`Return.java:117`) sums the prices of the products it holds and is called from its own constructor; `generateReturnReceipt()` uses only its own data. `Sale.canBeReturned()` (`Sale.java:194`) decides the 30-day rule from the sale's own date.

- **Warranties — Correct.** `Warranty` derives `endDate` from `getDurationInMonths()` in its constructor (`Warranty.java:35`); `isActive(LocalDate)` (`Warranty.java:112`) uses its own dates; `ExtendedWarranty.getAdditionalCost()` uses the price of the product it covers.

## Creator

**Verdict:** Correct — objects are created by the class that aggregates them or holds their initializing data.

- **Products — Correct.** `ProductService.registerVideoGame` and `registerConsole` (`ProductService.java:43`, `:61`) create the products they add to the inventory list they aggregate. `ProductRepository.fromCsvLine` (`ProductRepository.java:116`) creates products from the records it reads, so it holds the initializing data.

- **Model classes in general — Correct.** Domain classes in scope do not create other domain objects; creation stays in the services and repositories that aggregate them or hold their data.

## Controller

**Verdict:** Not applicable — no class in scope receives system events.

- **Products — Not applicable.** None of these classes receives a system event; menu events are received by `ConsoleMenu` (Technical Lead).

- **Exceptions and validation — Not applicable.** The reference repository has no `exceptions` or `validation` packages.

## Low Coupling

**Verdict:** Correct — model classes depend only on the model; ProductRepository and ProductService follow the allowed layer direction.

- **Products — Correct.** `Product`, `VideoGame` and `Console` import nothing outside the model. `ProductRepository` depends only on the model; `ProductService` depends only on `ProductRepository` and the model.

- **Accessories — Correct.** The accessory hierarchy depends only on `Product`.

- **Promotions — Correct except for V-01.** `Promotion` and its subclasses depend on `Sale` and `Product`, which they need to compute a discount; `CategoryDiscount` additionally depends on the concrete classes `VideoGame` and `Console` (part of V-01).

- **Returns — Correct.** `Return` depends on `Sale` and `Product`, the two objects it describes.

- **Warranties — Correct.** The hierarchy depends only on `Product` and `Sale`.

## High Cohesion

**Verdict:** Correct — each class in scope has one focused purpose.

- **Products — Correct.** `ProductRepository` only reads and writes `data/products.csv`; `ProductService` only applies inventory rules; the product classes only hold product data and behavior.

- **Accessories — Correct.** Each subclass adds only its own attributes (`connectionType`; `lengthInMeters`/`connectorType`; `capacityInGB`/`memoryType`) and its description.

- **Promotions — Correct.** Each promotion class holds only its parameters and its calculation.

- **Returns — Correct.** `Return` holds only return data, its refund calculation and its receipt.

- **Warranties — Correct.** Each subclass contributes only its three polymorphic facts.

## Polymorphism

**Verdict:** Violation — see V-01.

- **Products — Correct.** `Product.getDescription()` is abstract and `VideoGame`/`Console` override it. `ProductRepository.toCsvLine`/`fromCsvLine` (`ProductRepository.java:90-126`) branch on the concrete type and on a type tag; this is **not** treated as a violation: the repository is a Pure Fabrication whose job is to keep the CSV format out of the model, and moving serialization into the hierarchy would put persistence knowledge inside the domain classes, which Taller 1 forbids.

- **Accessories — Correct.** `Controller`, `Cable` and `Memory` override `getDescription()`; no code in scope asks an accessory for its concrete type.

- **Promotions — Mixed.** `Promotion.calculateDiscount(Sale)` (`Promotion.java:122`) is abstract and overridden by the three subclasses, which is correct use of Polymorphism. Inside `CategoryDiscount.calculateDiscount`, however, the category of each product is resolved with conditionals on the concrete type (V-01).

- **Warranties — Correct.** `getDurationInMonths()`, `getWarrantyType()` and `getAdditionalCost()` are abstract and overridden by `BasicWarranty` and `ExtendedWarranty`; `generateWarrantyCertificate()` (`Warranty.java:123`) is written once on the base class and uses those polymorphic facts.

### V-01

| Field | Content |
|---|---|
| ID | V-01 |
| Pattern | Polymorphism (also recorded under Protected Variations) |
| Module | Promotions (Requerimiento 2) |
| Evidence | `CategoryDiscount.calculateDiscount(Sale)`, `CategoryDiscount.java:75-85`: `if ("VIDEOGAME".equals(targetCategory) && product instanceof VideoGame) {...} else if ("CONSOLE".equals(targetCategory) && product instanceof Console) {...}` |
| Explanation | The discount asks every product for its concrete class and maps a category string to that class itself. Which category a product belongs to varies by product type, so it is a responsibility of each product type, not a conditional inside the discount. |
| Consequence | A new product category (for example, a new `Product` subclass) silently gets no category discount until someone remembers to edit `CategoryDiscount`; the category strings duplicate knowledge that belongs to the product classes; `CategoryDiscount` is coupled to two concrete classes. |
| Proposed solution | Add an abstract `belongsToCategory(String category)` to `Product`, implemented by each subclass, and make `CategoryDiscount` sum the products for which `product.belongsToCategory(targetCategory)` is true. Patterns applied: Polymorphism and Protected Variations. |
| Other role's code involved | No — the product hierarchy and `CategoryDiscount` are both Developer 1's code. |

## Pure Fabrication

**Verdict:** Correct — ProductRepository is the fabrication that keeps file access out of the product hierarchy.

- **Products — Correct.** `ProductRepository` is an artificial class that owns file access so that `Product` stays free of I/O.

## Indirection

**Verdict:** Correct — ProductService mediates between the UI and ProductRepository.

- **Products — Correct.** `ProductService` sits between `ConsoleMenu` and `ProductRepository`; the UI never touches the repository.

- **Model classes — Not applicable.** The model classes have no collaborators that need an intermediary.

## Protected Variations

**Verdict:** Violation — see V-01 (same fragment).

- **Products — Correct.** A change of storage format is confined to `ProductRepository`; `ProductService` and the model are protected from it.

- **Promotions — Violation (V-01).** The vocabulary of product categories (`"VIDEOGAME"`, `"CONSOLE"`) and its mapping to classes is hard-coded inside `CategoryDiscount`, so the discount is not protected from a new product category. The full analysis is recorded once, under Polymorphism.

- **Warranties — Correct.** A new warranty type is added as a subclass without touching `Warranty` or the existing subclasses.

## Summary of verdicts

| Pattern | Verdict | Violations |
|---|---|---|
| Information Expert | Correct | — |
| Creator | Correct | — |
| Controller | Not applicable | — |
| Low Coupling | Correct | — |
| High Cohesion | Correct | — |
| Polymorphism | Violation | V-01 |
| Pure Fabrication | Correct | — |
| Indirection | Correct | — |
| Protected Variations | Violation | V-01 |

## Notes

- Fragments that were examined and judged **not** to be violations are recorded above with their justification (for example, the type tags in `ProductRepository`), so that the consolidation can see why they were discarded.
- No problem originating in another role's code was detected while auditing this scope, so nothing was reported to the Technical Lead.
