# Paeonia

Group 60: Jamie, Conrad, Josh, Andrew

## Project overview

Paeonia is a plant-identification and shopping application. The work from
`Josh_Branch` and `Conrad_Branch` has been merged into `main`, and
`PlantController.java` has since been added there. The repository has the main
MVC components, but the application is not yet buildable end to end because
some classes referenced by the controller are still missing.

## Project structure

The Java files currently live in the repository root rather than in separate
`model`, `view`, and `controller` source folders.

| Area | Files | Responsibility |
|---|---|---|
| Plant and listing data | `Identification.java`, `PlantDetails.java`, `Listing.java`, `CartItem.java`, `CatalogResult.java` | Hold identification, care, catalog, and cart data. |
| Model state and queue | `PlantModel.java`, `PlantSnapshot.java`, `LinkedQueue.java` | Identify plants through injected services, manage listings and the FIFO cart, and expose state snapshots. |
| Service contracts | `PlantIdentifier.java`, `PlantCatalog.java` | Define interfaces for image identification and catalog lookups. |
| Swing UI | `PlantView.java` | Displays plant details, available listings, and cart contents; lets the user choose an image and invokes controller actions. |
| Controller | `PlantController.java` | Coordinates UI actions with the model and refreshes the view. Its `main` method is intended to start the application. |
| Tests | `tests/` | Standalone Java test programs cover the data classes, queue, model, services, and view. |

### View/controller integration status

`PlantView` declares `setController(PlantController)` and calls controller
operations for image identification, adding a listing to the cart, and
checkout. `PlantController.java` is now present on `main` and supplies those
actions. However, the controller's startup code references
`PlantNetIdentifier`, `LocalPlantCatalog`, and `ServiceException`, which are
not present in the current `main` tree. The controller also needs to be checked
against the current data-class APIs before the complete application can build
and run.

## Reflection

### Design and implementation

- The data classes separate plant-identification and shopping data from the UI.
- `PlantSnapshot` contains lists of plant details, available listings, and cart
  items. Empty lists represent a state with no identified plants or cart items.
- `PlantModel` depends on `PlantIdentifier` and `PlantCatalog` interfaces,
  keeping service implementations outside the model and allowing test doubles.
- `PlantView` uses Swing panels for plant information, available plants, and
  the cart. It renders a `PlantSnapshot`, shows a status label, and provides
  controls for selecting an image, adding a listing, and checking out.
- `PlantController` now provides the intended view-to-model coordination.
  Integration is incomplete until its missing service dependencies and API
  mismatches are resolved.

### Did the model stand alone?

`PlantModel` has no view or controller dependency. Its ten non-view test
programs compiled and passed on `Conrad_Branch`, using injected test services.
This confirms the model is independently testable; it does not establish that
the full application builds. `PlantController` has since been added to `main`,
but the missing controller dependencies still prevent a complete launch.

### What remains uncertain or incomplete

| Area | Status | Notes |
|---|---|---|
| Plant identification service | Interface present; implementation missing | `PlantIdentifier` defines the contract; `PlantNetIdentifier` is referenced by the controller but absent. |
| Catalog and care lookup | Interface present; implementation missing | `PlantCatalog` defines the contract; `LocalPlantCatalog` is referenced by the controller but absent. |
| Controller integration | Present but incomplete | `PlantController` exists, but references absent classes and must be verified against current model APIs. |
| Application startup | Entry point present but not buildable | `PlantController.main()` is intended to launch the app; missing dependencies currently block it. |
| Tests | Present | Ten non-view tests passed on `Conrad_Branch`. `PlantViewTest` was blocked by the then-missing controller; rerun it after the controller integration is fixed. |

## Next steps

1. Add or restore the controller's missing `PlantNetIdentifier`,
   `LocalPlantCatalog`, and `ServiceException` dependencies.
2. Resolve any controller/model data API mismatches and wire the controller to
   the view using `setController`.
3. Compile the complete application and run `PlantViewTest` with the controller.
4. Add or document a usable catalog and identifier configuration for running
   the app outside the test doubles.

## Attribution

The following attribution is based on the commit history and author tags on
`Josh_Branch`. Confirm that it matches the team's actual division of work before
submission.

| Files | Contributor | Evidence / contribution |
|---|---|---|
| `CartItem.java`, `CatalogResults.java`, `Identification.java`, `Listing.java`, `PlantDetails.java`, `PlantSnapshot.java` | Josh (JRMolina49) | First recorded in Josh's immutable-classes commit; later revisions are also recorded for several files. |
| `PlantView.java` | Josh (JRMolina49) | First recorded in Josh's view commit and revised in later branch commits. |
| `PlantModel.java`, service interfaces, queue, and model tests | Conrad (`Conrad_Branch`) | First recorded in Conrad's implementation commits; ten non-view tests were verified independently. |
| `PlantController.java` | Andrew (AndyAtVT) | Added to `main` in commit `0046c41`; coordinate with Andrew to confirm any updates or additional review. |
| `README.md` | Jamie | Existing project attribution; update if the team wants to credit additional authors or reviewers. |

**GenAI use:** AI assistance was used to inspect the branch and draft this
README update. Add any other project-related GenAI use according to the team's
actual work.
