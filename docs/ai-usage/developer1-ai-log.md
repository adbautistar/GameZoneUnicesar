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
