
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;

public class PlantNetIdentifier implements PlantIdentifier {

    private final HttpClient client;
    private final String apiKey;
    private final URI endpoint;

    public PlantNetIdentifier(String apiKey)
    {
        if (apiKey == null || apiKey.isBlank())
        {
            throw new IllegalArgumentException("API key cannot be null or blank.");
        }

        this.apiKey = apiKey;
        this.client = HttpClient.newHttpClient();
        this.endpoint = URI.create(
            "https://my-api.plantnet.org/v2/identify/all"
        );
    }

    @Override
    public List<Identification> identify(byte[] image)
    {
        if (image == null || image.length == 0)
        {
            throw new IllegalArgumentException("Image cannot be null or empty.");
        }

        String boundary = "----PlantNetBoundary12345";

        try
        {
            String requestUrl = endpoint + "?api-key="
                + URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

            byte[] requestBody = createRequestBody(image, boundary);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestBody))
                .build();

            HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );

            // PlantNet returns 404 when no plant species is found.
            if (response.statusCode() == 404)
            {
                return List.of();
            }

            if (response.statusCode() != 200)
            {
                throw new ServiceException(
                    "PlantNet service returned status: " + response.statusCode()
                );
            }

            return parseResults(response.body());
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new ServiceException("Plant identification was interrupted.");
        }
        catch (IOException | JsonParseException e)
        {
            throw new ServiceException("Unable to communicate with PlantNet.");
        }
    }

    private byte[] createRequestBody(byte[] image, String boundary)
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        String header =
            "--" + boundary + "\r\n"
            + "Content-Disposition: form-data; name=\"images\"; filename=\"plant.jpg\"\r\n"
            + "Content-Type: image/jpeg\r\n\r\n";

        output.writeBytes(header.getBytes(StandardCharsets.UTF_8));
        output.writeBytes(image);
        output.writeBytes(("\r\n--" + boundary + "--\r\n")
            .getBytes(StandardCharsets.UTF_8));

        return output.toByteArray();
    }

    private List<Identification> parseResults(String json)
    {
        JsonObject response = JsonParser.parseString(json).getAsJsonObject();
        JsonArray results = response.getAsJsonArray("results");

        List<Identification> identifications = new ArrayList<>();

        if (results == null)
        {
            return identifications;
        }

        for (JsonElement element : results)
        {
            JsonObject result = element.getAsJsonObject();
            JsonObject species = result.getAsJsonObject("species");

            String scientificName = species.get("scientificName")
                .getAsString();

            String commonName = scientificName;

            JsonArray commonNames = species.getAsJsonArray("commonNames");

            if (commonNames != null && !commonNames.isEmpty())
            {
                commonName = commonNames.get(0).getAsString();
            }

            String speciesId = scientificName;

            if (result.has("gbif") && result.getAsJsonObject("gbif").has("id"))
            {
                speciesId = result.getAsJsonObject("gbif")
                    .get("id").getAsString();
            }

            double confidence = result.get("score").getAsDouble();

            identifications.add(new Identification(
                speciesId,
                scientificName,
                commonName,
                confidence
            ));
        }

        return identifications;
    }
}
