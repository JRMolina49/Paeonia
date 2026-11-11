# Paeonia

Group 60: Jamie, Conrad, Josh, Andrew

## Project overview

Paeonia is a plant-identification and shopping application. The project uses
model-view-controller (MVC) roles, with the Java files currently kept in the
repository root rather than separate source folders.

## Project structure

| Area | Files | Responsibility |
|---|---|---|
| Plant and listing data | `Identification.java`, `PlantDetails.java`, `Listing.java`, `CartItem.java`, `CatalogResult.java` | Hold identification, care, catalog, and cart data. |
| Model state and queue | `PlantModel.java`, `PlantSnapshot.java`, `LinkedQueue.java` | Process identifications through injected services, manage listings and the FIFO cart, and expose state snapshots. |
| Service contracts | `PlantIdentifier.java`, `PlantCatalog.java` | Define interfaces for image identification and catalog lookups. |
| Swing UI | `PlantView.java` | Displays plant details, available listings, and cart contents; lets the user choose an image and invokes controller actions. |
| Controller and application entry point | `PlantController.java` | Coordinates UI actions with the model. `PlantController.main()` is the intended application entry point. |
| Tests | `tests/` | Standalone Java test programs cover the data classes, queue, model, services, and view. |

### Running the model and application

- **Standalone model tests:** `PlantModelTest.main()` exercises `PlantModel`
  with test identifier and catalog services. `PlantModel` itself is not a
  standalone application and has no `main()` method.
- **Application startup:** `PlantController.main()` constructs the services
  and model, then creates the Swing view. This is the intended entry point for
  running the application.
- **Current build status:** The complete project does not currently compile.
  `PlantController` references `PlantNetIdentifier`, `LocalPlantCatalog`, and
  `ServiceException`, which are not present in the repository. It also calls a
  `CartItem` method that does not match the current `CartItem` API. Resolve
  these integration issues before running the application or its view test.

## Reflection

### Design and implementation

- The data classes separate plant-identification and shopping data from the UI.
- `PlantSnapshot` contains lists of plant details, available listings, and cart
  items. Empty lists represent a state with no identified plants or cart items.
- `PlantModel` depends on the `PlantIdentifier` and `PlantCatalog` interfaces,
  keeping service implementations outside the model and allowing test doubles.
- `PlantView` uses Swing panels for plant information, available plants, and
  the cart. It renders a `PlantSnapshot` and provides controls for selecting
  an image, adding a listing, and checking out.
- `PlantController` coordinates view requests with model operations. Its
  integration needs the missing service classes and API mismatch described
  above resolved before the full application can launch.

### Does the model stand alone?

`PlantModel` has no view or controller dependency and can be tested
independently through `PlantModelTest.main()` using injected test services.
The full application is intended to start through `PlantController.main()`,
but that path is currently blocked by the compile issues listed above.

### Current limitations

| Area | Status | Notes |
|---|---|---|
| Plant identification | Interface present; implementation missing | `PlantIdentifier` defines the contract; `PlantNetIdentifier` is referenced by the controller but absent. |
| Catalog and care lookup | Interface present; implementation missing | `PlantCatalog` defines the contract; `LocalPlantCatalog` is referenced by the controller but absent. |
| Controller integration | Present but incomplete | `PlantController` exists, but references absent classes and has a `CartItem` API mismatch. |
| Application startup | Entry point present but not buildable | `PlantController.main()` is intended to launch the app; the compile issues above block it. |
| Tests | Present | Standalone tests cover the model and other project components. The full test set must compile after controller integration is fixed. |

## Next steps

1. Add or restore `PlantNetIdentifier`, `LocalPlantCatalog`, and
   `ServiceException`.
2. Resolve the `CartItem` API mismatch and wire the controller to the view with
   `setController`.
3. Compile the complete project and run all tests, including `PlantViewTest`.
4. Provide a usable catalog and identifier configuration for running the
   application outside the test doubles.

## Attribution

| Files | Contributor | Contribution |
|---|---|---|
| `CartItem.java`, `Identification.java`, `Listing.java`, `PlantDetails.java`, `PlantSnapshot.java` | Josh | Wrote and revised. |
| `PlantView.java` | Josh | Wrote and revised. |
| `CatalogResult.java`, `PlantCatalog.java`, `PlantIdentifier.java`, `LinkedQueue.java`, `PlantModel.java`, and model tests | Conrad | Wrote and revised. |
| `PlantController.java` | Andrew | Wrote and revised. |
| `README.md` | Jamie | Wrote and revised. |

**GenAI use:** AI assistance was used to inspect the project and help draft and
revise this README. Add any other project-related GenAI use according to the
team's actual work.
