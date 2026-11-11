import java.util.List;
import java.util.Optional;

public class PlantSnapshot {
    private final Optional<PlantDetails> plant;
    private final List<Listing> listings;
    private final List<CartItem> cart;

    public PlantSnapshot(Optional<PlantDetails> plant, List<Listing> listings, List<CartItem> cart)
    {
        this.plant = plant;
        this.listings = listings;
        this.cart = cart;
    }

    public Optional<PlantDetails> getPlant()
    {
        return plant;
    }

    public List<Listing> getListings()
    {
        return listings;
    }

    public List<CartItem> getCart()
    {
        return cart;
    }
}
