/**
 * Immutable class for a buyable listing of a species.
 * @author Joshua Molina (joshm05)
 * @version November 11, 2026
 */

public class Listing {
    /**
     * Data field declarations.
     */
    private final String id;
    private final String speciesId;
    private final String displayName;
    private final String category;
    private final int priceCents;

    /**
     * Constructor method for the Listing class.
     * Creates a Listing object containing the passed information on the plant.
     * @param id : The specific identification of the listing.
     * @param speciesId : The specific identification of the plant species.
     * @param displayName : The name shown on screen.
     * @param category : The categorization of the plant species.
     * @param priceCents : The cost of the plant. 
     */
    public Listing(String id, String speciesId, String displayName, String category, int priceCents)
    {
        this.id = id;
        this.speciesId = speciesId;
        this.displayName = displayName;
        this.category = category;
        this.priceCents = priceCents;
    }

    /**
     * Getter method for the id variable
     * @return the specific identification of the listing.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Getter method for the speciesId variable.
     * @return the specific identification of the plant species.
     */
    public String getSpeciesId()
    {
        return speciesId;
    }

    /**
     * Getter method for the displayName variable.
     * @return the name shown on screen.
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * Getter method for the category variable.
     * @return the categorization of the plant species.
     */
    public String getCategory()
    {
        return category;
    }

    /**
     * Getter method for the priceCents variable.
     * @return the cost of the plant.
     */ 
    public int getPriceCents()
    {
        return priceCents;
    }
}
