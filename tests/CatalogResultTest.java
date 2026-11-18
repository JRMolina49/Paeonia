import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * JUnit checks for CatalogResult.
 * @author Conrad
 */
public final class CatalogResultTest
{
    /**
     * Runs the checks.
     * @throws Exception if a check fails
     */
    @Test
    public void testContract() throws Exception
    {
        Listing listing = TestSupport.listing();
        List<Listing> source = new ArrayList<>(List.of(listing));
        CatalogResult result = new CatalogResult("Care", source);
        source.clear();
        TestSupport.check("Care".equals(result.getCareText()), "Care text");
        TestSupport.check(result.getListings().equals(List.of(listing)), "Copy listings");
        TestSupport.expect(UnsupportedOperationException.class,
            () -> result.getListings().clear());
        TestSupport.expect(IllegalArgumentException.class, () -> new CatalogResult(null, List.of()));
        TestSupport.expect(IllegalArgumentException.class, () -> new CatalogResult("", null));
        source.add(null);
        TestSupport.expect(IllegalArgumentException.class, () -> new CatalogResult("", source));
        TestSupport.check(new CatalogResult("", List.of()).getListings().isEmpty(),
            "Unknown species can have empty care and listings");
    }
}
