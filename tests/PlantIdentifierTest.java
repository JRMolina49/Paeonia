import java.util.Arrays;
import java.util.List;

/**
 * Standalone checks for PlantIdentifier.
 * @author Conrad
 */
public final class PlantIdentifierTest
{
    private PlantIdentifierTest()
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
        byte[] image = TestSupport.image();
        Identification identification = new Identification("species", "Scientific", "Common", 0.9);
        int[] calls = {0};
        PlantIdentifier identifier = bytes ->
        {
            calls[0]++;
            TestSupport.check(Arrays.equals(image, bytes), "Forward encoded image unchanged");
            bytes[0] = 0;
            return List.of(identification);
        };
        PlantModel model = new PlantModel(identifier, id -> new CatalogResult("", List.of()));
        byte firstByte = image[0];
        model.identify(image);
        TestSupport.check(calls[0] == 1, "Injected identifier called once");
        TestSupport.check(image[0] == firstByte, "Service cannot mutate caller image");
        TestSupport.check(model.snapshot().getPlant().get(0).getConfidence() == 0.9,
            "Use service identification");
        TestSupport.expect(IllegalArgumentException.class, () -> model.identify(null));
        TestSupport.check(calls[0] == 1, "Invalid image does not reach identifier");
        PlantModel noMatch = new PlantModel(bytes -> List.of(), id ->
        {
            throw new AssertionError("No match must not query catalog");
        });
        TestSupport.expect(IllegalArgumentException.class, () -> noMatch.identify(image));
        TestSupport.check(noMatch.snapshot().getPlant().isEmpty(), "No match leaves empty state");
        System.out.println("PlantIdentifierTest passed");
    }
}
