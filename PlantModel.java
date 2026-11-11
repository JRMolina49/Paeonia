import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.imageio.ImageIO;

/**
 * Owns the current identification, available listings, and FIFO shopping cart.
 * Model calls must be serialized by the controller.
 *
 * @author Conrad
 */
public class PlantModel
{
    private final PlantIdentifier identifier;
    private final PlantCatalog catalog;
    private final LinkedQueue<CartItem> cart;
    private List<PlantDetails> currentPlant;
    private List<Listing> currentListings;

    /**
     * Creates an empty model using the supplied services.
     * @param identifier image identification service
     * @param catalog care and listing catalog
     * @throws IllegalArgumentException if either service is null
     */
    public PlantModel(PlantIdentifier identifier, PlantCatalog catalog)
    {
        if (identifier == null || catalog == null)
        {
            throw new IllegalArgumentException("Identifier and catalog are required");
        }
        this.identifier = identifier;
        this.catalog = catalog;
        cart = new LinkedQueue<>();
        currentPlant = List.of();
        currentListings = List.of();
    }

    /**
     * Identifies an image and retrieves care and listings for each result.
     * A failed request leaves the current plant, listings, and cart unchanged.
     * @param image encoded image bytes supported by ImageIO
     * @throws IllegalArgumentException if the image is invalid or has no matches
     * @throws IllegalStateException if a service returns invalid data
     */
    public void identify(byte[] image)
    {
        if (image == null || image.length == 0)
        {
            throw new IllegalArgumentException("Select a valid plant image");
        }
        byte[] requestImage = image.clone();
        validateImage(requestImage);
        List<Identification> results = identifier.identify(requestImage);
        if (results == null)
        {
            throw new IllegalStateException("Identifier returned no result collection");
        }
        if (results.isEmpty())
        {
            throw new IllegalArgumentException("No plant recognized; select a clear plant image");
        }

        List<PlantDetails> plants = new ArrayList<>();
        Map<String, Listing> listings = new LinkedHashMap<>();
        for (Identification result : results)
        {
            if (result == null)
            {
                throw new IllegalStateException("Identifier returned a null identification");
            }
            CatalogResult details = catalog.lookup(result.getSpeciesId());
            if (details == null)
            {
                throw new IllegalStateException("Catalog returned a null result");
            }
            plants.add(new PlantDetails(result.getSpeciesId(),
                result.getScientificName(), result.getCommonName(),
                details.getCareText(), result.getConfidence()));
            for (Listing listing : details.getListings())
            {
                if (!result.getSpeciesId().equals(listing.getSpeciesId()))
                {
                    throw new IllegalStateException("Catalog returned a listing for another species");
                }
                Listing previous = listings.putIfAbsent(listing.getId(), listing);
                if (previous != null && !sameListing(previous, listing))
                {
                    throw new IllegalStateException("Catalog returned conflicting listing IDs");
                }
            }
        }
        List<PlantDetails> nextPlants = List.copyOf(plants);
        List<Listing> nextListings = List.copyOf(listings.values());
        currentPlant = nextPlants;
        currentListings = nextListings;
    }

    /**
     * Adds an available listing to the back of the cart.
     * @param listingId the selected listing ID
     * @throws IllegalArgumentException if the ID is null, blank, or unavailable
     */
    public void addToCart(String listingId)
    {
        if (listingId == null || listingId.isBlank())
        {
            throw new IllegalArgumentException("Select an available listing");
        }
        for (Listing listing : currentListings)
        {
            if (listing.getId().equals(listingId))
            {
                cart.enqueue(new CartItem(listing));
                return;
            }
        }
        throw new IllegalArgumentException("Select an available listing");
    }

    /**
     * Removes the oldest cart item.
     * @return the next item for checkout
     * @throws NoSuchElementException if the cart is empty
     */
    public CartItem checkoutNext()
    {
        if (cart.isEmpty())
        {
            throw new NoSuchElementException("Your cart is empty");
        }
        return cart.dequeue();
    }

    /**
     * Copies the current state without exposing the mutable queue.
     * @return an immutable snapshot with cart items in FIFO order
     */
    public PlantSnapshot snapshot()
    {
        return new PlantSnapshot(currentPlant, currentListings, cart.toList());
    }

    private static void validateImage(byte[] image)
    {
        try
        {
            if (ImageIO.read(new ByteArrayInputStream(image)) == null)
            {
                throw new IllegalArgumentException("Select a supported, readable image");
            }
        }
        catch (IOException exception)
        {
            throw new IllegalArgumentException("Select a supported, readable image", exception);
        }
    }

    private static boolean sameListing(Listing first, Listing second)
    {
        return first.getSpeciesId().equals(second.getSpeciesId())
            && first.getDisplayName().equals(second.getDisplayName())
            && first.getCategory().equals(second.getCategory())
            && first.getPriceCents() == second.getPriceCents();
    }
}
