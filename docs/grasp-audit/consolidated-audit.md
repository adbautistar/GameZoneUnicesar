# Consolidated GRASP Audit — Requerimiento 7

| Item | Value |
|---|---|
| Consolidated by | Technical Lead |
| Audited commit | `8ca31bf` (`develop`, tag `v1.4.1`) |
| Date | 2026-10-13 |
| Sources | `leader-audit.md`, `developer1-audit.md`, `developer2-audit.md` |

This document is the mandatory reference for Phase 2 (correction): the team corrects exactly the violations listed here.

## Consolidation work

1. **Coverage:** every production class was checked against the three scopes (table below). No class was left unaudited and none was audited twice; the two methods added to another role's class (`Sale.canBeReturned`, `ProductService.restoreStock`) were audited by the role that wrote them.
2. **Evidence:** every violation in the three reports cites class, method and code fragment on `8ca31bf`, states a consequence and a proposed solution. No finding had to be returned to its author. Fragments discarded as non-violations (type tags inside repository serialization) carry their justification in the individual reports and are accepted.
3. **Boundary findings:** findings reported from two sides of the same problem were merged into one violation with the correction split by owner (see "Boundary findings").
4. **Team verdicts:** a pattern is a team-level violation when at least one report holds a confirmed violation for it.

## Coverage

| Package | Class | Audited by |
|---|---|---|
| model | `Product`, `VideoGame`, `Console` | Developer 1 |
| model | `Accessory`, `Controller`, `Cable`, `Memory` | Developer 1 |
| model | `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount` | Developer 1 |
| model | `Return` | Developer 1 |
| model | `Warranty`, `BasicWarranty`, `ExtendedWarranty` | Developer 1 |
| model | `Person`, `Customer`, `Seller` | Developer 2 |
| model | `Sale` (except `canBeReturned`) | Technical Lead |
| model | `Sale.canBeReturned` | Developer 1 |
| persistence | `ProductRepository` | Developer 1 |
| persistence | `PersonRepository`, `AccessoryRepository`, `PromotionRepository`, `ReturnRepository`, `WarrantyRepository` | Developer 2 |
| persistence | `SaleRepository` | Technical Lead |
| service | `ProductService` (except `restoreStock`) | Developer 1 |
| service | `ProductService.restoreStock` | Technical Lead |
| service | `PersonService`, `AccessoryService`, `PromotionService`, `ReturnService`, `WarrantyService` | Developer 2 |
| service | `SaleService` | Technical Lead |
| ui | `ConsoleMenu` | Technical Lead |
| (root) | `Main` | Technical Lead |
| exceptions, validation | — | Not present in the reference repository |

Totals: 35 production classes — Developer 1: 17, Developer 2: 13, Technical Lead: 5 (plus `ProductService.restoreStock`).

## Team verdict per pattern

| Pattern | Leader | Developer 1 | Developer 2 | Team verdict |
|---|---|---|---|---|
| Information Expert | Violation | Correct | Violation | **Violation** |
| Creator | Correct | Correct | Correct | **Correct** |
| Controller | Violation | Not applicable | Not applicable | **Violation** |
| Low Coupling | Violation | Correct | Violation | **Violation** |
| High Cohesion | Violation | Correct | Correct | **Violation** |
| Polymorphism | Violation | Violation | Violation | **Violation** |
| Pure Fabrication | Violation | Correct | Violation | **Violation** |
| Indirection | Violation | Correct | Violation | **Violation** |
| Protected Variations | Violation | Violation | Violation | **Violation** |

Creator is the only pattern applied correctly across the whole system: every entity is created by the service that records it or by the repository that holds its data.

## Definitive violations

| ID | Pattern | Evidence (summary) | Origin | Correction owner |
|---|---|---|---|---|
| V-01 | Polymorphism / Protected Variations | `CategoryDiscount.calculateDiscount` maps category strings to `VideoGame`/`Console` with `instanceof` | developer1-audit V-01 | Developer 1 |
| V-02 | Polymorphism / Protected Variations | `AccessoryService.listAccessoriesByType` maps type strings to subclasses with `instanceof` | developer2-audit V-01 | Boundary: Developer 1 + Developer 2 |
| V-03 | Low Coupling | `ReturnRepository` depends on `SaleService`, `ProductService`, `AccessoryService` | developer2-audit V-02 | Developer 2 (`Main` wiring: Technical Lead) |
| V-04 | Low Coupling | `WarrantyRepository` depends on `SaleService`, `ProductService`, `AccessoryService` | developer2-audit V-06 | Developer 2 (`Main` wiring: Technical Lead) |
| V-05 | Indirection / Polymorphism | Product-or-accessory lookup and `instanceof Accessory` stock dispatch repeated in `ReturnRepository`, `WarrantyRepository`, `ReturnService`, `SaleService`, `SaleRepository` | developer2-audit V-03, V-08; leader-audit V-06 (accessory part), V-07 | Boundary: Developer 2 + Technical Lead |
| V-06 | Information Expert | `ReturnService.registerReturn` searches the sale's products itself | developer2-audit V-04 | Boundary: Developer 1 + Developer 2 |
| V-07 | Information Expert | `ReturnService` views and monthly balance navigate getter chains on `Return` and `Sale` | developer2-audit V-05 | Boundary: Developer 1 + Developer 2 |
| V-08 | Information Expert | `WarrantyService.findWarrantyByProduct` navigates the warranty's associations | developer2-audit V-07 | Boundary: Developer 1 + Developer 2 |
| V-09 | Pure Fabrication | `RET-`/`WAR-`/`SALE-` + `System.currentTimeMillis()` written inline in three services | developer2-audit V-09; leader-audit V-08 | Boundary: Developer 2 + Technical Lead |
| V-10 | Information Expert | `SaleService.registerSale` and `SaleRepository.fromCsvLine` adjust the sale total through setters | leader-audit V-03 | Technical Lead |
| V-11 | Low Coupling / High Cohesion | `SaleRepository` depends on three services and rebuilds purchase history while parsing | leader-audit V-01, V-02 | Technical Lead |
| V-12 | High Cohesion / Low Coupling / Protected Variations | `SaleService.registerSale` does the whole use case; `SaleService` ↔ `WarrantyService` cycle wired by setter with a silent `null` guard | leader-audit V-04, V-05 | Technical Lead |
| V-13 | Polymorphism / Protected Variations | `instanceof Console` decides warranty eligibility in `SaleService` and `ConsoleMenu` | leader-audit V-06 (console part) | Boundary: Developer 1 + Technical Lead |
| V-14 | Controller | `ConsoleMenu.registerSale` applies the eligibility rule and infers the warranty messages | leader-audit V-10 | Technical Lead |
| V-15 | High Cohesion | `ConsoleMenu` (863 lines) holds the UI of every module | leader-audit V-11 | Technical Lead |
| V-16 | Information Expert | `SaleService.viewSalesByCustomer/BySeller` navigate getter chains on `Sale` | leader-audit V-09 | Technical Lead |

The full evidence, explanation and consequence of each violation are in the individual report named in the "Origin" column.

### Agreed solution per violation

| ID | Agreed solution | Pattern applied |
|---|---|---|
| V-01 | `Product.belongsToCategory(String)` implemented by each subclass; `CategoryDiscount` delegates to it | Polymorphism, Protected Variations |
| V-02 | `Accessory.getAccessoryType()` implemented by each subclass (Developer 1); `AccessoryService` filters with it (Developer 2) | Polymorphism, Protected Variations |
| V-03 | `ReturnRepository` persists and loads references only (`ReturnRecord`); `ReturnService` resolves them | Low Coupling |
| V-04 | `WarrantyRepository` persists and loads references only (`WarrantyRecord`); `WarrantyService` resolves them | Low Coupling |
| V-05 | `ItemCatalog` in the service layer (`findItem`, `decreaseStock`, `restoreStock`) created by Developer 2 and used by every client | Indirection, Protected Variations |
| V-06 | `Sale.findProduct(String)` (Developer 1, return-related model work per the Requerimiento 3 precedent); used by `ReturnService` | Information Expert |
| V-07 | `Return.belongsToCustomer`, `Return.belongsToSale`, `Return.wasRegisteredIn`, `Sale.wasMadeIn` (Developer 1); used by `ReturnService` | Information Expert |
| V-08 | `Warranty.covers(productId, saleId)` (Developer 1); used by `WarrantyService` | Information Expert |
| V-09 | `IdGenerator.next(prefix)` created by Developer 2 and adopted by the Technical Lead; same id format | Pure Fabrication |
| V-10 | `Sale.applyPromotion(name, discount)` and `Sale.addCharge(amount)` | Information Expert |
| V-11 | `SaleRepository` persists and loads references only (`SaleRecord`); `SaleService` resolves them and rebuilds purchase history | Low Coupling, High Cohesion |
| V-12 | `SaleRegistrationService` (use-case controller) owns the registration; `SaleService` keeps storage and queries; setter injection removed | Controller, High Cohesion, Low Coupling |
| V-13 | `Product.isWarrantyEligible()` overridden by `Console` (Developer 1); used by the sale registration (Technical Lead) | Polymorphism, Protected Variations |
| V-14 | The sale registration use case answers which items are eligible; `ConsoleMenu` only asks and prints | Controller |
| V-15 | One submenu class per module in `com.gamezone.ui`; `ConsoleMenu` keeps the main loop | High Cohesion |
| V-16 | `Sale.wasMadeBy(customerId)`, `Sale.wasHandledBy(sellerId)` | Information Expert |

## Boundary findings

| ID | How the correction is split |
|---|---|
| V-02 | Developer 1 adds `getAccessoryType()` to the accessory hierarchy first; Developer 2 then rewrites `listAccessoriesByType`. |
| V-03, V-04 | Developer 2 changes the repositories and services. Because `Main` belongs to the Technical Lead, the Technical Lead commits the new wiring inside Developer 2's task group; Developer 2 keeps the old constructors as `@Deprecated` until that commit and removes them afterwards, so every commit compiles. |
| V-05 | Reported by Developer 2 (returns/warranties) and the Technical Lead (sales). Developer 2 creates `ItemCatalog` because both catalogs it bridges are already used by Developer 2's code; each owner then replaces the duplicated lookups in their own classes. |
| V-06, V-07 | The violation is in Developer 2's service, but the fix needs query methods on `Return` (Developer 1) and on `Sale`. Following the Requerimiento 3 precedent (`Sale.canBeReturned` was written by Developer 1 as return-related model work), the new return-related methods on `Sale` are also written by Developer 1. |
| V-08 | Developer 1 adds `Warranty.covers`; Developer 2 uses it. |
| V-09 | Reported by both Developer 2 and the Technical Lead. Developer 2 creates `IdGenerator` (two of the three call sites); the Technical Lead adopts it for sales. |
| V-13 | Developer 1 adds `isWarrantyEligible()`; the Technical Lead uses it in the sale registration and the UI. |

**Correction order** (see `grasp-analysis.md`, Q5): Developer 1 first (model methods that other corrections need), then Developer 2 (fabrications and repositories), then the Technical Lead (sale registration, UI and documentation).

## Out-of-scope findings

Functional defects found during the audit. They are **not** corrected in Requerimiento 7, because correcting them would change what the user observes; the refactor must preserve them.

| ID | Defect | Evidence | Reported by |
|---|---|---|---|
| OOS-01 | The extended-warranty surcharge is lost after a restart. | `SaleRepository.fromCsvLine` ignores the persisted total (field 5) and recomputes it as the sum of prices minus the discount; sales with an extended warranty reload with a lower total, which also changes the monthly balance. | Technical Lead |
| OOS-02 | The receipt subtotal includes the warranty surcharge without listing it. | `Sale.generateReceipt` prints `Subtotal = totalAmount + discountAmount`; when an extended warranty was added, the subtotal no longer matches the listed product prices. | Technical Lead |
| OOS-03 | Two warranties can receive the same id. | `WarrantyService.assignBasicWarranty` and `assignExtendedWarranty` both use `"WAR-" + System.currentTimeMillis()`; for a console with an extended warranty both run in the same registration and can share the millisecond. | Developer 2 |
