/**
 * One listing selected for FIFO checkout.
 *
 * @author Josh (original data classes)
 * @author Conrad
 */
public final class CartItem
{
    private final Listing listing;

    /**
     * Creates a validated value object.
     * @param listing the listing value
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
     * Returns the listing value.
     * @return the listing value
     */
    public Listing getListing()
    {
        return listing;
    }
}
