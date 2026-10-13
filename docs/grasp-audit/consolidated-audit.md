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
