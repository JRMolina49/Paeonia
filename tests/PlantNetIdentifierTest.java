import static org.junit.Assert.*;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

/** Checks PlantNet response and HTTP contracts using only a loopback server. */
public class PlantNetIdentifierTest
{
    private static final String RESULT = "{\"results\":[{\"score\":0.9,\"species\":{"
        + "\"scientificName\":\"Aloe vera (L.) Burm.f.\","
        + "\"scientificNameWithoutAuthor\":\"Aloe vera\",\"commonNames\":[\"Aloe\"]},"
        + "\"images\":[{\"url\":{\"m\":\"https://images.plantnet.org/photo.jpg\"},"
        + "\"author\":\"Photographer\",\"license\":\"CC BY-SA\",\"organ\":\"leaf\"},"
        + "{\"url\":{\"m\":\"https://example.com/untrusted.jpg\"}},null]}]}";

    @Test
    public void parsesNamesConfidenceAndReferenceImages()
    {
        Identification result = PlantNetIdentifier.parseResults(RESULT).get(0);
        assertEquals("Aloe vera", result.getSpeciesId());
        assertEquals("Aloe vera (L.) Burm.f.", result.getScientificName());
        assertEquals("Aloe", result.getCommonName());
        assertEquals(0.9, result.getConfidence(), 0.0);
        assertEquals(1, result.getReferenceImages().size());
        PlantDetails.ReferenceImage image = result.getReferenceImages().get(0);
        assertEquals("https://images.plantnet.org/photo.jpg", image.getUrl());
        assertEquals("Photographer", image.getAuthor());
        assertEquals("CC BY-SA", image.getLicense());
        assertEquals("leaf", image.getOrgan());
        assertTrue(PlantNetIdentifier.parseResults("{\"results\":[]}").isEmpty());
        String fallback = "{\"results\":[{\"score\":1,\"species\":{\"scientificName\":\"Aloe\"},"
            + "\"images\":[{\"url\":{\"s\":\"https://plantnet.org/s.jpg\"},"
            + "\"organs\":[{\"name\":\"flower\"}]}]}]}";
        Identification unnamed = PlantNetIdentifier.parseResults(fallback).get(0);
        assertEquals("Aloe", unnamed.getCommonName());
        assertEquals("flower", unnamed.getReferenceImages().get(0).getOrgan());
        assertEquals("Unknown author", unnamed.getReferenceImages().get(0).getAuthor());
    }

    @Test
    public void rejectsMalformedResponsesAndInvalidArguments()
    {
        for (String json : new String[] {null, "", "garbage", "[]", "null",
            "{\"results\":[null]}", "{\"results\":[{\"score\":0.9}]}",
            RESULT.replace("0.9", "2.0")})
        {
            assertThrows(ServiceException.class, () -> PlantNetIdentifier.parseResults(json));
        }
        for (String key : new String[] {null, "", " "})
        {
            assertThrows(IllegalArgumentException.class, () -> new PlantNetIdentifier(key));
        }
        PlantNetIdentifier identifier = new PlantNetIdentifier("test-key");
        for (byte[] invalid : new byte[][] {null, new byte[0], {1, 2}})
        {
            assertThrows(IllegalArgumentException.class, () -> identifier.identify(invalid));
        }
    }

    @Test
    public void sendsJpegMultipartAndHandlesHttpStatuses() throws Exception
    {
        AtomicReference<byte[]> body = new AtomicReference<>();
        AtomicReference<String> contentType = new AtomicReference<>();
        AtomicReference<String> query = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/success", exchange ->
        {
            body.set(exchange.getRequestBody().readAllBytes());
            contentType.set(exchange.getRequestHeaders().getFirst("Content-Type"));
            query.set(exchange.getRequestURI().getRawQuery());
            byte[] response = RESULT.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            try (java.io.OutputStream output = exchange.getResponseBody())
            {
                output.write(response);
            }
        });
        server.createContext("/empty", exchange ->
        {
            exchange.getRequestBody().readAllBytes();
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        });
        server.createContext("/failure", exchange ->
        {
            exchange.getRequestBody().readAllBytes();
            exchange.sendResponseHeaders(503, -1);
            exchange.close();
        });
        server.start();
        try
        {
            String base = "http://127.0.0.1:" + server.getAddress().getPort();
            HttpClient client = HttpClient.newHttpClient();
            PlantNetIdentifier identifier = new PlantNetIdentifier("test key", client, URI.create(base + "/success"));
            List<Identification> results = identifier.identify(TestSupport.image());
            assertEquals("Aloe vera", results.get(0).getSpeciesId());
            assertEquals("api-key=test+key", query.get());
            assertTrue(contentType.get().startsWith("multipart/form-data; boundary="));
            String multipart = new String(body.get(), StandardCharsets.ISO_8859_1);
            assertTrue(multipart.contains("Content-Type: image/jpeg\r\n"));
            assertTrue(multipart.contains("filename=\"plant.jpg\""));
            int dataStart = multipart.indexOf("\r\n\r\n") + 4;
            assertEquals(0xff, body.get()[dataStart] & 0xff);
            assertEquals(0xd8, body.get()[dataStart + 1] & 0xff);
            assertTrue(new PlantNetIdentifier("test", client, URI.create(base + "/empty"))
                .identify(TestSupport.image()).isEmpty());
            PlantNetIdentifier unavailable = new PlantNetIdentifier("test", client, URI.create(base + "/failure"));
            byte[] image = TestSupport.image();
            assertThrows(ServiceException.class, () -> unavailable.identify(image));
        }
        finally
        {
            server.stop(0);
        }
    }
}
