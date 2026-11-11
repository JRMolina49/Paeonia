import java.util.List;

/**
 * Immutable class for the catalog.
 * @author Joshua Molina (joshm05)
 * @author Conrad
 * @version November 11, 2026
 */
public final class CatalogResult
{
    private final String careText;
    private final List<Listing> listings;

    /**
     * Constructor method for the CatalogResult class.
     * Sets the catalog as the passed information
     * @param careText : Text on how to care for the specified plant.
     * @param listings : List containing the listings for the specified plant.
     * @throws IllegalArgumentException if a required value is invalid
     */
    public CatalogResult(String careText, List<Listing> listings)
    {
        if (careText == null)
        {
            throw new IllegalArgumentException("Invalid careText");
        }
        this.careText = careText;
        if (listings == null || listings.stream().anyMatch(item -> item == null))
        {
            throw new IllegalArgumentException("listings must contain non-null values");
        }
        this.listings = List.copyOf(listings);
    }

    /**
     * Getter method for the careText variable.
     * @return the text on how to care for an individual plant.
     */
    public String getCareText()
    {
        return careText;
    }

    /**
     * Getter method for the listings variable.
     * @return list of plant listings.
     */
    public List<Listing> getListings()
    {
        return listings;
    }
}
