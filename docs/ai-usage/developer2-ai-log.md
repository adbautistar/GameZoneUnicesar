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
