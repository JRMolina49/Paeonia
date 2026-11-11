import java.util.List;

/**
 * Care information and available listings for a species.
 *
 * @author Josh (original data classes)
 * @author Conrad
 */
public final class CatalogResult
{
    private final String careText;
    private final List<Listing> listings;

    /**
     * Creates a validated value object.
     * @param careText the careText value
     * @param listings the listings value
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
     * Returns the careText value.
     * @return the careText value
     */
    public String getCareText()
    {
        return careText;
    }

    /**
     * Returns the listings value.
     * @return the listings value
     */
    public List<Listing> getListings()
    {
        return listings;
    }
}
