# Version Control — GameZone Unicesar

This document consolidates the version control activity of the project: the branching strategy, every branch created, every Pull Request merged, commits grouped by simulated team member and by phase, the full commit log, branch protection rules, and release tags. It is generated from the actual state of the repository (`git log`, `git tag`, and the GitHub API via `gh`), not reconstructed from memory.

**Snapshot:** commit `b694e0c` on `feature/grasp-refactor`, taken during Phase 15 just before the Technical Lead's GRASP correction PR and the `v1.5.0` release PR were opened. Those two PRs and the `v1.5.0` tag are listed in the Phase 15 notes below.

## Branching Strategy

The project follows a simplified Git Flow, as defined in [CLAUDE.md](../CLAUDE.md):

- **`main`** — protected, stable. Only receives merges from `develop` via Pull Request, at release increments.
- **`develop`** — protected, integration branch. Only receives merges from `feature/*` branches via Pull Request.
- **`feature/*`** — temporary, one per functional unit. Branches from `develop`, merges back into `develop`, and is deleted immediately after its last merge.

Three patterns were used over the life of the project:

- **Baseline (Phases 5-7):** one feature branch per role, each with its own PR.
- **Extended modules (Phases 10-13):** one shared feature branch per module; the three roles commit to it in sequential task groups and a single PR closes the module.
- **Requerimiento 7 (Phase 15):** one shared feature branch per part (audit, correction) with **three PRs per branch, one per role** (Developer 1, Developer 2, then the Technical Lead); the branch is deleted after the third PR.

Both `main` and `develop` have branch protection enabled on GitHub:

| Setting | `main` | `develop` |
|---|---|---|
| Force pushes allowed | No | No |
| Branch deletion allowed | No | No |
| Required approving reviews | 0 | 0 |

The required approving review count is 0 because this is a single-user reference implementation: every Pull Request is self-approved by the same GitHub account (`adbautistar`), which is the one documented deviation from the taller specification (see [README.md](../README.md#reference-implementation-notice)).

## Branches Created

21 branches existed over the life of the project. `main` and `develop` are permanent; every `feature/*` branch was deleted immediately after its last Pull Request was merged, per [CLAUDE.md](../CLAUDE.md) rule 7.

| Branch | Purpose | Created from | Merged into | Status |
|---|---|---|---|---|
| `main` | Stable, release branch | (initial) | — | Active |
| `develop` | Integration branch | `main` | — | Active |
| `feature/analysis-design` | Phase 2 — analysis and design docs | `develop` | `develop` (PR #1) | Deleted |
| `feature/team-docs` | Phase 3 — team organization, README | `develop` | `develop` (PR #2) | Deleted |
| `feature/maven-setup` | Phase 4 — Maven config, package skeleton | `develop` | `develop` (PR #3) | Deleted |
| `feature/product-module` | Phase 5 — Product module (Developer 1) | `develop` | `develop` (PR #4) | Deleted |
| `feature/person-module` | Phase 6 — Person module (Developer 2) | `develop` | `develop` (PR #5) | Deleted |
| `feature/sale-module` | Phase 7 — Sale module, UI, Main (Technical Lead) | `develop` | `develop` (PR #6) | Deleted |
| `feature/integration-test` | Phase 8 — integration test outcome log | `develop` | `develop` (PR #7) | Deleted |
| `feature/final-docs` | Phase 9 — final README, JavaDoc/diagram fixes | `develop` | `develop` (PR #9) | Deleted |
| `feature/version-control-docs` | Phase 9+ — this version control document | `develop` | `develop` (PR #11) | Deleted |
| `feature/extend-scope-v2` | Phases 10-13 — CLAUDE.md and TEAM.md scope extension | `develop` | `develop` (PR #13) | Deleted |
| `feature/accessory-module` | Phase 10 — Accessory module (three task groups) | `develop` | `develop` (PR #14) | Deleted |
| `feature/promotion-module` | Phase 11 — Promotion module (three task groups) | `develop` | `develop` (PR #16) | Deleted |
| `feature/return-module` | Phase 12 — Return module (three task groups) | `develop` | `develop` (PR #18) | Deleted |
| `feature/warranty-module` | Phase 13 — Warranty module (three task groups) | `develop` | `develop` (PR #20) | Deleted |
| `feature/test-suite` | Phase 14 — automated test suite | `develop` | `develop` (PR #22) | Deleted |
| `feature/grasp-audit` | Phase 15, part 1 — GRASP audit (Requerimiento 7) | `develop` | `develop` (PR #24, PR #25, PR #26) | Deleted |
| `feature/grasp-refactor` | Phase 15, part 2 — GRASP correction (Requerimiento 7) | `develop` | `develop` (PR #27, PR #28, PR #29) | Active (Phase 15, final PR pending) |
| `feature/solid-audit` |  | `develop` | `develop` (PR #31, PR #32, PR #33) | Deleted |
| `feature/solid-refactor` |  | `develop` | `develop` (PR #34, PR #35) | Deleted |

`develop` was merged into `main` 9 times through release Pull Requests (#8, #10, #12, #15, #17, #19, #21, #23, #30), one per tag.

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
| [#11](https://github.com/adbautistar/GameZoneUnicesar/pull/11) | docs: add version control documentation | `feature/version-control-docs` | `develop` | 2026-09-02 16:06 | adbautistar (self) |
| [#12](https://github.com/adbautistar/GameZoneUnicesar/pull/12) | release: v1.0.2 version control documentation | `develop` | `main` | 2026-09-02 16:07 | adbautistar (self) |
| [#13](https://github.com/adbautistar/GameZoneUnicesar/pull/13) | docs: extend CLAUDE.md and TEAM.md for modules 10-13 | `feature/extend-scope-v2` | `develop` | 2026-09-11 16:33 | adbautistar (self) |
| [#14](https://github.com/adbautistar/GameZoneUnicesar/pull/14) | feat: implement complete accessory module | `feature/accessory-module` | `develop` | 2026-09-11 20:15 | adbautistar (self) |
| [#15](https://github.com/adbautistar/GameZoneUnicesar/pull/15) | release: v1.1.0 accessory module | `develop` | `main` | 2026-09-11 20:16 | adbautistar (self) |
| [#16](https://github.com/adbautistar/GameZoneUnicesar/pull/16) | feat: implement complete promotion module | `feature/promotion-module` | `develop` | 2026-09-11 20:34 | adbautistar (self) |
| [#17](https://github.com/adbautistar/GameZoneUnicesar/pull/17) | release: v1.2.0 promotion module | `develop` | `main` | 2026-09-11 20:35 | adbautistar (self) |
| [#18](https://github.com/adbautistar/GameZoneUnicesar/pull/18) | feat: implement complete return module | `feature/return-module` | `develop` | 2026-09-11 20:48 | adbautistar (self) |
| [#19](https://github.com/adbautistar/GameZoneUnicesar/pull/19) | release: v1.3.0 return module | `develop` | `main` | 2026-09-11 20:49 | adbautistar (self) |
| [#20](https://github.com/adbautistar/GameZoneUnicesar/pull/20) | feat: implement complete warranty module | `feature/warranty-module` | `develop` | 2026-09-11 21:03 | adbautistar (self) |
| [#21](https://github.com/adbautistar/GameZoneUnicesar/pull/21) | release: v1.4.0 warranty module | `develop` | `main` | 2026-09-11 21:04 | adbautistar (self) |
| [#22](https://github.com/adbautistar/GameZoneUnicesar/pull/22) | test: add comprehensive automated test suite | `feature/test-suite` | `develop` | 2026-09-12 15:40 | adbautistar (self) |
| [#23](https://github.com/adbautistar/GameZoneUnicesar/pull/23) | release: v1.4.1 test suite | `develop` | `main` | 2026-09-12 15:41 | adbautistar (self) |
| [#24](https://github.com/adbautistar/GameZoneUnicesar/pull/24) | docs: developer1 GRASP audit | `feature/grasp-audit` | `develop` | 2026-10-13 15:33 | adbautistar (self) |
| [#25](https://github.com/adbautistar/GameZoneUnicesar/pull/25) | docs: developer2 GRASP audit | `feature/grasp-audit` | `develop` | 2026-10-13 15:35 | adbautistar (self) |
| [#26](https://github.com/adbautistar/GameZoneUnicesar/pull/26) | docs: leader GRASP audit and consolidated audit | `feature/grasp-audit` | `develop` | 2026-10-13 15:38 | adbautistar (self) |
| [#27](https://github.com/adbautistar/GameZoneUnicesar/pull/27) | refactor: developer1 GRASP corrections | `feature/grasp-refactor` | `develop` | 2026-10-13 15:44 | adbautistar (self) |
| [#28](https://github.com/adbautistar/GameZoneUnicesar/pull/28) | refactor: developer2 GRASP corrections | `feature/grasp-refactor` | `develop` | 2026-10-13 15:53 | adbautistar (self) |
| [#29](https://github.com/adbautistar/GameZoneUnicesar/pull/29) | refactor: leader GRASP corrections | `feature/grasp-refactor` | `develop` | 2026-10-13 16:08 | adbautistar (self) |
| [#30](https://github.com/adbautistar/GameZoneUnicesar/pull/30) | release: v1.5.0 GRASP audit and correction | `develop` | `main` | 2026-10-13 16:09 | adbautistar (self) |
| [#31](https://github.com/adbautistar/GameZoneUnicesar/pull/31) | docs: developer1 SOLID audit | `feature/solid-audit` | `develop` | 2026-10-14 05:10 | adbautistar (self) |
| [#32](https://github.com/adbautistar/GameZoneUnicesar/pull/32) | docs: developer2 SOLID audit | `feature/solid-audit` | `develop` | 2026-10-14 05:12 | adbautistar (self) |
| [#33](https://github.com/adbautistar/GameZoneUnicesar/pull/33) | docs: leader SOLID audit and consolidated audit | `feature/solid-audit` | `develop` | 2026-10-14 05:15 | adbautistar (self) |
| [#34](https://github.com/adbautistar/GameZoneUnicesar/pull/34) | refactor: developer1 SOLID corrections | `feature/solid-refactor` | `develop` | 2026-10-14 05:19 | adbautistar (self) |
| [#35](https://github.com/adbautistar/GameZoneUnicesar/pull/35) | refactor: developer2 SOLID corrections | `feature/solid-refactor` | `develop` | 2026-10-14 05:28 | adbautistar (self) |

**Total merged Pull Requests at this snapshot: 35** (minimum required by the taller: 6; Phase 15 requires 3 per part).

## Commits by Team Member

Counts of every commit reachable from the snapshot commit `b694e0c` (`git shortlog -sn HEAD`).

| Simulated identity | Git author | Commits | Role |
|---|---|---|---|
| Technical Lead | `alfredo Bautista <adbautista@unicesar.edu.co>` | 134 | Setup, Sale module, UI, Main, integration, documentation |
| Developer 2 | `Dev2 Simulated <dev2@gamezone-ref.local>` | 88 | Person module; repositories and services of Requerimientos 1-4 |
| Developer 1 | `Dev1 Simulated <dev1@gamezone-ref.local>` | 76 | Product module; model work of Requerimientos 1-4 |
| Technical Lead (merge commits) | `adbautistar` (GitHub account, via PR merge) | 26 | Pull Request merges |

**Total commits: 324.**

Note on the two Technical Lead rows: commits authored with the local git identity are regular commits made with `git commit`. Merge commits are attributed to the GitHub account `adbautistar`, because `gh pr merge` creates them through the GitHub REST API with the authenticated account's identity. Both represent the same person (Alfredo David Bautista Romero) simulating the Technical Lead role. Dev1 and Dev2 commits use the `--author` flag with a distinct simulated identity, per the commit attribution strategy in [CLAUDE.md](../CLAUDE.md).

## Commits by Phase

| Phase | Range | Developer 1 | Developer 2 | Technical Lead | Merge commits | Total |
|---|---|---|---|---|---|---|
| Taller 1 baseline (Phases 0-9) | `v1.0.2` | 12 | 12 | 31 | 12 | 67 |
| Phase 10 — Accessories | `v1.0.2..v1.1.0` | 6 | 6 | 10 | 3 | 25 |
| Phase 11 — Promotions | `v1.1.0..v1.2.0` | 6 | 6 | 7 | 2 | 21 |
| Phase 12 — Returns | `v1.2.0..v1.3.0` | 6 | 6 | 7 | 2 | 21 |
| Phase 13 — Warranties | `v1.3.0..v1.4.0` | 6 | 6 | 6 | 2 | 20 |
| Phase 14 — Test suite | `v1.4.0..v1.4.1` | 6 | 8 | 8 | 2 | 24 |
| Phase 15 — GRASP audit and correction (so far) | `v1.4.1..HEAD` | 34 | 44 | 65 | 11 | 154 |

Minimums: Taller 1 — 12 commits per role, 36 in total, 6 PRs. Phases 10-13 — 6 commits per task group (18 per phase). Phase 15 — 6 commits per role in each part, and 3 PRs per part.

Phase 15 detail:

| Part | Developer 1 | Developer 2 | Technical Lead | PRs |
|---|---|---|---|---|
| Part 1 — audit (`feature/grasp-audit`) | 8 | 9 | 10 | #24, #25, #26 |
| Part 2 — correction (`feature/grasp-refactor`, up to this snapshot) | 26 | 35 | 55 (including setup and the `Main` coordination commit) | #27, #28, and the Technical Lead's PR that contains this document |

## Full Commit Log

Chronological, oldest first, every commit reachable from `b694e0c` (`git log --reverse --pretty="%h %ad %an: %s" --date=short`).

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
| `dec7570` | 2026-09-02 | alfredo Bautista | docs: rewrite README with full sections and links |
| `d13dc6d` | 2026-09-02 | alfredo Bautista | docs: fix class diagram to match actual implementation |
| `41ef60c` | 2026-09-02 | adbautistar | Merge pull request #9 from adbautistar/feature/final-docs |
| `6b4f7b6` | 2026-09-02 | alfredo Bautista | docs: add version control documentation |
| `e3672ef` | 2026-09-02 | adbautistar | Merge pull request #11 from adbautistar/feature/version-control-docs |
| `cc45331` | 2026-09-11 | alfredo Bautista | docs: extend CLAUDE.md and TEAM.md for the four new modules (Phases 10-13) |
| `8692e7e` | 2026-09-11 | adbautistar | Merge pull request #13 from adbautistar/feature/extend-scope-v2 |
| `4e40bac` | 2026-09-11 | Dev1 Simulated | feat(accessory): add abstract Accessory class extending Product |
| `224a323` | 2026-09-11 | Dev1 Simulated | feat(accessory): add console compatibility methods to Accessory |
| `b8bba6f` | 2026-09-11 | Dev1 Simulated | feat(accessory): add Controller subclass with connection type |
| `2cd17d0` | 2026-09-11 | Dev1 Simulated | feat(accessory): add Cable subclass with length and connector type |
| `5f0cfc1` | 2026-09-11 | Dev1 Simulated | feat(accessory): add Memory subclass with capacity and memory type |
| `433d997` | 2026-09-11 | Dev1 Simulated | docs(accessory): log AI usage for accessory hierarchy in developer1-ai-log |
| `31d5951` | 2026-09-11 | Dev2 Simulated | feat(accessory): add AccessoryRepository with save method |
| `9da94e0` | 2026-09-11 | Dev2 Simulated | feat(accessory): implement loadAll with type discriminator |
| `32d5e0c` | 2026-09-11 | Dev2 Simulated | feat(accessory): add AccessoryService with registration methods |
| `10028f4` | 2026-09-11 | Dev2 Simulated | feat(accessory): add listing and filtering methods to AccessoryService |
| `46c7eeb` | 2026-09-11 | Dev2 Simulated | chore(accessory): add preloaded accessories csv with three entries |
| `1974e4e` | 2026-09-11 | Dev2 Simulated | docs(accessory): log AI usage for accessory persistence in developer2-ai-log |
| `0480d3a` | 2026-09-11 | alfredo Bautista | feat(sale): extend SaleService to accept accessories in sales |
| `a77a0d3` | 2026-09-11 | alfredo Bautista | feat(sale): dispatch stock updates to product or accessory service |
| `bafe919` | 2026-09-11 | alfredo Bautista | feat(main): wire AccessoryRepository, AccessoryService and inject into SaleService |
| `f3430cf` | 2026-09-11 | alfredo Bautista | feat(ui): add accessory management submenu |
| `1fe2f63` | 2026-09-11 | alfredo Bautista | docs: describe accessory module in README |
| `4e5095a` | 2026-09-11 | alfredo Bautista | docs(leader): log AI usage for accessory integration |
| `771b68f` | 2026-09-11 | alfredo Bautista | fix(sale): resolve accessory references when reloading sales.csv |
| `f1c8e08` | 2026-09-11 | alfredo Bautista | fix(main): pass accessoryService to updated SaleRepository constructor |
| `e53e511` | 2026-09-11 | alfredo Bautista | docs(leader): log AI usage for accessory sale-persistence fix |
| `c0dd409` | 2026-09-11 | adbautistar | Merge pull request #14 from adbautistar/feature/accessory-module |
| `9782251` | 2026-09-11 | Dev1 Simulated | feat(promotion): add abstract Promotion class with vigency method |
| `a806f7c` | 2026-09-11 | Dev1 Simulated | feat(promotion): add PercentageDiscount subclass |
| `e587b1c` | 2026-09-11 | Dev1 Simulated | feat(promotion): add CategoryDiscount subclass with instanceof filter |
| `6c1bbff` | 2026-09-11 | Dev1 Simulated | feat(promotion): add BulkPurchaseDiscount subclass with quantity threshold |
| `a9d5c29` | 2026-09-11 | Dev1 Simulated | feat(sale): add applied promotion name and discount amount to Sale |
| `b4161da` | 2026-09-11 | Dev1 Simulated | docs(promotion): log AI usage for promotion hierarchy in developer1-ai-log |
| `60e9e7f` | 2026-09-11 | Dev2 Simulated | feat(promotion): add PromotionRepository with save method |
| `d05cb9d` | 2026-09-11 | Dev2 Simulated | feat(promotion): implement loadAll with type discriminator |
| `6b4b89d` | 2026-09-11 | Dev2 Simulated | feat(promotion): add PromotionService with registration methods |
| `e91d70b` | 2026-09-11 | Dev2 Simulated | feat(promotion): implement findBestPromotionFor with polymorphic discount evaluation |
| `16d8543` | 2026-09-11 | Dev2 Simulated | chore(promotion): add preloaded promotions csv with three entries |
| `d7669ca` | 2026-09-11 | Dev2 Simulated | docs(promotion): log AI usage for promotion persistence in developer2-ai-log |
| `db3aab0` | 2026-09-11 | alfredo Bautista | feat(sale): apply best active promotion when registering a sale |
| `24818be` | 2026-09-11 | alfredo Bautista | feat(main): wire PromotionRepository, PromotionService and inject into SaleService |
| `cca027c` | 2026-09-11 | alfredo Bautista | feat(ui): add promotion management submenu |
| `0648dd6` | 2026-09-11 | alfredo Bautista | feat(ui): support target category selection in category promotion registration |
| `b5cdb85` | 2026-09-11 | alfredo Bautista | docs: describe promotion module in README |
| `b7e1fa8` | 2026-09-11 | alfredo Bautista | feat(sale): persist applied promotion name and discount amount |
| `dfb8683` | 2026-09-11 | alfredo Bautista | docs(leader): log AI usage for promotion integration |
| `2eacc1c` | 2026-09-11 | adbautistar | Merge pull request #16 from adbautistar/feature/promotion-module |
| `6be48dd` | 2026-09-11 | Dev1 Simulated | feat(return): add Return class with core attributes and constructor |
| `1f6ffc2` | 2026-09-11 | Dev1 Simulated | feat(return): implement calculateRefundAmount method |
| `5ca687e` | 2026-09-11 | Dev1 Simulated | feat(return): implement generateReturnReceipt method |
| `c8168e3` | 2026-09-11 | Dev1 Simulated | feat(sale): add canBeReturned method with 30-day rule |
| `6b702dc` | 2026-09-11 | Dev1 Simulated | docs(return): log AI usage for Return class design in developer1-ai-log |
| `0d4dbdd` | 2026-09-11 | Dev1 Simulated | docs(return): log AI usage for canBeReturned method in developer1-ai-log |
| `a7916c3` | 2026-09-11 | alfredo Bautista | feat(product): add restoreStock method |
| `d3017e3` | 2026-09-11 | alfredo Bautista | feat(sale): add findById lookup to SaleService |
| `5df0f01` | 2026-09-11 | Dev2 Simulated | feat(return): add ReturnRepository with save method |
| `a1d6bfa` | 2026-09-11 | Dev2 Simulated | feat(return): implement loadAll with sale and product resolution |
| `44eec81` | 2026-09-11 | Dev2 Simulated | feat(return): add ReturnService with registerReturn and validations |
| `10ac61a` | 2026-09-11 | Dev2 Simulated | feat(return): add view methods filtered by customer and by sale |
| `c5d8776` | 2026-09-11 | Dev2 Simulated | feat(return): implement monthly balance calculation |
| `e58b36c` | 2026-09-11 | Dev2 Simulated | docs(return): log AI usage for return persistence and service in developer2-ai-log |
| `da61b4c` | 2026-09-11 | alfredo Bautista | feat(main): wire ReturnRepository, ReturnService and inject into ConsoleMenu |
| `f7fb33e` | 2026-09-11 | alfredo Bautista | feat(ui): add return management submenu and monthly balance option |
| `4da6d47` | 2026-09-11 | alfredo Bautista | feat(ui): implement return registration flow with error handling |
| `f56b56f` | 2026-09-11 | alfredo Bautista | docs: describe return module and monthly balance in README |
| `1bb8f5d` | 2026-09-11 | alfredo Bautista | docs(leader): log AI usage for return integration |
| `245e2dd` | 2026-09-11 | adbautistar | Merge pull request #18 from adbautistar/feature/return-module |
| `ac78dfc` | 2026-09-11 | Dev1 Simulated | feat(warranty): add abstract Warranty class with polymorphic duration calculation |
| `7481f72` | 2026-09-11 | Dev1 Simulated | feat(warranty): add isActive vigency check to Warranty |
| `257c24f` | 2026-09-11 | Dev1 Simulated | feat(warranty): add generateWarrantyCertificate to Warranty |
| `147e4d2` | 2026-09-11 | Dev1 Simulated | feat(warranty): add BasicWarranty subclass with 6-month duration |
| `d050b57` | 2026-09-11 | Dev1 Simulated | feat(warranty): add ExtendedWarranty subclass with 12-month duration and 10 percent cost |
| `047c9f0` | 2026-09-11 | Dev1 Simulated | docs(warranty): log AI usage for warranty hierarchy in developer1-ai-log |
| `d2b0d04` | 2026-09-11 | Dev2 Simulated | feat(warranty): add WarrantyRepository with save method |
| `7680985` | 2026-09-11 | Dev2 Simulated | feat(warranty): implement loadAll with type discriminator and dependency resolution |
| `84b4186` | 2026-09-11 | Dev2 Simulated | feat(warranty): add WarrantyService with basic and extended warranty assignment |
| `474138a` | 2026-09-11 | Dev2 Simulated | feat(warranty): add findWarrantyByProduct and listing methods |
| `351c9fe` | 2026-09-11 | Dev2 Simulated | feat(warranty): implement listWarrantiesExpiringSoon with configurable days |
| `dbf03dc` | 2026-09-11 | Dev2 Simulated | docs(warranty): log AI usage for warranty persistence in developer2-ai-log |
| `cb17095` | 2026-09-11 | alfredo Bautista | feat(sale): assign automatic basic warranty and optional extended warranty on sale registration |
| `54be3bd` | 2026-09-11 | alfredo Bautista | feat(main): wire WarrantyRepository, WarrantyService and inject into SaleService |
| `f151d47` | 2026-09-11 | alfredo Bautista | feat(ui): prompt for extended warranty during sale registration |
| `22b6db5` | 2026-09-11 | alfredo Bautista | feat(ui): add warranty management submenu |
| `dadd769` | 2026-09-11 | alfredo Bautista | docs: describe warranty module in README |
| `0f8efa0` | 2026-09-11 | alfredo Bautista | docs(leader): log AI usage for warranty integration |
| `89715f5` | 2026-09-11 | adbautistar | Merge pull request #20 from adbautistar/feature/warranty-module |
| `d837ec3` | 2026-09-12 | alfredo Bautista | chore(test): add JUnit 5, AssertJ and Mockito dependencies |
| `27b1eae` | 2026-09-12 | alfredo Bautista | chore(test): configure JaCoCo with 70 percent coverage on service package |
| `f407647` | 2026-09-12 | alfredo Bautista | refactor(persistence): add file-path constructor overload to repositories for test isolation |
| `8f71ecd` | 2026-09-12 | alfredo Bautista | chore(test): scaffold test source and resources directories |
| `091784e` | 2026-09-12 | alfredo Bautista | test(support): add TestDataFactory with sample builders for all domain entities |
| `225aa78` | 2026-09-12 | alfredo Bautista | test(support): add TempFileHelper for isolated file operations in persistence tests |
| `8e25bae` | 2026-09-12 | alfredo Bautista | docs: document testing commands and log AI usage for test infrastructure |
| `b3b9216` | 2026-09-12 | Dev1 Simulated | test(model): add tests for Product hierarchy |
| `6051961` | 2026-09-12 | Dev1 Simulated | test(model): add tests for Accessory hierarchy and compatibility |
| `d64dd2c` | 2026-09-12 | Dev1 Simulated | test(model): add tests for Promotion hierarchy and discount calculations |
| `939c681` | 2026-09-12 | Dev1 Simulated | test(model): add tests for Return class and refund calculation |
| `bfa5aaa` | 2026-09-12 | Dev1 Simulated | test(model): add tests for canBeReturned 30-day rule |
| `ff1d71d` | 2026-09-12 | Dev1 Simulated | test(model): add tests for Warranty hierarchy with duration and cost |
| `ed4e4c6` | 2026-09-12 | Dev2 Simulated | test(persistence): add round-trip tests for all repositories |
| `abb9f96` | 2026-09-12 | Dev2 Simulated | test(service): add tests for ProductService including restoreStock |
| `6fcdbd7` | 2026-09-12 | Dev2 Simulated | test(service): add tests for AccessoryService with type filtering |
| `995e48d` | 2026-09-12 | Dev2 Simulated | test(service): add tests for PromotionService including validation and best selection |
| `6d888ef` | 2026-09-12 | Dev2 Simulated | test(service): add tests for ReturnService with mocked dependencies |
| `205492a` | 2026-09-12 | Dev2 Simulated | test(service): add tests for WarrantyService with duration and expiring soon |
| `4a0744a` | 2026-09-12 | Dev2 Simulated | test(integration): add end-to-end scenarios across modules |
| `3b1cce1` | 2026-09-12 | Dev2 Simulated | docs(test): log AI usage for persistence, service and integration tests in developer2-ai-log |
| `3866ce9` | 2026-09-12 | alfredo Bautista | docs(leader): report per-package coverage after test suite completion |
| `8ca31bf` | 2026-09-12 | adbautistar | Merge pull request #22 from adbautistar/feature/test-suite |
| `36aa4e2` | 2026-10-13 | Dev1 Simulated | docs(audit): add developer1 GRASP audit scope and product module findings |
| `edc681e` | 2026-10-13 | Dev1 Simulated | docs(audit): add accessory hierarchy findings to developer1 audit |
| `0938af8` | 2026-10-13 | Dev1 Simulated | docs(audit): add promotion hierarchy findings to developer1 audit |
| `d6b7172` | 2026-10-13 | Dev1 Simulated | docs(audit): add Return and canBeReturned findings to developer1 audit |
| `85d38fa` | 2026-10-13 | Dev1 Simulated | docs(audit): add warranty hierarchy findings to developer1 audit |
| `d708ffe` | 2026-10-13 | Dev1 Simulated | docs(audit): complete developer1 audit with not-applicable verdicts and summary |
| `87548cb` | 2026-10-13 | Dev1 Simulated | docs(audit): answer GRASP analysis questions 1 and 3 |
| `57be2a3` | 2026-10-13 | Dev1 Simulated | docs(audit): log AI usage for developer1 GRASP audit |
| `2d89370` | 2026-10-13 | adbautistar | Merge pull request #24 from adbautistar/feature/grasp-audit |
| `eda8ccf` | 2026-10-13 | Dev2 Simulated | docs(audit): add developer2 GRASP audit scope and person module findings |
| `a90dbde` | 2026-10-13 | Dev2 Simulated | docs(audit): add accessory repository and service findings to developer2 audit |
| `dae8e2e` | 2026-10-13 | Dev2 Simulated | docs(audit): add promotion repository and service findings to developer2 audit |
| `00ef0cc` | 2026-10-13 | Dev2 Simulated | docs(audit): add return repository and service findings to developer2 audit |
| `256e606` | 2026-10-13 | Dev2 Simulated | docs(audit): add warranty repository and service findings to developer2 audit |
| `140c39e` | 2026-10-13 | Dev2 Simulated | docs(audit): complete developer2 audit with cross-cutting findings and summary |
| `fe6f83a` | 2026-10-13 | Dev2 Simulated | docs(audit): answer GRASP analysis question 4 |
| `4645582` | 2026-10-13 | Dev2 Simulated | docs(audit): clarify test evidence criterion in GRASP question 4 |
| `47e8733` | 2026-10-13 | Dev2 Simulated | docs(audit): log AI usage for developer2 GRASP audit |
| `c9f12d1` | 2026-10-13 | adbautistar | Merge pull request #25 from adbautistar/feature/grasp-audit |
| `da5375d` | 2026-10-13 | alfredo Bautista | docs(audit): add leader GRASP audit scope and Sale findings |
| `6a51eb0` | 2026-10-13 | alfredo Bautista | docs(audit): add SaleRepository findings to leader audit |
| `edf700e` | 2026-10-13 | alfredo Bautista | docs(audit): add SaleService findings to leader audit |
| `9dfb337` | 2026-10-13 | alfredo Bautista | docs(audit): add ConsoleMenu findings to leader audit |
| `9f4864b` | 2026-10-13 | alfredo Bautista | docs(audit): complete leader audit with Main, restoreStock and summary |
| `bf103ba` | 2026-10-13 | alfredo Bautista | docs(audit): verify audit coverage and record team verdicts per GRASP pattern |
| `e9aadf7` | 2026-10-13 | alfredo Bautista | docs(audit): consolidate GRASP violations with definitive ids and owners |
| `b961637` | 2026-10-13 | alfredo Bautista | docs(audit): document boundary findings and out-of-scope defects |
| `38c838d` | 2026-10-13 | alfredo Bautista | docs(audit): answer GRASP analysis questions 2 and 5 |
| `4406a9d` | 2026-10-13 | alfredo Bautista | docs(audit): log AI usage for leader GRASP audit and consolidation |
| `7bf4931` | 2026-10-13 | adbautistar | Merge pull request #26 from adbautistar/feature/grasp-audit |
| `54961c7` | 2026-10-13 | alfredo Bautista | docs(refactor): define non-regression strategy and record baseline results |
| `d4e03ec` | 2026-10-13 | alfredo Bautista | docs(refactor): add before class diagram for GRASP correction |
| `3174829` | 2026-10-13 | Dev1 Simulated | refactor(model): let each product answer whether it belongs to a category (V-01) |
| `3e98b77` | 2026-10-13 | Dev1 Simulated | refactor(model): delegate category matching in CategoryDiscount to products (V-01) |
| `2812ca9` | 2026-10-13 | Dev1 Simulated | refactor(model): expose warranty eligibility polymorphically on Product (V-13) |
| `9dac17d` | 2026-10-13 | Dev1 Simulated | refactor(model): expose accessory type from each accessory subclass (V-02) |
| `c1873e9` | 2026-10-13 | Dev1 Simulated | refactor(model): add product lookup and month check to Sale for return queries (V-06, V-07) |
| `ed3f800` | 2026-10-13 | Dev1 Simulated | refactor(model): add ownership and period queries to Return (V-07) |
| `345c8d7` | 2026-10-13 | Dev1 Simulated | refactor(model): add coverage query to Warranty (V-08) |
| `dcafab6` | 2026-10-13 | Dev1 Simulated | docs(refactor): log developer1 GRASP corrections |
| `578ecae` | 2026-10-13 | Dev1 Simulated | docs(refactor): log AI usage for developer1 GRASP corrections |
| `843e02d` | 2026-10-13 | adbautistar | Merge pull request #27 from adbautistar/feature/grasp-refactor |
| `31f9476` | 2026-10-13 | Dev2 Simulated | refactor(service): extract identifier generation into IdGenerator (V-09) |
| `e9da721` | 2026-10-13 | Dev2 Simulated | refactor(service): introduce ItemCatalog as single entry point for sellable items (V-05) |
| `f37a6d2` | 2026-10-13 | Dev2 Simulated | refactor(service): filter accessories by their own type (V-02) |
| `1cba7a3` | 2026-10-13 | Dev2 Simulated | refactor(service): route ReturnService through ItemCatalog and domain queries (V-05, V-06, V-07) |
| `1909b8c` | 2026-10-13 | Dev2 Simulated | refactor(persistence): let ReturnRepository load references only and resolve them in ReturnService (V-03) |
| `f3ff88b` | 2026-10-13 | Dev2 Simulated | refactor(persistence): let WarrantyRepository load references only and resolve them in WarrantyService (V-04, V-08) |
| `7512a7b` | 2026-10-13 | alfredo Bautista | refactor(main): wire return and warranty services through ItemCatalog (V-03, V-04) |
| `c0a4b5f` | 2026-10-13 | Dev2 Simulated | refactor(persistence): remove transitional service-based repository API (V-03, V-04) |
| `29bff08` | 2026-10-13 | Dev2 Simulated | test(service): cover reference resolution moved from repositories into services |
| `7e02d73` | 2026-10-13 | Dev2 Simulated | docs(refactor): log developer2 GRASP corrections |
| `4e30ccd` | 2026-10-13 | Dev2 Simulated | docs(refactor): log AI usage for developer2 GRASP corrections |
| `598e55d` | 2026-10-13 | adbautistar | Merge pull request #28 from adbautistar/feature/grasp-refactor |
| `b2d3d9c` | 2026-10-13 | alfredo Bautista | refactor(model): let Sale apply promotions and surcharges to its own total (V-10) |
| `0bdcf6b` | 2026-10-13 | alfredo Bautista | refactor(service): ask Sale who made and handled it in history queries (V-16) |
| `819b498` | 2026-10-13 | alfredo Bautista | refactor(persistence): make SaleRepository persist references only and resolve them in SaleService (V-11) |
| `29eb2d4` | 2026-10-13 | alfredo Bautista | refactor(service): extract sale registration use case into SaleRegistrationService (V-12, V-13) |
| `a4d48b8` | 2026-10-13 | alfredo Bautista | refactor(ui): let the service layer decide warranty eligibility during sale registration (V-14) |
| `03867d5` | 2026-10-13 | alfredo Bautista | refactor(ui): extract product submenu from ConsoleMenu (V-15) |
| `8aa1963` | 2026-10-13 | alfredo Bautista | refactor(ui): extract people submenu from ConsoleMenu (V-15) |
| `8dd334d` | 2026-10-13 | alfredo Bautista | refactor(ui): extract sales submenu from ConsoleMenu (V-15) |
| `143e16f` | 2026-10-13 | alfredo Bautista | refactor(ui): extract accessory submenu from ConsoleMenu (V-15) |
| `d78894c` | 2026-10-13 | alfredo Bautista | refactor(ui): extract promotion submenu from ConsoleMenu (V-15) |
| `9918a3c` | 2026-10-13 | alfredo Bautista | refactor(ui): extract returns submenu from ConsoleMenu (V-15) |
| `a60734d` | 2026-10-13 | alfredo Bautista | refactor(ui): extract warranty submenu from ConsoleMenu (V-15) |
| `5ed00b6` | 2026-10-13 | alfredo Bautista | refactor(ui): order submenus as in the main menu and document ConsoleMenu delegation (V-15) |
| `2de8859` | 2026-10-13 | alfredo Bautista | test(service): cover sale reference resolution moved into SaleService |
| `ff70ad6` | 2026-10-13 | alfredo Bautista | docs(refactor): log leader GRASP corrections and final non-regression results |
| `2381c2c` | 2026-10-13 | alfredo Bautista | docs(refactor): state exactly when the intermediate non-regression runs were taken |
| `c41e547` | 2026-10-13 | alfredo Bautista | docs(refactor): clarify which baseline tests changed their assertions |
| `e23352f` | 2026-10-13 | alfredo Bautista | docs(refactor): add after class diagram for GRASP correction |
| `1db3bb2` | 2026-10-13 | alfredo Bautista | docs: update class and layer diagrams after GRASP correction |
| `9fcd045` | 2026-10-13 | alfredo Bautista | docs: describe GRASP audit and correction in README |
| `a277025` | 2026-10-13 | alfredo Bautista | docs: update CLAUDE.md and version control log for Phase 15 |
| `981ddd4` | 2026-10-13 | alfredo Bautista | docs(refactor): log AI usage for leader GRASP corrections |
| `6ef1add` | 2026-10-13 | alfredo Bautista | docs: simplify registerSale signature so the class diagram renders |
| `dd695ce` | 2026-10-13 | adbautistar | Merge pull request #29 from adbautistar/feature/grasp-refactor |
| `60fb135` | 2026-10-14 | Dev1 Simulated | docs(audit): add developer1 SOLID audit scope and product module findings |
| `a8b8402` | 2026-10-14 | Dev1 Simulated | docs(audit): add accessory hierarchy findings to developer1 SOLID audit |
| `0705210` | 2026-10-14 | Dev1 Simulated | docs(audit): add promotion hierarchy findings to developer1 SOLID audit |
| `f83d639` | 2026-10-14 | Dev1 Simulated | docs(audit): add Return and canBeReturned findings to developer1 SOLID audit |
| `5e7eacf` | 2026-10-14 | Dev1 Simulated | docs(audit): add warranty hierarchy findings to developer1 SOLID audit |
| `b0e6516` | 2026-10-14 | Dev1 Simulated | docs(audit): complete developer1 SOLID audit with GRASP additions and summary |
| `abc4a46` | 2026-10-14 | Dev1 Simulated | docs(audit): label the summary column of the developer1 SOLID audit as principle |
| `9c8b711` | 2026-10-14 | Dev1 Simulated | docs(audit): answer SOLID analysis questions 1 and 3 |
| `6b663be` | 2026-10-14 | Dev1 Simulated | docs(audit): log AI usage for developer1 SOLID audit |
| `8c8f72a` | 2026-10-14 | adbautistar | Merge pull request #31 from adbautistar/feature/solid-audit |
| `7df52f8` | 2026-10-14 | Dev2 Simulated | docs(audit): add developer2 SOLID audit scope and person module findings |
| `9c53135` | 2026-10-14 | Dev2 Simulated | docs(audit): add accessory repository and service findings to developer2 SOLID audit |
| `6344267` | 2026-10-14 | Dev2 Simulated | docs(audit): add promotion repository and service findings to developer2 SOLID audit |
| `6c803ae` | 2026-10-14 | Dev2 Simulated | docs(audit): add return repository and service findings to developer2 SOLID audit |
| `9352037` | 2026-10-14 | Dev2 Simulated | docs(audit): add warranty repository and service findings to developer2 SOLID audit |
| `21919e5` | 2026-10-14 | Dev2 Simulated | docs(audit): complete developer2 SOLID audit with GRASP additions and summary |
| `b8d5030` | 2026-10-14 | Dev2 Simulated | docs(audit): answer SOLID analysis question 4 |
| `844f7f7` | 2026-10-14 | Dev2 Simulated | docs(audit): log AI usage for developer2 SOLID audit |
| `6b8daa1` | 2026-10-14 | adbautistar | Merge pull request #32 from adbautistar/feature/solid-audit |
| `c938133` | 2026-10-14 | alfredo Bautista | docs(audit): add leader SOLID audit scope and Sale findings |
| `711438c` | 2026-10-14 | alfredo Bautista | docs(audit): add SaleRepository and SaleRecord findings to leader SOLID audit |
| `54ba853` | 2026-10-14 | alfredo Bautista | docs(audit): add sale service findings to leader SOLID audit |
| `8c51fef` | 2026-10-14 | alfredo Bautista | docs(audit): add console menu findings to leader SOLID audit |
| `085937f` | 2026-10-14 | alfredo Bautista | docs(audit): complete leader SOLID audit with Main, restoreStock and summary |
| `e563874` | 2026-10-14 | alfredo Bautista | docs(audit): verify SOLID audit coverage and record team verdicts per principle |
| `1e87c27` | 2026-10-14 | alfredo Bautista | docs(audit): consolidate SOLID violations with definitive ids and owners |
| `7ee14e0` | 2026-10-14 | alfredo Bautista | docs(audit): document SOLID boundary findings, accepted fragments and out-of-scope status |
| `366e11f` | 2026-10-14 | alfredo Bautista | docs(audit): answer SOLID analysis questions 2 and 5 |
| `0150660` | 2026-10-14 | alfredo Bautista | docs(audit): log AI usage for leader SOLID audit and consolidation |
| `123ba66` | 2026-10-14 | adbautistar | Merge pull request #33 from adbautistar/feature/solid-audit |
| `3ead9c9` | 2026-10-14 | alfredo Bautista | docs(refactor): define SOLID non-regression strategy and record baseline results |
| `3c46aac` | 2026-10-14 | alfredo Bautista | docs(refactor): add before class diagram for SOLID correction |
| `2c35c84` | 2026-10-14 | Dev1 Simulated | refactor(product): depend on a ProductStore abstraction in ProductService (V-10) |
| `88b44c4` | 2026-10-14 | Dev1 Simulated | refactor(model): move return receipt layout into ReturnReceipt (V-01) |
| `81b6435` | 2026-10-14 | Dev1 Simulated | refactor(model): delegate generateReturnReceipt to ReturnReceipt (V-01) |
| `069eefa` | 2026-10-14 | Dev1 Simulated | refactor(model): move warranty certificate layout into WarrantyCertificate (V-01) |
| `7a37100` | 2026-10-14 | Dev1 Simulated | refactor(model): let each warranty expose its kind and how to build it (V-05) |
| `8c467b1` | 2026-10-14 | Dev1 Simulated | docs(refactor): log developer1 SOLID corrections |
| `abcd5c4` | 2026-10-14 | Dev1 Simulated | docs(refactor): log AI usage for developer1 SOLID corrections |
| `7518791` | 2026-10-14 | adbautistar | Merge pull request #34 from adbautistar/feature/solid-refactor |
| `371058f` | 2026-10-14 | Dev2 Simulated | refactor(person): depend on a PersonStore abstraction in PersonService (V-11) |
| `2bbdbcc` | 2026-10-14 | Dev2 Simulated | refactor(accessory): depend on a AccessoryStore abstraction in AccessoryService (V-11) |
| `e041a00` | 2026-10-14 | Dev2 Simulated | refactor(promotion): depend on a PromotionStore abstraction in PromotionService (V-11) |
| `ee56dba` | 2026-10-14 | Dev2 Simulated | refactor(return): depend on a ReturnStore abstraction in ReturnService (V-11) |
| `753f70d` | 2026-10-14 | Dev2 Simulated | refactor(warranty): depend on a WarrantyStore abstraction in WarrantyService (V-11) |
| `846dd90` | 2026-10-14 | Dev2 Simulated | refactor(service): introduce ItemLookup for read-only item lookups (V-09) |
| `6122f0d` | 2026-10-14 | Dev2 Simulated | refactor(service): add ItemSource abstraction and implement it in AccessoryService (V-04) |
| `16a9fdf` | 2026-10-14 | Dev1 Simulated | refactor(product): implement ItemSource in ProductService (V-04) |
| `1e26194` | 2026-10-14 | Dev2 Simulated | refactor(service): let ItemCatalog delegate to item sources instead of checking types (V-04) |
| `bc10283` | 2026-10-14 | Dev2 Simulated | refactor(service): introduce SaleLookup read-only view of sales (V-07) |
| `80b200a` | 2026-10-14 | alfredo Bautista | refactor(service): implement SaleLookup in SaleService (V-07) |
| `6a474d8` | 2026-10-14 | Dev2 Simulated | refactor(service): depend on SaleLookup in return and warranty services (V-07) |
| `7978e6f` | 2026-10-14 | Dev2 Simulated | refactor(persistence): keep warranty kinds in WarrantyKind only (V-05) |
| `9336a26` | 2026-10-14 | Dev2 Simulated | refactor(service): extract monthly balance into MonthlyBalanceService (V-03) |
| `c836dda` | 2026-10-14 | alfredo Bautista | refactor(ui): ask MonthlyBalanceService for the monthly balance (V-03) |
| `9937ff4` | 2026-10-14 | Dev2 Simulated | refactor(service): remove monthly balance from ReturnService (V-03) |
| `fd12437` | 2026-10-14 | Dev2 Simulated | docs(refactor): log developer2 SOLID corrections |
| `f301cbd` | 2026-10-14 | Dev2 Simulated | docs(refactor): log AI usage for developer2 SOLID corrections |
| `598ef4f` | 2026-10-14 | adbautistar | Merge pull request #35 from adbautistar/feature/solid-refactor |
| `15326b2` | 2026-10-14 | alfredo Bautista | refactor(model): move sale receipt layout into SaleReceipt (V-02) |
| `d40fc1c` | 2026-10-14 | alfredo Bautista | refactor(sale): depend on a SaleStore abstraction in SaleService (V-12) |
| `cfe279a` | 2026-10-14 | alfredo Bautista | refactor(service): introduce PersonLookup for people lookups in sales (V-08) |
| `b286423` | 2026-10-14 | Dev2 Simulated | refactor(person): implement PersonLookup in PersonService (V-08) |
| `58f1b91` | 2026-10-14 | alfredo Bautista | refactor(service): depend on PersonLookup in the sales services (V-08) |
| `ed8c23c` | 2026-10-14 | alfredo Bautista | refactor(service): depend on ItemLookup in SaleService (V-09) |
| `8aa330d` | 2026-10-14 | alfredo Bautista | refactor(service): define PromotionSelector and WarrantyAssigner for sale registration (V-13) |
| `a041ac7` | 2026-10-14 | Dev2 Simulated | refactor(service): implement PromotionSelector and WarrantyAssigner in the promotion and warranty services (V-13) |
| `86d217b` | 2026-10-14 | alfredo Bautista | refactor(service): depend on PromotionSelector and WarrantyAssigner in SaleRegistrationService (V-13) |
| `cf4c18e` | 2026-10-14 | alfredo Bautista | refactor(ui): add MenuOption and implement it in every submenu (V-06) |
| `46fdb3a` | 2026-10-14 | alfredo Bautista | refactor(ui): move the monthly balance option into MonthlyBalanceMenu (V-06) |
| `7d7d33f` | 2026-10-14 | alfredo Bautista | refactor(ui): let ConsoleMenu number and run the options composed in Main (V-06) |
| `b5631d6` | 2026-10-14 | alfredo Bautista | docs(refactor): log leader SOLID corrections and final non-regression results |
| `00b1420` | 2026-10-14 | alfredo Bautista | docs(refactor): correct the number of coordination commits in the SOLID log |
| `4693865` | 2026-10-14 | alfredo Bautista | docs(refactor): add after class diagram for SOLID correction |
| `45d9650` | 2026-10-14 | alfredo Bautista | docs: update class and layer diagrams after SOLID correction |
| `b694e0c` | 2026-10-14 | alfredo Bautista | docs: describe SOLID audit and correction in README |

## Tags / Releases

Annotated tags mark each increment released to `main`:

| Tag | Date | Commit | Message |
|---|---|---|---|
| `v1.0.0` | 2026-09-02 | `cd5db10` | First functional increment - all ten operations working |
| `v1.0.1` | 2026-09-02 | `5795759` | Final documentation release |
| `v1.0.2` | 2026-09-02 | `38583b7` | Version control documentation added |
| `v1.1.0` | 2026-09-11 | `707ce47` | Accessory module release |
| `v1.2.0` | 2026-09-11 | `487709d` | Promotion module release |
| `v1.3.0` | 2026-09-11 | `344ee71` | Return module release |
| `v1.4.0` | 2026-09-11 | `99e772d` | Warranty module release |
| `v1.4.1` | 2026-09-12 | `05cd326` | Test suite patch release |
| `v1.5.0` | 2026-10-13 | `e6e4d67` | GRASP audit and correction |
| `v1.5.0` | — | — | Planned at the end of Phase 15: GRASP audit and correction |

## Quantitative Summary

| Metric | Required | Actual (this snapshot) |
|---|---|---|
| Total commits | ≥ 36 (Taller 1) | 324 |
| Commits — Developer 1 | ≥ 12 (Taller 1) | 76 |
| Commits — Developer 2 | ≥ 12 (Taller 1) | 88 |
| Commits — Technical Lead (incl. merges) | ≥ 12 (Taller 1) | 160 |
| Merged Pull Requests | ≥ 6 (Taller 1) | 35 |
| Feature branches remaining | 0 at the end of each phase | 1 (`feature/grasp-refactor`, deleted when its last PR merges) |
| Release tags | — | `v1.0.0`, `v1.0.1`, `v1.0.2`, `v1.1.0`, `v1.2.0`, `v1.3.0`, `v1.4.0`, `v1.4.1`, `v1.5.0` |

*Regenerate this document whenever new commits, branches, PRs or tags are added, so it stays a source of truth rather than a stale snapshot.*
