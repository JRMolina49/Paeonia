import java.util.List;

/**
 * Standalone checks for PlantCatalog.
 * @author Conrad
 */
public final class PlantCatalogTest
{
    private PlantCatalogTest()
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
        byte[] image = TestSupport.image();
        PlantIdentifier identifier = bytes -> List.of(
            new Identification("species", "Scientific", "Common", 0.9));
        Listing listing = TestSupport.listing();
        PlantCatalog catalog = id ->
        {
            TestSupport.check("species".equals(id), "Look up identified species");
            return new CatalogResult("Water weekly", List.of(listing));
        };
        PlantModel known = new PlantModel(identifier, catalog);
        known.identify(image);
        TestSupport.check(known.snapshot().getListings().equals(List.of(listing)), "Catalog listings");
        TestSupport.check("Water weekly".equals(known.snapshot().getPlant().get(0).getCareText()),
            "Catalog care");
        PlantModel unknown = new PlantModel(identifier, id -> new CatalogResult("", List.of()));
        unknown.identify(image);
        TestSupport.check(unknown.snapshot().getListings().isEmpty(), "Unknown species has no listings");
        TestSupport.check(unknown.snapshot().getPlant().get(0).getCareText().isEmpty(),
            "Unknown species has no invented care");
        PlantModel nullResult = new PlantModel(identifier, id -> null);
        TestSupport.expect(IllegalStateException.class, () -> nullResult.identify(image));
        Listing wrongSpecies = new Listing("wrong", "other", "Other", "plant", 100);
        PlantModel mismatched = new PlantModel(identifier,
            id -> new CatalogResult("Care", List.of(wrongSpecies)));
        TestSupport.expect(IllegalStateException.class, () -> mismatched.identify(image));
        Listing conflicting = new Listing("id", "species", "Different", "plant", 200);
        PlantModel conflictingIds = new PlantModel(identifier,
            id -> new CatalogResult("Care", List.of(listing, conflicting)));
        TestSupport.expect(IllegalStateException.class, () -> conflictingIds.identify(image));
        System.out.println("PlantCatalogTest passed");
    }
}
