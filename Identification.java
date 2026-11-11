/**
 * Immutable class for the indentifying features of a plant.
 * @author Joshua Molina (joshm05)
 * @version November 11, 2026
 */

public final class Identification 
{
    /**
     * Data field declarations.
     */
    private final String speciesId;
    private final String scientificName;
    private final String commonName;
    private final double confidence;

    /**
     * Constructor method for the Identification class.
     * Creates a Indentification object with the passed information.
     * @param speciesId : The specific identification of the species.
     * @param scientificName : The species' scientific name.
     * @param commonName : The species' colloquial name.
     * @param confidence : The level of confidence in the indentification of the species
     */
    public Identification(String speciesId, String scientificName, String commonName, double confidence)
    {
        this.speciesId = speciesId;
        this.scientificName = scientificName;
        this.commonName = commonName;
        this.confidence = confidence;
    }

    /**
     * Getter method for the speciesId variable.
     * @return the id of the species.
     */
    public String getSpeciesId()
    {
        return speciesId;
    }

    /**
     * Getter method for the scientificName variable.
     * @return the scientific name of the species.
     */
    public String getScientificName()
    {
        return scientificName;
    }

    /**
     * Getter method for the commonName variable
     * @return the common name of the species.
     */
    public String getCommonName()
    {
        return commonName;
    }

    /**
     * Getter method for the confidence variable.
     * @return the confidence that a specific plant has been identified.
     */
    public double getConfidence()
    {
        return confidence;
    }
    
}
