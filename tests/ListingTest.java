import org.junit.Test;

/**
 * JUnit checks for Listing.
 * @author Conrad
 */
public final class ListingTest
{
    /**
     * Runs the checks.
     * @throws Exception if a check fails
     */
    @Test
    public void testContract() throws Exception
    {
        Listing listing = TestSupport.listing();
        TestSupport.check("id".equals(listing.getId()), "Listing ID");
        TestSupport.check("species".equals(listing.getSpeciesId()), "Species ID");
        TestSupport.check("Plant".equals(listing.getDisplayName()), "Display name");
        TestSupport.check("plant".equals(listing.getCategory()), "Category");
        TestSupport.check(listing.getPriceCents() == 1000, "Price in cents");
        for (String invalid : new String[] {null, "", " "})
        {
            TestSupport.expect(IllegalArgumentException.class,
                () -> new Listing(invalid, "species", "Plant", "plant", 10));
            TestSupport.expect(IllegalArgumentException.class,
                () -> new Listing("id", invalid, "Plant", "plant", 10));
            TestSupport.expect(IllegalArgumentException.class,
                () -> new Listing("id", "species", invalid, "plant", 10));
            TestSupport.expect(IllegalArgumentException.class,
                () -> new Listing("id", "species", "Plant", invalid, 10));
        }
        TestSupport.expect(IllegalArgumentException.class,
            () -> new Listing("id", "species", "Plant", "plant", -1));
        TestSupport.check(new Listing("id", "species", "Plant", "plant", 0)
            .getPriceCents() == 0, "Zero price is valid");
    }
}
