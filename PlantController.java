import java.util.ArrayList;
import java.util.NoSuchElementException;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Controller: converts UI requests into model calls and updates the view.
 * The view never touches the model directly, and the model never touches the view.
 */
public class PlantController {

    private final PlantModel model;
    private final PlantView view;
    private boolean busy;

    /**
     * Creates a controller that coordinates the given model and view.
     *
     * @throws IllegalArgumentException if model or view is null
     */
    public PlantController(PlantModel model, PlantView view) {
        if (model == null || view == null) {
            throw new IllegalArgumentException("Model and view must not be null.");
        }
        this.model = model;
        this.view = view;
        this.busy = false;
    }

    /** Runs the application. */
    public static void main(String[] args) {
        try {
            String apiKey = System.getenv("PLANTNET_API_KEY");
            PlantIdentifier identifier = new PlantNetIdentifier(apiKey);

            // TODO: Replace with real catalog data
            PlantCatalog catalog =
                new LocalPlantCatalog(new ArrayList<>(), new ArrayList<>());

            PlantModel model = new PlantModel(identifier, catalog);

            SwingUtilities.invokeLater(() -> {
                PlantView view = new PlantView();
                PlantController controller = new PlantController(model, view);

                // The view forwards user events to this controller
                view.render(model.snapshot());
            });
        } catch (RuntimeException e) {
            System.err.println("Failed to start application: " + e.getMessage());
        }
    }

    /**
     * Sends the picture of a plant to the identification model on a background
     * worker, then refreshes the view with the resulting snapshot.
     */
    public void identifyPhoto(byte[] image) {
        if (busy) {
            return;
        }
        if (image == null || image.length == 0) {
            view.showMessage("Please fix the camera or put a plant image in view.");
            return;
        }

        setBusy(true);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                model.identify(image);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    view.render(model.snapshot());
                } catch (Exception e) {
                    handleIdentifyFailure(e.getCause() != null ? e.getCause() : e);
                } finally {
                    setBusy(false);
                }
            }
        }.execute();
    }

    /** Adds the listing with the given id to the shopping cart. */
    public void addPlant(String listingId) {
        try {
            model.addToCart(listingId);
            view.render(model.snapshot());
        } catch (IllegalArgumentException e) {
            view.showMessage("Please select an available listing.");
        }
    }

    /** Requests the next cart item for checkout. */
    public void checkoutNext() {
        try {
            CartItem item = model.checkoutNext();
            view.showMessage("Checked out: " + item.getListing().getDisplayName());
            view.render(model.snapshot());
        } catch (NoSuchElementException e) {
            view.showMessage("Your cart is empty");
        }
    }

    /** Informs the user that the camera cannot be used. */
    public void cameraUnavailable() {
        view.showMessage("Camera access is unavailable. Please enable camera "
            + "access in your settings, or select an image file instead.");
    }

    private void handleIdentifyFailure(Throwable cause) {
        if (cause instanceof IllegalArgumentException) {
            // invalid image, or no plant recognized in it
            view.showMessage("No plant was identified. Please select or take "
                + "a clear image of a plant.");
        } else if (cause instanceof ServiceException) {
            view.showMessage("The identification service is unavailable. "
                + "Please try again later.");
        } else {
            view.showMessage("Something went wrong while identifying the plant.");
        }
    }

    private void setBusy(boolean busy) {
        this.busy = busy;
        view.setBusy(busy);
    }
}
