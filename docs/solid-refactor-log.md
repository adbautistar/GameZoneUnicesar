# SOLID Refactor Log — Requerimiento 8

Change log of Phase 2 (correction). Every violation of `docs/solid-audit/consolidated-audit.md` must appear here at least once, or in the out-of-scope section of the consolidated audit with its justification.

## Non-regression strategy

Defined before the first correction, as answered in `docs/solid-analysis.md` (Q4). It reuses the two checks of Requerimiento 7, re-baselined on the code about to change.

**Check 1 — automated suite.** `mvn clean verify` (JUnit 5 + AssertJ + Mockito, JaCoCo line-coverage check of 70% on `com.gamezone.service`). Every baseline test must still exist and pass; tests may be added, never removed. When a correction moves a behavior to a new collaborator, its tests move with it and keep asserting the same results, and the new class gets a test with real objects.

**Check 2 — scripted menu run.** The compiled application runs from a scratch working directory with its own fixture `data/` folder and a fixed input script that exercises every menu operation of every module (the script and fixtures are described in `docs/grasp-refactor-log.md`). Standard output, standard error, exit code and the final `data/*.csv` files are captured; millisecond identifiers and dates derived from today are normalized; the result is compared line by line with the baseline. Expected result: no differences. Out-of-scope defects OOS-01..03 are part of the expected output.

## Baseline results (develop `123ba66`, before the first correction)

| Check | Result |
|---|---|
| `mvn clean verify` | BUILD SUCCESS — 93 tests, 0 failures, 0 errors; JaCoCo check passed |
| Scripted menu run | Exit code 0; 1,051 normalized lines; two consecutive runs identical; SHA-256 prefix `d404c06d889e6046` (same as the final result of Requerimiento 7) |

## Change log

| Class | Change | Violation ID | Principle applied | Commit / PR | Owner |
|---|---|---|---|---|---|
| `ProductStore` (new), `ProductRepository`, `ProductService` | New store abstraction (`saveAll`, `loadAll`) in the persistence package; `ProductRepository` implements it; `ProductService` depends on it. Tests unchanged (a mocked `ProductRepository` is a `ProductStore`) | V-10 | DIP | `2c35c84` / PR #34 | Developer 1 |
| `ReturnReceipt` (new), `Return` | Receipt layout moved verbatim into `ReturnReceipt.render()`; `generateReturnReceipt()` delegates (kept as domain API of Requerimiento 3). Done in two commits because the first one only added the class | V-01 | SRP | `88b44c4`, `81b6435` / PR #34 | Developer 1 |
| `WarrantyCertificate` (new), `Warranty` | Certificate layout moved verbatim into `WarrantyCertificate.render()`; `generateWarrantyCertificate()` delegates | V-01 | SRP | `069eefa` / PR #34 | Developer 1 |
| `WarrantyKind` (new), `Warranty`, `BasicWarranty`, `ExtendedWarranty` | Enum listing the warranty kinds, each able to build its warranty; abstract `Warranty.getKind()` implemented by both subclasses. Additive — Developer 2 uses it next | V-05 (model part) | OCP | `7a37100` / PR #34 | Developer 1 |
| `PersonStore`, `AccessoryStore`, `PromotionStore`, `ReturnStore`, `WarrantyStore` (new) + repositories + services | One store abstraction per repository (persistence package); repositories implement them; the five services depend on them. Tests unchanged | V-11 | DIP | `371058f`, `2bbdbcc`, `e041a00`, `ee56dba`, `753f70d` / PR #35 | Developer 2 |
| `ItemLookup` (new), `ItemCatalog`, `WarrantyService` | Lookup-only interface implemented by `ItemCatalog`; `WarrantyService` depends on it | V-09 (Developer 2 part) | ISP | `846dd90` / PR #35 | Developer 2 |
| `ItemSource` (new), `AccessoryService` | Item-source abstraction (`findItem`, `decreaseStock`, `restoreStock`) implemented by `AccessoryService` (additive) | V-04 | OCP | `6122f0d` / PR #35 | Developer 2 |
| `ProductService` | **Coordination commit:** implements `ItemSource` (`decreaseStock` → `updateStock(id, -1)`, `restoreStock(id)` → `restoreStock(id, 1)`) | V-04 | OCP | `16a9fdf` / PR #35 | Developer 1 |
| `ItemCatalog`, `ReturnServiceTest`, `ItemCatalogTest` (new) | `ItemCatalog(ItemSource...)` asks its sources in order and lets the owning source move the stock — no `instanceof` left in the service layer. The two stock tests of `ReturnServiceTest` now stub `findItem` on the owning mock and verify `restoreStock(id)` on it (same behavior: the owning catalog restores one unit); `ItemCatalogTest` checks real stock changes for a product and an accessory (93 → 96 tests) | V-04 | OCP | `1e26194` / PR #35 | Developer 2 |
| `SaleLookup` (new) | Read-only view of sales (`findById`, `viewAllSales`) | V-07 | ISP | `bc10283` / PR #35 | Developer 2 |
| `SaleService` | **Coordination commit:** implements `SaleLookup` | V-07 | ISP | `80b200a` / PR #35 | Technical Lead |
| `ReturnService`, `WarrantyService` | Depend on `SaleLookup` instead of the whole `SaleService` | V-07 | ISP | `6a474d8` / PR #35 | Developer 2 |
| `WarrantyRepository`, `WarrantyService` | The repository writes `warranty.getKind().name()`; the service builds through `WarrantyKind` (unknown tag → same exception message, after the same product and sale checks). The `"BASIC"`/`"EXTENDED"` constants are gone from both classes | V-05 | OCP | `7978e6f` / PR #35 | Developer 2 |
| `MonthlyBalanceService` (new) | Monthly balance (same computation) over `SaleLookup` and `ReturnService.viewAllReturns()` | V-03 | SRP | `9336a26` / PR #35 | Developer 2 |
| `ReturnMenu`, `ConsoleMenu`, `Main` | **Coordination commit:** the menu asks `MonthlyBalanceService` for the balance; wiring updated | V-03 | SRP | `c836dda` / PR #35 | Technical Lead |
| `ReturnService`, `ReturnServiceTest`, `MonthlyBalanceServiceTest` (new), `EndToEndScenariosTest` | `generateMonthlyBalance` removed from `ReturnService`; its two tests moved verbatim to `MonthlyBalanceServiceTest`; end-to-end scenario 5 asks `MonthlyBalanceService` | V-03 | SRP | `9937ff4` / PR #35 | Developer 2 |
| `SaleReceipt` (new), `Sale` | Receipt layout (with the promotion breakdown, OOS-02 preserved) moved verbatim into `SaleReceipt.render()`; `generateReceipt()` delegates (Taller 1 API) | V-02 | SRP | `15326b2` / PR #36 | Technical Lead |
| `SaleStore` (new), `SaleRepository`, `SaleService` | Store abstraction implemented by `SaleRepository`; `SaleService` depends on it | V-12 | DIP | `d40fc1c` / PR #36 | Technical Lead |
| `PersonLookup` (new) | Lookup of customers and sellers by id | V-08 | ISP | `cfe279a` / PR #36 | Technical Lead |
| `PersonService` | **Coordination commit:** implements `PersonLookup` | V-08 | ISP | `b286423` / PR #36 | Developer 2 |
| `SaleService`, `SaleRegistrationService` | Depend on `PersonLookup` instead of the whole `PersonService` | V-08 | ISP | `58f1b91` / PR #36 | Technical Lead |
| `SaleService` | Depends on `ItemLookup` instead of the whole `ItemCatalog` | V-09 (Technical Lead part) | ISP | `ed8c23c` / PR #36 | Technical Lead |
| `PromotionSelector`, `WarrantyAssigner` (new) | Abstractions owned by the sale registration use case | V-13 | DIP, ISP | `8aa330d` / PR #36 | Technical Lead |
| `PromotionService`, `WarrantyService` | **Coordination commit:** implement `PromotionSelector` and `WarrantyAssigner` | V-13 | DIP | `a041ac7` / PR #36 | Developer 2 |
| `SaleRegistrationService` | Depends on `PromotionSelector` and `WarrantyAssigner` instead of the concrete services | V-13 | DIP, ISP | `86d217b` / PR #36 | Technical Lead |
| `MenuOption` (new), the seven submenus | Main-menu option abstraction (`getLabel`, `run`); every submenu implements it (`run()` shows the submenu) | V-06 | OCP, DIP | `cf4c18e` / PR #36 | Technical Lead |
| `MonthlyBalanceMenu` (new), `ReturnMenu`, `ConsoleMenu` | The monthly-balance option moved verbatim out of `ReturnMenu` into its own `MenuOption` | V-06 | OCP, SRP | `46fdb3a` / PR #36 | Technical Lead |
| `ConsoleMenu`, `Main` | `ConsoleMenu(Scanner, List<MenuOption>)` numbers the options by position and runs the chosen one (same labels, same "0. Salir" and "Opcion invalida."); `Main` creates the `Scanner` and composes the list in the original order. `ConsoleMenu` no longer knows any submenu (109 → 64 lines) | V-06 | OCP, DIP | `7d7d33f` / PR #36 | Technical Lead |

## Coordination and trade-off notes

- **Coordination commits (5):** whenever a new abstraction had to be implemented by another role's class, that class's owner added the `implements` clause inside the group that needed it — `16a9fdf` (Developer 1, `ProductService implements ItemSource`), `80b200a` (Technical Lead, `SaleService implements SaleLookup`), `c836dda` (Technical Lead, menu and `Main` call `MonthlyBalanceService`), `b286423` (Developer 2, `PersonService implements PersonLookup`), `a041ac7` (Developer 2, `PromotionService`/`WarrantyService` implement the use-case abstractions). Every commit of the phase compiles.
- **Store interfaces live in the persistence package.** Classic DIP places the abstraction with the high-level module; here that would make the persistence layer depend on the service layer, which Taller 1 forbids. The interfaces sit next to their implementations and the services depend on them, so the arrow still points `service → persistence`.
- **Receipt methods kept as delegations.** `generateReceipt()`, `generateReturnReceipt()` and `generateWarrantyCertificate()` are required APIs; the layout has its own class and reason to change.
- **`SaleRegistrationService` still depends on `SaleService` and `ItemCatalog` concretely** for its two write operations (`recordSale`, `decreaseStock`). Both are the single owners of those writes and no alternative is anticipated; the read-only and policy dependencies are abstractions.
- **Repository type switches unchanged** (accepted in the consolidated audit). The warranty repository no longer has one: it writes `warranty.getKind()`.
- **Out-of-scope defects preserved:** OOS-01..03 behave exactly as in the baseline.

## Final results (end of Phase 2)

| Check | Baseline | Final |
|---|---|---|
| `mvn clean verify` | BUILD SUCCESS, 93 tests | BUILD SUCCESS, **96 tests**, 0 failures, 0 errors; JaCoCo check passed. All 93 baseline tests kept: the two monthly-balance tests moved verbatim to `MonthlyBalanceServiceTest`; two `ReturnServiceTest` stock tests now verify `restoreStock(id)` on the owning `ItemSource` (same behavior); 3 new `ItemCatalogTest` tests |
| Line coverage `com.gamezone.service` | 83.2% | 85.0% (347/408) |
| Scripted menu run | 1,051 normalized lines, SHA-256 `d404c06d889e6046` | 1,051 normalized lines, SHA-256 `d404c06d889e6046` — **diff empty** |
| Interfaces in the system | 0 | 14 (7 stores, 4 lookups/sources, 2 use-case policies, 1 menu option) |
| Services depending on a concrete repository | 7 | 0 |
| `instanceof` outside repository serialization | 2 (`ItemCatalog`) + 1 (`WarrantyRepository` tag) | 0 |
| `ConsoleMenu` | 109 lines, `switch` over 8 concrete submenus | 64 lines, no submenu known |
| Production classes and interfaces | 48 | 68 |

Intermediate runs of the scripted check (all with an empty diff): end of the Developer 1 group, after `1e26194`, `7978e6f`, `c836dda`, `9937ff4`, `86d217b`, `46fdb3a` and `7d7d33f`. `mvn clean compile` was run before every code commit and `mvn clean verify` at the end of each group.
