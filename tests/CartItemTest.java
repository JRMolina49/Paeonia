import org.junit.Test;

/**
 * JUnit checks for CartItem.
 * @author Conrad
 */
public final class CartItemTest
{
    /**
     * Runs the checks.
     * @throws Exception if a check fails
     */
    @Test
    public void testContract() throws Exception
    {
        Listing listing = TestSupport.listing();
        CartItem item = new CartItem(listing);
        TestSupport.check(item.getListing() == listing, "Keep selected listing");
        TestSupport.expect(IllegalArgumentException.class, () -> new CartItem(null));
    }
}
