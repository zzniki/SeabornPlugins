package com.seabornmc.seabornCosmetics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.seabornmc.seabornCosmetics.cosmetics.Hat;
import org.bukkit.NamespacedKey;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class APILoader {

    private static final String API_URL = "http://localhost:8055/items/Hats?fields=*";

    public static Map<String, Hat> loadHats() {
        Map<String, Hat> hats = new HashMap<>();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .timeout(Duration.ofSeconds(7))
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            InputStreamReader reader = new InputStreamReader(response.body());
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray dataArray = root.getAsJsonArray("data");

            for (JsonElement element : dataArray) {
                JsonObject obj = element.getAsJsonObject();

                String id = obj.get("id").getAsString();
                Hat hat = new Hat(id);

                if (obj.has("display_name") && !obj.get("display_name").isJsonNull()) {
                    hat.setDisplayName(obj.get("display_name").getAsString());
                }
                if (obj.has("short_description") && !obj.get("short_description").isJsonNull()) {
                    hat.setShortDescription(obj.get("short_description").getAsString());
                }
                hat.setItemModel(new NamespacedKey("seaborn_hats", id));

                hats.put(id, hat);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hats;
    }

}
