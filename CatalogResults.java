import java.util.List;

public class CatalogResults {
    private final String careText;
    private final List<Listing> listings;

    public CatalogResults(String careText, List<Listing> listings)
    {
        this.careText = careText;
        this.listings = listings;
    }
    
    public String getCareText()
    {
        return careText;
    }

    public List<Listing> getListings()
    {
        return listings;
    }
}
