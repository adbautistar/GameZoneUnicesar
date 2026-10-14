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

## Single Responsibility Principle (SRP)

**Verdict:** Pending — audit in progress.

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

## Liskov Substitution Principle (LSP)

**Verdict:** Pending — audit in progress.

- **Sale — Not applicable.** `Sale` has no subclasses.

## Interface Segregation Principle (ISP)

**Verdict:** Pending — audit in progress.

_No module audited yet contributes to this pattern._

## Dependency Inversion Principle (DIP)

**Verdict:** Pending — audit in progress.

_No module audited yet contributes to this pattern._
