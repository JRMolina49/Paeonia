import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
        view.setController(this);
    }

    /** Runs the application. */
    public static void main(String[] args) {
        try {
            String apiKey = System.getenv("PLANTNET_API_KEY");
            boolean demo = apiKey == null || apiKey.isBlank()
                || Arrays.asList(args).contains("--demo");
            PlantIdentifier identifier = demo
                ? image -> List.of(new Identification("Helianthus annuus",
                    "Helianthus annuus", "Sunflower", 1.0))
                : new PlantNetIdentifier(apiKey);
            PlantCatalog catalog = new LocalPlantCatalog(
                List.of(new PlantDetails("Helianthus annuus", "Helianthus annuus",
                    "Sunflower", "Full sun; water when the top of the soil is dry.", 1.0)),
                List.of(new Listing("sunflower-seeds", "Helianthus annuus",
                    "Sunflower seeds", "Seeds", 399),
                    new Listing("sunflower-starter", "Helianthus annuus",
                        "Sunflower starter plant", "Plant", 899)));

            PlantModel model = new PlantModel(identifier, catalog);
            if (demo) {
                // Preload sample data so the cart workflow can be tried immediately.
                ByteArrayOutputStream image = new ByteArrayOutputStream();
                ImageIO.write(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB),
                    "png", image);
                model.identify(image.toByteArray());
            }

            SwingUtilities.invokeLater(() -> {
                PlantView view = new PlantView();
                new PlantController(model, view);
                view.render(model.snapshot());

                JFrame frame = new JFrame(demo ? "Paeonia - Demo" : "Paeonia");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                JLabel mode = new JLabel(demo
                    ? "Demo: sample sunflower results for any valid image. No live identification."
                    : "Live PlantNet identification. Shopping uses the local sample catalog.");
                mode.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                frame.add(mode, BorderLayout.NORTH);
                frame.add(view, BorderLayout.CENTER);
                frame.setSize(850, 650);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        } catch (IOException | RuntimeException e) {
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
        if (busy) {
            return;
        }
        try {
            model.addToCart(listingId);
            view.render(model.snapshot());
        } catch (IllegalArgumentException e) {
            view.showMessage("Please select an available listing.");
        }
    }

    /** Requests the next cart item for checkout. */
    public void checkoutNext() {
        if (busy) {
            return;
        }
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
