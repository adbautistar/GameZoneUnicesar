# GRASP Analysis — Requerimiento 7

Answers to the five orienting questions of the GRASP audit and correction. Each answer was discussed by the team and written by the role indicated.

## Q1. Criteria for a verdict and minimum evidence

*Written by Developer 1.*

A fragment is a **violation** when a responsibility is placed where the pattern says it should not be, and the placement has a concrete cost we can point to in our own code: a change that would have to be repeated in several places, a dependency that goes against the allowed layer direction, a class that has to be edited when an unrelated variant is added, or a getter chain that reaches into another object's data. A pattern is **correct** when we can name the class and method where it is applied and explain why that class is the right owner. A pattern is **not applicable** when the audited code has no point where the question the pattern answers even arises (for example, Controller for model classes that never receive a system event).

We used the "warning sign" of each pattern from class as a first filter (getter chains for Information Expert, `if`/`instanceof` on the type for Polymorphism, logic in the UI for Controller, and so on), but a warning sign alone was not enough: a type check inside a repository's CSV serialization, for example, was judged correct because the repository is a Pure Fabrication whose purpose is to keep the file format out of the model.

Minimum evidence accepted in the consolidation for each verdict:

- **Violation:** class, method and the exact code fragment or line range on the audited commit; why it breaks the pattern; a concrete consequence; a proposed correction that names the pattern applied; and whether the correction touches another role's code.
- **Correct:** at least one class and method where the pattern is applied, with one sentence explaining why.
- **Not applicable:** one sentence explaining why the audited scope has no point of application.

## Q2. Boundary findings

*Written by the Technical Lead.*

Seven of the sixteen consolidated violations are boundary findings (V-02, V-05, V-06, V-07, V-08, V-09, V-13). They appeared in two ways:

- **The same problem seen from both sides.** Developer 2 reported the "find a product, otherwise an accessory" lookup and the `instanceof Accessory` stock dispatch in the return and warranty code; the Technical Lead reported the same lookup and dispatch in the sale code. In the consolidation they became one violation, **V-05**, because the correction is one design move (an intermediary for sellable items) even though it touches both roles' classes. The same happened with identifier generation (**V-09**).
- **A violation in one role's code whose fix needs another role's class.** Developer 2's `ReturnService` searches the products of a sale and navigates `getOriginalSale().getCustomer().getId()`; the experts that should answer are `Sale` and `Return`. The violation is Developer 2's (**V-06**, **V-07**), but the new query methods live in model classes, so Developer 1 writes them and Developer 2 uses them.

Each boundary finding was registered once, with every origin listed, and its correction was split by owner in the "Boundary findings" table of `consolidated-audit.md`. The rule applied is the one in the statement: whoever is responsible for the code that changes makes that change. The one place where this forces a cross-role commit is `Main`, which belongs to the Technical Lead but must be rewired when Developer 2 changes the repository and service constructors (V-03, V-04); the Technical Lead makes that commit inside Developer 2's task group.

## Q3. Fragments that break more than one pattern

*Written by Developer 1.*

Yes. The clearest case in Developer 1's scope is `CategoryDiscount.calculateDiscount` (developer1-audit V-01). The same fragment breaks **Polymorphism** — it asks each product for its concrete class with `instanceof` instead of letting each product answer — and **Protected Variations** — the vocabulary of categories is hard-coded in the discount, so a new product category is an unprotected variation point.

We recorded it **once**, with a single ID, under the pattern that best describes the root cause (Polymorphism), and added a short cross-reference under Protected Variations that points to the same ID. The summary table lists the ID under both patterns, so the count of distinct violations is not inflated.

The pattern that guides the correction is the one whose solution removes the root cause. Here that is Polymorphism: once each product answers `belongsToCategory`, the variation point is also protected, so one change fixes both. When two patterns proposed different corrections for the same fragment, the team chose the one that removed the cause rather than the symptom and recorded the decision in the consolidated audit.

## Q4. Non-regression strategy

*Written by Developer 2.*

The correction is a refactor, so "nothing broke" means: for the same inputs, every menu operation prints the same text and leaves the same content in `data/`. The team uses two complementary checks, run on `develop` **before** the first correction (baseline) and again on every Pull Request of the correction and at the end:

1. **Automated test suite** — `mvn clean verify` (JUnit 5 tests for model, persistence, services and end-to-end scenarios, plus the JaCoCo coverage check on the service layer). The baseline records the number of tests and the build result. When a refactor changes a constructor or a return type, the affected tests are adapted only in how objects are built; no assertion about behavior is removed or weakened. Responsibilities that move between classes (for example, resolving references when data is loaded) get new tests in their new home.
2. **Scripted run of the whole menu** — the application is run from a scratch copy of `data/` with a fixed input script that exercises every menu operation: all registrations and listings of products, people, accessories and promotions; sales with a console and an extended warranty and with an active promotion; the three sales histories; returns, their three queries and the monthly balance; and the four warranty queries. Values that legitimately change between runs (millisecond-based identifiers and today's date) are normalized, and the normalized output is compared with the baseline using a line-by-line diff.

The evidence shown at the end is the baseline and final results of both checks, recorded in `docs/grasp-refactor-log.md`: BUILD SUCCESS with every baseline test still present and passing (new tests may be added, none removed), and an empty diff between the baseline and final normalized outputs. Defects recorded as out of scope are part of the expected output, so a refactor that "fixes" one by accident also shows up in the diff.

## Q5. Order of the corrections and coordination

*Written by the Technical Lead.*

The order follows the dependencies between corrections, from the classes other corrections need to the classes that use them:

1. **Developer 1 — model first.** The polymorphic and query methods (`belongsToCategory`, `isWarrantyEligible`, `getAccessoryType`, `Sale.findProduct`, `Sale.wasMadeIn`, the `Return` queries, `Warranty.covers`) are purely additive and nothing else compiles against them yet, so they can go first without breaking anything. V-01 is also corrected here because it only involves Developer 1's code.
2. **Developer 2 — fabrications and repositories.** `IdGenerator` and `ItemCatalog` are created, the accessory filter and the return and warranty services start using the model methods from step 1, and the return and warranty repositories stop depending on services.
3. **Technical Lead — sales and UI last.** The sale code adopts `ItemCatalog` and `IdGenerator` from step 2 and the eligibility method from step 1; the registration use case is extracted, which also removes the circular dependency; the UI is split last because it only moves code that already calls the final service API.

Coordination rules on the shared branch `feature/grasp-refactor`:

- Only one role works on the branch at a time, and each task group ends with `mvn clean verify` green and its Pull Request merged before the next one starts.
- Every commit must compile. When a change in one role's class breaks another role's class (Developer 2's constructors and the Technical Lead's `Main`), the change is made in three steps: Developer 2 adds the new constructors and marks the old ones `@Deprecated`; the Technical Lead rewires `Main`; Developer 2 removes the deprecated code.
- Test adaptations forced by a signature change are made by the author of that change, in the same commit, without touching any assertion about behavior.
