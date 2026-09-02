# Version Control — GameZone Unicesar

This document consolidates the version control activity of the project: the branching strategy, every branch created, the full commit log, commits grouped by simulated team member, every Pull Request opened and merged, branch protection rules, and release tags. It is generated from the actual state of the repository (`git log`, `git shortlog`, `git tag`, and the GitHub API via `gh`), not reconstructed from memory.

## Branching Strategy

The project follows a simplified Git Flow, as defined in [CLAUDE.md](../CLAUDE.md):

- **`main`** — protected, stable. Only receives merges from `develop` via Pull Request, at release increments.
- **`develop`** — protected, integration branch. Only receives merges from `feature/*` branches via Pull Request.
- **`feature/*`** — temporary, one per functional unit. Branches from `develop`, merges back into `develop`, and is deleted immediately after merge.

Both `main` and `develop` have branch protection enabled on GitHub:

| Setting | `main` | `develop` |
|---|---|---|
| Force pushes allowed | No | No |
| Branch deletion allowed | No | No |
| Required approving reviews | 0 | 0 |

The required approving review count is 0 because this is a single-user reference implementation: every Pull Request is self-approved by the same GitHub account (`adbautistar`), which is the one documented deviation from the taller specification (see [README.md](../README.md#reference-implementation-notice)).

## Branches Created

Ten branches existed over the life of the project. `main` and `develop` are permanent; every `feature/*` branch was deleted immediately after its Pull Request was merged, per [CLAUDE.md](../CLAUDE.md) rule 7.

| Branch | Purpose | Created from | Merged into | Status |
|---|---|---|---|---|
| `main` | Stable, release branch | (initial) | — | Active |
| `develop` | Integration branch | `main` | — | Active |
| `feature/analysis-design` | Phase 2 — analysis & design docs | `develop` | `develop` (PR #1) | Deleted |
| `feature/team-docs` | Phase 3 — team organization, README | `develop` | `develop` (PR #2) | Deleted |
| `feature/maven-setup` | Phase 4 — Maven config, package skeleton | `develop` | `develop` (PR #3) | Deleted |
| `feature/product-module` | Phase 5 — Product module (Developer 1) | `develop` | `develop` (PR #4) | Deleted |
| `feature/person-module` | Phase 6 — Person module (Developer 2) | `develop` | `develop` (PR #5) | Deleted |
| `feature/sale-module` | Phase 7 — Sale module, UI, Main (Technical Lead) | `develop` | `develop` (PR #6) | Deleted |
| `feature/integration-test` | Phase 8 — integration test outcome log | `develop` | `develop` (PR #7) | Deleted |
| `feature/final-docs` | Phase 9 — final README, JavaDoc/diagram fixes | `develop` | `develop` (PR #9) | Deleted |

`develop` was additionally merged into `main` twice, directly (no intermediate feature branch), as release Pull Requests: PR #8 (`v1.0.0`) and PR #10 (`v1.0.1`).

At the time of writing, `git branch -a` shows only `main` and `develop` — no feature branches remain, confirming rule 7 was followed throughout.

## Pull Requests

All Pull Requests were self-approved and merged with a regular merge (not squash or rebase), preserving full commit history, per [CLAUDE.md](../CLAUDE.md) rules 5 and 6. "Reviewed by" is the same single GitHub account (`adbautistar`) for every PR — the documented single-user deviation.

| # | Title | Branch | Target | Merged (UTC) | Reviewed by |
|---|---|---|---|---|---|
| [#1](https://github.com/adbautistar/GameZoneUnicesar/pull/1) | docs: analysis and design documentation | `feature/analysis-design` | `develop` | 2026-09-01 22:06 | adbautistar (self) |
| [#2](https://github.com/adbautistar/GameZoneUnicesar/pull/2) | docs: team organization and README | `feature/team-docs` | `develop` | 2026-09-01 22:09 | adbautistar (self) |
| [#3](https://github.com/adbautistar/GameZoneUnicesar/pull/3) | chore: maven setup and four-layer package structure | `feature/maven-setup` | `develop` | 2026-09-02 13:33 | adbautistar (self) |
| [#4](https://github.com/adbautistar/GameZoneUnicesar/pull/4) | feat: implement product module | `feature/product-module` | `develop` | 2026-09-02 13:40 | adbautistar (self) |
| [#5](https://github.com/adbautistar/GameZoneUnicesar/pull/5) | feat: implement person module | `feature/person-module` | `develop` | 2026-09-02 13:47 | adbautistar (self) |
| [#6](https://github.com/adbautistar/GameZoneUnicesar/pull/6) | feat: implement sale module, console UI and application entry point | `feature/sale-module` | `develop` | 2026-09-02 13:59 | adbautistar (self) |
| [#7](https://github.com/adbautistar/GameZoneUnicesar/pull/7) | docs: log end-to-end integration test outcome | `feature/integration-test` | `develop` | 2026-09-02 14:12 | adbautistar (self) |
| [#8](https://github.com/adbautistar/GameZoneUnicesar/pull/8) | release: v1.0.0 first functional increment of GameZone Unicesar | `develop` | `main` | 2026-09-02 14:12 | adbautistar (self) |
| [#9](https://github.com/adbautistar/GameZoneUnicesar/pull/9) | docs: complete final documentation and javadoc coverage | `feature/final-docs` | `develop` | 2026-09-02 14:17 | adbautistar (self) |
| [#10](https://github.com/adbautistar/GameZoneUnicesar/pull/10) | release: v1.0.1 final documentation | `develop` | `main` | 2026-09-02 14:18 | adbautistar (self) |

**Total merged Pull Requests: 10** (minimum required by the taller: 6).

## Commits by Team Member

Counts below are `git shortlog -sn main` — every commit reachable from `main`, i.e. the full, final history.

| Simulated identity | Git author | Commits on `main` | Role / Module |
|---|---|---|---|
| Developer 1 | `Dev1 Simulated <dev1@gamezone-ref.local>` | 12 | Product module |
| Developer 2 | `Dev2 Simulated <dev2@gamezone-ref.local>` | 12 | Person module |
| Technical Lead | `alfredo Bautista <adbautista@unicesar.edu.co>` | 30 | Setup, Sale module, UI, Main, all documentation |
| Technical Lead (merge commits) | `adbautistar` (GitHub account, via PR merge) | 10 | 10 Pull Request merges |

**Total commits on `main`: 64** (minimum required by the taller: 36; minimum per developer: 12).

Note on the two Technical Lead rows: commits authored with the local git identity (`alfredo Bautista <adbautista@unicesar.edu.co>`) are regular commits made with `git commit`. The 10 merge commits are attributed to the GitHub account `adbautistar` instead, because `gh pr merge` creates the merge commit through the GitHub REST API using the authenticated account's identity rather than the local `git config` identity — both represent the same person (Alfredo David Bautista Romero), simulating the Technical Lead role, per the commit attribution strategy in [CLAUDE.md](../CLAUDE.md).

Dev1 and Dev2 commits use the `--author` flag with a distinct simulated identity for each, while remaining signed under the real user's GitHub authentication when pushed — also per the commit attribution strategy in [CLAUDE.md](../CLAUDE.md).

## Full Commit Log

Chronological, oldest first, as it appears on `main` (`git log main --pretty="%h %ad %an: %s" --date=short`, reversed).

| Hash | Date | Author | Message |
|---|---|---|---|
| `82862dc` | 2026-09-01 | alfredo Bautista | chore: initialize repository with license, gitignore, readme and claude context |
| `62cadc3` | 2026-09-01 | alfredo Bautista | docs: add analysis.md with answers to orienting questions |
| `8af4c25` | 2026-09-01 | alfredo Bautista | docs: add hierarchy diagram for model layer |
| `71bf839` | 2026-09-01 | alfredo Bautista | docs: add full class diagram grouped by layer |
| `357fc65` | 2026-09-01 | alfredo Bautista | docs: add layer dependency diagram |
| `d910cff` | 2026-09-01 | adbautistar | Merge pull request #1 from adbautistar/feature/analysis-design |
| `7453211` | 2026-09-01 | alfredo Bautista | docs: add TEAM.md with roles, modules and activity commitments |
| `be7c9ef` | 2026-09-01 | alfredo Bautista | docs: rewrite README with project sections and links |
| `c76837d` | 2026-09-01 | alfredo Bautista | docs: initialize AI usage logs for three simulated roles |
| `1da676a` | 2026-09-01 | adbautistar | Merge pull request #2 from adbautistar/feature/team-docs |
| `298fff2` | 2026-09-02 | alfredo Bautista | chore: add maven pom.xml with java 17 and exec plugin |
| `4ed3e85` | 2026-09-02 | alfredo Bautista | chore: create four-layer package structure with gitkeep |
| `908cbcb` | 2026-09-02 | alfredo Bautista | chore: add data folder for file-based persistence |
| `c36131c` | 2026-09-02 | alfredo Bautista | feat: add Main class stub as application entry point |
| `17941d8` | 2026-09-02 | alfredo Bautista | docs: log AI usage for maven setup in leader-ai-log |
| `c4fe0ed` | 2026-09-02 | adbautistar | Merge pull request #3 from adbautistar/feature/maven-setup |
| `a8e4625` | 2026-09-02 | Dev1 Simulated | feat(product): add abstract Product class with common attributes |
| `dfb350a` | 2026-09-02 | Dev1 Simulated | feat(product): add updateStock method to Product |
| `5c2fa56` | 2026-09-02 | Dev1 Simulated | feat(product): add abstract getDescription method to Product |
| `7d0ddc0` | 2026-09-02 | Dev1 Simulated | feat(product): add VideoGame subclass with platform, genre, ageRating |
| `01cbfc7` | 2026-09-02 | Dev1 Simulated | feat(product): implement getDescription in VideoGame |
| `48e778d` | 2026-09-02 | Dev1 Simulated | feat(product): add Console subclass with brand, model, generation |
| `5ef2cfe` | 2026-09-02 | Dev1 Simulated | feat(product): implement getDescription in Console |
| `4127fb8` | 2026-09-02 | Dev1 Simulated | feat(product): add ProductRepository with CSV persistence |
| `de8509c` | 2026-09-02 | Dev1 Simulated | feat(product): add ProductService with registration methods |
| `6b72a72` | 2026-09-02 | Dev1 Simulated | feat(product): add stock update and product lookup to ProductService |
| `bd90e0b` | 2026-09-02 | Dev1 Simulated | docs(product): add JavaDoc to product module classes |
| `169e3eb` | 2026-09-02 | Dev1 Simulated | docs(product): log AI usage for product module in developer1-ai-log |
| `0b14f5c` | 2026-09-02 | adbautistar | Merge pull request #4 from adbautistar/feature/product-module |
| `3771093` | 2026-09-02 | Dev2 Simulated | feat(person): add abstract Person class with common attributes |
| `732ad1d` | 2026-09-02 | Dev2 Simulated | feat(person): add getFullName method to Person |
| `1904a3e` | 2026-09-02 | Dev2 Simulated | feat(person): add Customer subclass with email |
| `84b1720` | 2026-09-02 | Dev2 Simulated | feat(person): add Seller subclass with employeeCode and shift |
| `3afc41c` | 2026-09-02 | Dev2 Simulated | feat(person): add PersonRepository customer persistence methods |
| `c7dd1e5` | 2026-09-02 | Dev2 Simulated | feat(person): add PersonRepository seller persistence methods |
| `5270c42` | 2026-09-02 | Dev2 Simulated | feat(person): add PersonService with customer registration |
| `f039773` | 2026-09-02 | Dev2 Simulated | feat(person): add customer and seller lookup to PersonService |
| `24d3df7` | 2026-09-02 | Dev2 Simulated | feat(person): add customer and seller listing to PersonService |
| `4163aa5` | 2026-09-02 | Dev2 Simulated | chore(person): add preloaded sellers csv with three entries |
| `603e0b2` | 2026-09-02 | Dev2 Simulated | docs(person): add JavaDoc to person module classes |
| `8266e03` | 2026-09-02 | Dev2 Simulated | docs(person): log AI usage for person module in developer2-ai-log |
| `260c332` | 2026-09-02 | adbautistar | Merge pull request #5 from adbautistar/feature/person-module |
| `1133de7` | 2026-09-02 | alfredo Bautista | feat(sale): add Sale class with attributes and constructor |
| `44f4183` | 2026-09-02 | alfredo Bautista | feat(sale): add calculateTotal method to Sale |
| `245b13e` | 2026-09-02 | alfredo Bautista | feat(sale): add generateReceipt method to Sale |
| `73cf62f` | 2026-09-02 | alfredo Bautista | refactor(person): add purchaseHistory field to Customer for sale integration |
| `84e8e49` | 2026-09-02 | alfredo Bautista | feat(sale): add SaleRepository with CSV persistence and reference resolution |
| `84d9506` | 2026-09-02 | alfredo Bautista | feat(sale): add SaleService with registerSale validation and stock update |
| `5ba798d` | 2026-09-02 | alfredo Bautista | feat(sale): add sales history queries to SaleService |
| `87f6869` | 2026-09-02 | alfredo Bautista | feat(ui): add ConsoleMenu with main menu in Spanish |
| `da403d7` | 2026-09-02 | alfredo Bautista | feat(ui): add product submenu with three operations |
| `b2d7b8f` | 2026-09-02 | alfredo Bautista | feat(ui): add person submenu with three operations |
| `cd6749e` | 2026-09-02 | alfredo Bautista | feat(ui): add sale submenu with four operations |
| `bbc40f5` | 2026-09-02 | alfredo Bautista | feat(main): wire dependencies and launch console menu in Main |
| `6068009` | 2026-09-02 | alfredo Bautista | docs(leader): log AI usage for sale module integration and ui |
| `89eabb3` | 2026-09-02 | alfredo Bautista | docs(leader): add missing fourth AI usage entry for sale module phase |
| `d401ffd` | 2026-09-02 | adbautistar | Merge pull request #6 from adbautistar/feature/sale-module |
| `017c4a0` | 2026-09-02 | alfredo Bautista | docs(leader): log integration test outcome |
| `c332c9c` | 2026-09-02 | adbautistar | Merge pull request #7 from adbautistar/feature/integration-test |
| `cd5db10` | 2026-09-02 | adbautistar | Merge pull request #8 from adbautistar/develop |
| `dec7570` | 2026-09-02 | alfredo Bautista | docs: rewrite README with full sections and links |
| `d13dc6d` | 2026-09-02 | alfredo Bautista | docs: fix class diagram to match actual implementation |
| `41ef60c` | 2026-09-02 | adbautistar | Merge pull request #9 from adbautistar/feature/final-docs |
| `5795759` | 2026-09-02 | adbautistar | Merge pull request #10 from adbautistar/develop |

## Tags / Releases

Annotated tags mark the two functional increments released to `main`:

| Tag | Date | Commit | Message |
|---|---|---|---|
| `v1.0.0` | 2026-09-02 | `cd5db10` | First functional increment - all ten operations working |
| `v1.0.1` | 2026-09-02 | `5795759` | Final documentation release |

## Quantitative Summary

| Metric | Required (taller minimum) | Actual |
|---|---|---|
| Total commits on `main` | ≥ 36 | 64 |
| Commits — Developer 1 (`Dev1 Simulated`) | ≥ 12 | 12 |
| Commits — Developer 2 (`Dev2 Simulated`) | ≥ 12 | 12 |
| Commits — Technical Lead (`alfredo Bautista` + merge commits) | ≥ 12 | 40 |
| Merged Pull Requests | ≥ 6 | 10 |
| Feature branches remaining | 0 | 0 |
| Release tags | — | `v1.0.0`, `v1.0.1` |

*This document reflects the state of the repository as of 2026-09-02. Regenerate it (or extend it) whenever new commits, branches, PRs, or tags are added, so it stays a source of truth rather than a stale snapshot.*
