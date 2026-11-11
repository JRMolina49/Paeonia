/**
 * Supplies care information and listings for a species.
 *
 * @author Conrad
 */
public interface PlantCatalog
{
    /**
     * Looks up a species in the catalog.
     * @param speciesId non-blank species identifier
     * @return a non-null result; unknown species have empty care and listings
     * @throws IllegalArgumentException if the identifier is null or blank
     */
    CatalogResult lookup(String speciesId);
}
