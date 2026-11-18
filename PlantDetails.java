import java.net.URI;
import java.util.List;

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
    private final List<ReferenceImage> referenceImages;

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
        this(speciesId, scientificName, commonName, careText, confidence, List.of());
    }

    /**
     * Creates plant details with optional PlantNet reference images.
     * @param speciesId specific identification of the plant species
     * @param scientificName scientific name of the plant species
     * @param commonName common name of the plant species
     * @param careText care information
     * @param confidence confidence of the identification
     * @param referenceImages PlantNet reference images and attribution
     */
    public PlantDetails(String speciesId, String scientificName, String commonName,
            String careText, double confidence, List<ReferenceImage> referenceImages)
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
        if (referenceImages == null || referenceImages.stream().anyMatch(image -> image == null))
        {
            throw new IllegalArgumentException("Reference images must contain non-null values");
        }
        this.referenceImages = List.copyOf(referenceImages);
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

    public List<ReferenceImage> getReferenceImages()
    {
        return referenceImages;
    }

    /** PlantNet-hosted image metadata shown alongside its required attribution. */
    public static final class ReferenceImage
    {
        private final String url;
        private final String author;
        private final String license;
        private final String organ;

        public ReferenceImage(String url, String author, String license, String organ)
        {
            if (url == null || url.isBlank())
            {
                throw new IllegalArgumentException("Image URL cannot be null or blank");
            }
            URI imageUri;
            try
            {
                imageUri = URI.create(url);
            }
            catch (IllegalArgumentException e)
            {
                throw new IllegalArgumentException("Invalid PlantNet image URL", e);
            }
            String host = imageUri.getHost();
            if (!"https".equalsIgnoreCase(imageUri.getScheme())
                    || host == null
                    || !(host.equalsIgnoreCase("plantnet.org")
                        || host.toLowerCase(java.util.Locale.ROOT).endsWith(".plantnet.org")))
            {
                throw new IllegalArgumentException("Image URL must use PlantNet HTTPS hosting");
            }
            this.url = url;
            this.author = author == null || author.isBlank() ? "Unknown author" : author;
            this.license = license == null || license.isBlank()
                ? "License not provided" : license;
            this.organ = organ == null ? "" : organ;
        }

        public String getUrl()
        {
            return url;
        }

        public String getAuthor()
        {
            return author;
        }

        public String getLicense()
        {
            return license;
        }

        public String getOrgan()
        {
            return organ;
        }
    }
}
