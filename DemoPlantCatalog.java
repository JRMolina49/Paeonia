import java.util.List;

/**
 * Small demonstration catalog. Its sample listings are not real inventory.
 */
public final class DemoPlantCatalog
{
    private DemoPlantCatalog()
    {
        // Utility class.
    }

    public static LocalPlantCatalog create()
    {
        List<PlantDetails> details = List.of(
            new PlantDetails("Aloe vera", "Aloe vera", "Aloe",
                "Use a fast-draining mix and let the soil dry between waterings. "
                    + "Provide bright light.",
                0.0),
            new PlantDetails("Monstera deliciosa", "Monstera deliciosa", "Monstera",
                "Provide bright, indirect light and water when the top of the soil "
                    + "has begun to dry.",
                0.0),
            new PlantDetails("Paeonia lactiflora", "Paeonia lactiflora", "Chinese peony",
                "Grow in full sun and well-drained soil. Water during dry periods "
                    + "and avoid planting the crown too deeply.",
                0.0)
        );

        List<Listing> listings = List.of(
            new Listing("demo-aloe", "Aloe vera",
                "SAMPLE ONLY - Aloe plant (not for sale)", "Demo plant", 500),
            new Listing("demo-monstera", "Monstera deliciosa",
                "SAMPLE ONLY - Monstera plant (not for sale)", "Demo plant", 1200),
            new Listing("demo-peony", "Paeonia lactiflora",
                "SAMPLE ONLY - Peony plant (not for sale)", "Demo plant", 1500)
        );

        return new LocalPlantCatalog(details, listings);
    }
}
