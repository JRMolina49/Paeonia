
public class PlantDetails {
    private final String speciesId;
    private final String scientificName;
    private final String commonName;
    private final String careText;
    private final double confidence;

    public PlantDetails(String speciesId, String scientificName, String commonName, String careText, double confidence)
    {
        this.speciesId = speciesId;
        this.scientificName = scientificName;
        this.commonName = commonName;
        this.careText = careText;
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

    public String getCareText()
    {
        return careText;
    }

    public double getConfidence()
    {
        return confidence;
    }
}
