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
