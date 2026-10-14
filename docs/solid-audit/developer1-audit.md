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

## Single Responsibility Principle (SRP)

**Verdict:** Pending — audit in progress.

- **Products — Correct.** `Product` and its subclasses hold product data and their description; `ProductRepository` only reads and writes `data/products.csv`; `ProductService` only applies inventory rules.

## Open/Closed Principle (OCP)

**Verdict:** Pending — audit in progress.

- **Products — Correct for the hierarchy, accepted for the repository.** A new product type is a new subclass of `Product`, implementing `getDescription` and `belongsToCategory`, with no change to existing classes. `ProductRepository.toCsvLine/fromCsvLine` branch on the type and would change for a new type. This is **accepted, not counted**: a new product type necessarily brings new CSV columns, so the format changes anyway; the knowledge sits in one class whose single purpose is that format; and the GRASP audit judged it a correct Pure Fabrication.

## Liskov Substitution Principle (LSP)

**Verdict:** Pending — audit in progress.

- **Products — Correct.** `VideoGame` and `Console` implement `getDescription` and `belongsToCategory` without narrowing preconditions or throwing; `Console.isWarrantyEligible` only changes a value the base declares as overridable.

## Interface Segregation Principle (ISP)

**Verdict:** Pending — audit in progress.

- **Products — Not applicable.** `ProductService` depends only on `ProductRepository`, and uses all of its methods.

## Dependency Inversion Principle (DIP)

**Verdict:** Pending — audit in progress.

- **Products — Violation (V-02).** See the full analysis below.
