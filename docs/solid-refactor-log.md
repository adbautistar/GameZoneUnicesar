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
