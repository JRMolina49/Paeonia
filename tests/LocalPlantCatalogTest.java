import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

/** Checks concrete catalog filtering, ownership, and invalid input. */
public class LocalPlantCatalogTest
{
    @Test
    public void looksUpOnlyMatchingListingsAndCopiesInput()
    {
        List<PlantDetails> plants = new ArrayList<>(List.of(TestSupport.details()));
        Listing matching = TestSupport.listing();
        List<Listing> listings = new ArrayList<>(List.of(matching,
            new Listing("other", "other-species", "Other", "plant", 200)));
        LocalPlantCatalog catalog = new LocalPlantCatalog(plants, listings);
        plants.clear();
        listings.clear();
        assertEquals("Water weekly", catalog.lookup("species").getCareText());
        assertEquals(List.of(matching), catalog.lookup("species").getListings());
        assertThrows(UnsupportedOperationException.class,
            () -> catalog.lookup("species").getListings().clear());
        assertTrue(catalog.lookup("unknown").getListings().isEmpty());
        assertEquals("", catalog.lookup("unknown").getCareText());
    }

    @Test
    public void rejectsInvalidConstructorAndLookupInput()
    {
        assertThrows(IllegalArgumentException.class, () -> new LocalPlantCatalog(null, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new LocalPlantCatalog(List.of(), null));
        assertThrows(IllegalArgumentException.class,
            () -> new LocalPlantCatalog(Arrays.asList((PlantDetails)null), List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new LocalPlantCatalog(List.of(), Arrays.asList((Listing)null)));
        LocalPlantCatalog catalog = new LocalPlantCatalog(List.of(), List.of());
        for (String invalid : new String[] {null, "", " "})
        {
            assertThrows(IllegalArgumentException.class, () -> catalog.lookup(invalid));
        }
    }
}
