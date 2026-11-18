import org.junit.Test;

/**
 * JUnit checks for Identification.
 * @author Conrad
 */
public final class IdentificationTest
{
    /**
     * Runs the checks.
     * @throws Exception if a check fails
     */
    @Test
    public void testContract() throws Exception
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
    }

    /** Reference metadata remains immutable and independent of caller lists. */
    @Test
    public void testReferenceImages()
    {
        PlantDetails.ReferenceImage reference = new PlantDetails.ReferenceImage(
            "https://images.plantnet.org/photo.jpg", "Author", "CC BY", "leaf");
        java.util.List<PlantDetails.ReferenceImage> images = new java.util.ArrayList<>();
        images.add(reference);
        Identification value = new Identification("species", "Scientific", "Common", 0.9, images);
        images.clear();
        org.junit.Assert.assertEquals(java.util.List.of(reference), value.getReferenceImages());
        org.junit.Assert.assertThrows(UnsupportedOperationException.class,
            () -> value.getReferenceImages().clear());
        images.add(null);
        org.junit.Assert.assertThrows(IllegalArgumentException.class, () -> new Identification("species", "Scientific", "Common", 0.9, images));
        org.junit.Assert.assertThrows(IllegalArgumentException.class,
            () -> new Identification("species", "Scientific", "Common", 0.9, null));
    }
}
