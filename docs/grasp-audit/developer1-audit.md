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

## Information Expert

**Verdict:** Pending — audit in progress.

- **Products — Correct.** `Product.updateStock(int)` (`Product.java:108`) changes the stock it owns; `ProductService.updateStock` (`ProductService.java:84`) locates the product and delegates the change to it instead of computing the new value itself. `VideoGame.getDescription` and `Console.getDescription` build the description from their own fields.

## Creator

**Verdict:** Pending — audit in progress.

- **Products — Correct.** `ProductService.registerVideoGame` and `registerConsole` (`ProductService.java:43`, `:61`) create the products they add to the inventory list they aggregate. `ProductRepository.fromCsvLine` (`ProductRepository.java:116`) creates products from the records it reads, so it holds the initializing data.

## Controller

**Verdict:** Pending — audit in progress.

- **Products — Not applicable.** None of these classes receives a system event; menu events are received by `ConsoleMenu` (Technical Lead).

## Low Coupling

**Verdict:** Pending — audit in progress.

- **Products — Correct.** `Product`, `VideoGame` and `Console` import nothing outside the model. `ProductRepository` depends only on the model; `ProductService` depends only on `ProductRepository` and the model.

## High Cohesion

**Verdict:** Pending — audit in progress.

- **Products — Correct.** `ProductRepository` only reads and writes `data/products.csv`; `ProductService` only applies inventory rules; the product classes only hold product data and behavior.

## Polymorphism

**Verdict:** Pending — audit in progress.

- **Products — Correct.** `Product.getDescription()` is abstract and `VideoGame`/`Console` override it. `ProductRepository.toCsvLine`/`fromCsvLine` (`ProductRepository.java:90-126`) branch on the concrete type and on a type tag; this is **not** treated as a violation: the repository is a Pure Fabrication whose job is to keep the CSV format out of the model, and moving serialization into the hierarchy would put persistence knowledge inside the domain classes, which Taller 1 forbids.

## Pure Fabrication

**Verdict:** Pending — audit in progress.

- **Products — Correct.** `ProductRepository` is an artificial class that owns file access so that `Product` stays free of I/O.

## Indirection

**Verdict:** Pending — audit in progress.

- **Products — Correct.** `ProductService` sits between `ConsoleMenu` and `ProductRepository`; the UI never touches the repository.

## Protected Variations

**Verdict:** Pending — audit in progress.

- **Products — Correct.** A change of storage format is confined to `ProductRepository`; `ProductService` and the model are protected from it.
