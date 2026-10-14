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

*Written by the Technical Lead.*

Seven of the thirteen consolidated violations are boundary findings (V-03, V-04, V-05, V-07, V-08, V-09, V-13). Almost all of them have the same shape, which is specific to ISP and DIP: **the violation is in the client, but the correction needs the supplier to implement a new abstraction.**

- **V-07:** Developer 2's `ReturnService` and `WarrantyService` depend on the whole `SaleService` just to read sales. The violation is Developer 2's, so Developer 2 creates `SaleLookup` and changes both clients; but `SaleService` is the Technical Lead's class, so the Technical Lead adds `implements SaleLookup`.
- **V-08** is the mirror case: the Technical Lead's sales services depend on the whole `PersonService`; the Technical Lead creates `PersonLookup` and Developer 2 makes `PersonService` implement it.
- **V-13:** following DIP, the abstractions belong to the high-level module that needs them, so the Technical Lead's `SaleRegistrationService` defines `PromotionSelector` and `WarrantyAssigner`, and Developer 2's services implement them.
- **V-09** was reported from both sides (`WarrantyService` by Developer 2, `SaleService` by the Technical Lead) and merged into one violation with one interface, `ItemLookup`, created by the owner of `ItemCatalog`.
- **V-04 and V-05** go the other way: the violation is in Developer 2's code but the fix needs Developer 1's classes (`ProductService` implements `ItemSource`; the warranty hierarchy gains `WarrantyKind`).

Each boundary finding is registered once in `consolidated-audit.md` with every origin and the split of the correction. The rule is the one in the statement: whoever is responsible for the code that changes makes that change, so one-line `implements` clauses in another role's class are committed by that class's owner, as coordination commits inside the group that needs them.

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

*Written by the Technical Lead.*

The order is Developer 1, then Developer 2, then the Technical Lead, as in Requerimiento 7, because it follows the dependencies:

1. **Developer 1:** its corrections are self-contained or additive (`ProductStore`, the receipt and certificate layouts, `WarrantyKind`), and Developer 2 needs `WarrantyKind` before changing the warranty repository and service.
2. **Developer 2:** the store interfaces of its five services, then the abstractions that other roles' classes must implement (`ItemLookup`, `ItemSource`, `SaleLookup`), then `MonthlyBalanceService`.
3. **Technical Lead:** the sales services can only depend on `PersonLookup`, `ItemLookup`, `PromotionSelector` and `WarrantyAssigner` once those exist and are implemented, and the menu change (V-06) goes last because it needs `MonthlyBalanceService` and touches `Main`.

Coordination rules on the shared branch `feature/solid-refactor`:

- One role works on the branch at a time; each group ends with `mvn clean verify` green, an empty diff of the scripted run, and its Pull Request merged.
- Every commit must compile. A new abstraction is always introduced in three steps: create the interface (additive); the owner of the implementing class adds `implements` (coordination commit, which compiles because the class already has the methods); the client switches to the interface.
- When a behavior moves to a new class (the monthly balance), the old method is kept until the menu calls the new class, and only then removed, so no intermediate commit breaks `ReturnMenu` or `Main`.
