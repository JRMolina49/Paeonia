import java.awt.Component;
import java.awt.Container;
import java.util.List;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/**
 * Checks Swing rendering and busy state on the event dispatch thread.
 * @author Conrad
 */
public final class PlantViewTest
{
    private PlantViewTest()
    {
        // Utility class.
    }

    /**
     * Runs the view checks without opening windows or dialogs.
     * @param args unused
     * @throws Exception if a check fails
     */
    public static void main(String[] args) throws Exception
    {
        Locale original = Locale.getDefault();
        try
        {
            Locale.setDefault(Locale.US);
            SwingUtilities.invokeAndWait(() ->
            {
                checkRendering();
                checkBusy();
                checkMessages();
                checkWithoutController();
            });
        }
        finally
        {
            Locale.setDefault(original);
        }
        System.out.println("PlantViewTest passed");
    }

    private static void checkRendering()
    {
        PlantView view = new PlantView();
        JPanel plants = panel(view, "Plant Information");
        JPanel shopping = panel(view, "Available Plants");
        JPanel cart = panel(view, "Shopping Cart");
        PlantSnapshot empty = new PlantSnapshot(List.of(), List.of(), List.of());
        view.render(empty);
        TestSupport.check(plants.getComponentCount() == 0
            && shopping.getComponentCount() == 0 && cart.getComponentCount() == 0,
            "Empty snapshot must render without stale content");

        Listing first = TestSupport.listing();
        Listing second = new Listing("second", "species", "Second plant", "plant", 250);
        view.render(new PlantSnapshot(List.of(TestSupport.details()),
            List.of(first, second), List.of(new CartItem(second), new CartItem(first))));
        TestSupport.check(hasLabel(plants, "Plant: Common"), "Common name displayed");
        TestSupport.check(hasLabel(plants, "Care Instructions: Water weekly"), "Care displayed");
        TestSupport.check(shopping.getComponentCount() == 2, "Render both listings");
        TestSupport.check(hasLabel(shopping, "Plant") && hasLabel(shopping, "$10.00"),
            "First listing name and price");
        TestSupport.check(hasLabel(shopping, "Second plant") && hasLabel(shopping, "$2.50"),
            "Second listing name and price");
        TestSupport.check(cart.getComponentCount() == 2, "Render both cart items");
        TestSupport.check("Second plant".equals(((JLabel)cart.getComponent(0)).getText())
            && "Plant".equals(((JLabel)cart.getComponent(1)).getText()),
            "Display cart in snapshot order");

        view.render(new PlantSnapshot(List.of(), List.of(first), List.of()));
        TestSupport.check(plants.getComponentCount() == 0 && cart.getComponentCount() == 0,
            "New snapshot clears old plant and cart");
        TestSupport.check(shopping.getComponentCount() == 1
            && !hasLabel(shopping, "Second plant"), "New snapshot replaces listings");
        view.render(empty);
        TestSupport.check(shopping.getComponentCount() == 0, "Clear remaining listing");
    }

    private static void checkBusy()
    {
        PlantView view = new PlantView();
        JButton select = button(view, "Select Plant Image");
        JButton checkout = button(view, "Checkout");
        PlantSnapshot populated = new PlantSnapshot(List.of(),
            List.of(TestSupport.listing()), List.of());
        view.render(populated);
        TestSupport.check(select.isEnabled() && checkout.isEnabled()
            && hasLabel(view, "Ready"), "Initially ready");
        view.setBusy(true);
        TestSupport.check(!select.isEnabled() && !checkout.isEnabled(),
            "Busy state disables image selection and checkout");
        TestSupport.check(!button(view, "Add to Cart").isEnabled(), "Disable listing buttons while busy");
        view.render(populated);
        TestSupport.check(!button(view, "Add to Cart").isEnabled(), "New listing buttons respect busy state");
        TestSupport.check(!hasLabel(view, "Ready"), "Busy status replaces ready status");
        view.setBusy(false);
        TestSupport.check(select.isEnabled() && checkout.isEnabled()
            && hasLabel(view, "Ready"), "Restore ready state");
        TestSupport.check(button(view, "Add to Cart").isEnabled(), "Restore listing buttons");
    }

    private static void checkMessages()
    {
        PlantView view = new PlantView();
        // These must return without attempting to open a dialog in headless mode.
        view.showMessage(null);
        view.showMessage("");
        view.showMessage("  ");
        RecordingView recording = new RecordingView();
        recording.render(new PlantSnapshot(List.of(TestSupport.details()), List.of(), List.of()));
        recording.render(null);
        TestSupport.check(recording.message != null && !recording.message.isBlank(),
            "Null snapshot produces a message");
        TestSupport.check(hasLabel(recording, "Plant: Common"),
            "Null snapshot preserves previous display");
    }

    private static void checkWithoutController()
    {
        PlantView view = new PlantView();
        view.setController(null);
        view.render(new PlantSnapshot(List.of(), List.of(TestSupport.listing()), List.of()));
        button(view, "Add to Cart").doClick(0);
        button(view, "Checkout").doClick(0);
        TestSupport.check(panel(view, "Shopping Cart").getComponentCount() == 0,
            "Buttons safely handle an unattached controller");
    }

    private static JPanel panel(Container root, String title)
    {
        JPanel found = findPanel(root, title);
        if (found == null)
        {
            throw new AssertionError("Missing panel: " + title);
        }
        return found;
    }

    private static JPanel findPanel(Container root, String title)
    {
        if (root instanceof JPanel && ((JPanel)root).getBorder() instanceof TitledBorder
            && title.equals(((TitledBorder)((JPanel)root).getBorder()).getTitle()))
        {
            return (JPanel)root;
        }
        for (Component child : root.getComponents())
        {
            if (child instanceof Container)
            {
                JPanel found = findPanel((Container)child, title);
                if (found != null)
                {
                    return found;
                }
            }
        }
        return null;
    }

    private static JButton button(Container root, String text)
    {
        JButton found = findButton(root, text);
        if (found == null)
        {
            throw new AssertionError("Missing button: " + text);
        }
        return found;
    }

    private static JButton findButton(Container root, String text)
    {
        for (Component child : root.getComponents())
        {
            if (child instanceof JButton && text.equals(((JButton)child).getText()))
            {
                return (JButton)child;
            }
            if (child instanceof Container)
            {
                JButton found = findButton((Container)child, text);
                if (found != null)
                {
                    return found;
                }
            }
        }
        return null;
    }

    private static boolean hasLabel(Container root, String text)
    {
        for (Component child : root.getComponents())
        {
            if (child instanceof JLabel && text.equals(((JLabel)child).getText()))
            {
                return true;
            }
            if (child instanceof Container && hasLabel((Container)child, text))
            {
                return true;
            }
        }
        return false;
    }

    private static final class RecordingView extends PlantView
    {
        private static final long serialVersionUID = 1L;
        private String message;

        @Override
        public void showMessage(String text)
        {
            message = text;
        }
    }
}
