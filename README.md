# Paeonia

Group 60: Jamie, Conrad, Josh, Andrew

## Project overview

Paeonia is a plant-identification and shopping application. `Josh_Branch` contains the Swing view and immutable data classes.
`Conrad_Branch` contains the model, service interfaces, and model tests. Neither
branch currently has a complete runnable application: the view expects a
controller, but no `PlantController` or application driver is present in the
branch trees checked.

### Where the model is

The model implementation is on `Conrad_Branch`, not `Josh_Branch`. I verified
this by compiling a clean export of `Conrad_Branch` and running its ten
non-view test programs; all ten passed, including `PlantModelTest`. This
confirms the model can be exercised through its tests with injected test
services. It does not mean the full application can launch: there is no
application driver or production service implementation, and `PlantViewTest`
cannot compile until the missing controller is added.

## Project structure

The Java files currently live in the repository root rather than in separate
`model`, `view`, and `controller` source folders.

| Area | Files | Responsibility |
|---|---|---|
| Plant and listing data | `Identification.java`, `PlantDetails.java`, `Listing.java`, `CartItem.java`, `CatalogResults.java` | Hold identification, care, catalog, and cart data. |
| View state | `PlantSnapshot.java` | Carries an optional identified plant, available listings, and cart contents to the view. |
| Swing UI | `PlantView.java` | Displays plant details, available listings, and cart contents; lets the user choose an image and invokes controller actions. |
| Controller | Not present on `Josh_Branch` | Needed to connect the view to the model and handle its image, add-to-cart, and checkout actions. |
| Model, services, and driver | Not present on `Josh_Branch` | Needed for identification, catalog lookup, application startup, and model behavior. |

### View/controller integration status

`PlantView` declares `setController(PlantController)` and calls controller
operations for image identification, adding a listing to the cart, and
checkout. However, `PlantController.java` is not present in the latest
`Josh_Branch` commit available on GitHub, and no open pull request adds it.
Consequently, the view cannot compile as a complete application on this branch.
When the controller is added, its public method names and snapshot contract
should be aligned with the calls in `PlantView`.

## Reflection

### Design and implementation

- The data classes separate plant-identification and shopping data from the UI.
- `PlantSnapshot` uses `Optional<PlantDetails>` for the identified plant and
  lists for available listings and cart items. This lets the view represent
  the state where no plant has been identified yet.
- `PlantView` uses Swing panels for plant information, available plants, and
  the cart. It renders a `PlantSnapshot`, shows a status label, and provides
  controls for selecting an image, adding a listing, and checking out.
- The view is structured to delegate application actions to a controller
  rather than implement identification or cart rules itself. That boundary
  still needs a controller and model implementation to be complete.

### Did the model stand alone?

On `Conrad_Branch`, `PlantModel` has no view or controller dependency and its
tests run independently with injected test services. The model is therefore
testable on its own, but the full application is not yet runnable or integrated
with the Swing view. `Josh_Branch` contains the view and data classes, but not
the model implementation.

### What remains uncertain or incomplete

| Area | Status | Notes |
|---|---|---|
| Plant identification service | Interface only on `Conrad_Branch` | `PlantIdentifier` defines the contract; no production implementation is included. |
| Catalog and care lookup | Interface only on `Conrad_Branch` | `PlantCatalog` defines the contract; no production implementation is included. |
| Controller integration | Incomplete | `PlantView` references `PlantController`, but its source is absent from the branch trees checked. |
| Application startup | Not present | There is no driver or entry point for launching the complete application. |
| Tests | Present on `Conrad_Branch` | Ten non-view test programs compile and pass; `PlantViewTest` is blocked by the missing controller. `Josh_Branch` has no tests. |

## Next steps

1. Bring the model and its tests from `Conrad_Branch` together with the view and
   data classes from `Josh_Branch`.
2. Add the controller and align its method signatures with `PlantView` and the
   model.
3. Add production identifier/catalog implementations and an application entry
   point that wires the view, controller, and model.
4. Compile and run the view tests once controller integration is in place.

## Attribution

The following attribution is based on the commit history and author tags on
`Josh_Branch`. Confirm that it matches the team's actual division of work before
submission.

| Files | Contributor | Evidence / contribution |
|---|---|---|
| `CartItem.java`, `CatalogResults.java`, `Identification.java`, `Listing.java`, `PlantDetails.java`, `PlantSnapshot.java` | Josh (JRMolina49) | First recorded in Josh's immutable-classes commit; later revisions are also recorded for several files. |
| `PlantView.java` | Josh (JRMolina49) | First recorded in Josh's view commit and revised in later branch commits. |
| Model, service interfaces, and tests | Conrad (`Conrad_Branch`) | The model and ten non-view test programs are present on Conrad's branch; their tests compile and pass independently of the view. |
| `PlantController.java` and application driver | Not present in the branch trees checked | No implementation is available here to attribute. |
| `README.md` | Jamie | Existing project attribution; update if the team wants to credit additional authors or reviewers. |

**GenAI use:** AI assistance was used to inspect the branch and draft this
README update. Add any other project-related GenAI use according to the team's
actual work.
