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
