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

### Developer 1
- Product (abstract)
- VideoGame
- Console
- ProductRepository
- ProductService

### Developer 2
- Person (abstract)
- Customer
- Seller
- PersonRepository
- PersonService

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
