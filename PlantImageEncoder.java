import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

final class PlantImageEncoder
{
    private PlantImageEncoder()
    {
        // Utility class.
    }

    static byte[] toJpeg(byte[] imageBytes)
    {
        if (imageBytes == null || imageBytes.length == 0)
        {
            throw new IllegalArgumentException("Image cannot be null or empty.");
        }

        BufferedImage source;
        try
        {
            source = ImageIO.read(new ByteArrayInputStream(imageBytes));
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException("Unable to read the selected image.", e);
        }

        if (source == null)
        {
            throw new IllegalArgumentException("The selected file is not a supported image.");
        }

        BufferedImage rgb = new BufferedImage(
            source.getWidth(),
            source.getHeight(),
            BufferedImage.TYPE_INT_RGB
        );
        Graphics2D graphics = rgb.createGraphics();
        try
        {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
            graphics.drawImage(source, 0, 0, null);
        }
        finally
        {
            graphics.dispose();
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try
        {
            if (!ImageIO.write(rgb, "jpg", output))
            {
                throw new ServiceException("No JPEG encoder is available.");
            }
        }
        catch (IOException e)
        {
            throw new ServiceException("Unable to convert the image to JPEG.", e);
        }
        return output.toByteArray();
    }
}
