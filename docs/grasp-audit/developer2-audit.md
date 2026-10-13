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
| Accessories (Requerimiento 1) | `AccessoryRepository`, `AccessoryService` |
| Promotions (Requerimiento 2) | `PromotionRepository`, `PromotionService` |

## Information Expert

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `Person.getFullName()` builds the name from its own fields; `Customer.addPurchase(Sale)` updates the history it owns.

- **Accessories — Correct.** `AccessoryService.findAccessoriesCompatibleWith` (`AccessoryService.java:62`) asks each accessory `isCompatibleWith(consoleId)` instead of reading its list.

- **Promotions — Correct.** `PromotionService.listActivePromotions` asks each promotion `isActive(today)`; `findBestPromotionFor` (`PromotionService.java:76`) asks each promotion `calculateDiscount(sale)` and only compares the results.

## Creator

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonService.registerCustomer` (`PersonService.java:42`) creates the `Customer` it adds to the list it aggregates; `PersonRepository` creates customers and sellers from the records it reads.

- **Accessories — Correct.** `AccessoryService.registerController/registerCable/registerMemory` (`AccessoryService.java:23-42`) create the accessories they aggregate.

- **Promotions — Correct.** `PromotionService.registerPercentageDiscount/CategoryDiscount/BulkPurchaseDiscount` create the promotions they aggregate.

## Controller

**Verdict:** Pending — audit in progress.

- **Persons — Not applicable.** No class in scope receives menu events; `ConsoleMenu` (Technical Lead) does.

## Low Coupling

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonRepository` depends only on the model; `PersonService` depends only on `PersonRepository` and the model.

## High Cohesion

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonRepository` only reads and writes the two CSV files; `PersonService` only registers and looks up people.

- **Accessories — Correct.** `AccessoryRepository` only handles `data/accessories.csv`; `AccessoryService` only handles accessory rules.

- **Promotions — Correct.** `PromotionService` holds registration, validation of percentage and dates, and selection of the best promotion — all promotion rules.

## Polymorphism

**Verdict:** Pending — audit in progress.

- **Accessories — Mixed.** `AccessoryRepository.toCsvLine`/`fromCsvLine` branch on the concrete accessory type; judged **correct** for the same reason as the other repositories (the repository is a Pure Fabrication that keeps the CSV format out of the model). `AccessoryService.listAccessoriesByType` is a violation (V-01).

- **Promotions — Correct.** `findBestPromotionFor` relies on the polymorphic `calculateDiscount` and never asks for the promotion's type; `PromotionRepository` serialization judged correct (Pure Fabrication).

### V-01

| Field | Content |
|---|---|
| ID | V-01 |
| Pattern | Polymorphism (also recorded under Protected Variations) |
| Module | Accessories (Requerimiento 1) |
| Evidence | `AccessoryService.listAccessoriesByType(String)`, `AccessoryService.java:48-60`: `if ("CONTROLLER".equals(type) && accessory instanceof Controller) ... else if ("CABLE".equals(type) && accessory instanceof Cable) ... else if ("MEMORY".equals(type) && accessory instanceof Memory)` |
| Explanation | The service asks each accessory for its concrete class and maps a type string to it. The type of an accessory is something each accessory knows about itself. |
| Consequence | A new accessory subclass is never listed by type until this method is edited; the service is coupled to the three concrete subclasses. |
| Proposed solution | Let each accessory expose its own type (`getAccessoryType()`, implemented by each subclass) and filter with `type.equals(accessory.getAccessoryType())`. |
| Other role's code involved | Yes — the accessory hierarchy is Developer 1's code; Developer 1 must add `getAccessoryType()`. |

## Pure Fabrication

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonRepository` is the fabrication that keeps file access out of `Customer` and `Seller`.

## Indirection

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** `PersonService` mediates between the UI and `PersonRepository`.

## Protected Variations

**Verdict:** Pending — audit in progress.

- **Persons — Correct.** A change of file format is confined to `PersonRepository`.

- **Accessories — Violation (V-01).** The accessory type vocabulary (`"CONTROLLER"`, `"CABLE"`, `"MEMORY"`) and its mapping to classes is hard-coded in the service. Full analysis under Polymorphism.
