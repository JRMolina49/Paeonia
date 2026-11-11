/**
 * Standalone checks for PlantDetails.
 * @author Conrad
 */
public final class PlantDetailsTest
{
    private PlantDetailsTest()
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
        PlantDetails details = TestSupport.details();
        TestSupport.check("species".equals(details.getSpeciesId()), "Species ID");
        TestSupport.check("Scientific".equals(details.getScientificName()), "Scientific name");
        TestSupport.check("Common".equals(details.getCommonName()), "Common name");
        TestSupport.check("Water weekly".equals(details.getCareText()), "Care text");
        TestSupport.check(details.getConfidence() == 0.9, "Confidence");
        for (String invalid : new String[] {null, "", " "})
        {
            TestSupport.expect(IllegalArgumentException.class,
                () -> new PlantDetails(invalid, "Scientific", "Common", "Care", 0.9));
            TestSupport.expect(IllegalArgumentException.class,
                () -> new PlantDetails("species", invalid, "Common", "Care", 0.9));
        }
        TestSupport.expect(IllegalArgumentException.class,
            () -> new PlantDetails("species", "Scientific", null, "Care", 0.9));
        TestSupport.expect(IllegalArgumentException.class,
            () -> new PlantDetails("species", "Scientific", "Common", null, 0.9));
        for (double invalid : new double[] {-0.1, 1.1, Double.NaN,
            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
        {
            TestSupport.expect(IllegalArgumentException.class,
                () -> new PlantDetails("species", "Scientific", "", "", invalid));
        }
        for (double boundary : new double[] {0.0, 1.0})
        {
            TestSupport.check(new PlantDetails("species", "Scientific", "", "", boundary)
                .getConfidence() == boundary, "Confidence boundary and empty optional text");
        }
        System.out.println("PlantDetailsTest passed");
    }
}
