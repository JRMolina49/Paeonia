import java.awt.Component;
import java.awt.Container;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/** Checks controller wiring, background work, cart actions, and failures. */
public final class PlantControllerTest
{
    private PlantControllerTest()
    {
        // Utility class.
    }

    /**
     * Runs controller checks without opening dialogs or making HTTP requests.
     * @param args unused
     * @throws Exception if a check fails
     */
    public static void main(String[] args) throws Exception
    {
        byte[] image = TestSupport.image();
        Listing listing = TestSupport.listing();
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch entered = new CountDownLatch(1);
        PlantIdentifier identifier = bytes ->
        {
            TestSupport.check(!SwingUtilities.isEventDispatchThread(), "Identify off the Swing thread");
            entered.countDown();
            try
            {
                if (!release.await(5, TimeUnit.SECONDS))
                {
                    throw new AssertionError("Worker was not released");
                }
            }
            catch (InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(exception);
            }
            return List.of(new Identification("species", "Scientific", "Common", 0.9));
        };
        PlantModel model = new PlantModel(identifier,
            id -> new CatalogResult("Care", List.of(listing)));
        RecordingView view = new RecordingView();
        PlantController[] controller = new PlantController[1];
        SwingUtilities.invokeAndWait(() ->
        {
            TestSupport.expect(IllegalArgumentException.class, () -> new PlantController(null, view));
            TestSupport.expect(IllegalArgumentException.class, () -> new PlantController(model, null));
            controller[0] = new PlantController(model, view);
            controller[0].identifyPhoto(null);
            TestSupport.check(view.message != null, "Invalid image produces message");
            view.completed = new CountDownLatch(1);
            controller[0].identifyPhoto(image);
        });
        try
        {
            TestSupport.check(entered.await(5, TimeUnit.SECONDS), "Identification worker started");
            SwingUtilities.invokeAndWait(() ->
            {
                TestSupport.check(!button(view, "Select Plant Image").isEnabled(), "Busy during identification");
                controller[0].addPlant("id");
                controller[0].checkoutNext();
                TestSupport.check(model.snapshot().getCart().isEmpty(), "Cart actions ignored while busy");
            });
        }
        finally
        {
            release.countDown();
        }
        TestSupport.check(view.completed.await(5, TimeUnit.SECONDS), "Identification completed");
        SwingUtilities.invokeAndWait(() ->
        {
            TestSupport.check(button(view, "Select Plant Image").isEnabled(), "Restore controls");
            button(view, "Add to Cart").doClick(0);
            TestSupport.check(model.snapshot().getCart().size() == 1, "View forwards add to controller");
            button(view, "Checkout").doClick(0);
            TestSupport.check(model.snapshot().getCart().isEmpty(), "View forwards checkout");
            TestSupport.check(view.message.contains("Plant"), "Checkout uses Listing getters");
            controller[0].checkoutNext();
            TestSupport.check(view.message.contains("empty"), "Empty cart message");
            controller[0].addPlant("missing");
            TestSupport.check(model.snapshot().getCart().isEmpty(), "Reject unavailable listing");
            view.completed = new CountDownLatch(1);
            controller[0].identifyPhoto(new byte[] {1, 2});
        });
        TestSupport.check(view.completed.await(5, TimeUnit.SECONDS), "Invalid image restores controls");
        TestSupport.check(model.snapshot().getPlant().size() == 1, "Invalid image preserves results");

        PlantModel failing = new PlantModel(bytes ->
        {
            throw new ServiceException("Unavailable");
        }, id -> new CatalogResult("", List.of()));
        SwingUtilities.invokeAndWait(() ->
        {
            PlantController failureController = new PlantController(failing, view);
            view.completed = new CountDownLatch(1);
            failureController.identifyPhoto(image);
        });
        TestSupport.check(view.completed.await(5, TimeUnit.SECONDS), "Service failure completes");
        SwingUtilities.invokeAndWait(() ->
        {
            TestSupport.check(view.message.contains("service is unavailable"), "Service error message");
            TestSupport.check(button(view, "Select Plant Image").isEnabled(), "Restore controls after failure");
        });
        System.out.println("PlantControllerTest passed");
    }

    private static JButton button(Container root, String text)
    {
        for (Component child : root.getComponents())
        {
            if (child instanceof JButton && text.equals(((JButton)child).getText()))
            {
                return (JButton)child;
            }
            if (child instanceof Container)
            {
                JButton found = button((Container)child, text);
                if (found != null)
                {
                    return found;
                }
            }
        }
        return null;
    }

    private static final class RecordingView extends PlantView
    {
        private static final long serialVersionUID = 1L;
        private String message;
        private volatile CountDownLatch completed;

        @Override
        public void showMessage(String value)
        {
            message = value;
        }

        @Override
        public void setBusy(boolean value)
        {
            super.setBusy(value);
            if (!value && completed != null)
            {
                completed.countDown();
            }
        }
    }
}
