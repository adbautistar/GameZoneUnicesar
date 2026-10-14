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
| Accessories (Requerimiento 1) | `AccessoryRepository`, `AccessoryService` |
| Promotions (Requerimiento 2) | `PromotionRepository`, `PromotionService` |
| Returns (Requerimiento 3) | `ReturnRepository`, `ReturnService` (including `generateMonthlyBalance`) |
| Warranties (Requerimiento 4) | `WarrantyRepository`, `WarrantyService` |

## Single Responsibility Principle (SRP)

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonRepository` only handles the two CSV files; `PersonService` only registers and looks up people; `Customer` and `Seller` only hold their data.

- **Accessories — Correct.** `AccessoryRepository` handles the CSV file; `AccessoryService` handles accessory rules.

- **Promotions — Correct.** `PromotionService` holds registration, validation of dates and percentages, and selection of the best promotion — all promotion rules.

- **Warranties — Correct.** `WarrantyService` assigns, resolves and queries warranties; `WarrantyRepository` handles the file.

### V-01

| Field | Content |
|---|---|
| ID | V-01 |
| Principle | SRP |
| Module | Returns (Requerimiento 3) |
| Evidence | `ReturnService.generateMonthlyBalance(int, int)` (`ReturnService.java:156`) sums every sale of the month through `saleService.viewAllSales()` and subtracts the refunds, inside the class that registers and queries returns. |
| Explanation | Return handling and financial reporting are different responsibilities with different reasons to change (return rules versus how the business wants its balance computed). |
| Consequence | A change in the balance (for example, excluding a kind of sale) forces a change in `ReturnService`; the service depends on the whole sales history only for the report. |
| Proposed solution | Extract the report into `MonthlyBalanceService`, which reads sales and returns; `ReturnService` keeps registration and queries. |
| Other role's code involved | Yes — `ReturnMenu` and `Main` (Technical Lead) must call the new service. |

## Open/Closed Principle (OCP)

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonService` has no type switch; customers and sellers are handled through their own methods.

- **Accessories — Accepted, not counted.** `AccessoryRepository` branches on the accessory type to write and read the file, and `AccessoryService` has one register method per type. A new accessory type necessarily brings new CSV columns and new input fields, so both change anyway; the knowledge is localized in one class each. `listAccessoriesByType` became type-agnostic in the GRASP correction (V-02 of Requerimiento 7).

- **Promotions — Correct for selection, accepted for registration.** `findBestPromotionFor` works for any `Promotion` subclass. The type switch in `PromotionRepository` and the one-register-method-per-type API are accepted for the same reason as accessories.

- **Returns — Correct.** Return registration does not depend on the type of the returned items since the GRASP correction (it delegates stock to `ItemCatalog`).

### V-02

| Field | Content |
|---|---|
| ID | V-02 |
| Principle | OCP |
| Module | Warranties (Requerimiento 4), changed in the GRASP correction |
| Evidence | `WarrantyRepository.toCsvLine` (`WarrantyRepository.java:67`): `warranty instanceof ExtendedWarranty ? EXTENDED_TYPE : BASIC_TYPE`; `WarrantyService.resolve` (`WarrantyService.java:61-66`): `if (BASIC_TYPE.equals(record.getType())) ... if (EXTENDED_TYPE.equals(record.getType()))`; the constants `"BASIC"`/`"EXTENDED"` are declared in both classes. |
| Explanation | The set of warranty kinds is known in two classes. Since the GRASP correction moved the resolution into the service, the repository writes the tag and the service reads it. |
| Consequence | A new warranty kind requires modifying both `WarrantyRepository` and `WarrantyService` and keeping their constants in sync; forgetting one corrupts the file or fails on load. |
| Proposed solution | Let one abstraction know every kind and how to build it (a `WarrantyKind` in the model, exposed by `Warranty.getKind()`); the repository writes `getKind()` and the service builds through the kind. |
| Other role's code involved | Yes — the warranty hierarchy is Developer 1's code. |

## Liskov Substitution Principle (LSP)

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `Customer` and `Seller` keep every behavior of `Person` (`getFullName`, getters and setters) unchanged.

- **Warranties — Correct.** Both services and repositories in scope have no subclasses; the warranties they build are used only through the `Warranty` contract.

## Interface Segregation Principle (ISP)

**Verdict:** Pending — audit in progress.

- **Persons — Not applicable as a client.** `PersonService` depends only on `PersonRepository` and uses all of it. (How other roles use `PersonService` is audited in their own code.)

## Dependency Inversion Principle (DIP)

**Verdict:** Pending — audit in progress.

_No module audited yet contributes to this pattern._
