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
| Services | `PlantIdentifier.java`, `PlantCatalog.java`, `PlantNetIdentifier.java`, `LocalPlantCatalog.java`, `ServiceException.java` | Define the service contracts, PlantNet image-identification client, local catalog, and service error type. |
| Swing UI | `PlantView.java` | Displays plant details, available listings, and cart contents; lets the user choose an image and invokes controller actions. |
| Controller and application entry point | `PlantController.java` | Coordinates UI actions with the model. `PlantController.main()` is the intended application entry point. |
| Tests | `tests/` | Standalone Java test programs cover the data classes, queue, model, services, and view. |

### Running the model and application

- **Standalone model tests:** `PlantModelTest.main()` exercises `PlantModel`
  with test identifier and catalog services. `PlantModel` itself is not a
  standalone application and has no `main()` method.
- **Application startup:** `PlantController.main()` is the intended entry
  point. It reads the `PLANTNET_API_KEY` environment variable, creates the
  identifier, model, empty local catalog, view, and controller. When run
  directly, it opens the plant panel in a separate window.
- **Current integration status:** The service classes and controller are
  present, and the controller's checkout call matches `CartItem.getListing()`.
  The panel is displayed, but `main()` does not call
  `view.setController(controller)`, so the view's image, add-to-cart, and
  checkout actions are not connected to the controller.

## Reflection
### How did the design absorb the change?
We did everything in the next steps section, including improving the UI and tests. It was surprising we got everything done. Our changes touched almost every file.

### Did the change cross the model/view line?
No. Everything we changed was part of our original spec document so there was no crossing the model/view boundary.

### What changed from our D3 plan?
Nothing changed. We did the next steps on the readme.

### What doesn't work yet?
The core functionality of the app works. We need to implement the stretch goals and improve the UI.

### Design and implementation

- The data classes separate plant-identification and shopping data from the UI.
- `PlantSnapshot` contains lists of plant details, available listings, and cart
  items. Empty lists represent a state with no identified plants or cart items.
- `PlantModel` depends on the `PlantIdentifier` and `PlantCatalog` interfaces,
  keeping service implementations outside the model and allowing test doubles.
- `PlantNetIdentifier` sends image requests to PlantNet and requires a valid
  `PLANTNET_API_KEY` plus network access.
- `LocalPlantCatalog` implements catalog lookup, but `PlantController.main()`
  currently constructs it with empty plant and listing lists, so no care data
  or shopping listings are available from the application startup path.
- `PlantView` uses Swing panels for plant information, available plants, and
  the cart. It renders a `PlantSnapshot` and provides controls for selecting
  an image, adding a listing, and checking out.
- `PlantController` coordinates view requests with model operations. Its
  startup path opens the panel in a separate window. It still needs to attach
  the controller to the view so the UI actions reach the model.

### Does the model stand alone?

`PlantModel` has no view or controller dependency and can be tested
independently through `PlantModelTest.main()` using injected test services.
The application starts through `PlantController.main()`, which opens the panel
in a separate window. The controller and service implementation classes are
present, but the current startup code does not connect the view's event handlers
to the controller.

### Current limitations

| Area | Status | Notes |
|---|---|---|
| Plant identification | Implemented; requires configuration | `PlantNetIdentifier` calls PlantNet and requires a valid `PLANTNET_API_KEY` and network access. |
| Catalog and care lookup | Implemented with local data | `LocalPlantCatalog` supports lookups, but `PlantController.main()` supplies empty data lists. |
| Controller integration | Implemented, startup wiring incomplete | `PlantController` provides identification, add-to-cart, checkout, and error handling. The panel opens, but `main()` does not call `view.setController(controller)`, so view actions are not connected. |
| Application window | Opens separately | Running `PlantController.main()` opens the plant panel in a separate window. |
| Tests | Present | Standalone tests cover the model and other project components; run them after verifying the current build setup. |

### Changes Made from the Spec Doc:
 Some classes were changed and condensed for the sake of a working prototype so that we may test certain functionalities. The main method in `PlantController.java` was modified so that it no longer required the use of an api, and instead relied on dummy data. Nothing else was changed from the orginial specs doc.

## Next steps

### What is left for the final build?
1. Improve the UI

### Stretch goals
1. Implement the PlantDex (stretch goal)
2. Location pin (to pin where the plants were found)

### 3-minute demo path
1. (0:00) Launch the app and show the empty starting state.
2. (0:20) Choose a plant image
3. (1:00) Show care details and available listings for the identified plant.
4. (1:30) Add two listings to the cart.
6. (2:30) Check out the cart. The first item added comes out first (FIFO). Then try checkout on an empty cart to
   show the "Your cart is empty" message.

## Who does what?
Conrad will improve the UI. Josh will implement the PlantDex. Jamie and Andrew will implement the Location pinning feature. Conard will do the demo.

## Attribution

| Files | Contributor | Contribution |
|---|---|---|
| `CartItem.java`, `Identification.java`, `Listing.java`, `PlantDetails.java`, `PlantSnapshot.java` | Josh | Wrote and revised. |
| `PlantView.java` | Josh | Wrote and revised. |
| `CatalogResult.java`, `PlantCatalog.java`, `PlantIdentifier.java`, `LinkedQueue.java`, `PlantModel.java`, and model tests | Conrad and Jamie | Wrote and revised. |
| `PlantController.java` | Andrew | Wrote and revised. |
| `README.md` | Jamie and Andrew | Wrote and revised. |

**GenAI use:** AI assistance was used to inspect the project and help revise this README.
