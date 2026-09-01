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

## Class inventory (15 classes total)

**Model (7):**
- `Product` (abstract): `id`, `title`, `price`, `stock`; abstract `getDescription()`; `updateStock(int)`.
- `VideoGame` extends `Product`: `platform`, `genre`, `ageRating`.
- `Console` extends `Product`: `brand`, `model`, `generation`.
- `Person` (abstract): `id`, `firstName`, `lastName`, `phone`; `getFullName()`.
- `Customer` extends `Person`: `email`, `purchaseHistory` (List<Sale>).
- `Seller` extends `Person`: `employeeCode`, `shift`.
- `Sale`: `id`, `date`, `customer`, `seller`, `products` (List<Product>), `totalAmount`; `calculateTotal()`, `generateReceipt()`.

**Persistence (3):**
- `ProductRepository`: `saveAll(List<Product>)`, `loadAll(): List<Product>`.
- `PersonRepository`: separate methods for customers and sellers.
- `SaleRepository`: `saveAll(List<Sale>)`, `loadAll(): List<Sale>`.

**Service (3):**
- `ProductService`: `registerVideoGame(...)`, `registerConsole(...)`, `listAllProducts()`, `updateStock(...)`.
- `PersonService`: `registerCustomer(...)`, `listAllCustomers()`, `listAllSellers()`.
- `SaleService`: `registerSale(Customer, Seller, List<Product>)` (validates stock, updates inventory, calculates total), `viewAllSales()`, `viewSalesByCustomer(...)`, `viewSalesBySeller(...)`.

**UI (1):**
- `ConsoleMenu`: main menu and submenus in Spanish. Uses only services.

**Entry point (1):**
- `Main`: loads data via repositories, injects services into `ConsoleMenu`, launches menu.

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

## Console UI rules

- All prompts, menu labels, error messages, and confirmations in **Spanish**.
- Identifiers, method calls, and code structure in **English**.
- Menu displays the 10 mandatory operations grouped by module.

## The 10 mandatory operations

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

## Git Flow rules

- Branches: `main` (protected), `develop` (protected), `feature/*` (temporary).
- Feature branches derive from `develop` and merge back to `develop` via PR.
- `main` receives merges only from `develop` via PR at stable increments.
- Feature branch naming: `feature/<module>-module` (e.g., `feature/product-module`).

## Minimum quantitative requirements

- **Commits per simulated developer:** minimum 12 atomic commits.
- **Total commits:** minimum 36 across the three simulated developers.
- **Pull Requests:** minimum 6 approved and merged.

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
    └── ai-usage/
        ├── leader-ai-log.md
        ├── developer1-ai-log.md
        └── developer2-ai-log.md
```

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
