

public class Identification 
{
    private final String speciesId;
    private final String scientificName;
    private final String commonName;
    private final double confidence;

    public Identification(String speciesId, String scientificName, String commonName, double confidence)
    {
        this.speciesId = speciesId;
        this.scientificName = scientificName;
        this.commonName = commonName;
        this.confidence = confidence;
    }

    public String getSpeciesId()
    {
        return speciesId;
    }

    public String getScientificName()
    {
        return scientificName;
    }

    public String getCommonName()
    {
        return commonName;
    }

    public double getConfidence()
    {
        return confidence;
    }
    
}
