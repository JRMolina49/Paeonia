/**
 * Immutable class for the catalog.
 * @author Joshua Molina (joshm05)
 * @version November 11, 2026
 */

/**
 * Imports for the List container.
 */
import java.util.List;

public class CatalogResults {
    /**
     * Data field declarations 
     */
    private final String careText;
    private final List<Listing> listings;

    /**
     * Constructor method for the CatalogResults class.
     * Sets the catalog as the passed information 
     * @param careText : Text on how to care for the specified plant.
     * @param listings : List containing the listings for the specified plant.
     */
    public CatalogResults(String careText, List<Listing> listings)
    {
        this.careText = careText;
        this.listings = listings;
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
