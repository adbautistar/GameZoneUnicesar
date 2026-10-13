# GRASP Refactor Log — Requerimiento 7

Change log of Phase 2 (correction). Every violation of `docs/grasp-audit/consolidated-audit.md` must appear here at least once, or in the out-of-scope section of the consolidated audit with its justification.

## Non-regression strategy

Defined before the first correction, as answered in `docs/grasp-analysis.md` (Q4). The correction is a refactor: for the same inputs, every menu operation must print the same text and leave the same content in `data/`.

**Check 1 — automated suite.** `mvn clean verify` (JUnit 5 + AssertJ + Mockito, JaCoCo line-coverage check of 70% on `com.gamezone.service`). Every baseline test must still exist and pass; tests may be added, never removed. When a constructor or a return type changes, tests are adapted only in how objects are built.

**Check 2 — scripted menu run.** The compiled application (`target/classes`) is run from a scratch working directory with its own `data/` folder and a fixed input script; the run never touches the repository's `data/` files.

- *Fixture data:* the repository's `sellers.csv`, `accessories.csv` and `promotions.csv`, plus two products (`VG001`, `CN001`), one customer (`C001`), two existing sales (`SALE-1000`, 5 days old, with a promotion and an extended warranty; `SALE-0900`, 45 days old) and the two warranties of `SALE-1000`.
- *Operations covered (every menu option):*
  - Products: register video game, register console, invalid price, list.
  - People: register customer, list customers, list sellers.
  - Accessories: register controller, cable and memory; list all; list by each type and an unknown type; compatible accessories for an existing and an unknown console.
  - Promotions: register percentage, category (expired) and bulk-purchase promotions; reversed dates; list all; list active.
  - Sales: console with extended warranty; video game plus accessories with a promotion; unknown product, customer and seller; insufficient stock; non-numeric and zero quantity; full history; history by known and unknown customer and seller.
  - Returns: valid partial return; product not in the sale; sale older than 30 days; unknown sale; list all; by customer (two); by sale (two).
  - Monthly balance: current month, an empty month, non-numeric input.
  - Warranties: query an existing and a missing warranty; list all; list active; expiring in 30 and 400 days.
  - Invalid main-menu option and exit.
- *Captured:* standard output, standard error, exit code and the final content of the eight `data/*.csv` files.
- *Normalization:* millisecond identifiers (`SALE-`, `RET-`, `WAR-` followed by 13 digits) become `<ms>`; today's date and the dates derived from it (today − 5, today − 45, and the +6/+12-month warranty end dates) become tokens such as `<TODAY>`.
- *Comparison:* line-by-line `diff` against the baseline output. Expected result: no differences.

Defects recorded as out of scope (OOS-01..03) are part of the expected output. For example, the baseline shows `SALE-1000` reloaded with total `2592500.0` instead of the persisted `2872500.0` (OOS-01), and the refactor must keep it that way.

## Baseline results (develop `7bf4931`, before the first correction)

| Check | Result |
|---|---|
| `mvn clean verify` | BUILD SUCCESS — 88 tests, 0 failures, 0 errors; JaCoCo check passed |
| Scripted menu run | Exit code 0; 1,051 normalized lines; two consecutive runs identical (deterministic); SHA-256 prefix `d404c06d889e6046` |

## Change log

| Class | Change | Violation ID | Pattern applied | Commit / PR | Owner |
|---|---|---|---|---|---|
| `Product`, `VideoGame`, `Console`, `Accessory` | Added abstract `belongsToCategory(String)`; video games answer `"VIDEOGAME"`, consoles `"CONSOLE"`, accessories never match (same results as before for every target string) | V-01 | Polymorphism, Protected Variations | `3174829` / PR #27 | Developer 1 |
| `CategoryDiscount` | `calculateDiscount` sums the products for which `product.belongsToCategory(targetCategory)`; no more category strings or `instanceof` | V-01 | Polymorphism, Protected Variations | `3e98b77` / PR #27 | Developer 1 |
| `Product`, `Console` | Added `isWarrantyEligible()` (false by default, `Console` overrides with true) for the sale registration to use | V-13 (model part) | Polymorphism, Protected Variations | `2812ca9` / PR #27 | Developer 1 |
| `Accessory`, `Controller`, `Cable`, `Memory` | Added abstract `getAccessoryType()` returning `"CONTROLLER"`, `"CABLE"`, `"MEMORY"` | V-02 (model part) | Polymorphism | `9dac17d` / PR #27 | Developer 1 |
| `Sale` | Added `findProduct(String)` and `wasMadeIn(int, int)` (return-related model work, Requerimiento 3 precedent) | V-06, V-07 (model part) | Information Expert | `c1873e9` / PR #27 | Developer 1 |
| `Return` | Added `belongsToCustomer(String)`, `belongsToSale(String)`, `wasRegisteredIn(int, int)` | V-07 (model part) | Information Expert | `ed3f800` / PR #27 | Developer 1 |
| `Warranty` | Added `covers(String productId, String saleId)` | V-08 (model part) | Information Expert | `345c8d7` / PR #27 | Developer 1 |
| `IdGenerator` (new), `ReturnService`, `WarrantyService` | New fabrication `IdGenerator.next(prefix)` (same `PREFIX-<millis>` format, so OOS-03 is preserved); return and warranty ids generated through it | V-09 | Pure Fabrication | `31f9476` / PR #28 | Developer 2 |
| `ItemCatalog` (new) | Single intermediary for sellable items: `findItem` (products first, then accessories), `decreaseStock`, `restoreStock`. **Trade-off:** it keeps the only remaining `instanceof Accessory`, because it is the one class that knows which catalog owns an item; clients no longer check types | V-05 | Indirection, Protected Variations | `e9da721` / PR #28 | Developer 2 |
| `AccessoryService` | `listAccessoriesByType` filters with `accessory.getAccessoryType().equals(type)`; no type strings mapped to classes | V-02 | Polymorphism, Protected Variations | `f37a6d2` / PR #28 | Developer 2 |
| `ReturnService` | Depends on `ItemCatalog` instead of `ProductService` + `AccessoryService`; `registerReturn` uses `sale.findProduct` and `itemCatalog.restoreStock`; queries and monthly balance use `Return.belongsToCustomer/belongsToSale/wasRegisteredIn` and `Sale.wasMadeIn` | V-05, V-06, V-07 | Indirection, Information Expert | `1cba7a3` / PR #28 | Developer 2 |
| `ReturnRecord` (new), `ReturnRepository`, `ReturnService` | Repository loads `ReturnRecord`s (ids only); `ReturnService` resolves sale and items (same skip messages). Old service-taking API kept `@Deprecated` for one step | V-03 | Low Coupling | `1909b8c` / PR #28 | Developer 2 |
| `WarrantyRecord` (new), `WarrantyRepository`, `WarrantyService` | Repository loads `WarrantyRecord`s (ids only); `WarrantyService` resolves product and sale and rebuilds BASIC/EXTENDED warranties (same messages); `findWarrantyByProduct` uses `warranty.covers` | V-04, V-08 | Low Coupling, Information Expert | `f3ff88b` / PR #28 | Developer 2 |
| `Main` | **Boundary coordination:** the Technical Lead (owner of `Main`) rewired returns and warranties inside Developer 2's task group — `ItemCatalog` created, path-only repositories, new service constructors | V-03, V-04 | Low Coupling | `7512a7b` / PR #28 | Technical Lead |
| `ReturnRepository`, `WarrantyRepository`, `ReturnService`, `WarrantyService` + tests | Removed the transitional `@Deprecated` API; `loadAll()` now returns records. Tests adapted only in construction and in what a repository round-trip returns (ids instead of resolved objects) | V-03, V-04 | Low Coupling | `c0a4b5f` / PR #28 | Developer 2 |
| `ReturnServiceTest`, `WarrantyServiceTest` | New tests for the responsibility that moved into the services: resolving stored records (88 → 90 tests) | V-03, V-04 | — | `29bff08` / PR #28 | Developer 2 |
