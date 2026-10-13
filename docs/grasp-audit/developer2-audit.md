# GRASP Audit — Developer 2

| Item | Value |
|---|---|
| Role | Developer 2 (simulated) |
| Requirement | Requerimiento 7 — GRASP audit and correction, Phase 1 (audit) |
| Audited commit | `8ca31bf` (`develop`, tag `v1.4.1`) |
| Date | 2026-10-13 |
| Scope rule | Only the code written by Developer 2, per the responsibility table of Requerimiento 7 |

Verdicts: **Violation** (the code breaks the pattern at one or more points), **Correct** (the pattern is applied correctly), **Not applicable** (the audited code has no point where the pattern applies).

## Scope

| Module (requirement) | Classes and methods audited |
|---|---|
| Persons (Taller 1) | `Person`, `Customer`, `Seller`, `PersonRepository`, `PersonService` |

## Information Expert

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `Person.getFullName()` builds the name from its own fields; `Customer.addPurchase(Sale)` updates the history it owns.

## Creator

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonService.registerCustomer` (`PersonService.java:42`) creates the `Customer` it adds to the list it aggregates; `PersonRepository` creates customers and sellers from the records it reads.

## Controller

**Verdict:** Pending — audit in progress.

- **Persons — Not applicable.** No class in scope receives menu events; `ConsoleMenu` (Technical Lead) does.

## Low Coupling

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonRepository` depends only on the model; `PersonService` depends only on `PersonRepository` and the model.

## High Cohesion

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonRepository` only reads and writes the two CSV files; `PersonService` only registers and looks up people.

## Polymorphism

**Verdict:** Pending — audit in progress.

_No module audited yet contributes to this pattern._

## Pure Fabrication

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonRepository` is the fabrication that keeps file access out of `Customer` and `Seller`.

## Indirection

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonService` mediates between the UI and `PersonRepository`.

## Protected Variations

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** A change of file format is confined to `PersonRepository`.
