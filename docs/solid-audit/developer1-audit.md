# SOLID Audit — Developer 1

| Item | Value |
|---|---|
| Role | Developer 1 (simulated) |
| Requirement | Requerimiento 8 — SOLID audit and correction, Phase 1 (audit) |
| Audited commit | `dd695ce` (`develop`, same tree as tag `v1.5.0`) |
| Date | 2026-10-14 |
| Scope rule | Only the code written by Developer 1, including what Developer 1 created or changed in the GRASP correction (`docs/grasp-refactor-log.md`) |

Verdicts: **Violation** (the code breaks the principle at one or more points), **Correct** (the code follows the principle), **Not applicable** (the audited code has no point where the principle applies).

## Scope

| Module (requirement) | Classes and methods audited |
|---|---|
| Products (Taller 1) | `Product`, `VideoGame`, `Console`, `ProductRepository`, `ProductService` (except `restoreStock`, Technical Lead) |
| Accessories (Requerimiento 1) | `Accessory`, `Controller`, `Cable`, `Memory` |
| Promotions (Requerimiento 2) | `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount` |
| Returns (Requerimiento 3) | `Return`, `Sale.canBeReturned` |
| Warranties (Requerimiento 4) | `Warranty`, `BasicWarranty`, `ExtendedWarranty` |
| GRASP correction (Requerimiento 7) | `Product.belongsToCategory`, `Product.isWarrantyEligible`, `Accessory.getAccessoryType`, `Sale.findProduct`, `Sale.wasMadeIn`, `Return.belongsToCustomer/belongsToSale/wasRegisteredIn`, `Warranty.covers` |
| Exceptions and validation (Requerimiento 6) | Not present in the reference repository — nothing to audit |

## Single Responsibility Principle (SRP)

**Verdict:** Violation — see V-01.

- **Products — Correct.** `Product` and its subclasses hold product data and their description; `ProductRepository` only reads and writes `data/products.csv`; `ProductService` only applies inventory rules.

- **Accessories — Correct.** Each subclass adds only its attributes, its description and its type.

- **Promotions — Correct.** Each promotion holds its parameters and its discount calculation; `Promotion.isActive` only uses its dates.

- **Returns — Violation (V-01).** `Return` computes its refund (business) and also lays out the Spanish receipt (presentation).

- **Warranties — Violation (V-01).** `Warranty` holds the coverage rules and also lays out the Spanish certificate.

- **GRASP additions — Correct.** Each added method answers one question with the class's own data (`belongsToCategory`, `findProduct`, `wasMadeIn`, `covers`, the `Return` queries).

### V-01

| Field | Content |
|---|---|
| ID | V-01 |
| Principle | SRP |
| Module | Returns (Requerimiento 3) and Warranties (Requerimiento 4) |
| Evidence | `Return.generateReturnReceipt()` (`Return.java:133`) and `Warranty.generateWarrantyCertificate()` (`Warranty.java:123`) build the user-facing Spanish text: borders (`=====`), labels (`Motivo:`, `Fecha de fin:`), money format (`%.2f`). |
| Explanation | Each class has two reasons to change: its business rules (refund, coverage) and the layout of what the user reads. Presentation is a different responsibility from the domain rule. |
| Consequence | A change in the receipt layout (for example, a new label or another currency format) forces a change in the domain classes and puts their business tests at risk. |
| Proposed solution | Move each layout into its own class (`ReturnReceipt`, `WarrantyCertificate`) that reads the domain object through its getters; keep `generateReturnReceipt()` and `generateWarrantyCertificate()` as thin delegations, because Requerimientos 3 and 4 define them as part of the domain API. |
| Other role's code involved | No. |

## Open/Closed Principle (OCP)

**Verdict:** Correct — the hierarchies are extended by adding subclasses; the repository type switch was examined and accepted (see notes).

- **Products — Correct for the hierarchy, accepted for the repository.** A new product type is a new subclass of `Product`, implementing `getDescription` and `belongsToCategory`, with no change to existing classes. `ProductRepository.toCsvLine/fromCsvLine` branch on the type and would change for a new type. This is **accepted, not counted**: a new product type necessarily brings new CSV columns, so the format changes anyway; the knowledge sits in one class whose single purpose is that format; and the GRASP audit judged it a correct Pure Fabrication.

- **Accessories — Correct.** A new accessory is a new subclass implementing `getDescription` and `getAccessoryType`.

- **Promotions — Correct.** New promotion strategies are added as subclasses of `Promotion`; `CategoryDiscount` no longer needs to change for a new product category since the GRASP correction (V-01 of Requerimiento 7).

- **Warranties — Correct for the hierarchy.** A new warranty type is a new subclass implementing duration, type and cost. (Its persistence tag is handled outside Developer 1's code; not audited here.)

## Liskov Substitution Principle (LSP)

**Verdict:** Correct — every subclass can replace its base class without changing the expected behavior.

- **Products — Correct.** `VideoGame` and `Console` implement `getDescription` and `belongsToCategory` without narrowing preconditions or throwing; `Console.isWarrantyEligible` only changes a value the base declares as overridable.

- **Accessories — Correct.** `Accessory.belongsToCategory` always answers `false`, which is a valid answer under the base contract ("does this product belong to the category?"); `isCompatibleWith` keeps the base behavior of every `Product` method.

- **Promotions — Correct.** All three subclasses return a non-negative discount for any sale and never throw, as `PromotionService.findBestPromotionFor` expects from any `Promotion`.

- **Returns — Not applicable.** `Return` has no subclasses or base class besides `Object`.

- **Warranties — Correct.** `BasicWarranty` and `ExtendedWarranty` return a positive duration, a type label and a non-negative cost; `generateWarrantyCertificate` and `isActive` behave the same for both.

- **GRASP additions — Correct.** The overrides added in Requerimiento 7 (`belongsToCategory`, `isWarrantyEligible`, `getAccessoryType`) keep the base contracts.

## Interface Segregation Principle (ISP)

**Verdict:** Not applicable — Developer 1's classes only depend on their own repository; no client in scope depends on a wide interface.

- **Products — Not applicable.** `ProductService` depends only on `ProductRepository`, and uses all of its methods.

- **Exceptions and validation — Not applicable.** Not present in the reference repository.

## Dependency Inversion Principle (DIP)

**Verdict:** Violation — see V-02.

- **Products — Violation (V-02).** See the full analysis below.

### V-02

| Field | Content |
|---|---|
| ID | V-02 |
| Principle | DIP |
| Module | Products (Taller 1) |
| Evidence | `ProductService` field `private final ProductRepository repository` and constructor `ProductService(ProductRepository repository)` (`ProductService.java:18`, `:27`). |
| Explanation | The inventory rules (high-level policy) depend on the concrete CSV repository (low-level detail) instead of on an abstraction of "where products are stored". |
| Consequence | Changing the storage (another file format, a database) requires changing `ProductService`; the service cannot be built over another storage without a subclass of the CSV repository. |
| Proposed solution | Introduce a `ProductStore` abstraction (`saveAll`, `loadAll`) implemented by `ProductRepository`, and make `ProductService` depend on it. The interface lives in the persistence package so that the Taller 1 layer rule (`service → persistence`) still holds. |
| Other role's code involved | No. |

## Summary of verdicts

| Principle | Verdict | Violations |
|---|---|---|
| Single Responsibility Principle (SRP) | Violation | V-01 |
| Open/Closed Principle (OCP) | Correct | — |
| Liskov Substitution Principle (LSP) | Correct | — |
| Interface Segregation Principle (ISP) | Not applicable | — |
| Dependency Inversion Principle (DIP) | Violation | V-02 |

## Notes

- **Examined and accepted (not counted):** the type switch in `ProductRepository.toCsvLine/fromCsvLine` under OCP (see the OCP section). This is the same fragment the GRASP audit judged correct as a Pure Fabrication.
- **Relation with GRASP:** the GRASP correction already removed an OCP problem in Developer 1's code — `CategoryDiscount` no longer maps category strings to classes (Requerimiento 7, V-01).
- No problem originating in another role's code was detected in this scope.
