/**
 * Immutable class for containing the information within a cart item.
 * @author Joshua Molina (joshm05)
 * @version November 11, 2026
 */

public final class CartItem {
    /**
     * Data field declaration
     */
    private final Listing listing;

    /**
     * Constructor method for CartItem class.
     * Sets the cart item as the passed item.
     * @param listing : individual item within the cart.
     */
    public CartItem(Listing listing)
    {
        this.listing = listing;
    }

    /**
     * Getter method for the listing field.
     * @return item within the list. 
     */
    public Listing getListing()
    {
        return listing;
    }
}
