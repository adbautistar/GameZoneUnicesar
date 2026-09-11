# Team

## Members

| Name | Student Code | GitHub Username | Role | Module | Feature Branch |
|---|---|---|---|---|---|
| Alfredo Bautista | 2020XXXX | adbautistar | Technical Lead | Sale + UI + Main | feature/sale-module |
| Dev1 Simulated | 2020XXX1 | (single-user reference) | Developer 1 | Product | feature/product-module |
| Dev2 Simulated | 2020XXX2 | (single-user reference) | Developer 2 | Person | feature/person-module |

## Class Distribution

### Technical Lead
- Sale
- SaleRepository
- SaleService
- ConsoleMenu
- Main

Extended modules (Phases 10-13, Task Group C — integration and UI in each):
- SaleService extension points (accessory/promotion/warranty resolution, best-promotion application, extended-warranty parameter)
- ProductService.restoreStock (Phase 12)
- Main and ConsoleMenu wiring for all four new modules

### Developer 1
- Product (abstract)
- VideoGame
- Console
- ProductRepository
- ProductService

Extended modules (Phases 10-13, Task Group A — domain model in each):
- Accessory (abstract), Controller, Cable, Memory (Phase 10)
- Promotion (abstract), PercentageDiscount, CategoryDiscount, BulkPurchaseDiscount (Phase 11)
- Sale.appliedPromotionName / discountAmount / setTotalAmount (Phase 11, targeted edit)
- Return, Sale.canBeReturned (Phase 12)
- Warranty (abstract), BasicWarranty, ExtendedWarranty (Phase 13)

### Developer 2
- Person (abstract)
- Customer
- Seller
- PersonRepository
- PersonService

Extended modules (Phases 10-13, Task Group B — persistence and service in each):
- AccessoryRepository, AccessoryService (Phase 10)
- PromotionRepository, PromotionService (Phase 11)
- ReturnRepository, ReturnService (Phase 12)
- WarrantyRepository, WarrantyService (Phase 13)

## Committed Activities

### Technical Lead
1. Create the project repository on GitHub with initial configuration.
2. Configure main and develop branches and enable branch protection.
3. Configure the Maven project with the initial pom.xml and four-layer package structure.
4. Author TEAM.md with team information, roles, and class distribution.
5. Implement the Sale class with attributes, constructor, and basic methods.
6. Implement the total calculation method for Sale.
7. Implement SaleRepository for sale persistence.
8. Implement SaleService with validation rules.
9. Implement the basic structure of ConsoleMenu.
10. Implement the three submenus (products, people, sales).
11. Implement Main with initial data loading and dependency injection.
12. Review and merge developer Pull Requests.
13. Author the final README.md with build and run instructions.

### Developer 1
1. Create the feature/product-module branch.
2. Implement the abstract Product class with common attributes, constructor, and shared methods.
3. Declare the abstract description method in Product.
4. Implement VideoGame subclass with its specific attributes and description implementation.
5. Implement Console subclass with its specific attributes and description implementation.
6. Implement ProductRepository with save and load methods.
7. Implement ProductService with registration, listing, and stock update methods.
8. Add JavaDoc in English to all product module classes.
9. Open a Pull Request to the Technical Lead for module integration.

### Developer 2
1. Create the feature/person-module branch.
2. Implement the abstract Person class with common attributes, constructor, and shared methods.
3. Declare any abstract or business method the subclasses must implement.
4. Implement Customer subclass with its specific attributes.
5. Implement Seller subclass with its specific attributes.
6. Implement PersonRepository with save and load methods for customers and sellers.
7. Implement PersonService with registration and listing methods.
8. Add JavaDoc in English to all person module classes.
9. Open a Pull Request to the Technical Lead for module integration.

## Extended Modules (Phases 10-13)

Starting with Phase 10, new functionality is delivered as additive modules on a shared feature branch per module (`feature/accessory-module`, `feature/promotion-module`, `feature/return-module`, `feature/warranty-module`), instead of one branch per role. Each phase follows the same fixed task-group split for all four modules:

- **Task Group A (Developer 1):** implements the module's domain/model classes.
- **Task Group B (Developer 2):** implements the module's persistence and service classes, gated on Task Group A being merged into the branch.
- **Task Group C (Technical Lead):** integrates the module into `SaleService`/`Main`/`ConsoleMenu`/`README.md`, gated on Task Groups A and B being merged into the branch.

The exact commit-by-commit breakdown, functional verification steps, and release plan for each module are fully specified in their respective `fase-1X-modulo-*.md` documents (not tracked in git — see `.gitignore`). Class ownership for these modules is listed above under Class Distribution.
