
public class CartItem {
    private final Listing listing;

    public CartItem(Listing listing)
    {
        this.listing = listing;
    }

    public Listing getListing()
    {
        return listing;
    }
}
