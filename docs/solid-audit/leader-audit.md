# SOLID Audit — Technical Lead

| Item | Value |
|---|---|
| Role | Technical Lead |
| Requirement | Requerimiento 8 — SOLID audit and correction, Phase 1 (audit) |
| Audited commit | `dd695ce` (`develop`, same tree as tag `v1.5.0`) |
| Date | 2026-10-14 |
| Scope rule | Only the code written by the Technical Lead, including what the Technical Lead created or changed in the GRASP correction (`docs/grasp-refactor-log.md`) |

Verdicts: **Violation** (the code breaks the principle at one or more points), **Correct** (the code follows the principle), **Not applicable** (the audited code has no point where the principle applies).

## Scope

| Module (requirement) | Classes and methods audited |
|---|---|
| Sales (Taller 1), promotion changes (Requerimiento 2), GRASP additions | `Sale` (except `canBeReturned`, `findProduct`, `wasMadeIn`, owned by Developer 1) |
| Sales persistence (Taller 1, Requerimiento 7) | `SaleRepository`, `SaleRecord` |
| Sales services (Taller 1, Requerimientos 1, 2, 4, 7) | `SaleService`, `SaleRegistrationService` |

## Single Responsibility Principle (SRP)

**Verdict:** Pending — audit in progress.

- **SaleRepository and SaleRecord — Correct.** The repository only reads and writes `data/sales.csv`; the record only carries stored values.

- **SaleService and SaleRegistrationService — Correct.** Since the GRASP correction (V-12 of Requerimiento 7), `SaleService` keeps the sales history (load, record, queries) and `SaleRegistrationService` owns the registration use case. Each has one reason to change. This is a SOLID problem the GRASP correction had already solved.

### V-01

| Field | Content |
|---|---|
| ID | V-01 |
| Principle | SRP |
| Module | Sales (Taller 1) and promotion breakdown (Requerimiento 2) |
| Evidence | `Sale.generateReceipt()` (`Sale.java:187`) builds the Spanish receipt: borders, labels (`Cliente:`, `Descuento (...)`, `Total final:`), money format and the subtotal/discount breakdown. |
| Explanation | `Sale` changes for sale rules (totals, promotions, returns) and for the layout of the receipt the user reads: two reasons to change. |
| Consequence | Every layout change touches the domain class; the receipt logic (including the out-of-scope subtotal defect OOS-02) is mixed with the totals logic. |
| Proposed solution | Move the layout into `SaleReceipt` (model), which reads the sale through its getters; keep `generateReceipt()` as a delegation because it is part of the Taller 1 API. |
| Other role's code involved | No. |

## Open/Closed Principle (OCP)

**Verdict:** Pending — audit in progress.

- **Sale — Correct.** Promotions and surcharges reach the total through `applyPromotion` and `addCharge`, which work for any promotion or warranty type.

- **SaleRepository — Correct.** It has no type switch; sale items are stored as ids.

- **SaleRegistrationService — Correct.** Warranty eligibility is asked to each product (`isWarrantyEligible`) and the stock to `ItemCatalog`, so new product types do not modify the use case.

## Liskov Substitution Principle (LSP)

**Verdict:** Pending — audit in progress.

- **Sale — Not applicable.** `Sale` has no subclasses.

- **Sales services — Not applicable.** No inheritance in scope.

## Interface Segregation Principle (ISP)

**Verdict:** Pending — audit in progress.

### V-02

| Field | Content |
|---|---|
| ID | V-02 |
| Principle | ISP |
| Module | Sales services (Taller 1, Requerimiento 7) |
| Evidence | `SaleService` (`SaleService.java:22`) and `SaleRegistrationService` (`SaleRegistrationService.java:25`) hold the whole `PersonService` but only call `findCustomerById` and `findSellerById` (`SaleService.java:48-49`, `SaleRegistrationService.java:72`, `:76`). |
| Explanation | Both clients depend on customer registration and the listings, which they never use. |
| Consequence | Any change in how people are registered or listed affects the sales services. |
| Proposed solution | Introduce a `PersonLookup` (`findCustomerById`, `findSellerById`) and depend on it. |
| Other role's code involved | Yes — `PersonService` (Developer 2) must implement `PersonLookup`. |

### V-03

| Field | Content |
|---|---|
| ID | V-03 |
| Principle | ISP |
| Module | Sales services (Requerimiento 7) |
| Evidence | `SaleService` holds the whole `ItemCatalog` (`SaleService.java:23`) but only calls `findItem` (`:55`). |
| Explanation | The client depends on stock operations it never uses. |
| Consequence | Changes in stock handling affect the loading of the sales history. |
| Proposed solution | Depend on a lookup-only abstraction (`ItemLookup`). |
| Other role's code involved | Yes — `ItemCatalog` is Developer 2's code; reported for consolidation (same as Developer 2's V-05). |

## Dependency Inversion Principle (DIP)

**Verdict:** Pending — audit in progress.

### V-04

| Field | Content |
|---|---|
| ID | V-04 |
| Principle | DIP |
| Module | Sales (Taller 1) |
| Evidence | `SaleService` field `private final SaleRepository repository` (`SaleService.java:21`) and its constructor parameter. |
| Explanation | The sales history (policy) depends on the concrete CSV repository (detail). |
| Consequence | Changing storage requires changing `SaleService`. |
| Proposed solution | Introduce `SaleStore` (`saveAll`, `loadAll`) in the persistence package, implemented by `SaleRepository`. |
| Other role's code involved | No. |

### V-05

| Field | Content |
|---|---|
| ID | V-05 |
| Principle | DIP (also ISP) |
| Module | Sale registration (Requerimientos 2, 4, 7) |
| Evidence | `SaleRegistrationService` fields `PromotionService promotionService` and `WarrantyService warrantyService` (`SaleRegistrationService.java:27-28`); it only calls `findBestPromotionFor` (`:94`), `assignBasicWarranty` (`:101`) and `assignExtendedWarranty` (`:105`). |
| Explanation | The registration use case is the highest-level policy of the system, yet it depends on two concrete services and on all of their operations. |
| Consequence | Any change in the promotion or warranty services (registration, listings, persistence) can affect sale registration; the use case cannot be reused with another promotion or warranty policy. |
| Proposed solution | Define, as part of the use case, the abstractions it needs — `PromotionSelector` (`findBestPromotionFor`) and `WarrantyAssigner` (`assignBasicWarranty`, `assignExtendedWarranty`) — and let the services implement them. |
| Other role's code involved | Yes — `PromotionService` and `WarrantyService` (Developer 2) must implement them. |
