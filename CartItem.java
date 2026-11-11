/**
 * Immutable class for containing the information within a cart item.
 * @author Joshua Molina (joshm05)
 * @author Conrad
 * @version November 11, 2026
 */
public final class CartItem
{
    private final Listing listing;

    /**
     * Constructor method for CartItem class.
     * Sets the cart item as the passed item.
     * @param listing : individual item within the cart.
     * @throws IllegalArgumentException if a required value is invalid
     */
    public CartItem(Listing listing)
    {
        if (listing == null)
        {
            throw new IllegalArgumentException("listing is required");
        }
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
