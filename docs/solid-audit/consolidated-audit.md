# Consolidated SOLID Audit — Requerimiento 8

| Item | Value |
|---|---|
| Consolidated by | Technical Lead |
| Audited commit | `dd695ce` (`develop`, same tree as tag `v1.5.0`, after the GRASP correction) |
| Date | 2026-10-14 |
| Sources | `leader-audit.md`, `developer1-audit.md`, `developer2-audit.md` |

This document is the mandatory reference for Phase 2 (correction): the team corrects exactly the violations listed here.

## Consolidation work

1. **Coverage:** every production class was checked against the three scopes, including the 13 classes added by the GRASP correction (ownership from `docs/grasp-refactor-log.md`). No class was left unaudited and none was audited twice. Methods written by one role inside another role's class were audited by their author (`Sale.canBeReturned`, `Sale.findProduct`, `Sale.wasMadeIn` by Developer 1; `ProductService.restoreStock` by the Technical Lead).
2. **Evidence:** every violation cites class, method and lines on `dd695ce`, a consequence and a proposed correction. No finding had to be returned to its author.
3. **Boundary findings:** the same problem reported from two sides was merged into one violation, and corrections that need another role's class were split by owner.
4. **Accepted fragments:** fragments that match a warning sign but were judged acceptable are listed with their justification, so that they are not mistaken for omissions.

## Coverage

| Package | Classes | Audited by |
|---|---|---|
| model | `Product`, `VideoGame`, `Console`, `Accessory`, `Controller`, `Cable`, `Memory`, `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount`, `Return`, `Warranty`, `BasicWarranty`, `ExtendedWarranty` | Developer 1 |
| model | `Person`, `Customer`, `Seller` | Developer 2 |
| model | `Sale` (Developer 1's methods `canBeReturned`, `findProduct`, `wasMadeIn` audited by Developer 1) | Technical Lead |
| persistence | `ProductRepository` | Developer 1 |
| persistence | `PersonRepository`, `AccessoryRepository`, `PromotionRepository`, `ReturnRepository`, `WarrantyRepository`, `ReturnRecord`, `WarrantyRecord` | Developer 2 |
| persistence | `SaleRepository`, `SaleRecord` | Technical Lead |
| service | `ProductService` (`restoreStock` audited by the Technical Lead) | Developer 1 |
| service | `PersonService`, `AccessoryService`, `PromotionService`, `ReturnService`, `WarrantyService`, `ItemCatalog`, `IdGenerator` | Developer 2 |
| service | `SaleService`, `SaleRegistrationService` | Technical Lead |
| ui | `ConsoleMenu`, `ProductMenu`, `PersonMenu`, `SaleMenu`, `AccessoryMenu`, `PromotionMenu`, `ReturnMenu`, `WarrantyMenu` | Technical Lead |
| (root) | `Main` | Technical Lead |
| exceptions, validation | — | Not present in the reference repository |

Totals: 48 production classes — Developer 1: 17, Developer 2: 17, Technical Lead: 14.

## Team verdict per principle

| Principle | Leader | Developer 1 | Developer 2 | Team verdict |
|---|---|---|---|---|
| Single Responsibility (SRP) | Violation | Violation | Violation | **Violation** |
| Open/Closed (OCP) | Violation | Correct | Violation | **Violation** |
| Liskov Substitution (LSP) | Correct | Correct | Correct | **Correct** |
| Interface Segregation (ISP) | Violation | Not applicable | Violation | **Violation** |
| Dependency Inversion (DIP) | Violation | Violation | Violation | **Violation** |

LSP holds across the whole system: every subclass (products, accessories, promotions, warranties, people) can replace its base class without changing what callers expect. The other four principles are violated, mostly because the system has **no abstractions at all** — every dependency is on a concrete class.

## Definitive violations

| ID | Principle | Evidence (summary) | Origin | Correction owner |
|---|---|---|---|---|
| V-01 | SRP | `Return.generateReturnReceipt` and `Warranty.generateWarrantyCertificate` lay out user-facing text inside domain classes | developer1-audit V-01 | Developer 1 |
| V-02 | SRP | `Sale.generateReceipt` lays out the receipt (and its discount breakdown) inside the domain class | leader-audit V-01 | Technical Lead |
| V-03 | SRP | `ReturnService.generateMonthlyBalance` mixes financial reporting with return handling | developer2-audit V-01 | Developer 2 (`ReturnMenu`/`Main`: Technical Lead) |
| V-04 | OCP | `ItemCatalog` chooses the catalog with `instanceof Accessory` | developer2-audit V-03 | Boundary: Developer 2 + Developer 1 |
| V-05 | OCP | Warranty type tags known in `WarrantyRepository` and `WarrantyService` | developer2-audit V-02 | Boundary: Developer 1 + Developer 2 |
| V-06 | OCP / DIP | `ConsoleMenu` hard-codes labels and a `switch` bound to concrete submenus | leader-audit V-06 | Technical Lead |
| V-07 | ISP | `ReturnService`, `WarrantyService` depend on the whole `SaleService` to read sales | developer2-audit V-04 | Boundary: Developer 2 + Technical Lead |
| V-08 | ISP | `SaleService`, `SaleRegistrationService` depend on the whole `PersonService` to find people | leader-audit V-02 | Boundary: Technical Lead + Developer 2 |
| V-09 | ISP | `WarrantyService`, `SaleService` depend on the whole `ItemCatalog` to find items | developer2-audit V-05; leader-audit V-03 | Boundary: Developer 2 + Technical Lead |
| V-10 | DIP | `ProductService` depends on the concrete `ProductRepository` | developer1-audit V-02 | Developer 1 |
| V-11 | DIP | `PersonService`, `AccessoryService`, `PromotionService`, `ReturnService`, `WarrantyService` depend on concrete repositories | developer2-audit V-06 | Developer 2 |
| V-12 | DIP | `SaleService` depends on the concrete `SaleRepository` | leader-audit V-04 | Technical Lead |
| V-13 | DIP / ISP | `SaleRegistrationService` depends on the concrete `PromotionService` and `WarrantyService` | leader-audit V-05 | Boundary: Technical Lead + Developer 2 |

The full evidence, explanation and consequence of each violation are in the individual report named in the "Origin" column.

### Agreed solution per violation

| ID | Agreed solution | Principle applied |
|---|---|---|
| V-01 | `ReturnReceipt` and `WarrantyCertificate` (model) hold the layout; `generateReturnReceipt()` and `generateWarrantyCertificate()` stay as delegations (domain API of Requerimientos 3 and 4) | SRP |
| V-02 | `SaleReceipt` (model) holds the layout; `generateReceipt()` stays as a delegation (Taller 1 API) | SRP |
| V-03 | `MonthlyBalanceService` computes the balance from sales and returns; `ReturnService` keeps registration and queries | SRP |
| V-04 | `ItemSource` (find item, decrease stock, restore stock) implemented by `ProductService` and `AccessoryService`; `ItemCatalog` asks the source that owns the item — no type check | OCP |
| V-05 | `WarrantyKind` (model) knows every kind and how to build it; `Warranty.getKind()`; repository writes the kind, service builds through it | OCP |
| V-06 | `MenuOption` (label + action) implemented by the submenus and a `MonthlyBalanceMenu`; `ConsoleMenu` numbers and dispatches the options it receives; `Main` composes the list | OCP, DIP |
| V-07 | `SaleLookup` (`findById`, `viewAllSales`) implemented by `SaleService` | ISP |
| V-08 | `PersonLookup` (`findCustomerById`, `findSellerById`) implemented by `PersonService` | ISP |
| V-09 | `ItemLookup` (`findItem`) implemented by `ItemCatalog` | ISP |
| V-10 | `ProductStore` implemented by `ProductRepository` | DIP |
| V-11 | `PersonStore`, `AccessoryStore`, `PromotionStore`, `ReturnStore`, `WarrantyStore` implemented by the repositories | DIP |
| V-12 | `SaleStore` implemented by `SaleRepository` | DIP |
| V-13 | `PromotionSelector` and `WarrantyAssigner`, owned by the use case, implemented by `PromotionService` and `WarrantyService` | DIP, ISP |

Placement of the new abstractions: the store interfaces live in `com.gamezone.persistence`, next to the repositories that implement them, so that the Taller 1 layer rule (`service → persistence`, never the reverse) still holds; every other interface lives in `com.gamezone.service` or `com.gamezone.ui`, next to the clients that need it.
