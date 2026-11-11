/**
 * Immutable class for the full details and listings of a plant.
 * @author Joshua Molina (joshm05)
 * @version November 11, 2026
 */

/**
 * Imports List and Optional containers.
 */
import java.util.List;
import java.util.Optional;

public class PlantSnapshot {
    private final Optional<PlantDetails> plant;
    private final List<Listing> listings;
    private final List<CartItem> cart;

    /**
     * Constructor method for the PlantSnapshot class.
     * @param plant : Optional container housing the information of a specific plant.
     * @param listings : List container housing the listings of the plant. 
     * @param cart : List container housing the items within the cart.
     */
    public PlantSnapshot(Optional<PlantDetails> plant, List<Listing> listings, List<CartItem> cart)
    {
        this.plant = plant;
        this.listings = listings;
        this.cart = cart;
    }

    /**
     * Getter method for the plant variable.
     * @return Optional container housing the information of a specific plant.
     */
    public Optional<PlantDetails> getPlant()
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
