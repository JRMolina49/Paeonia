import java.util.List;

/**
 * Identifies an image without exposing service-specific HTTP behavior.
 *
 * @author Conrad
 */
public interface PlantIdentifier
{
    /**
     * Identifies a supported image.
     * @param image encoded image bytes
     * @return non-null identification results; empty when no plant matches
     * @throws IllegalArgumentException if the image is invalid
     * @throws RuntimeException if the service cannot complete its request
     */
    List<Identification> identify(byte[] image);
}
