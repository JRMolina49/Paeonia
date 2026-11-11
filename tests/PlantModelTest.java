import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.imageio.ImageIO;

/**
 * Checks Model validation, identification, snapshots, and cart behavior.
 * @author Conrad
 */
public final class PlantModelTest
{
    private PlantModelTest()
    {
        // Utility class.
    }

    /**
     * Runs the Model checks.
     * @param args unused
     * @throws Exception if a check or image encoding fails
     */
    public static void main(String[] args) throws Exception
    {
        checkModel();
        checkCartAcrossIdentifications();
        System.out.println("PlantModelTest passed");
    }

    private static void checkModel() throws Exception
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB),
            "png", output);
        byte[] image = output.toByteArray();
        FakeIdentifier identifier = new FakeIdentifier();
        Listing first = listing("first");
        Listing second = listing("second");
        PlantCatalog catalog = id -> new CatalogResult("Water weekly",
            List.of(first, second));
        expect(IllegalArgumentException.class, () -> new PlantModel(null, catalog));
        expect(IllegalArgumentException.class, () -> new PlantModel(identifier, null));
        PlantModel model = new PlantModel(identifier, catalog);
        check(model.snapshot().getPlant().isEmpty(), "Initial plants must be empty");
        check(model.snapshot().getListings().isEmpty(), "Initial listings must be empty");
        expect(NoSuchElementException.class, model::checkoutNext);
        expect(IllegalArgumentException.class, () -> model.identify(null));
        expect(IllegalArgumentException.class, () -> model.identify(new byte[0]));
        expect(IllegalArgumentException.class, () -> model.identify(new byte[] {1, 2}));
        check(identifier.calls == 0, "Invalid images must not reach service");
        model.identify(image);
        check(model.snapshot().getPlant().size() == 1, "Identification missing");
        check("Water weekly".equals(model.snapshot().getPlant().get(0).getCareText()),
            "Care must come from catalog");
        model.addToCart("first");
        model.addToCart("second");
        PlantSnapshot before = model.snapshot();
        expect(IllegalArgumentException.class, () -> model.addToCart(null));
        expect(IllegalArgumentException.class, () -> model.addToCart(" "));
        expect(IllegalArgumentException.class, () -> model.addToCart("unknown"));
        identifier.results = List.of();
        expect(IllegalArgumentException.class, () -> model.identify(image));
        check(model.snapshot().getPlant().get(0) == before.getPlant().get(0),
            "No match must preserve identification");
        check(model.snapshot().getListings().equals(before.getListings()),
            "No match must preserve listings");
        check(model.snapshot().getCart().size() == 2, "Failures must preserve cart");
        identifier.fail = true;
        expect(IllegalStateException.class, () -> model.identify(image));
        check(model.snapshot().getPlant().get(0) == before.getPlant().get(0),
            "Service failure must preserve identification");
        identifier.fail = false;
        identifier.results = List.of(new Identification("species", "Scientific", "Common", 0.9));
        model.identify(image);
        check(model.snapshot().getCart().size() == 2, "Identification must preserve cart");
        check(model.checkoutNext().getListing() == first, "Checkout must be FIFO");
        check(before.getCart().size() == 2, "Old snapshots must stay unchanged");
        check(model.checkoutNext().getListing() == second, "Second item missing");
        expect(NoSuchElementException.class, model::checkoutNext);
        PlantModel unknown = new PlantModel(identifier,
            id -> new CatalogResult("", List.of()));
        unknown.identify(image);
        check(unknown.snapshot().getListings().isEmpty(), "Unknown catalog result");
        check(unknown.snapshot().getPlant().size() == 1, "Keep identification without catalog data");

        // A later lookup failure must not publish a partially assembled result.
        PlantCatalog failingCatalog = id ->
        {
            if ("other".equals(id))
            {
                throw new IllegalStateException("Catalog unavailable");
            }
            return new CatalogResult("Care", List.of(first));
        };
        PlantModel atomic = new PlantModel(identifier, failingCatalog);
        atomic.identify(image);
        PlantSnapshot original = atomic.snapshot();
        identifier.results = List.of(
            new Identification("species", "Scientific", "Common", 0.9),
            new Identification("other", "Other", "Other", 0.7));
        expect(IllegalStateException.class, () -> atomic.identify(image));
        check(atomic.snapshot().getPlant().equals(original.getPlant()),
            "Partial catalog failure must preserve plants");
        check(atomic.snapshot().getListings().equals(original.getListings()),
            "Partial catalog failure must preserve listings");

        PlantModel multiple = new PlantModel(identifier, id ->
            new CatalogResult("Care", "species".equals(id) ? List.of(first) : List.of()));
        multiple.identify(image);
        check(multiple.snapshot().getPlant().size() == 2, "Keep all identifications");
        check(multiple.snapshot().getListings().size() == 1, "Combine available listings");
        identifier.results = List.of(
            new Identification("species", "Scientific", "Common", 0.9),
            new Identification("species", "Scientific", "Common", 0.8));
        multiple.identify(image);
        check(multiple.snapshot().getListings().size() == 1, "Deduplicate identical listings");
        identifier.results = null;
        expect(IllegalStateException.class, () -> multiple.identify(image));
        identifier.results = new ArrayList<>();
        identifier.results.add(null);
        expect(IllegalStateException.class, () -> multiple.identify(image));
    }

    private static void checkCartAcrossIdentifications() throws Exception
    {
        byte[] image = TestSupport.image();
        FakeIdentifier identifier = new FakeIdentifier();
        Listing original = listing("original");
        Listing replacement = new Listing("replacement", "other", "Other plant", "plant", 2000);
        PlantModel model = new PlantModel(identifier, species ->
            new CatalogResult("Care", List.of("species".equals(species) ? original : replacement)));
        model.identify(image);
        model.addToCart("original");
        model.addToCart("original");
        PlantSnapshot before = model.snapshot();

        // Reject a corrupt PNG without calling the identifier or changing state.
        int calls = identifier.calls;
        byte[] truncated = java.util.Arrays.copyOf(image, 20);
        expect(IllegalArgumentException.class, () -> model.identify(truncated));
        check(identifier.calls == calls, "Corrupt images must not reach the service");
        check(model.snapshot().getCart().equals(before.getCart()), "Corrupt image preserves cart");
        check(model.snapshot().getPlant().equals(before.getPlant()), "Corrupt image preserves plant");
        check(model.snapshot().getListings().equals(before.getListings()), "Corrupt image preserves listings");

        identifier.results = List.of(new Identification("other", "Other scientific", "Other", 0.8));
        model.identify(image);
        check(model.snapshot().getListings().equals(List.of(replacement)), "Replace available listings");
        expect(IllegalArgumentException.class, () -> model.addToCart("original"));
        model.addToCart("replacement");
        check(model.snapshot().getCart().size() == 3, "Keep queued items across identifications");
        check(model.checkoutNext().getListing() == original, "First queued item stays first");
        check(model.checkoutNext().getListing() == original, "Repeated additions remain separate items");
        check(model.checkoutNext().getListing() == replacement, "New listing follows older items");
        expect(NoSuchElementException.class, model::checkoutNext);
        model.addToCart("replacement");
        check(model.checkoutNext().getListing() == replacement, "Reuse cart after draining");
        check(model.snapshot().getCart().isEmpty(), "Cart is empty after checkout");
        check(before.getCart().size() == 2 && before.getListings().equals(List.of(original)),
            "Earlier snapshots stay unchanged");
    }

    private static Listing listing(String id)
    {
        return new Listing(id, "species", "Plant", "plant", 1000);
    }

    private static void check(boolean condition, String message)
    {
        if (!condition)
        {
            throw new AssertionError(message);
        }
    }

    private static void expect(Class<? extends Throwable> type, Runnable action)
    {
        try
        {
            action.run();
        }
        catch (Throwable failure)
        {
            if (type.isInstance(failure))
            {
                return;
            }
            throw new AssertionError("Unexpected failure", failure);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }

    private static final class FakeIdentifier implements PlantIdentifier
    {
        private List<Identification> results = List.of(
            new Identification("species", "Scientific", "Common", 0.9));
        private int calls;
        private boolean fail;

        @Override
        public List<Identification> identify(byte[] image)
        {
            calls++;
            if (fail)
            {
                throw new IllegalStateException("Simulated service failure");
            }
            return results;
        }
    }
}
