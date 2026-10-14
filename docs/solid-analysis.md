# SOLID Analysis — Requerimiento 8

Answers to the five orienting questions of the SOLID audit and correction. Each answer was discussed by the team and written by the role indicated.

## Q1. Criteria for a verdict and minimum evidence

*Written by Developer 1.*

We asked one concrete question per principle and only recorded a violation when the answer pointed to a cost we could show in our own code:

- **SRP:** does the class have more than one reason to change, coming from different actors? (For example, business rules versus the layout of what the user reads.)
- **OCP:** to add a variant we can already anticipate (a new item catalog, a new warranty kind, a new menu module), do we have to modify existing classes instead of adding new ones?
- **LSP:** can every subclass be used wherever its base class is expected without the caller noticing — no narrowed preconditions, no unexpected exceptions, no broken results?
- **ISP:** does a client depend on operations it never calls?
- **DIP:** does a high-level policy depend on a concrete low-level detail instead of on an abstraction?

A principle is **correct** when we can name the classes where it holds and why; **not applicable** when the audited code has no point where the question arises (for example, ISP for classes that only depend on their own repository and use all of it).

Some fragments match a warning sign but were **accepted** after discussion, and every report lists them with the reason, so that the consolidation can see why they were not counted. The main case is the type switch inside the CSV repositories: a new product type changes the persisted format anyway, and the knowledge sits in the one class whose purpose is that format.

Minimum evidence accepted in the consolidation: for a violation, class, method and code fragment or lines on the audited commit, the reason it breaks the principle, a concrete consequence, a proposed correction, and whether it touches another role's code; for a correct verdict, at least one class and method and a sentence of justification; for not applicable, one sentence explaining why.

## Q2. Boundary findings

*Answer pending (Technical Lead).*

## Q3. Relation with the GRASP audit (Requerimiento 7)

*Written by Developer 1, with input from all roles.*

We found the four possible relations, with concrete cases:

1. **A GRASP trade-off that SOLID flags.** The GRASP correction kept one `instanceof Accessory` inside `ItemCatalog` on purpose, as the only class that knows which catalog owns an item. Under OCP that is still a violation: a new kind of sellable item would require modifying `ItemCatalog`. It is recorded again here and corrected with an `ItemSource` abstraction.
2. **A problem introduced by the GRASP correction.** Moving reference resolution out of `WarrantyRepository` (GRASP V-04) left the warranty type tags (`BASIC`/`EXTENDED`) known in two classes: the repository writes them and `WarrantyService` reads them. A new warranty kind would require editing both. This is an OCP violation created by a GRASP fix.
3. **A SOLID problem that GRASP had already solved.** `SaleService` used to register sales, store them and answer queries — an SRP violation. The GRASP correction extracted `SaleRegistrationService` (GRASP V-12), so the SOLID audit found `SaleService` already correct. Likewise, `CategoryDiscount` no longer needs to change for a new product category since GRASP V-01, which is also what OCP asks for.
4. **The same fragment judged differently.** The type switches inside the CSV repositories were *correct* for GRASP (Pure Fabrication) and are *accepted but examined* for OCP, with the justification given in Q1.

Fragments that break more than one principle (for example, `SaleRegistrationService` depending on the concrete `PromotionService` and `WarrantyService`, which is both a DIP and an ISP problem) were recorded once, under the principle whose correction removes the root cause, with a cross-reference.

## Q4. Non-regression strategy

*Written by Developer 2.*

The strategy is the one that already proved itself in Requerimiento 7, re-baselined on the code we are about to change. For the same inputs, every menu operation must print the same text and leave the same content in `data/`.

1. **Automated suite** — `mvn clean verify` (JUnit 5, AssertJ, Mockito; JaCoCo 70% line-coverage check on `com.gamezone.service`). Every baseline test must still exist and pass. Most SOLID corrections only change the *type* a constructor receives (an interface instead of a concrete class), so existing tests keep compiling unchanged: a mocked concrete repository is also an implementation of the new abstraction. When a correction moves a behavior to a new collaborator (the monthly balance, the stock dispatch of `ItemCatalog`), the tests of that behavior move with it and keep asserting the same results, and a new test covers the new class with real objects.
2. **Scripted run of the whole menu** — the same fixture data and input script as in Requerimiento 7: every registration, listing and query of every module, sales with promotions and warranties, returns (valid and rejected), the monthly balance and the warranty queries. Millisecond identifiers and dates are normalized and the output, together with the final `data/*.csv` files, is compared line by line with the baseline taken on `develop` before the first correction.

Both checks run before the first correction, at the end of each role's task group and at the end of the phase. The evidence is recorded in `docs/solid-refactor-log.md`: build result and test count, and an empty diff (same SHA-256) between baseline and final outputs. Out-of-scope defects from Requerimiento 7 are part of the expected output.

## Q5. Order of the corrections and coordination

*Answer pending (Technical Lead).*
