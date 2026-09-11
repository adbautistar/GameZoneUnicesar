# AI Usage Log — Developer 2

This log records AI-assisted decisions taken by the Developer 2 role during the development of the Person module.

### Entry 1

**Date:** 2026-09-02
**Tool used:** Claude Code

**Reason for use:**
Implement the abstract Person class and its concrete Customer and Seller subclasses to model everyone who interacts with the store.

**Problem faced:**
Person needed to hold the attributes and behavior common to both customers and sellers without being instantiable on its own, and Customer specifically had to omit purchaseHistory in this phase to avoid a forward dependency on the Sale class, which does not exist yet.

**Prompt used:**
ejecuta la fase 6

**Solution obtained and decision taken:**
Declared Person as an abstract class holding id, firstName, lastName, and phone, with a concrete getFullName() method combining first and last name. Customer extends Person and adds only email in this phase; Seller extends Person and adds employeeCode and shift. This matches the abstraction decision documented in docs/analysis.md (Q1, Q2) and defers purchaseHistory to Phase 7, once Sale exists, to keep the person module free of forward references.

### Entry 2

**Date:** 2026-09-02
**Tool used:** Claude Code

**Reason for use:**
Design PersonRepository so customers and sellers, which are unrelated concrete types beyond sharing a common parent, can each be persisted to their own file.

**Problem faced:**
Unlike products, customers and sellers do not need a discriminator column in a single file — they are always loaded and saved as separate, independently-sized collections — so the repository needed two clearly separated read/write paths instead of one polymorphic one.

**Prompt used:**
ejecuta la fase 6

**Solution obtained and decision taken:**
Implemented saveAllCustomers/loadAllCustomers against data/customers.csv and saveAllSellers/loadAllSellers against data/sellers.csv as two independent method pairs, both following the same pattern as ProductRepository (overwrite on save, return an empty ArrayList when the file does not exist yet, rethrow IOException as RuntimeException). This keeps the two entity types decoupled at the persistence layer while reusing a consistent I/O pattern across the whole persistence layer.

### Entry 3

**Date:** 2026-09-02
**Tool used:** Claude Code

**Reason for use:**
Decide how to preload the three mandatory sellers required before first execution, and how PersonService should expose lookups for later use by SaleService.

**Problem faced:**
CLAUDE.md requires the seller file to be preloaded with 3 sellers before first execution, and the sale module (Phase 7) will need to resolve a Customer or Seller by id without duplicating PersonService's internal list-scanning logic.

**Prompt used:**
ejecuta la fase 6

**Solution obtained and decision taken:**
Created data/sellers.csv directly with the three specified rows (S001-S003), written as plain "Manana" instead of "Mañana" to avoid encoding issues in the CSV, as instructed. Added findCustomerById(String) and findSellerById(String) to PersonService, mirroring ProductService.findById, so SaleService can resolve both participants of a sale by id in Phase 7 using the same lookup convention already established in the product module.

### Entry 4

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Design AccessoryRepository's CSV format and reconstruction logic so it stays self-contained, unlike SaleRepository which needs other services to resolve references.

**Problem faced:**
Accessory has one attribute ProductRepository's CSV format has no equivalent for: compatibleConsoleIds, a list rather than a scalar field, which needs to survive a round trip through a single CSV column without colliding with the comma used as the field separator.

**Prompt used:**
ejecuta la fase 10

**Solution obtained and decision taken:**
Reused the exact discriminator-column pattern from ProductRepository (CONTROLLER/CABLE/MEMORY as the first field, followed by the shared Product columns, then type-specific ones), and serialized compatibleConsoleIds as a single pipe-separated column (e.g., "CN001|CN002"), parsed back with String.split("\\|") and defaulting to an empty list when the field is blank. Unlike SaleRepository, AccessoryRepository needs no constructor dependencies on other services — every field it needs to reconstruct a Controller, Cable, or Memory lives in its own CSV row, so it stays a simple, self-contained repository.

### Entry 5

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Decide how AccessoryService should expose lookup, filtering, and stock operations so Task Group C (SaleService integration) can treat accessories the same way it already treats products.

**Problem faced:**
SaleService's registerSale needed to resolve a sale item id against either ProductService or AccessoryService and, on the stock-update side, dispatch to whichever service actually owns that item — this only works cleanly if AccessoryService mirrors ProductService's findById(String) and updateStock(String, int) signatures exactly.

**Prompt used:**
ejecuta la fase 10

**Solution obtained and decision taken:**
Gave AccessoryService the same method shapes as ProductService: findById(String) returning null when not found (not throwing), and updateStock(String, int) that looks the accessory up, delegates to its inherited Product.updateStock(int), and persists — identical contract to ProductService.updateStock, so Task Group C's dispatch logic in SaleService can call either service the same way once an item's source service is known. listAccessoriesByType(String) uses instanceof against the three concrete subclasses rather than a stored type field, keeping the discriminator logic in one place (the repository) instead of duplicating it in the model.

### Entry 6

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Design PromotionRepository's CSV format for three promotion subtypes with different column counts, following the same discriminator convention already used for products and accessories.

**Problem faced:**
PercentageDiscount, CategoryDiscount, and BulkPurchaseDiscount each need a different number of type-specific trailing columns (one, two, and two respectively), plus every row shares two LocalDate fields that need a stable, unambiguous text format to round-trip correctly.

**Prompt used:**
ejecuta la fase 11

**Solution obtained and decision taken:**
Used the same PERCENTAGE/CATEGORY/BULK discriminator-column pattern as ProductRepository and AccessoryRepository: id, name, startDate, endDate always come right after the discriminator, then each subtype appends only its own fields. Dates are formatted and parsed with DateTimeFormatter.ISO_LOCAL_DATE (yyyy-MM-dd) exactly as the phase specifies, so the same column round-trips identically regardless of which subtype's row it belongs to. loadAll() reconstructs the correct subclass by branching on the discriminator, mirroring fromCsvLine in ProductRepository and AccessoryRepository — the third repository following this exact same shape in the codebase.

### Entry 7

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Implement findBestPromotionFor so SaleService can pick a single promotion to apply without knowing anything about how each promotion type computes its discount.

**Problem faced:**
"Best" promotion means the one with the highest calculateDiscount(sale) result among currently active promotions, but some promotions (BulkPurchaseDiscount below its threshold, CategoryDiscount with no matching products) legitimately return 0.0, which must not be treated as a valid "best" choice — the method needs to return null in that case, not a promotion that grants no actual discount.

**Prompt used:**
ejecuta la fase 11

**Solution obtained and decision taken:**
findBestPromotionFor iterates listActivePromotions() (itself filtering promotions by isActive(LocalDate.now())), invokes calculateDiscount(sale) polymorphically on each, and only updates the running best when a promotion's discount is strictly greater than the current best (initialized at 0.0). This means a promotion contributing exactly 0.0 never becomes "best", so the method naturally returns null both when the active list is empty and when every active promotion's condition (category match, minimum quantity) fails for this particular sale — matching the specification without a separate empty-list special case.

### Entry 8

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Decide how ReturnRepository.loadAll() should handle a return whose sale or product references cannot be resolved, since the phase spec explicitly asks for "log error and skip" instead of the hard-failure RuntimeException every other repository in this project throws on an unresolvable reference.

**Problem faced:**
SaleRepository, PersonRepository, and the others all throw when a referenced id cannot be found, treating it as data corruption; ReturnRepository is the first repository asked to instead degrade gracefully — skip the unresolvable return (or, for a single unresolvable product within an otherwise valid return, skip just that item) and keep loading the rest of the file.

**Prompt used:**
ejecuta la fase 12

**Solution obtained and decision taken:**
fromCsvLine returns null (not an exception) when saleService.findById(saleId) fails to resolve, after printing a message to System.err; loadAll() checks for that null and simply does not add it to the result list, so one corrupted or stale return row does not stop the rest of the file from loading. Product-id resolution inside a return is handled the same way at a finer grain: a single unresolvable product (tried against productService first, then accessoryService, matching the same fallback used everywhere else) is skipped from that return's returnedProducts list rather than discarding the whole return, since the other returned products in the same row are still perfectly valid data.

### Entry 9

**Date:** 2026-09-11
**Tool used:** Claude Code

**Reason for use:**
Design registerReturn's validation order and the stock-restoration dispatch, and decide what "total sales" means for generateMonthlyBalance now that Phase 11 added discounts.

**Problem faced:**
registerReturn has to reject a return for three distinct reasons (sale not found, sale too old, product not part of the original sale) before touching any state, and needed to route the stock restoration to whichever service actually owns each returned item (Product vs Accessory) exactly like SaleService already does for the sale-time decrement; separately, "total sales" for the monthly balance could mean the pre-discount subtotal or the actual amount the store received.

**Prompt used:**
ejecuta la fase 12

**Solution obtained and decision taken:**
registerReturn validates in order — sale exists, sale.canBeReturned() (the 30-day rule from Task Group A), then each requested product id is matched against sale.getProducts() by id, throwing a Spanish IllegalArgumentException naming the offending id on the first failure — before any Return is constructed or any stock is touched. Stock restoration mirrors SaleService's dispatch pattern exactly (instanceof Accessory routes to accessoryService.updateStock(id, +1), everything else to productService.restoreStock(id, 1)). generateMonthlyBalance sums sale.getTotalAmount() specifically — the post-discount total already stored on Sale (Phase 11) — since that reflects what the store actually took in, not the undiscounted subtotal.
