# Paeonia

**CS 2114 · Project 3 · Deliverable 4 — Second Prototype + Tests**

**Group 60:** Jamie, Conrad, Josh, and Andrew

Paeonia is a Java Swing application that identifies a plant from an image,
shows plant information and reference photos, and lets the user add sample
listings to a FIFO shopping cart. Identification can use PlantNet or an offline
demo. Listings and checkout are demonstrations; no inventory is purchased and
no payment is processed.

## Compile and Run

### Requirements

- JDK 11 or newer (`java -version` and `javac -version`).
- Maven (`mvn -version`). Maven downloads Gson and JUnit on the first build.
- A graphical desktop to run the application. Tests can run headlessly.
- A PlantNet API key and internet access for live identification only.

Run these commands from the repository root:

```sh
mvn compile dependency:copy-dependencies -DincludeScope=runtime
java -cp "target/classes:target/dependency/*" PlantController --demo
```

The commands above use the macOS/Linux classpath separator. On Windows, replace
`:` with `;` in the Java classpath.

**Offline demo:** `--demo` loads sample aloe information and a sample listing.
Selecting any supported image returns the same canned aloe result. This mode
lets us demonstrate image validation, rendering, the cart, and checkout without
an API key. It does not recognize the contents of the selected image or supply
PlantNet reference photos.

**Live identification:** set `PLANTNET_API_KEY` in the environment, then omit
`--demo`. For example, in a macOS/Linux terminal:

```sh
export PLANTNET_API_KEY="your-key-here"
java -cp "target/classes:target/dependency/*" PlantController
```

Without a nonblank key, startup selects demo mode automatically. In live mode,
choose **Select Plant Image** and select a readable image. The application
identifies it in a background worker, then displays the results and any supported
PlantNet reference photos. Care information and sample listings are currently
available only for aloe, monstera, and Chinese peony. Other recognized species
can still be displayed, with a message when no sample listing is available.

Use **Add Sample Item** to enqueue a listing and **Demo Checkout** to remove the
oldest cart item. The cart remains intact when another plant is identified.

## Run the Tests

```sh
mvn clean test
```

To run just the core model and queue tests:

```sh
mvn -Dtest=PlantModelTest,LinkedQueueTest test
```

The suite uses **JUnit 4**, with **27 test methods across 17 test classes**.
Maven discovers tests in `tests/`; results are written to
`target/surefire-reports/`. A successful run reports zero failures and errors.
These are JUnit tests, so run them through Maven or a JUnit runner rather than
calling a test class's `main()` method.

Model tests inject identifier and catalog services and run independently of the
view. HTTP contract tests use a local loopback server instead of PlantNet; they
require no API key. The additional Swing and controller tests run headlessly,
perform UI operations on the event dispatch thread, and use recording views
instead of opening message dialogs.

| Test area | What is checked |
|---|---|
| Model | Identification, invalid/corrupt images, no matches, invalid service data, atomic state replacement, snapshots, cart validation, FIFO checkout, and cart retention across identifications. |
| Queue | Normal enqueue/peek/dequeue, null enqueue, empty peek/dequeue, size and empty state, independent list copies, reuse after draining, and order after interleaved enqueues/dequeues. |
| New reference-image state | Metadata parsing and getters, allowed URL validation, default attribution text, defensive copies, immutable lists, propagation into snapshots, and preservation after failed identification. |
| Service implementations | Catalog filtering and validation; sample catalog entries; JPEG conversion; multipart upload format; successful, no-match, and failed HTTP responses; malformed response data. |
| Data classes | Constructor validation, getters, boundaries, and immutable snapshots/collections. |
| Controller and view | Event wiring, background identification, busy controls, cart actions, replacement of displayed results, and error recovery. |

Methods without input parameters, such as `snapshot()`, `size()`, and
`isEmpty()`, are checked in empty and populated states rather than given an
artificial invalid argument.

## Design

The model owns identification results, available listings, and the cart. User
actions follow this path:

```text
PlantView → PlantController → PlantModel → PlantIdentifier / PlantCatalog
                                     ↓
                               PlantSnapshot
                                     ↓
                                 PlantView
```

| Responsibility | Classes |
|---|---|
| Model state and FIFO cart | `PlantModel`, `LinkedQueue`, `PlantSnapshot` |
| Identification and reference-photo data | `Identification`, `PlantDetails`, `PlantDetails.ReferenceImage` |
| Catalog and cart data | `CatalogResult`, `Listing`, `CartItem` |
| Service contracts | `PlantIdentifier`, `PlantCatalog` |
| PlantNet requests, parsing, and upload conversion | `PlantNetIdentifier`, `PlantImageEncoder`, `ServiceException` |
| Local care information and sample listings | `LocalPlantCatalog`, `DemoPlantCatalog` |
| Actions, background work, and application startup | `PlantController` |
| Swing rendering, file selection, and photo display | `PlantView` |

The view renders snapshots instead of keeping its own plant, listing, or cart
collections. Its image cache and loading indicators are presentation state.
`PlantModel` has no Swing dependency and uses the service interfaces so tests
can replace network and catalog behavior.

## Reflection

### How the design absorbed the change

The repository's D3 Next Steps called for wiring the controller, populating the
catalog, integrating the API, and testing the concrete service classes. D4
implements that identification flow and extends its model data to carry
**PlantNet reference photos and their attribution** alongside each result.
Reference photos are visible after a successful live identification when the
response contains supported image metadata.

The feature required more than a view redesign: `Identification` and
`PlantDetails` now hold immutable reference-image lists, and
`PlantModel.identify()` carries those lists into the state exposed by
`PlantSnapshot`. The existing snapshot structure could carry the richer plant
objects without changing its public methods.

| Class touched for the feature | Change |
|---|---|
| `PlantNetIdentifier` | Parse reference-image URLs and attribution; normalize the species key for catalog lookup. |
| `PlantImageEncoder` (new) | Convert readable uploads to actual JPEG bytes before sending JPEG multipart data. |
| `Identification` | Carry reference-image metadata from the identifier. |
| `PlantDetails` | Store immutable reference images and define the `ReferenceImage` value type. |
| `PlantModel` | Preserve reference-image metadata when assembling identified plants. |
| `PlantView` | Show the uploaded image and load/display a reference image with attribution. |
| `PlantController` | Connect the new display behavior to successful identification and use the populated sample catalog. |
| `DemoPlantCatalog` (new) | Supply care and sample listings for the supported demonstration species. |

The API and catalog work matched the D3 plan recorded in the repository.
Reference-photo metadata expanded that plan: it also required changes to
`Identification`, `PlantDetails`, `PlantModel`, and the view. D3 did not record a
class-by-class prediction of which classes would remain unchanged, so we cannot
claim that every affected class matched such a prediction.

`LinkedQueue`, `PlantSnapshot`, `Listing`, `CartItem`, `CatalogResult`,
`PlantIdentifier`, and `PlantCatalog` did not need changes for this feature.
The later test/integration work also added early null-listing validation to
`LocalPlantCatalog`, restored busy-state behavior in the view, hardened response
handling, and configured JUnit in `pom.xml`.

### Model/view boundary

The change crossed layers because new identification data had to reach the
screen. The model stores image metadata; it does not download display images or
create Swing components. The controller still requests identification and
passes the resulting snapshot to the view. The view learned how to display a
`ReferenceImage`, including its author, license, and optional plant part, while
cart rules and identification state stayed in the model.

One coupling remains: `PlantNetIdentifier` constructs the nested
`PlantDetails.ReferenceImage` type, and image-host validation is specific to
PlantNet. Moving that metadata into a standalone value class would make another
identification provider cheaper to add without changing the data contract used
by the view.

### Changes from D3

- The sample catalog is populated instead of being empty. Unknown species still
  produce an identification, but may have no care information or sample listing.
- Existing constructors remain available; overloads add reference-image lists
  without forcing every caller to provide photos.
- Uploads are converted to JPEG rather than merely being labeled as JPEG.
- The app retains an explicit offline demo alongside live API identification.
- Standalone test programs were converted to JUnit, and dedicated concrete-service
  tests were added. Model/queue checks still run independently of the UI.
- Identification has connection/request timeouts, and malformed service responses
  become controlled service errors. Failed model operations preserve prior state.

### What does not work yet

- Live PlantNet credentials, real image recognition, and reference-photo downloads
  have not been verified by the automated suite. Local HTTP tests check the
  request/response contract, not the live service.
- Camera capture is not implemented; users select an image file.
- The local catalog has only three species. Listings, prices, and checkout are
  sample data, with no real seller, order, or payment integration.
- Cart contents are held in memory and disappear when the application closes.
- Automated coverage does not simulate every network timing condition or establish
  visual layout quality. Timeout duration and remote-image failures still need
  manual checks.

## Next Steps

### Remaining work and proposed owners

| Priority | Remaining item | Owner |
|---|---|---|
| 1 | Verify live identification and reference-image attribution with known aloe, monstera, and peony images; check no-match and unavailable-service behavior. | Josh and Andrew |
| 2 | Check view layout, unreadable files, repeated identification, and recovery after errors; retain disabled cart controls while requests run. | Josh and Andrew |
| 3 | Review model/service edge cases and test contracts; manually check timeout recovery without losing previous results or cart items. | Conrad and Jamie |
| 4 | Confirm the submitted D3 comparison, per-file attribution, final scope, and demo timing; package source, tests, and README together. | Jamie, with all members reviewing their contributions |

### Stretch goals

For the final build, the proposed commitments are reference photos with
attribution, a usable offline demo, and reliable error recovery. We are dropping
camera capture, persistent carts, a large catalog, and real seller/payment
integration from this build. These choices keep the demonstration focused on
the model, controller/view integration, and FIFO queue behavior.

### Three-minute demo

Prepare the API key, a known catalog-species image, and an unreadable image file
before the demo. Start live mode before presenting; allow at most 20 seconds for
an identification request. If live access is unavailable, use the labeled demo
and explain that remote reference photos require live data.

| Time | Demonstration |
|---|---|
| 0:00–0:20 | Introduce the app and identify model, controller, and view responsibilities. |
| 0:20–1:00 | Select the known image. Show the identification, care information, uploaded preview, and the new reference-photo/attribution feature when available. |
| 1:00–1:35 | Add a sample item twice, check out one, add another, and check out again. Explain the FIFO order and that checkout is simulated. |
| 1:35–2:05 | Select the unreadable file. Show the error, restored controls, and preserved results/cart; demonstrate empty-cart handling if time permits. |
| 2:05–2:35 | Show the passing JUnit result and point to model/queue, interleaving, and reference-image tests. |
| 2:35–3:00 | Explain the changed classes, the model/view boundary, and remaining limitations. |

## Attribution

Each Java source and test file has its own row. Nested `ReferenceImage` is listed
separately for clarity. The table combines repository history with the prior team
attribution; contributions made outside Git still need confirmation by the team.

| Class or file | Contributor(s) | Contribution |
|---|---|---|
| `CartItem.java` | Josh; Conrad | Josh wrote the data class; Conrad revised validation and tested it. |
| `CatalogResult.java` | Conrad; Jamie* | Model/catalog result implementation, revisions, and tests. |
| `DemoPlantCatalog.java` | Josh | Wrote the sample care/listing catalog. |
| `Identification.java` | Josh; Conrad | Josh wrote identification data and added photo metadata; Conrad revised validation and tested it. |
| `LinkedQueue.java` | Conrad; Jamie* | FIFO queue implementation, revisions, and tests. |
| `Listing.java` | Josh; Conrad | Josh wrote listing data; Conrad revised validation and tested it. |
| `LocalPlantCatalog.java` | Josh; Conrad | Josh wrote lookup behavior; Conrad integrated the result contract, validated inputs, and tested it. |
| `PlantCatalog.java` | Conrad; Jamie* | Catalog interface and contract tests. |
| `PlantController.java` | Andrew; Conrad; Josh | Andrew wrote the controller; Conrad revised startup, demo mode, wiring, and busy/error behavior; Josh integrated the new image display/catalog flow. |
| `PlantDetails.java` | Josh; Conrad | Josh wrote plant data and added reference images; Conrad revised validation and tested it. |
| `PlantDetails.ReferenceImage` | Josh; Conrad | Josh wrote image metadata and URL validation; Conrad added metadata/validation tests. |
| `PlantIdentifier.java` | Conrad; Jamie* | Identifier interface and contract tests. |
| `PlantImageEncoder.java` | Josh | Wrote upload conversion to JPEG. |
| `PlantModel.java` | Conrad; Jamie*; Josh | Conrad implemented state, validation, snapshots, and queue/cart operations; Josh added reference-image propagation. |
| `PlantNetIdentifier.java` | Josh; Conrad | Josh wrote HTTP identification and metadata parsing; Conrad revised JPEG integration, timeouts, response errors, and tests. |
| `PlantSnapshot.java` | Josh; Conrad | Josh wrote snapshot data; Conrad revised list-based state and defensive copies and tested them. |
| `PlantView.java` | Josh; Conrad | Josh wrote/redesigned the Swing UI and image display; Conrad revised busy controls and tested rendering/integration. |
| `ServiceException.java` | Josh | Wrote the service error type. |
| `tests/CartItemTest.java` | Conrad | Wrote validation/getter checks and converted them to JUnit. |
| `tests/CatalogResultTest.java` | Conrad | Wrote result validation/copy checks and converted them to JUnit. |
| `tests/DemoPlantCatalogTest.java` | Conrad | Wrote JUnit checks for sample species, care, and labeled listings. |
| `tests/IdentificationTest.java` | Conrad | Wrote validation checks, converted them to JUnit, and added reference-image tests. |
| `tests/LinkedQueueTest.java` | Conrad | Wrote queue contracts, empty/null cases, interleaved FIFO checks, and JUnit conversion. |
| `tests/ListingTest.java` | Conrad | Wrote validation/boundary checks and converted them to JUnit. |
| `tests/LocalPlantCatalogTest.java` | Conrad | Wrote JUnit checks for filtering, copying, unknown species, and invalid input. |
| `tests/PlantCatalogTest.java` | Conrad | Wrote isolated catalog-contract checks and converted them to JUnit. |
| `tests/PlantControllerTest.java` | Conrad | Wrote wiring, background-work, cart, and failure checks; adapted them to the revised UI and JUnit. |
| `tests/PlantDetailsTest.java` | Conrad | Wrote validation checks and added JUnit reference-image/URL coverage. |
| `tests/PlantIdentifierTest.java` | Conrad | Wrote isolated identifier-contract checks and converted them to JUnit. |
| `tests/PlantImageEncoderTest.java` | Conrad | Wrote JUnit checks for JPEG conversion, transparency, and invalid images. |
| `tests/PlantModelTest.java` | Conrad | Wrote isolated model/state/cart checks and JUnit feature/failure coverage. |
| `tests/PlantNetIdentifierTest.java` | Conrad | Wrote offline parsing, metadata, validation, and loopback HTTP contract tests. |
| `tests/PlantSnapshotTest.java` | Conrad | Wrote snapshot validation/defensive-copy checks and converted them to JUnit. |
| `tests/PlantViewTest.java` | Conrad | Wrote headless rendering/control checks and adapted them to the revised UI and JUnit. |
| `tests/ServiceExceptionTest.java` | Conrad | Wrote message/cause checks. |
| `tests/TestSupport.java` | Conrad | Wrote shared assertions, fixtures, and image data for tests. |
| `pom.xml` | Josh; Conrad | Josh added the Maven/Gson build; Conrad configured JUnit and test discovery. |
| `README.md` | Jamie; Conrad | Jamie wrote earlier documentation; Conrad revised it for the integrated D4 prototype and tests. |

*The D3 README credits Conrad and Jamie jointly for the model area. That shared
credit is retained here; Jamie's specific per-file role must be confirmed before
submission rather than inferred from commits. Proposed remaining owners and
stretch-goal commitments above also need team agreement.*

**GenAI use:** Generative AI assisted with model and test implementation,
integration/debugging, and drafting/revising documentation. This includes JUnit
conversion, candidate validation/edge-case tests, and reference-image/service
checks. 
