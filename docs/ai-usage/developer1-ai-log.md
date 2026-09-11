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
