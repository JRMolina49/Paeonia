/**
 * An available catalog listing with its price in cents.
 *
 * @author Josh (original data classes)
 * @author Conrad
 */
public final class Listing
{
    private final String id;
    private final String speciesId;
    private final String displayName;
    private final String category;
    private final int priceCents;

    /**
     * Creates a validated value object.
     * @param id the id value
     * @param speciesId the speciesId value
     * @param displayName the displayName value
     * @param category the category value
     * @param priceCents the priceCents value
     * @throws IllegalArgumentException if a required value is invalid
     */
    public Listing(String id, String speciesId, String displayName, String category, int priceCents)
    {
        if (id == null || id.isBlank())
        {
            throw new IllegalArgumentException("Invalid id");
        }
        this.id = id;
        if (speciesId == null || speciesId.isBlank())
        {
            throw new IllegalArgumentException("Invalid speciesId");
        }
        this.speciesId = speciesId;
        if (displayName == null || displayName.isBlank())
        {
            throw new IllegalArgumentException("Invalid displayName");
        }
        this.displayName = displayName;
        if (category == null || category.isBlank())
        {
            throw new IllegalArgumentException("Invalid category");
        }
        this.category = category;
        if (priceCents < 0)
        {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.priceCents = priceCents;
    }

    /**
     * Returns the id value.
     * @return the id value
     */
    public String getId()
    {
        return id;
    }

    /**
     * Returns the speciesId value.
     * @return the speciesId value
     */
    public String getSpeciesId()
    {
        return speciesId;
    }

    /**
     * Returns the displayName value.
     * @return the displayName value
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * Returns the category value.
     * @return the category value
     */
    public String getCategory()
    {
        return category;
    }

    /**
     * Returns the priceCents value.
     * @return the priceCents value
     */
    public int getPriceCents()
    {
        return priceCents;
    }
}
