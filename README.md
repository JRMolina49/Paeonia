# Paeonia

Group 60: Jamie, Conrad, Josh, Andrew

## How to Run

**Model only (no view required)**
- Driver: 
- Tests (JUnit): 

**Thin view (console, goes through PlantController)**
- 
- Menu options: 

The model does not import anything from `view` or `controller`.

## Reflection

### What changed from the specification
| Area | Spec said | We built | Why |
|---|---|---|---|
| `PlantModel.identify()` | `<returns void; identifier returns List<Identification>>` | `<picks the highest-confidence Identification and stores it>` | `<reason>` |
| Snapshot plant field | `<List<PlantDetails> plant>` | `<Optional / single PlantDetails>` | `<reason>` |
| "Not found" in `CatalogResult` | `<undefined>` | `<empty result / exception>` | `<reason>` |
| Identifier used | `<PlantNetIdentifier>` | `<FakeIdentifier for MVP; PlantNetIdentifier stubbed>` | `<needs API key and HTTP, highest risk>` |
| Validation | `<spec location>` | `<where it actually lives>` | `<reason>` |
| Tests | `<spec test plan>` | `<which tests were written, which were not>` | `<reason>` |

### Did the model really stand alone?
- `<Yes/No. Model runs from ModelDriver and JUnit with no view.>`
- `<Anything moved across the model/view line, or anything in the model that still knows about the view. Write "nothing" if true.>`

### What happened to our unknowns
| Unknown (from scope) | Status | Notes |
|---|---|---|
| PlantNet HTTP requests | `<Open / Figured out>` | `<notes>` |
| Camera access | `<Open / Figured out>` | `<notes>` |
| Swing view | `<Open / Figured out>` | `<notes>` |
| `<other unknown>` | `<status>` | `<notes>` |

### What doesn't work yet
- `<insert bad input`

## Next Steps

### Remaining MVP work (in order)
1. `<e.g. real PlantNetIdentifier>`
2. `<e.g. Swing view>`
3. `<e.g. camera / file selection>`

### Change feature: `<Undo last cart add>`
- **What it does:** `<removes the most recently added CartItem; changes model state and is reachable from the controller and view>`
- **Predicted to touch:** `LinkedQueue.<removeLast()>`, `PlantModel.addToCart()`, `PlantModel.<undoLastAdd()>`, `PlantModel.snapshot()`, `PlantController.<undoLastAdd()>`, `PlantView.render()`
- **Predicted NOT to change:** `PlantIdentifier`, `PlantNetIdentifier`, `PlantCatalog`, `LocalPlantCatalog`, `Identification`, `PlantDetails`, `Listing`
- We will compare this prediction to what actually happens in Deliverable 4.

### Stretch goals
- **Committing to:** `<list>`
- **Dropping:** `<list>`

### New tests needed
- `LinkedQueue.<removeLast()>`: normal, empty queue, single item
- `PlantModel.<undoLastAdd()>`: normal, empty cart
- `PlantController.<undoLastAdd()>`: normal, empty cart message

### Who does what
| Item | Owner |
|---|---|
| `<remaining MVP item 1>` | `<name>` |
| `<remaining MVP item 2>` | `<name>` |
| Change feature: model and queue | `<Jamie / Conrad>` |
| Change feature: controller | `<Andrew>` |
| Change feature: view | `<Josh>` |
| New tests | `<name>` |

## Attribution

| File | Contributor | Contribution |
|---|---|---|
| `src/model/LinkedQueue.java` | Conrad | `<Wrote; GenAI-assisted / reviewed by ...>` |
| `src/model/Node.java` | Conrad | `<Wrote>` |
| `test/LinkedQueueTest.java` | Conrad | `<Wrote tests from the test plan; tested by ...>` |
| `src/model/PlantModel.java` | Jamie | `<Wrote; GenAI-assisted>` |
| `test/PlantModelTest.java` | Jamie | `<Wrote>` |
| `src/model/Identification.java`, `PlantDetails.java`, `Listing.java`, `CartItem.java`, `CatalogResult.java`, `PlantSnapshot.java` | `<name>` | `<Wrote records>` |
| `src/model/PlantIdentifier.java`, `PlantCatalog.java`, `ServiceException.java` | `<name>` | `<Wrote interfaces and exception>` |
| `src/model/LocalPlantCatalog.java` | `<name>` | `<Wrote with hardcoded plants>` |
| `src/model/FakeIdentifier.java` | `<name>` | `<Wrote canned identifier>` |
| `src/model/PlantNetIdentifier.java` | `<name>` | `<Stub only>` |
| `src/ModelDriver.java` | `<name>` | `<Wrote console driver for the model>` |
| `src/view/PlantView.java` | Josh | `<Wrote console view; GenAI-assisted>` |
| `src/controller/PlantController.java` | Andrew | `<Wrote; handles bad-input messages>` |
| `README.md` | Jamie | `Written and revised by Jamie` |

**GenAI use:** `<insert what we used ai for here>`
