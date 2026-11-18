import org.junit.Test;

/**
 * JUnit checks for PlantDetails.
 * @author Conrad
 */
public final class PlantDetailsTest
{
    /**
     * Runs the checks.
     * @throws Exception if a check fails
     */
    @Test
    public void testContract() throws Exception
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
    }

    /** Reference metadata remains immutable and independent of caller lists. */
    @Test
    public void testReferenceImages()
    {
        PlantDetails.ReferenceImage reference = new PlantDetails.ReferenceImage(
            "https://images.plantnet.org/photo.jpg", "Author", "CC BY", "leaf");
        java.util.List<PlantDetails.ReferenceImage> images = new java.util.ArrayList<>();
        images.add(reference);
        PlantDetails value = new PlantDetails("species", "Scientific", "Common", "Care", 0.9, images);
        images.clear();
        org.junit.Assert.assertEquals(java.util.List.of(reference), value.getReferenceImages());
        org.junit.Assert.assertThrows(UnsupportedOperationException.class,
            () -> value.getReferenceImages().clear());
        images.add(null);
        org.junit.Assert.assertThrows(IllegalArgumentException.class, () -> new PlantDetails("species", "Scientific", "Common", "Care", 0.9, images));
        org.junit.Assert.assertThrows(IllegalArgumentException.class,
            () -> new PlantDetails("species", "Scientific", "Common", "Care", 0.9, null));
    }

    @Test
    public void testReferenceImageMetadataAndInvalidUrls()
    {
        PlantDetails.ReferenceImage reference = new PlantDetails.ReferenceImage(
            "https://plantnet.org/photo.jpg", "Author", "CC BY-SA", "flower");
        org.junit.Assert.assertEquals("https://plantnet.org/photo.jpg", reference.getUrl());
        org.junit.Assert.assertEquals("Author", reference.getAuthor());
        org.junit.Assert.assertEquals("CC BY-SA", reference.getLicense());
        org.junit.Assert.assertEquals("flower", reference.getOrgan());
        PlantDetails.ReferenceImage fallback = new PlantDetails.ReferenceImage(
            "https://images.plantnet.org/photo.jpg", null, " ", null);
        org.junit.Assert.assertEquals("Unknown author", fallback.getAuthor());
        org.junit.Assert.assertEquals("License not provided", fallback.getLicense());
        org.junit.Assert.assertEquals("", fallback.getOrgan());
        for (String invalid : new String[] {null, "", " ", "bad url", "/relative",
            "http://plantnet.org/photo.jpg", "https://plantnet.org.evil.com/photo.jpg",
            "https://example.org/photo.jpg"})
        {
            org.junit.Assert.assertThrows(IllegalArgumentException.class,
                () -> new PlantDetails.ReferenceImage(invalid, "Author", "CC BY", "leaf"));
        }
    }
}
