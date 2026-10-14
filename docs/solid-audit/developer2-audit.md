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
| GRASP correction (Requerimiento 7) | `IdGenerator`, `ItemCatalog`, `ReturnRecord`, `WarrantyRecord`, reference resolution in `ReturnService` and `WarrantyService` |
| Exceptions and validation (Requerimiento 6) | Not present in the reference repository — nothing to audit |

## Single Responsibility Principle (SRP)

**Verdict:** Violation — see V-01.

- **Persons — Correct.** `PersonRepository` only handles the two CSV files; `PersonService` only registers and looks up people; `Customer` and `Seller` only hold their data.

- **Accessories — Correct.** `AccessoryRepository` handles the CSV file; `AccessoryService` handles accessory rules.

- **Promotions — Correct.** `PromotionService` holds registration, validation of dates and percentages, and selection of the best promotion — all promotion rules.

- **Warranties — Correct.** `WarrantyService` assigns, resolves and queries warranties; `WarrantyRepository` handles the file.

- **GRASP additions — Correct.** `IdGenerator` only generates ids; `ItemCatalog` only finds items and moves their stock; the record classes only carry stored values.

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

**Verdict:** Violation — see V-02, V-03.

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

### V-03

| Field | Content |
|---|---|
| ID | V-03 |
| Principle | OCP |
| Module | GRASP correction (Requerimiento 7) |
| Evidence | `ItemCatalog.decreaseStock` and `restoreStock` (`ItemCatalog.java:53`, `:67`): `if (item instanceof Accessory) { accessoryService... } else { productService... }`; `ItemCatalog` stores `ProductService` and `AccessoryService` as concrete fields. |
| Explanation | The catalog chooses where an item's stock lives by checking its concrete type. This was kept on purpose as a GRASP trade-off, but OCP asks that a new kind of sellable item be added without modifying existing classes. |
| Consequence | A third catalog (for example, merchandise) requires editing `ItemCatalog` in three places. |
| Proposed solution | Introduce an `ItemSource` abstraction (find an item, decrease and restore its stock) implemented by each catalog service; `ItemCatalog` receives its sources and asks the one that owns the item. |
| Other role's code involved | Yes — `ProductService` (Developer 1) must implement `ItemSource`. |

## Liskov Substitution Principle (LSP)

**Verdict:** Correct — no subclass or substitution in scope changes the expected behavior.

- **Persons — Correct.** `Customer` and `Seller` keep every behavior of `Person` (`getFullName`, getters and setters) unchanged.

- **Warranties — Correct.** Both services and repositories in scope have no subclasses; the warranties they build are used only through the `Warranty` contract.

- **GRASP additions — Correct.** No hierarchy was introduced.

## Interface Segregation Principle (ISP)

**Verdict:** Violation — see V-04, V-05.

- **Persons — Not applicable as a client.** `PersonService` depends only on `PersonRepository` and uses all of it. (How other roles use `PersonService` is audited in their own code.)

### V-04

| Field | Content |
|---|---|
| ID | V-04 |
| Principle | ISP |
| Module | Returns and Warranties (Requerimientos 3, 4, 7) |
| Evidence | `ReturnService` field `SaleService saleService` (`ReturnService.java:21`) uses only `findById` and `viewAllSales`; `WarrantyService(WarrantyRepository, SaleService, ItemCatalog)` uses only `saleService.findById` (`WarrantyService.java:55`). |
| Explanation | Both clients depend on the whole `SaleService` — recording sales and three history queries included — although they only read sales. |
| Consequence | Any change to `SaleService`'s writing or query API affects these clients; they cannot be built over a simpler source of sales. |
| Proposed solution | Introduce a read-only `SaleLookup` (`findById`, `viewAllSales`) and make both clients depend on it. |
| Other role's code involved | Yes — `SaleService` (Technical Lead) must implement `SaleLookup`. |

### V-05

| Field | Content |
|---|---|
| ID | V-05 |
| Principle | ISP |
| Module | Warranties (Requerimientos 4, 7) |
| Evidence | `WarrantyService` receives the whole `ItemCatalog` but only calls `findItem` (`WarrantyService.java:49`). |
| Explanation | The client depends on `decreaseStock` and `restoreStock`, which it never uses. |
| Consequence | Changes in stock handling affect a client that only needs lookups. |
| Proposed solution | Introduce `ItemLookup` (`findItem`) implemented by `ItemCatalog`, and depend on it. |
| Other role's code involved | No for the fix in `WarrantyService`; the same problem exists in the Technical Lead's `SaleService` (reported for consolidation). |

## Dependency Inversion Principle (DIP)

**Verdict:** Violation — see V-06.

- **Accepted, not counted: static `IdGenerator.next`.** Services call a static utility instead of an abstraction. It is stateless, no alternative implementation is anticipated and no test needs to substitute it, so the coupling is accepted.

### V-06

| Field | Content |
|---|---|
| ID | V-06 |
| Principle | DIP |
| Module | Persons, Accessories, Promotions, Returns, Warranties |
| Evidence | `PersonService`, `AccessoryService`, `PromotionService`, `ReturnService` and `WarrantyService` hold `private final <Concrete>Repository repository` and take the concrete repository in their constructors (`PersonService.java:17`, `AccessoryService.java:15`, `PromotionService.java:17`, `ReturnService.java:20`, `WarrantyService.java:24`). |
| Explanation | Business services (high level) depend on concrete CSV repositories (low level) instead of on abstractions of their storage. |
| Consequence | Changing storage requires changing every service; services cannot be built over another storage. |
| Proposed solution | One store abstraction per repository (`PersonStore`, `AccessoryStore`, `PromotionStore`, `ReturnStore`, `WarrantyStore`) in the persistence package, implemented by the repositories; services depend on them. |
| Other role's code involved | No. |

## Summary of verdicts

| Principle | Verdict | Violations |
|---|---|---|
| Single Responsibility Principle (SRP) | Violation | V-01 |
| Open/Closed Principle (OCP) | Violation | V-02, V-03 |
| Liskov Substitution Principle (LSP) | Correct | — |
| Interface Segregation Principle (ISP) | Violation | V-04, V-05 |
| Dependency Inversion Principle (DIP) | Violation | V-06 |

## Notes

- **Examined and accepted (not counted):** type switches in `AccessoryRepository` and `PromotionRepository` and the register-per-type APIs (OCP); the static `IdGenerator` (DIP). Justifications in the corresponding sections.
- **Relation with GRASP:** V-02 was introduced by the GRASP correction (resolution moved from `WarrantyRepository` to `WarrantyService`, leaving the type tags in both); V-03 is the `instanceof` that the GRASP correction kept as a documented trade-off.
- **Reported to the Technical Lead:** V-04 needs `SaleService` to implement the new interface; V-05 has a counterpart in `SaleService`; V-01 needs the menu and `Main` to call the new service.
