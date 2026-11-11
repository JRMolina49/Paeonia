/**
 * Standalone checks for CartItem.
 * @author Conrad
 */
public final class CartItemTest
{
    private CartItemTest()
    {
        // Utility class.
    }

    /**
     * Runs the checks.
     * @param args unused
     * @throws Exception if a check fails
     */
    public static void main(String[] args) throws Exception
    {
        Listing listing = TestSupport.listing();
        CartItem item = new CartItem(listing);
        TestSupport.check(item.getListing() == listing, "Keep selected listing");
        TestSupport.expect(IllegalArgumentException.class, () -> new CartItem(null));
        System.out.println("CartItemTest passed");
    }
}
