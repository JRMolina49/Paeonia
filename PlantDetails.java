/**
 * An identified plant together with its care information.
 *
 * @author Josh (original data classes)
 * @author Conrad
 */
public final class PlantDetails
{
    private final String speciesId;
    private final String scientificName;
    private final String commonName;
    private final String careText;
    private final double confidence;

    /**
     * Creates a validated value object.
     * @param speciesId the speciesId value
     * @param scientificName the scientificName value
     * @param commonName the commonName value
     * @param careText the careText value
     * @param confidence the confidence value
     * @throws IllegalArgumentException if a required value is invalid
     */
    public PlantDetails(String speciesId, String scientificName, String commonName, String careText, double confidence)
    {
        if (speciesId == null || speciesId.isBlank())
        {
            throw new IllegalArgumentException("Invalid speciesId");
        }
        this.speciesId = speciesId;
        if (scientificName == null || scientificName.isBlank())
        {
            throw new IllegalArgumentException("Invalid scientificName");
        }
        this.scientificName = scientificName;
        if (commonName == null)
        {
            throw new IllegalArgumentException("Invalid commonName");
        }
        this.commonName = commonName;
        if (careText == null)
        {
            throw new IllegalArgumentException("Invalid careText");
        }
        this.careText = careText;
        if (!Double.isFinite(confidence) || confidence < 0.0 || confidence > 1.0)
        {
            throw new IllegalArgumentException("Confidence must be between zero and one");
        }
        this.confidence = confidence;
    }

    /**
     * Returns the speciesId value.
     * @return the speciesId value
     */
    public String getSpeciesId()
    {
        return speciesId;
    }

    /**
     * Returns the scientificName value.
     * @return the scientificName value
     */
    public String getScientificName()
    {
        return scientificName;
    }

    /**
     * Returns the commonName value.
     * @return the commonName value
     */
    public String getCommonName()
    {
        return commonName;
    }

    /**
     * Returns the careText value.
     * @return the careText value
     */
    public String getCareText()
    {
        return careText;
    }

    /**
     * Returns the confidence value.
     * @return the confidence value
     */
    public double getConfidence()
    {
        return confidence;
    }
}
