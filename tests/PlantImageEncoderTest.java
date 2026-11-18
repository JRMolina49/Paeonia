import static org.junit.Assert.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import org.junit.Test;

/** Checks upload conversion without a network request. */
public class PlantImageEncoderTest
{
    @Test
    public void convertsTransparentPngToJpeg() throws Exception
    {
        BufferedImage source = new BufferedImage(4, 3, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream png = new ByteArrayOutputStream();
        ImageIO.write(source, "png", png);
        byte[] original = png.toByteArray();
        byte[] copy = original.clone();
        byte[] jpeg = PlantImageEncoder.toJpeg(original);
        assertEquals(0xff, jpeg[0] & 0xff);
        assertEquals(0xd8, jpeg[1] & 0xff);
        BufferedImage result = ImageIO.read(new ByteArrayInputStream(jpeg));
        assertEquals(4, result.getWidth());
        assertEquals(3, result.getHeight());
        assertEquals(0xffffff, result.getRGB(0, 0) & 0xffffff);
        assertArrayEquals(copy, original);
        assertNotSame(jpeg, PlantImageEncoder.toJpeg(jpeg));
    }

    @Test
    public void rejectsMissingAndUnreadableImages()
    {
        for (byte[] invalid : new byte[][] {null, new byte[0], {1, 2, 3}})
        {
            assertThrows(IllegalArgumentException.class, () -> PlantImageEncoder.toJpeg(invalid));
        }
    }
}
