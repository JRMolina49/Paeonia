import java.util.List;

/**
 * An immutable copy of the current plant, listing, and cart state.
 *
 * @author Josh (original data classes)
 * @author Conrad
 */
public final class PlantSnapshot
{
    private final List<PlantDetails> plant;
    private final List<Listing> listings;
    private final List<CartItem> cart;

    /**
     * Creates a validated value object.
     * @param plant the plant value
     * @param listings the listings value
     * @param cart the cart value
     * @throws IllegalArgumentException if a required value is invalid
     */
    public PlantSnapshot(List<PlantDetails> plant, List<Listing> listings, List<CartItem> cart)
    {
        if (plant == null || plant.stream().anyMatch(item -> item == null))
        {
            throw new IllegalArgumentException("plant must contain non-null values");
        }
        this.plant = List.copyOf(plant);
        if (listings == null || listings.stream().anyMatch(item -> item == null))
        {
            throw new IllegalArgumentException("listings must contain non-null values");
        }
        this.listings = List.copyOf(listings);
        if (cart == null || cart.stream().anyMatch(item -> item == null))
        {
            throw new IllegalArgumentException("cart must contain non-null values");
        }
        this.cart = List.copyOf(cart);
    }

    /**
     * Returns the plant value.
     * @return the plant value
     */
    public List<PlantDetails> getPlant()
    {
        return plant;
    }

    /**
     * Returns the listings value.
     * @return the listings value
     */
    public List<Listing> getListings()
    {
        return listings;
    }

    /**
     * Returns the cart value.
     * @return the cart value
     */
    public List<CartItem> getCart()
    {
        return cart;
    }
}
