/**
 * Class for managing a local plant catalog.
 * @author Joshua Molina (joshm05)
 * @version November 11, 2026
 */

/**
 * Imports for the List and Map containers.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalPlantCatalog implements PlantCatalog {
    /**
     * Data field declarations.
     */
    private final Map<String, PlantDetails> detailsBySpecies;
    private final List<Listing> listings;

    /**
     * Constructor method for the LocalPlantCatalog class.
     * @param details : List container housing the details of the plants.
     * @param listings : List container housing the listings of the plants.
     */
    public LocalPlantCatalog(List<PlantDetails> details, List<Listing> listings)
    {
        if (details == null || listings == null)
        {
            throw new IllegalArgumentException("Plant details and listings cannot be null.");
        }

        detailsBySpecies = new HashMap<>();

        for (PlantDetails plant : details)
        {
            if (plant == null || plant.getSpeciesId() == null
                    || plant.getSpeciesId().isBlank())
            {
                throw new IllegalArgumentException("Invalid plant details.");
            }

            detailsBySpecies.put(plant.getSpeciesId(), plant);
        }

        this.listings = List.copyOf(listings);
    }

    /**
     * Method to look up a plant in the catalog by its species ID.
     * @param speciesId : The specific identification of the plant species.
     * @return CatalogResults object containing the care text and listings for the specified plant.
     */
    @Override
    public CatalogResults lookup(String speciesId)
    {
        if (speciesId == null || speciesId.isBlank())
        {
            throw new IllegalArgumentException("Species ID cannot be null or blank.");
        }

        PlantDetails plant = detailsBySpecies.get(speciesId);

        // Return an empty result if the species is not in the catalog.
        if (plant == null)
        {
            return new CatalogResults("", List.of());
        }

        List<Listing> matchingListings = new ArrayList<>();

        for (Listing listing : listings)
        {
            if (listing == null)
            {
                throw new IllegalArgumentException("Listings cannot contain null values.");
            }

            if (listing.getSpeciesId().equals(speciesId))
            {
                matchingListings.add(listing);
            }
        }

        return new CatalogResults(plant.getCareText(), matchingListings);
    }
}

