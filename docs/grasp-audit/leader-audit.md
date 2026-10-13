# GRASP Audit — Technical Lead

| Item | Value |
|---|---|
| Role | Technical Lead |
| Requirement | Requerimiento 7 — GRASP audit and correction, Phase 1 (audit) |
| Audited commit | `8ca31bf` (`develop`, tag `v1.4.1`) |
| Date | 2026-10-13 |
| Scope rule | Only the code written by the Technical Lead, per the responsibility table of Requerimiento 7 |

Verdicts: **Violation** (the code breaks the pattern at one or more points), **Correct** (the pattern is applied correctly), **Not applicable** (the audited code has no point where the pattern applies).

## Scope

| Module (requirement) | Classes and methods audited |
|---|---|
| Sales (Taller 1) and promotion changes (Requerimiento 2) | `Sale` (including `applied promotion` fields and the receipt breakdown; excluding `canBeReturned`, owned by Developer 1) |
| Sales (Taller 1) | `SaleRepository` |

## Information Expert

**Verdict:** Pending — audit in progress.

- **Sale — Correct.** `Sale.calculateTotal()` (`Sale.java:148`) sums the prices of the products it holds; `generateReceipt()` (`Sale.java:163`) formats only its own data. The problem is not in `Sale` but in the classes that adjust its total from outside (V-03).

## Creator

**Verdict:** Pending — audit in progress.

- **Sale — Not applicable.** `Sale` creates no other domain object.

- **SaleRepository — Correct.** `fromCsvLine` creates each `Sale` from the record it reads, so it holds the initializing data.

## Controller

**Verdict:** Pending — audit in progress.

_No module audited yet contributes to this pattern._

## Low Coupling

**Verdict:** Pending — audit in progress.

- **Sale — Correct.** `Sale` depends only on `Customer`, `Seller` and `Product`.

### V-01

| Field | Content |
|---|---|
| ID | V-01 |
| Pattern | Low Coupling |
| Module | Sales (Taller 1) |
| Evidence | `SaleRepository` imports and stores `ProductService`, `PersonService` and `AccessoryService` (`SaleRepository.java:7-9`, constructors) and calls them in `fromCsvLine` (`:140-155`). |
| Explanation | A persistence class depends on the service layer, the reverse of the allowed direction. This was recorded as a known layering exception in Taller 1; the audit classifies it as a Low Coupling violation. |
| Consequence | The repository cannot be built or tested without three services; any change in their lookup methods breaks persistence; `Main` must build the services before the repository. |
| Proposed solution | Persist and load only references (a `SaleRecord` with ids); `SaleService` resolves them when it loads. |
| Other role's code involved | No. |

## High Cohesion

**Verdict:** Pending — audit in progress.

- **Sale — Correct.** `Sale` holds sale data, its total and its receipt.

### V-02

| Field | Content |
|---|---|
| ID | V-02 |
| Pattern | High Cohesion |
| Module | Sales (Taller 1) |
| Evidence | `SaleRepository.fromCsvLine`, `SaleRepository.java:166`: `customer.addPurchase(sale);` executed while parsing a CSV line. |
| Explanation | Reading a file and rebuilding the customers' purchase history are different responsibilities; the repository changes domain state as a side effect of loading. |
| Consequence | Anyone who calls `loadAll()` twice duplicates purchase histories; the rule "a recorded sale belongs to the customer's history" lives in two places (here and `SaleService.registerSale`). |
| Proposed solution | Let `SaleService` record the purchase history when it rebuilds sales and when it registers a new one. |
| Other role's code involved | No. |

## Polymorphism

**Verdict:** Pending — audit in progress.

_No module audited yet contributes to this pattern._

## Pure Fabrication

**Verdict:** Pending — audit in progress.

- **SaleRepository — Correct as a fabrication.** File access is kept out of `Sale`; the problems of this class are its dependencies and its extra responsibilities (V-01, V-02), not its existence.

## Indirection

**Verdict:** Pending — audit in progress.

_No module audited yet contributes to this pattern._

## Protected Variations

**Verdict:** Pending — audit in progress.

_No module audited yet contributes to this pattern._
