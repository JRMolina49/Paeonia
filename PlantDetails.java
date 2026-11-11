/**
 * Immutable class for the details of a particular plant.
 * @author Joshua Molina (joshm05)
 * @author Conrad
 * @version November 11, 2026
 */
public final class PlantDetails
{
    private final String speciesId;
    private final String scientificName;
    private final String commonName;
    private final String careText;
    private final double confidence;

    /**
     * Constructor method for the PlantDetails class.
     * Creates a PlantDetails object containing all the information passed into it.
     * @param speciesId : Specific identification of the plant species.
     * @param scientificName : Scientific name of the plant species.
     * @param commonName : Common name of the plant species.
     * @param careText : Text on how to care for the specific plant.
     * @param confidence : Confidence level of the accuracy of the identification.
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
     * Getter method for the speciesId variable.
     * @return specific identification of the plant species.
     */
    public String getSpeciesId()
    {
        return speciesId;
    }

    /**
     * Getter method for the scientificName variable.
     * @return scientific name of the plant species.
     */
    public String getScientificName()
    {
        return scientificName;
    }

    /**
     * Getter method for the commonName variable.
     * @return common name of the plant species.
     */
    public String getCommonName()
    {
        return commonName;
    }

    /**
     * Getter method for the careText variable.
     * @return text on how to care for the specific plant.
     */
    public String getCareText()
    {
        return careText;
    }

    /**
     * Getter method for the confidence variable.
     * @return confidence level of the accuracy of the identification.
     */
    public double getConfidence()
    {
        return confidence;
    }
}
