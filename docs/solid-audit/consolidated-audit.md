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
