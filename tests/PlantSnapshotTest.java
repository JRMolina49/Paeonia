import java.util.ArrayList;
import java.util.List;

/**
 * Standalone checks for PlantSnapshot.
 * @author Conrad
 */
public final class PlantSnapshotTest
{
    private PlantSnapshotTest()
    {
        // Utility class.
    }

    /**
     * Runs the checks.
     * @param args unused
     * @throws Exception if a check fails
     */
    public static void main(String[] args) throws Exception
    {
        PlantDetails details = TestSupport.details();
        Listing listing = TestSupport.listing();
        CartItem item = new CartItem(listing);
        List<PlantDetails> plants = new ArrayList<>(List.of(details));
        List<Listing> listings = new ArrayList<>(List.of(listing));
        List<CartItem> cart = new ArrayList<>(List.of(item, item));
        PlantSnapshot snapshot = new PlantSnapshot(plants, listings, cart);
        plants.clear();
        listings.clear();
        cart.clear();
        TestSupport.check(snapshot.getPlant().equals(List.of(details)), "Copy plants");
        TestSupport.check(snapshot.getListings().equals(List.of(listing)), "Copy listings");
        TestSupport.check(snapshot.getCart().equals(List.of(item, item)), "Copy cart and keep duplicates");
        TestSupport.expect(UnsupportedOperationException.class, () -> snapshot.getPlant().clear());
        TestSupport.expect(UnsupportedOperationException.class, () -> snapshot.getListings().clear());
        TestSupport.expect(UnsupportedOperationException.class, () -> snapshot.getCart().clear());
        TestSupport.expect(IllegalArgumentException.class,
            () -> new PlantSnapshot(null, List.of(), List.of()));
        TestSupport.expect(IllegalArgumentException.class,
            () -> new PlantSnapshot(List.of(), null, List.of()));
        TestSupport.expect(IllegalArgumentException.class,
            () -> new PlantSnapshot(List.of(), List.of(), null));
        plants.add(null);
        listings.add(null);
        cart.add(null);
        TestSupport.expect(IllegalArgumentException.class,
            () -> new PlantSnapshot(plants, List.of(), List.of()));
        TestSupport.expect(IllegalArgumentException.class,
            () -> new PlantSnapshot(List.of(), listings, List.of()));
        TestSupport.expect(IllegalArgumentException.class,
            () -> new PlantSnapshot(List.of(), List.of(), cart));
        PlantSnapshot empty = new PlantSnapshot(List.of(), List.of(), List.of());
        TestSupport.check(empty.getPlant().isEmpty() && empty.getListings().isEmpty()
            && empty.getCart().isEmpty(), "Empty snapshot");
        System.out.println("PlantSnapshotTest passed");
    }
}
