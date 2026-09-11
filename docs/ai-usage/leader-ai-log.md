# AI Usage Log — Technical Lead

This log records AI-assisted decisions taken by the Technical Lead role during the development of GameZone Unicesar reference implementation.

### Entry 1

**Date:** 2026-09-02
**Tool used:** Claude Code

**Reason for use:**
Set up the Maven project descriptor and the four-layer package skeleton so subsequent module work has a compilable base to build on.

**Problem faced:**
The repository still contained a NetBeans-generated pom.xml and a `game.gamezoneunicesar` package left over from project creation, which used the wrong groupId, wrong Java version property style, and the wrong root package for the `com.gamezone` architecture defined in CLAUDE.md.

**Prompt used:**
ejecuta la fase 4

**Solution obtained and decision taken:**
Replaced pom.xml with a descriptor using groupId `com.gamezone`, artifactId `gamezone-unicesar`, version `1.0.0-SNAPSHOT`, Java 17 source/target properties, and the exec-maven-plugin (3.1.0) configured with `com.gamezone.Main` as the main class. Deleted the obsolete `game.gamezoneunicesar` package and created the four target packages (`model`, `persistence`, `service`, `ui`) under `com.gamezone` with `.gitkeep` placeholders, a `data/` folder for file persistence, and a `Main` stub that prints a pending-initialization message. Verified the setup with `mvn clean compile`, which returned `BUILD SUCCESS`.

### Entry 2

**Date:** 2026-09-02
**Tool used:** Claude Code

**Reason for use:**
Design the Sale class and integrate it with the already-merged Product and Person modules without reopening either module's own files beyond a single targeted change.

**Problem faced:**
Sale needed read-only references to Customer, Seller, and a list of Product, and Customer needed a purchaseHistory collection that could only be added now that Sale exists, without disturbing the Customer API that Developer 2 had already delivered in Phase 6.

**Prompt used:**
ejecuta la fase 7

**Solution obtained and decision taken:**
Sale was implemented with only a constructor and getters for id, date, customer, seller, and products (no setters, matching the association/aggregation relationships documented in docs/analysis.md Q5), plus calculateTotal() and generateReceipt() as the two behaviors the model owns per Q6. Customer.java received a strictly additive edit: a purchaseHistory field initialized in the constructor, a getter, and an addPurchase(Sale) method, leaving every previously existing member untouched, as required by the phase constraints.

### Entry 3

**Date:** 2026-09-02
**Tool used:** Claude Code

**Reason for use:**
Decide how SaleRepository should reconstruct Sale objects from CSV given that a sale only stores ids for its customer, seller, and products, not full records.

**Problem faced:**
Unlike Product and Person, a Sale's persisted form is fundamentally relational (ids referencing other entities), so loadAll() cannot build a Sale from its own file alone — it needs a way to resolve those ids back into live objects, and those objects must be the same instances ProductService and PersonService already hold in memory so that later stock updates and purchase history stay consistent.

**Prompt used:**
ejecuta la fase 7

**Solution obtained and decision taken:**
SaleRepository takes ProductService and PersonService as constructor dependencies and calls their findById/findCustomerById/findSellerById methods to resolve each reference while parsing a CSV line, throwing a RuntimeException if any id cannot be resolved (a corrupted or hand-edited data file). Chose to recompute totalAmount via sale.calculateTotal() after reconstruction instead of trusting the stored column, since Sale exposes no setter for it and recomputing from the resolved products keeps the value always consistent with the model's own logic. Also call customer.addPurchase(sale) during reconstruction so purchaseHistory is repopulated in memory after a restart, even though it is not itself persisted to customers.csv.

### Entry 4

**Date:** 2026-09-02
**Tool used:** Claude Code

**Reason for use:**
Design the registerSale validation order and the ConsoleMenu error-handling pattern so business rule violations surface as clear Spanish messages instead of stack traces.

**Problem faced:**
registerSale has several distinct failure modes (empty product list, unknown customer/seller/product, insufficient stock for a repeated product) that each need a specific Spanish message, and the UI layer needed one consistent way to catch and display all of them without duplicating try/catch logic across ten menu operations.

**Prompt used:**
ejecuta la fase 7

**Solution obtained and decision taken:**
SaleService.registerSale validates in order — empty list, then customer, then seller, then each product, then stock per distinct product id (counting repeated occurrences via a HashMap before comparing against product.getStock()) — throwing IllegalArgumentException with a Spanish message at the first failure. Every ConsoleMenu operation method wraps its service call in its own try/catch(RuntimeException), printing e.getMessage() directly, since IllegalArgumentException messages are already written in Spanish; this keeps error handling local to each operation instead of one global handler, matching the phase's per-operation requirement.

### Entry 5

**Date:** 2026-09-02
**Tool used:** Claude Code

**Reason for use:**
Wire the four layers together in Main and validate the full application end to end before opening the Phase 7 pull request.

**Problem faced:**
Main had to instantiate repositories and services in the correct dependency order (SaleRepository and SaleService both need ProductService and PersonService already constructed), and the phase required a manual runtime test of all 10 mandatory operations plus a restart to confirm file-based persistence before the PR could be opened.

**Prompt used:**
ejecuta la fase 7

**Solution obtained and decision taken:**
Main constructs ProductRepository and PersonRepository first, then ProductService and PersonService, then SaleRepository and SaleService, then ConsoleMenu, wrapping the whole call to consoleMenu.start() in a try/catch(RuntimeException) that prints "Error fatal: " plus the message and exits with code 1 on an unrecoverable failure. Ran two full `mvn exec:java` sessions piping a scripted sequence of menu inputs: the first registered a video game, a console, and a customer, executed a sale, and exercised every list/history view; the second, run after restarting the process, listed products/customers/sellers and the sale history again to confirm data/products.csv, data/customers.csv, and data/sales.csv persisted correctly, including the decremented stock and the reconstructed sale receipt. All 10 mandatory operations passed before the PR was opened, so no additional fix commits were needed.

### Entry 6

**Date:** 2026-09-02
**Tool used:** Claude Code

**Reason for use:**
Run a full clean end-to-end integration test on develop, after all three modules were merged, before releasing v1.0.0 to main.

**Problem faced:**
Phase 7's manual test already covered the same 10 operations, but it ran against the feature branch in isolation; Phase 8 required repeating the test from a clean data state (only the preloaded sellers.csv present) directly on the integrated develop branch, to confirm nothing broke during the three merges (product, person, sale modules).

**Prompt used:**
ejecuta la fase 8

**Solution obtained and decision taken:**
Deleted data/products.csv, data/customers.csv, and data/sales.csv (data/sellers.csv kept), ran `mvn clean compile` (BUILD SUCCESS), then executed the 10 mandatory operations in order via a scripted `mvn exec:java` session using the example data from the phase document (VG001 "The Legend of Zelda", CN001 "Nintendo Switch OLED", customer C001 "Juan Rodriguez", seller S001). All 10 operations passed on the first attempt, so no hotfix commits were needed. Restarted the application and re-ran options 3, 5, 6, and 8: products, customers, sellers, and the sale history all persisted correctly, with VG001 and CN001 stock each reduced by exactly 1 (5→4 and 3→2) as expected. The test-generated CSV files were deleted again afterward, since they are local runtime artifacts, not deliverables, matching the same cleanup done in Phase 7.

### Entry 7

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Integrate the Accessory module (Task Groups A and B, already merged into this branch) into SaleService so a sale can include accessories alongside products, without changing registerSale's public parameter types.

**Problem faced:**
During the pre-execution review of fase-10-modulo-accesorios.md, found that the existing stock-validation loop in registerSale re-queried `productService.findById(id)` a second time by id to read stock — that lookup returns null (and NPEs) for an accessory id, since accessories live in a separate AccessoryService/AccessoryRepository, not ProductService's catalog.

**Prompt used:**
ejecuta la fase 10

**Solution obtained and decision taken:**
Added an AccessoryService dependency to SaleService's constructor. Item resolution now tries productService.findById first, falling back to accessoryService.findById, matching the existing IS-A relationship (Accessory extends Product, so it fits directly into the existing List<Product> products field — no change needed in Sale). Fixed the stock-validation bug by building a Map<String, Product> from the already-resolved items during the resolution loop, and reading stock from that map instead of re-querying either service by id; the stock-update loop dispatches to accessoryService.updateStock or productService.updateStock based on an instanceof Accessory check. Wired the new dependency through Main and ConsoleMenu in the following commits.

### Entry 8

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Design the accessory submenu and the sale-registration flow change so accessory ids and product ids can be entered interchangeably without confusing the user about which catalog an id belongs to.

**Problem faced:**
ConsoleMenu's registerSale flow only ever prompted for "product ids"; once accessories can also appear in a sale, the prompt text needed to make clear that either kind of id is accepted, and the new accessory submenu needed to mirror the product submenu's structure (register-by-type, list-all, list-filtered) plus one accessory-specific query (compatibility with a console) that has no product equivalent.

**Prompt used:**
ejecuta la fase 10

**Solution obtained and decision taken:**
Added main menu option 4 ("Gestion de accesorios") with a six-option submenu (register controller/cable/memory, list all, list by type, find compatible with a console), following the same try/catch(RuntimeException)-per-operation pattern established in Phase 7. Changed the sale-registration prompt to print "Puede ingresar identificadores de productos o accesorios." before asking for item ids, and reworded the per-item prompt from "ID del producto" to "ID del producto o accesorio", so the existing single-list input flow (which already worked, since SaleService now resolves against both catalogs) reads correctly to the user without requiring two separate input steps.

### Entry 9

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Fix a functional-verification failure: restarting the application after registering a sale that included an accessory crashed with "Failed to resolve product ACC010 for sale SALE-...".

**Problem faced:**
The pre-execution review (Entry 7) caught and fixed the SaleService stock-validation NPE, but missed a second, analogous gap: SaleRepository.fromCsvLine (used by loadAll() on every application start) only ever tried productService.findById to resolve a sale's item ids — it had no AccessoryService dependency at all, so it could not reconstruct a sale that included an accessory once the process restarted and reloaded data/sales.csv.

**Prompt used:**
ejecuta la fase 10

**Solution obtained and decision taken:**
Applied the exact same fallback pattern already used in SaleService and SaleRepository's existing customer/seller/product resolution: added AccessoryService as a third constructor dependency to SaleRepository, and in fromCsvLine, fall back to accessoryService.findById when productService.findById returns null for an item id. Updated Main's SaleRepository instantiation to pass accessoryService (already constructed earlier in the wiring order, so no reordering was needed). Re-ran the full functional verification (register accessories, register a sale with an accessory, restart, re-verify) end to end afterward to confirm the fix, per V3's instruction to repeat verification until all operations pass.

### Entry 10

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Wire PromotionService into SaleService, Main, and ConsoleMenu so registerSale applies the best active promotion automatically, matching fase-11's C1 specification.

**Problem faced:**
registerSale needed to insert the promotion step at a precise point — after calculateTotal() but before the stock-update loop — and Sale exposed no way to overwrite an already-computed total, so applying a discount required a new capability on Sale itself (added by Task Group A in this phase) rather than any change to how the total is originally calculated.

**Prompt used:**
ejecuta la fase 11

**Solution obtained and decision taken:**
Added PromotionService as SaleService's fourth constructor dependency. Right after sale.calculateTotal(), call promotionService.findBestPromotionFor(sale); when it returns a non-null Promotion, compute the discount via promotion.calculateDiscount(sale), record it on the sale (setAppliedPromotionName, setDiscountAmount), and overwrite the total with sale.setTotalAmount(originalTotal - discount) — the setter Task Group A added specifically for this. When no promotion applies, the sale is left completely untouched (appliedPromotionName stays null, discountAmount stays 0.0), so generateReceipt()'s existing single-Total-line behavior is preserved for undiscounted sales. Wired the same dependency through Main and ConsoleMenu, and added the five-option promotion submenu (menu option 5) with a numbered category-selection prompt for CategoryDiscount registration, following the exact per-operation try/catch(RuntimeException) pattern used everywhere else in ConsoleMenu.

### Entry 11

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Fix a persistence gap found during pre-execution review (before running the functional verification, unlike the Phase 10 gap which surfaced only when the app actually crashed): a discounted sale would silently lose its discount on the next application restart.

**Problem faced:**
SaleRepository.fromCsvLine always recomputes a sale's total via calculateTotal(), which sums the current product prices with no knowledge of any promotion, and the CSV format had no columns for appliedPromotionName or discountAmount at all — so after any restart, every previously discounted sale's receipt would silently show a higher, undiscounted total, with no error to signal that anything had changed.

**Prompt used:**
ejecuta la fase 11

**Solution obtained and decision taken:**
Extended the sales.csv format with two trailing columns (appliedPromotionName, using an empty string for "none"; discountAmount) written in toCsvLine. In fromCsvLine, after the usual calculateTotal() recomputes the pre-discount subtotal, if a non-empty promotion name is present the code re-applies the exact same three calls SaleService.registerSale uses (setAppliedPromotionName, setDiscountAmount, setTotalAmount(subtotal - discount)), so a reloaded sale's receipt matches the one originally printed at sale time. Guarded the column count (fields.length > 7) so the format stays backward-compatible with any pre-existing sales.csv row that predates this phase. Unlike the Phase 10 accessory-resolution bug, this one was caught by static review before running V2, not by a crash during it.
