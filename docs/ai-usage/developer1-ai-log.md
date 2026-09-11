# AI Usage Log — Developer 1

This log records AI-assisted decisions taken by the Developer 1 role during the development of the Product module.

### Entry 1

**Date:** 2026-09-02
**Tool used:** Claude Code

**Reason for use:**
Implement the abstract Product class and its concrete VideoGame and Console subclasses so the inventory can represent both product types uniformly.

**Problem faced:**
Product.getDescription() had to be declared in a way that forces every subclass to provide its own type-specific description, while keeping the shared attributes and stock logic in a single place.

**Prompt used:**
ejecuta la fase 5

**Solution obtained and decision taken:**
Declared Product as an abstract class holding id, title, price, and stock with a concrete updateStock(int) method, and an abstract getDescription() method. VideoGame and Console extend Product, add their own private attributes (platform/genre/ageRating and brand/model/generation respectively), and each overrides getDescription() with a formatted string built from both the inherited and the subclass-specific fields. This follows the CLAUDE.md class inventory directly and matches the polymorphism decision documented in docs/analysis.md (Q4).

### Entry 2

**Date:** 2026-09-02
**Tool used:** Claude Code

**Reason for use:**
Design the CSV persistence format for ProductRepository so both product subtypes can be saved to and reconstructed from a single file.

**Problem faced:**
A single products.csv file needs to hold two different subclasses with different sets of extra columns, and loadAll() must reconstruct the correct concrete type without any additional metadata beyond the file's own content.

**Prompt used:**
ejecuta la fase 5

**Solution obtained and decision taken:**
Added a discriminator column (VIDEOGAME or CONSOLE) as the first field of every CSV row, followed by the four shared columns (id, title, price, stock) and then the three type-specific columns. loadAll() reads the discriminator first and branches to construct a VideoGame or a Console accordingly, and returns an empty ArrayList when data/products.csv does not exist yet, matching the requirement that the file auto-creates on first save without requiring a pre-existing file on first run.

### Entry 3

**Date:** 2026-09-02
**Tool used:** Claude Code

**Reason for use:**
Decide how ProductService should manage in-memory state versus the repository, and how updateStock/findById should be exposed for later use by SaleService.

**Problem faced:**
Every registration and stock update needs to be reflected both in memory (for the current session) and on disk (for the next run), and other modules will need a way to look up a product by id without duplicating that logic.

**Prompt used:**
ejecuta la fase 5

**Solution obtained and decision taken:**
ProductService loads the full product list once in its constructor and keeps it as the single in-memory source of truth, calling repository.saveAll(products) after every mutating operation (register, updateStock) so the file always reflects the latest state. findById(String) was kept public specifically so SaleService can resolve product references by id in Phase 7 without needing its own lookup logic, and listAllProducts() returns an unmodifiable view to prevent external code from mutating the internal list directly.

### Entry 4

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Design the Accessory hierarchy so it extends Product cleanly and can carry console-compatibility data that Product itself has no concept of.

**Problem faced:**
Accessories are products (they need id/title/price/stock and participate in sales like any Product), but they also need a piece of data no existing Product subclass has: which consoles they work with, and that list needs to be queryable both from a single accessory (isCompatibleWith) and across the whole catalog (AccessoryService.findAccessoriesCompatibleWith in Task Group B).

**Prompt used:**
ejecuta la fase 10

**Solution obtained and decision taken:**
Made Accessory an abstract class extending Product, adding only compatibleConsoleIds (a List<String>) plus addCompatibleConsole/isCompatibleWith, and left getDescription() unoverridden so it remains abstract exactly as Product declared it — Accessory does not need to redeclare the abstract method, only its concrete subclasses (Controller, Cable, Memory) do. This keeps Accessory poly­morphic with the rest of the Product family: SaleService and ConsoleMenu can treat an Accessory as a Product wherever a generic product reference is needed (e.g., inside a Sale's product list), while AccessoryService and AccessoryRepository (Task Group B) handle the accessory-specific catalog and persistence separately from ProductService/ProductRepository.

### Entry 5

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Decide each accessory subclass's specific attributes and how getDescription() should present them, consistent with the pattern already used by VideoGame and Console.

**Problem faced:**
Controller, Cable, and Memory each needed a small set of type-specific attributes (connection type; length and connector type; capacity and memory type respectively) and a getDescription() implementation that reads naturally and mirrors the format already established by VideoGame/Console in Phase 5.

**Prompt used:**
ejecuta la fase 10

**Solution obtained and decision taken:**
Followed the exact constructor pattern from VideoGame/Console: each subclass's constructor takes the shared Product/Accessory parameters plus its own specific ones, calling super(...) first. getDescription() in each subclass reuses getId()/getTitle()/getPrice() from the inherited hierarchy and appends its own fields (connection type for Controller; length and connector for Cable; capacity and type for Memory), plus the inherited compatible-console list, so all three read consistently with each other and with the existing product descriptions.

### Entry 6

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Design the Promotion hierarchy so three unrelated discount strategies (flat percentage, category-restricted, bulk-quantity-gated) share one polymorphic contract that SaleService can evaluate without knowing which strategy it is calling.

**Problem faced:**
Each promotion type computes its discount from a different subset of the sale's data — the whole total, only certain products by category, or nothing at all unless a quantity threshold is met — but SaleService (Task Group C) needs to compare all of them uniformly to pick the best one, so every subclass has to return a plain currency amount from the same method signature.

**Prompt used:**
ejecuta la fase 11

**Solution obtained and decision taken:**
Declared Promotion as an abstract class holding id/name/startDate/endDate with a concrete isActive(LocalDate) vigency check, and one abstract calculateDiscount(Sale) method that every subclass implements independently: PercentageDiscount multiplies the sale's full total; CategoryDiscount filters sale.getProducts() with instanceof VideoGame/Console before summing and discounting only the matching subtotal; BulkPurchaseDiscount checks sale.getProducts().size() against a minimum before applying its percentage, returning 0.0 otherwise. Because all three return a plain double regardless of strategy, PromotionService.findBestPromotionFor (Task Group B) can iterate over any list of Promotion and compare calculateDiscount(sale) results directly.

### Entry 7

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Decide how to store and display a promotion's effect on Sale without touching the existing calculateTotal()/generateReceipt() contract that SaleRepository, ConsoleMenu, and earlier phases already depend on.

**Problem faced:**
Sale had no way to record that a discount was applied, and no setter for totalAmount at all — but the discount needs to be visible both in memory (for SaleService to apply it) and in the printed receipt, and Sale's existing constructor and calculateTotal() are used throughout the codebase and were off-limits per this phase's additive-only constraint.

**Prompt used:**
ejecuta la fase 11

**Solution obtained and decision taken:**
Added appliedPromotionName and discountAmount as new fields with plain getters/setters (defaulting to null/0.0, exactly like every other field added additively in this project), plus a setTotalAmount(double) setter Sale never had before, so Task Group C can overwrite the already-calculated total with (total - discount) instead of Sale needing to know about promotions itself. generateReceipt() now branches: when a promotion was applied (appliedPromotionName != null and discountAmount > 0) it prints a Subtotal/Descuento/Total final breakdown instead of the single Total line, computing the subtotal as totalAmount + discountAmount rather than storing it separately, so there is only one source of truth for the post-discount total.

### Entry 8

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Design the Return class so it can compute and later recompute its own refund amount, and produce a Spanish receipt, without needing to touch Sale beyond one small addition.

**Problem faced:**
A return needed an immutable link back to the sale and the exact products being returned (which may be a subset of the sale's products, since Phase 12 supports partial returns), plus a refund amount that is naturally derived from those products but should still be independently correctable if a manual adjustment is ever needed.

**Prompt used:**
ejecuta la fase 12

**Solution obtained and decision taken:**
Made Return a concrete class (unlike Product/Person/Promotion/Warranty, it has no subtypes) with getters for every field but no setters for originalSale or date — those are immutable facts about a return, matching the "no setters for immutable relationships" convention already used in Sale. reason and refundAmount do get setters, since a refund amount might legitimately need correcting after the fact. The constructor computes refundAmount immediately by calling calculateRefundAmount() (added in the next commit and refactored into the constructor), rather than leaving it at a placeholder value the way Sale.totalAmount starts at 0.0 — a Return is only ever created once its returnedProducts list is already final, so there is no reason to defer the calculation to an external call.

### Entry 9

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Decide the exact 30-day return-eligibility rule and where it belongs (Sale, not ReturnService), since ReturnService (Task Group B) needs to check it before allowing a return to be registered.

**Problem faced:**
The phase spec asks for a method that answers "can this sale still be returned", which is really a fact about the Sale itself (how old is it) rather than about any particular return, and needed an unambiguous day-counting rule that would not be off by one depending on how partial days are handled.

**Prompt used:**
ejecuta la fase 12

**Solution obtained and decision taken:**
Added canBeReturned() directly to Sale rather than to Return or ReturnService, since it only depends on data Sale already owns (its own date) and reads naturally as a question the sale answers about itself — the same reasoning already used for calculateTotal() and generateReceipt(). Implemented it with ChronoUnit.DAYS.between(date, LocalDate.now()) <= 30 exactly as specified, which counts whole calendar days between the sale date and today and treats day 30 itself as still eligible (<=, not <).
