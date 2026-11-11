import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

/** Shared assertions and sample values for the standalone tests. */
final class TestSupport
{
    private TestSupport()
    {
        // Utility class.
    }

    static void check(boolean condition, String message)
    {
        if (!condition)
        {
            throw new AssertionError(message);
        }
    }

    static void expect(Class<? extends Throwable> type, Runnable action)
    {
        try
        {
            action.run();
        }
        catch (Throwable failure)
        {
            if (type.isInstance(failure))
            {
                return;
            }
            throw new AssertionError("Unexpected failure", failure);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }

    static Listing listing()
    {
        return new Listing("id", "species", "Plant", "plant", 1000);
    }

    static PlantDetails details()
    {
        return new PlantDetails("species", "Scientific", "Common", "Water weekly", 0.9);
    }

    static byte[] image() throws Exception
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", output);
        return output.toByteArray();
    }
}
