# CLAUDE.md — GameZone Unicesar Reference Implementation

## Project identity

- **Name:** GameZone Unicesar
- **Type:** Reference implementation of Taller 1, Programación de Computadores III (SS462), Universidad Popular del Cesar.
- **Purpose:** Simulate a three-person team workflow from a single Git user.
- **Owner:** Alfredo David Bautista Romero (adbautistar).

## Absolute paths

- **Local project root:** `C:\Users\alfre\Desktop\NetBeansProjects\GameZoneUnicesar`
- **Remote repository:** `https://github.com/adbautistar/GameZoneUnicesar.git`
- **Repository status:** already created and public on GitHub (Phase 0 clones, does not create).

## Non-negotiable rules

1. All code, identifiers, comments, JavaDoc, commit messages, branch names, README, TEAM.md, and AI logs are in **English**.
2. All console messages shown to the end user are in **Spanish**.
3. Every commit is **atomic** (one logical change) and pushed **immediately** after creation. Never accumulate commits locally.
4. Every commit message follows **Conventional Commits**: `feat:`, `fix:`, `docs:`, `refactor:`, `chore:`.
5. Direct commits to `main` or `develop` are **forbidden**. All changes reach these branches via Pull Request from a feature branch.
6. `git push --force` is **forbidden** on any branch.
7. Feature branches are deleted from remote after merge.
8. Every Pull Request must correspond to one coherent functional unit; no cross-module PRs.
9. Self-approval of PRs is documented in the README as the only deviation from the taller (single-user reference implementation).

## Simulated team

| Role | Module | Branch | Simulated identity |
|---|---|---|---|
| Technical Lead | Sale + UI + Main | `feature/sale-module` | Alfredo Bautista |
| Developer 1 | Product | `feature/product-module` | Developer 1 (simulated) |
| Developer 2 | Person | `feature/person-module` | Developer 2 (simulated) |

## Architecture

Four-layer architecture. Package root: `com.gamezone`.

```
com.gamezone
├── model         (domain classes; no I/O, no UI references)
├── persistence   (file I/O; depends on model)
├── service       (business rules; depends on model and persistence)
├── ui            (console menu in Spanish; depends on service only)
└── Main.java     (application entry point)
```

**Dependency direction (strict):** `ui → service → persistence → model`.

- `model` depends on nothing.
- `persistence` depends only on `model`.
- `service` depends on `model` and `persistence`.
- `ui` depends only on `service`.

## Class inventory (35 classes total: 15 baseline + 20 from extended modules)

### Baseline (Taller 1, v1.0.x — 15 classes)

**Model (7):**
- `Product` (abstract): `id`, `title`, `price`, `stock`; abstract `getDescription()`; `updateStock(int)`.
- `VideoGame` extends `Product`: `platform`, `genre`, `ageRating`.
- `Console` extends `Product`: `brand`, `model`, `generation`.
- `Person` (abstract): `id`, `firstName`, `lastName`, `phone`; `getFullName()`.
- `Customer` extends `Person`: `email`, `purchaseHistory` (List<Sale>).
- `Seller` extends `Person`: `employeeCode`, `shift`.
- `Sale`: `id`, `date`, `customer`, `seller`, `products` (List<Product>), `totalAmount`; `calculateTotal()`, `generateReceipt()`. Getter is `getTotalAmount()` — extended modules that reference "total" mean this method (see Phase 11 note below); a `setTotalAmount(double)` setter is added additively in Phase 11.

**Persistence (3):**
- `ProductRepository`: `saveAll(List<Product>)`, `loadAll(): List<Product>`.
- `PersonRepository`: separate methods for customers and sellers.
- `SaleRepository`: `saveAll(List<Sale>)`, `loadAll(): List<Sale>`. Known layering exception: depends on `ProductService`/`PersonService` (service layer) to resolve references while reconstructing a `Sale` — documented in `docs/class-diagram.md`.

**Service (3):**
- `ProductService`: `registerVideoGame(...)`, `registerConsole(...)`, `listAllProducts()`, `updateStock(...)`, `findById(...)`.
- `PersonService`: `registerCustomer(...)`, `listAllCustomers()`, `listAllSellers()`, `findCustomerById(...)`, `findSellerById(...)`.
- `SaleService`: `registerSale(...)` (validates stock, updates inventory, calculates total), `viewAllSales()`, `viewSalesByCustomer(...)`, `viewSalesBySeller(...)`.

**UI (1):**
- `ConsoleMenu`: main menu and submenus in Spanish. Uses only services.

**Entry point (1):**
- `Main`: loads data via repositories, injects services into `ConsoleMenu`, launches menu.

### Extended modules (Phases 10-13 — 20 new classes)

Each module below is implemented additively: it must not change the existing public behavior of the baseline classes beyond the explicit, targeted extension points listed in its own `fase-1X-*.md` document (e.g., adding a field, adding a constructor parameter, adding a method). See each phase document for the exact extension points.

**Phase 10 — Accessory module (Developer 1: model; Developer 2: persistence/service; Technical Lead: integration/UI). Release: v1.1.0.**
- Model (4): `Accessory` (abstract, extends `Product`): `compatibleConsoleIds`; abstract `getDescription()` (inherited, still unimplemented). `Controller` extends `Accessory`: `connectionType`. `Cable` extends `Accessory`: `lengthInMeters`, `connectorType`. `Memory` extends `Accessory`: `capacityInGB`, `memoryType`.
- Persistence (1): `AccessoryRepository` — self-contained, persists `data/accessories.csv`, no cross-service dependency.
- Service (1): `AccessoryService` — `registerController/Cable/Memory(...)`, `listAllAccessories()`, `listAccessoriesByType(...)`, `findAccessoriesCompatibleWith(...)`, `findById(...)`, `updateStock(...)`.
- Targeted extension points on baseline classes: `SaleService` gains an `AccessoryService` dependency and resolves each sale item through `productService.findById` first, falling back to `accessoryService.findById`. Stock validation must look up the already-resolved item (not re-query `productService.findById` by id a second time, which would fail for accessory ids). `Main` and `ConsoleMenu` are wired accordingly (new main menu option 4).

**Phase 11 — Promotion module (same role split). Release: v1.2.0.**
- Model (4): `Promotion` (abstract): `startDate`, `endDate`, `isActive(date)`, abstract `calculateDiscount(Sale)`. `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount` extend `Promotion`.
- Persistence (1): `PromotionRepository` — self-contained, persists `data/promotions.csv`.
- Service (1): `PromotionService` — `registerPercentageDiscount/CategoryDiscount/BulkPurchaseDiscount(...)`, `listAllPromotions()`, `listActivePromotions()`, `findBestPromotionFor(Sale)`, `findById(...)`.
- Targeted extension points: `Sale` gains `appliedPromotionName`, `discountAmount`, and `setTotalAmount(double)` (additive; `generateReceipt()` shows the discount breakdown when present). `SaleService` gains a `PromotionService` dependency and applies the best active promotion after computing the total and before the stock update. New main menu option 5.

**Phase 12 — Return module (same role split). Release: v1.3.0.**
- Model (1): `Return` (concrete): `originalSale`, `returnedProducts`, `reason`, `refundAmount`; `calculateRefundAmount()`, `generateReturnReceipt()`.
- Persistence (1): `ReturnRepository` — depends on `SaleService`, `ProductService`, **and** `AccessoryService` (three dependencies) to resolve references from `data/returns.csv`.
- Service (1): `ReturnService` — `registerReturn(saleId, productIds, reason)` (supports partial, per-product returns; enforces the 30-day window; validates the products belong to the original sale), `viewAllReturns()`, `viewReturnsByCustomer(...)`, `viewReturnsBySale(...)`, `generateMonthlyBalance(month, year)`.
- Targeted extension points: `Sale` gains `canBeReturned()` (30-day rule via `ChronoUnit.DAYS`). `ProductService` gains `restoreStock(productId, quantity)` — functionally equivalent to `updateStock(id, +quantity)`, added for call-site clarity; must be committed before `ReturnService` depends on it. `AccessoryService.updateStock` is reused directly (no separate `restoreStock`) for accessory returns. New main menu options 6 (returns) and 7 (monthly balance).

**Phase 13 — Warranty module (same role split). Release: v1.4.0.**
- Model (3): `Warranty` (abstract): `product`, `sale`, `startDate`, `endDate` (derived in the constructor from the abstract `getDurationInMonths()` — safe here because both subclasses return literal constants, not subclass-field-dependent values); abstract `getDurationInMonths()`, `getWarrantyType()`, `getAdditionalCost()`; concrete `isActive(date)`, `generateWarrantyCertificate()`. `BasicWarranty` (6 months, no cost) and `ExtendedWarranty` (12 months, 10% of product price) extend `Warranty`.
- Persistence (1): `WarrantyRepository` — depends on `SaleService`, `ProductService`, `AccessoryService` to resolve references from `data/warranties.csv`.
- Service (1): `WarrantyService` — `assignBasicWarranty(...)`, `assignExtendedWarranty(...)`, `findWarrantyByProduct(...)`, `listAllWarranties()`, `listActiveWarranties()`, `listWarrantiesExpiringSoon(daysAhead)`.
- Targeted extension points: `SaleService` gains a `WarrantyService` dependency and an additional `registerSale` parameter (`productIdsWithExtendedWarranty`); every `Console` in a sale automatically receives a `BasicWarranty`, and an opted-in `Console` additionally receives an `ExtendedWarranty` whose `getAdditionalCost()` is added to the sale total (applied after the promotion discount). New main menu option 8.

## Encapsulation rules

- All domain attributes are `private`.
- Access via getters/setters when needed.
- Never declare `public` attributes.
- Abstract classes: `Product`, `Person`.
- Abstract methods: `Product.getDescription()`.
- Subclasses use explicit `@Override`.

## Persistence rules

- File-based persistence in the `data/` folder at the repository root.
- Format: choose one of {plain text, CSV, Java serialization} and use it consistently.
- Auto-load on application start.
- Auto-save after each state-changing operation.
- Seller file preloaded with **3 sellers** before first execution.
- `data/accessories.csv` (Phase 10) and `data/promotions.csv` (Phase 11) are preloaded with **3 rows each** before first execution of their phase, following the same convention as `data/sellers.csv`.
- `data/returns.csv` (Phase 12) and `data/warranties.csv` (Phase 13) start empty — they only accumulate entries created during use.

## Console UI rules

- All prompts, menu labels, error messages, and confirmations in **Spanish**.
- Identifiers, method calls, and code structure in **English**.
- Menu displays all mandatory operations grouped by module (see below).

## The mandatory operations (30 total: 10 baseline + 20 from extended modules)

### Baseline (Taller 1, v1.0.x)

**Products:**
1. Register new video game.
2. Register new console.
3. List all products in inventory.

**People:**
4. Register new customer.
5. List all customers.
6. List all sellers.

**Sales:**
7. Register new sale (select customer, seller, one or more products).
8. View complete sales history.
9. View sales history by customer.
10. View sales history by seller.

### Extended modules (Phases 10-13)

**Accessories (Phase 10, main menu option 4):**
11. Register a new controller.
12. Register a new cable.
13. Register a new memory.
14. List all accessories.
15. List accessories by type.
16. Query accessories compatible with a console. Sale registration additionally accepts accessory ids interchangeably with product ids.

**Promotions (Phase 11, main menu option 5):**
17. Register a new percentage discount promotion.
18. Register a new category discount promotion.
19. Register a new bulk-purchase discount promotion.
20. List all promotions.
21. List active promotions. Sale registration automatically applies the best active promotion and shows the discount breakdown in the receipt.

**Returns (Phase 12, main menu options 6-7):**
22. Register a new return (partial, per-product; within 30 days of the sale).
23. View all returns.
24. View returns by customer.
25. View returns by sale.
26. Query monthly balance (total sales minus total refunds for a given month/year).

**Warranties (Phase 13, main menu option 8):**
27. Query warranty by product and sale.
28. List all warranties.
29. List active warranties.
30. List warranties expiring soon (configurable days-ahead window). A basic warranty is assigned automatically to every console sold; an extended warranty is optional and offered during sale registration.

## Git Flow rules

- Branches: `main` (protected), `develop` (protected), `feature/*` (temporary).
- Feature branches derive from `develop` and merge back to `develop` via PR.
- `main` receives merges only from `develop` via PR at stable increments.
- Feature branch naming: `feature/<module>-module` (e.g., `feature/product-module`).
- **Baseline pattern (Phases 5-7):** one feature branch per role, each with its own independent PR into `develop` (e.g., `feature/product-module` opened and merged entirely by Developer 1 before Developer 2's branch starts).
- **Extended-module pattern (Phases 10-13):** a different, deliberate pattern — **one shared feature branch per module**, with all three roles committing to it sequentially in "Task Groups" (A = Developer 1, B = Developer 2, C = Technical Lead, each gated on the previous group being merged into the branch and `mvn clean compile` passing), ending in a single PR that covers the whole module. This does not violate rule 8 (no cross-module PRs) — all three task groups belong to the same functional unit (one module). See each `fase-1X-*.md` document for the exact task-group breakdown and commit sequence.

## Minimum quantitative requirements

- **Baseline (Taller 1, v1.0.x):** minimum 12 atomic commits per simulated developer, minimum 36 total, minimum 6 Pull Requests.
- **Per extended-module phase (10-13):** minimum 6 atomic commits per task group (18 per phase), plus 1 PR into `develop` and 1 release PR into `main`.
- These are floors, not targets — actual counts may exceed them (see `docs/version-control.md` for the running total as of the last phase executed).

## Commit attribution strategy

Since this is a single-user reference implementation, commit authorship is simulated via the `--author` flag with the following simulated identities:

- Developer 1: `"Dev1 Simulated <dev1@gamezone-ref.local>"`
- Developer 2: `"Dev2 Simulated <dev2@gamezone-ref.local>"`
- Technical Lead: default git config (Alfredo Bautista).

Example for Developer 1 commit:

```powershell
git commit --author="Dev1 Simulated <dev1@gamezone-ref.local>" -m "feat(product): add abstract Product class"
```

This produces visible author differentiation in `git log` while all commits remain signed under the real user's GitHub authentication.

## Documentation deliverables

Located under `docs/`:

- `analysis.md` — answers to the 11 orienting questions (in English).
- `hierarchy-diagram.md` — Mermaid class hierarchies (model layer only).
- `class-diagram.md` — full Mermaid class diagram (all four layers, grouped visually).
- `layers-diagram.md` — Mermaid layer dependency diagram.
- `ai-usage/leader-ai-log.md` — Technical Lead AI usage log.
- `ai-usage/developer1-ai-log.md` — Developer 1 AI usage log.
- `ai-usage/developer2-ai-log.md` — Developer 2 AI usage log.
- `version-control.md` — branching strategy, every branch created, full commit log, commits per simulated team member, and every Pull Request (added in Phase 9+; keep it current after each new phase/release).

## AI log entry template

Each entry in every `*-ai-log.md` file follows this exact structure:

```markdown
### Entry <number>

**Date:** YYYY-MM-DD
**Tool used:** Claude Code

**Reason for use:**
<one sentence>

**Problem faced:**
<one to three sentences>

**Prompt used:**
<verbatim prompt>

**Solution obtained and decision taken:**
<one paragraph>
```

## Repository top-level structure at delivery

```
GameZoneUnicesar/
├── README.md
├── TEAM.md
├── CLAUDE.md
├── pom.xml
├── .gitignore
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── gamezone/
│                   ├── model/
│                   ├── persistence/
│                   ├── service/
│                   ├── ui/
│                   └── Main.java
├── data/
└── docs/
    ├── analysis.md
    ├── hierarchy-diagram.md
    ├── class-diagram.md
    ├── layers-diagram.md
    ├── version-control.md
    └── ai-usage/
        ├── leader-ai-log.md
        ├── developer1-ai-log.md
        └── developer2-ai-log.md
```

Note: this tree reflects the baseline (Taller 1) delivery. Phases 10-13 add `data/accessories.csv`, `data/promotions.csv`, `data/returns.csv`, `data/warranties.csv`, and 20 new classes under `src/main/java/com/gamezone/{model,persistence,service}/` — see "Extended modules" under Class inventory above.

## Execution commands

```powershell
mvn clean compile
mvn exec:java "-Dexec.mainClass=com.gamezone.Main"
```

## Phase completion criterion

A phase is complete only when:

1. All specified files exist.
2. All specified commits are pushed to remote.
3. All specified PRs are merged and feature branches deleted.
4. `git status` reports a clean working tree.
5. `mvn clean compile` succeeds (from Fase 4 onward).

## Deviation from taller

This is the **only** documented deviation from the taller specification:

> Cross-review of Pull Requests is not simulated. All PRs in this repository are self-approved by the single user executing the reference implementation. This deviation is inherent to the single-user nature of the reference implementation and does not apply to student teams.
