# SOLID Audit — Developer 2

| Item | Value |
|---|---|
| Role | Developer 2 (simulated) |
| Requirement | Requerimiento 8 — SOLID audit and correction, Phase 1 (audit) |
| Audited commit | `dd695ce` (`develop`, same tree as tag `v1.5.0`) |
| Date | 2026-10-14 |
| Scope rule | Only the code written by Developer 2, including what Developer 2 created or changed in the GRASP correction (`docs/grasp-refactor-log.md`) |

Verdicts: **Violation** (the code breaks the principle at one or more points), **Correct** (the code follows the principle), **Not applicable** (the audited code has no point where the principle applies).

## Scope

| Module (requirement) | Classes and methods audited |
|---|---|
| Persons (Taller 1) | `Person`, `Customer`, `Seller`, `PersonRepository`, `PersonService` |

## Single Responsibility Principle (SRP)

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonRepository` only handles the two CSV files; `PersonService` only registers and looks up people; `Customer` and `Seller` only hold their data.

## Open/Closed Principle (OCP)

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonService` has no type switch; customers and sellers are handled through their own methods.

## Liskov Substitution Principle (LSP)

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `Customer` and `Seller` keep every behavior of `Person` (`getFullName`, getters and setters) unchanged.

## Interface Segregation Principle (ISP)

**Verdict:** Pending — audit in progress.

- **Persons — Not applicable as a client.** `PersonService` depends only on `PersonRepository` and uses all of it. (How other roles use `PersonService` is audited in their own code.)

## Dependency Inversion Principle (DIP)

**Verdict:** Pending — audit in progress.

_No module audited yet contributes to this pattern._
