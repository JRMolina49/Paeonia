import java.util.List;

/**
 * Immutable class for the full details and listings of a plant.
 * @author Joshua Molina (joshm05)
 * @author Conrad
 * @version November 11, 2026
 */
public final class PlantSnapshot
{
    private final List<PlantDetails> plant;
    private final List<Listing> listings;
    private final List<CartItem> cart;

    /**
     * Constructor method for the PlantSnapshot class.
     * @param plant : Immutable list housing the information of a specific plant.
     * @param listings : List container housing the listings of the plant.
     * @param cart : List container housing the items within the cart.
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
     * Getter method for the plant variable.
     * @return Immutable list housing the information of a specific plant.
     */
    public List<PlantDetails> getPlant()
    {
        return plant;
    }

    /**
     * Getter method for the listings variable.
     * @return List container housing the listings of the plant.
     */
    public List<Listing> getListings()
    {
        return listings;
    }

    /**
     * Getter method for the cart variable.
     * @return List container housing the items within the cart.
     */
    public List<CartItem> getCart()
    {
        return cart;
    }
}
