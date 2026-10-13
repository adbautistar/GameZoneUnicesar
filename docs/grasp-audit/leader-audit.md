# GRASP Audit — Technical Lead

| Item | Value |
|---|---|
| Role | Technical Lead |
| Requirement | Requerimiento 7 — GRASP audit and correction, Phase 1 (audit) |
| Audited commit | `8ca31bf` (`develop`, tag `v1.4.1`) |
| Date | 2026-10-13 |
| Scope rule | Only the code written by the Technical Lead, per the responsibility table of Requerimiento 7 |

Verdicts: **Violation** (the code breaks the pattern at one or more points), **Correct** (the pattern is applied correctly), **Not applicable** (the audited code has no point where the pattern applies).

## Scope

| Module (requirement) | Classes and methods audited |
|---|---|
| Sales (Taller 1) and promotion changes (Requerimiento 2) | `Sale` (including `applied promotion` fields and the receipt breakdown; excluding `canBeReturned`, owned by Developer 1) |
| Sales (Taller 1) | `SaleRepository` |
| Sales (Taller 1) and integrations (Requerimientos 1, 2, 4) | `SaleService`, including product-or-accessory resolution, promotion application and warranty assignment in `registerSale` |
| User interface (Taller 1 and Requerimientos 1-4) | `ConsoleMenu`, including every module submenu |

## Information Expert

**Verdict:** Pending — audit in progress.

- **Sale — Correct.** `Sale.calculateTotal()` (`Sale.java:148`) sums the prices of the products it holds; `generateReceipt()` (`Sale.java:163`) formats only its own data. The problem is not in `Sale` but in the classes that adjust its total from outside (V-03).

### V-03

| Field | Content |
|---|---|
| ID | V-03 |
| Pattern | Information Expert |
| Module | Sales (Taller 1) and Promotions/Warranties integration (Requerimientos 2 and 4) |
| Evidence | `SaleService.registerSale`, `SaleService.java:145-148` (`sale.setAppliedPromotionName(...)`, `sale.setDiscountAmount(discount)`, `sale.setTotalAmount(originalTotal - discount)`) and `:159` (`sale.setTotalAmount(sale.getTotalAmount() + extended.getAdditionalCost())`); `SaleRepository.fromCsvLine`, `:162-164`, repeats the discount arithmetic. |
| Explanation | The sale is the expert on its own total, but the arithmetic that changes it is written in a service and in a repository, which push the result back through setters. |
| Consequence | The total rules are duplicated in two classes and can drift apart; any client can leave a sale with an inconsistent total. |
| Proposed solution | Let `Sale` apply a promotion (`applyPromotion(name, discount)`) and add a surcharge (`addCharge(amount)`) to its own total; clients only call those methods. |
| Other role's code involved | No. |

### V-09

| Field | Content |
|---|---|
| ID | V-09 |
| Pattern | Information Expert |
| Module | Sales (Taller 1) |
| Evidence | `SaleService.viewSalesByCustomer` (`SaleService.java:197`) `sale.getCustomer().getId().equals(customerId)`; `viewSalesBySeller` (`:213`) `sale.getSeller().getId().equals(sellerId)`. |
| Explanation | The service navigates the sale's associations instead of asking the sale. |
| Consequence | The service depends on how `Sale` stores its participants. |
| Proposed solution | Ask the sale: `wasMadeBy(customerId)` and `wasHandledBy(sellerId)`. |
| Other role's code involved | No. |

## Creator

**Verdict:** Pending — audit in progress.

- **Sale — Not applicable.** `Sale` creates no other domain object.

- **SaleRepository — Correct.** `fromCsvLine` creates each `Sale` from the record it reads, so it holds the initializing data.

- **SaleService — Correct.** `registerSale` creates the `Sale` (`SaleService.java:139`) that it then records, aggregates and persists.

## Controller

**Verdict:** Pending — audit in progress.

- **ConsoleMenu — Mixed.** Every submenu delegates the operation to a service, except the sale registration (V-10).

### V-10

| Field | Content |
|---|---|
| ID | V-10 |
| Pattern | Controller |
| Module | User interface and Warranties integration (Requerimiento 4) |
| Evidence | `ConsoleMenu.registerSale`, `ConsoleMenu.java:325-343`: the menu resolves each id with `productService.findById`, decides with `instanceof Console` which items get the extended-warranty question, and prints "Garantia basica asignada automaticamente" for the consoles it found itself, without asking the service which warranties were assigned. |
| Explanation | The UI should collect input and delegate; here it applies a business rule (warranty eligibility) and reports a result it inferred instead of receiving it from the service layer. |
| Consequence | The eligibility rule exists in two layers and can drift; the message can disagree with what the service actually did. |
| Proposed solution | Ask the service layer which items are eligible (a method on the sale registration use case) and use that answer both for the questions and for the messages. |
| Other role's code involved | No. |

## Low Coupling

**Verdict:** Pending — audit in progress.

- **Sale — Correct.** `Sale` depends only on `Customer`, `Seller` and `Product`.

### V-01

| Field | Content |
|---|---|
| ID | V-01 |
| Pattern | Low Coupling |
| Module | Sales (Taller 1) |
| Evidence | `SaleRepository` imports and stores `ProductService`, `PersonService` and `AccessoryService` (`SaleRepository.java:7-9`, constructors) and calls them in `fromCsvLine` (`:140-155`). |
| Explanation | A persistence class depends on the service layer, the reverse of the allowed direction. This was recorded as a known layering exception in Taller 1; the audit classifies it as a Low Coupling violation. |
| Consequence | The repository cannot be built or tested without three services; any change in their lookup methods breaks persistence; `Main` must build the services before the repository. |
| Proposed solution | Persist and load only references (a `SaleRecord` with ids); `SaleService` resolves them when it loads. |
| Other role's code involved | No. |

### V-05

| Field | Content |
|---|---|
| ID | V-05 |
| Pattern | Low Coupling (also Protected Variations) |
| Module | Warranties integration (Requerimiento 4) |
| Evidence | `SaleService` field `warrantyService` injected by `setWarrantyService` (`SaleService.java:71`) because of the cycle `SaleService → WarrantyService → WarrantyRepository → SaleService`; `registerSale` guards it with `if (warrantyService != null)` (`:151`); `Main.java:53` wires it after construction. |
| Explanation | Two services depend on each other through persistence. The cycle is broken by hand and the guard hides a wiring mistake instead of failing. |
| Consequence | If the setter call is forgotten, sales are registered silently without warranties; construction order in `Main` becomes fragile. |
| Proposed solution | Break the cycle: move registration into a use-case controller that depends on both services (V-04), and remove the reverse dependency from the warranty repository (reported for consolidation). |
| Other role's code involved | Yes — `WarrantyRepository` is Developer 2's code. |

## High Cohesion

**Verdict:** Pending — audit in progress.

- **Sale — Correct.** `Sale` holds sale data, its total and its receipt.

### V-02

| Field | Content |
|---|---|
| ID | V-02 |
| Pattern | High Cohesion |
| Module | Sales (Taller 1) |
| Evidence | `SaleRepository.fromCsvLine`, `SaleRepository.java:166`: `customer.addPurchase(sale);` executed while parsing a CSV line. |
| Explanation | Reading a file and rebuilding the customers' purchase history are different responsibilities; the repository changes domain state as a side effect of loading. |
| Consequence | Anyone who calls `loadAll()` twice duplicates purchase histories; the rule "a recorded sale belongs to the customer's history" lives in two places (here and `SaleService.registerSale`). |
| Proposed solution | Let `SaleService` record the purchase history when it rebuilds sales and when it registers a new one. |
| Other role's code involved | No. |

### V-04

| Field | Content |
|---|---|
| ID | V-04 |
| Pattern | High Cohesion |
| Module | Sales (Taller 1) and integrations (Requerimientos 1, 2, 4) |
| Evidence | `SaleService.registerSale`, `SaleService.java:94-177` (about 80 lines). |
| Explanation | One method validates input, resolves people and items, checks stock, generates the id, applies promotions, assigns warranties, updates stock, records history and persists. `SaleService` mixes the sale registration use case with sale storage and queries. |
| Consequence | Every new module that touches a sale (accessories, promotions, warranties) was added to the same method; the method is hard to read, test and change safely. |
| Proposed solution | Extract the registration use case into its own class (a use-case controller such as `SaleRegistrationService`), leaving `SaleService` with storage and queries. |
| Other role's code involved | No. |

### V-11

| Field | Content |
|---|---|
| ID | V-11 |
| Pattern | High Cohesion |
| Module | User interface (Taller 1 and Requerimientos 1-4) |
| Evidence | `ConsoleMenu.java`, 863 lines: main loop plus `showProductMenu` (`:118`), `showPersonMenu` (`:205`), `showSaleMenu` (`:276`), `showAccessoryMenu` (`:387`), `showPromotionMenu` (`:542`), `showReturnMenu` (`:669`), `showMonthlyBalance` (`:769`), `showWarrantyMenu` (`:782`) and every operation of every module. |
| Explanation | One class owns the user interface of seven modules and depends on seven services; each new module made it larger. |
| Consequence | Hard to navigate and review; any change to one module's screens touches the class every other module uses. |
| Proposed solution | Keep the main loop in `ConsoleMenu` and move each module's submenu into its own class in `com.gamezone.ui`. |
| Other role's code involved | No. |

## Polymorphism

**Verdict:** Pending — audit in progress.

- **SaleService — Violation (V-06).** See the full analysis below.

### V-06

| Field | Content |
|---|---|
| ID | V-06 |
| Pattern | Polymorphism (also Protected Variations) |
| Module | Accessories and Warranties integration (Requerimientos 1 and 4) |
| Evidence | `SaleService.registerSale`: `if (product instanceof Console)` for warranty eligibility (`SaleService.java:153`) and `if (product instanceof Accessory)` for stock dispatch (`:165`); `ConsoleMenu.registerSale`: `if (product instanceof Console)` (`ConsoleMenu.java:329`). |
| Explanation | Two variations — which items receive a warranty and where an item's stock lives — are decided with type checks in the clients, and the warranty rule is repeated in the service and in the UI. |
| Consequence | Making another product type eligible for warranties, or adding another kind of sellable item, requires editing several classes in two layers. |
| Proposed solution | Let products answer their warranty eligibility polymorphically (`isWarrantyEligible()`, overridden by `Console`) and move the stock dispatch behind a single intermediary (see V-07). |
| Other role's code involved | Yes — the product hierarchy is Developer 1's code. |

## Pure Fabrication

**Verdict:** Pending — audit in progress.

- **SaleRepository — Correct as a fabrication.** File access is kept out of `Sale`; the problems of this class are its dependencies and its extra responsibilities (V-01, V-02), not its existence.

### V-08

| Field | Content |
|---|---|
| ID | V-08 |
| Pattern | Pure Fabrication |
| Module | Sales (Taller 1) |
| Evidence | `SaleService.registerSale`, `SaleService.java:138`: `"SALE-" + System.currentTimeMillis()`. |
| Explanation | Identifier generation is a technical responsibility written inline in a domain service. |
| Consequence | The identifier scheme is repeated wherever entities are created. |
| Proposed solution | Use a fabricated `IdGenerator` shared by every service that creates entities. |
| Other role's code involved | Yes — reported to the consolidation: Developer 2's services generate `RET-`/`WAR-` ids the same way. |

## Indirection

**Verdict:** Pending — audit in progress.

- **ConsoleMenu — Correct.** The menu reaches persistence only through services; it never imports a repository.

### V-07

| Field | Content |
|---|---|
| ID | V-07 |
| Pattern | Indirection |
| Module | Accessories integration (Requerimiento 1) |
| Evidence | `productService.findById(id)`, then `accessoryService.findById(id)` in `SaleService.registerSale` (`SaleService.java:111-113`) and `SaleRepository.fromCsvLine` (`SaleRepository.java:148-150`). |
| Explanation | Sale code talks directly to the product and accessory catalogs and knows the lookup order; there is no intermediary for "a sellable item". |
| Consequence | The lookup and the stock dispatch are duplicated; a third catalog would require editing every client. |
| Proposed solution | Use a single intermediary for sellable items (find, decrease stock, restore stock). |
| Other role's code involved | Yes — reported to the consolidation as the same duplication exists in Developer 2's return and warranty code. |

## Protected Variations

**Verdict:** Pending — audit in progress.

- **SaleService — Violation (V-05, V-06).** The circular dependency is wired by hand and guarded by a silent `null` check (V-05); warranty eligibility is fixed to `Console` in two layers (V-06).
