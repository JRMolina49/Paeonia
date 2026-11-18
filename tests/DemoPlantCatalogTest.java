import static org.junit.Assert.*;
import org.junit.Test;

/** Checks that the demo offers usable, clearly labeled sample data. */
public class DemoPlantCatalogTest
{
    @Test
    public void includesCareAndSampleListingForEachDemoSpecies()
    {
        LocalPlantCatalog catalog = DemoPlantCatalog.create();
        for (String species : new String[] {"Aloe vera", "Monstera deliciosa", "Paeonia lactiflora"})
        {
            CatalogResult result = catalog.lookup(species);
            assertFalse(result.getCareText().isBlank());
            assertEquals(1, result.getListings().size());
            assertEquals(species, result.getListings().get(0).getSpeciesId());
            assertTrue(result.getListings().get(0).getDisplayName().contains("SAMPLE ONLY"));
        }
        assertTrue(catalog.lookup("unknown").getListings().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> catalog.lookup(null));
    }
}
