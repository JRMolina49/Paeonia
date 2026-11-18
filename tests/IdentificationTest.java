/**
 * Standalone checks for Identification.
 * @author Conrad
 */
public final class IdentificationTest
{
    private IdentificationTest()
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
        Identification result = new Identification("species", "Scientific", "Common", 0.9);
        TestSupport.check("species".equals(result.getSpeciesId()), "Species ID");
        TestSupport.check("Scientific".equals(result.getScientificName()), "Scientific name");
        TestSupport.check("Common".equals(result.getCommonName()), "Common name");
        TestSupport.check(result.getConfidence() == 0.9, "Confidence");
        for (String invalid : new String[] {null, "", " "})
        {
            TestSupport.expect(IllegalArgumentException.class,
                () -> new Identification(invalid, "Scientific", "Common", 0.9));
            TestSupport.expect(IllegalArgumentException.class,
                () -> new Identification("species", invalid, "Common", 0.9));
        }
        TestSupport.expect(IllegalArgumentException.class,
            () -> new Identification("species", "Scientific", null, 0.9));
        for (double invalid : new double[] {-0.1, 1.1, Double.NaN,
            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
        {
            TestSupport.expect(IllegalArgumentException.class,
                () -> new Identification("species", "Scientific", "", invalid));
        }
        for (double boundary : new double[] {0.0, 1.0})
        {
            TestSupport.check(new Identification("species", "Scientific", "", boundary)
                .getConfidence() == boundary, "Confidence boundary and missing common name");
        }
        System.out.println("IdentificationTest passed");
    }
}
