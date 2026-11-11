
public class Listing {
    private final String id;
    private final String speciesId;
    private final String displayName;
    private final String category;
    private final int priceCents;

    public Listing(String id, String speciesId, String displayName, String category, int priceCents)
    {
        this.id = id;
        this.speciesId = speciesId;
        this.displayName = displayName;
        this.category = category;
        this.priceCents = priceCents;
    }

    public String getId()
    {
        return id;
    }

    public String getSpeciesId()
    {
        return speciesId;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getCategory()
    {
        return category;
    }

    public int getPriceCents()
    {
        return priceCents;
    }
}
