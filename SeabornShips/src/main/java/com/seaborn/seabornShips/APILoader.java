package com.seaborn.seabornShips;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.NamespacedKey;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APILoader {

    private static final String API_URL = "http://localhost:8055/items/Boats?fields=*";

    public static Map<String, BoatType> loadBoats() {
        Map<String, BoatType> boats = new HashMap<>();
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .timeout(Duration.ofSeconds(10))
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            JsonObject json = JsonParser.parseReader(
                    new InputStreamReader(response.body(), StandardCharsets.UTF_8)
            ).getAsJsonObject();

            JsonArray dataArray = json.getAsJsonArray("data");

            for (JsonElement element : dataArray) {
                JsonObject obj = element.getAsJsonObject();

                String id = obj.get("id").getAsString();
                String displayName = obj.get("display_name").getAsString();
                String description = obj.get("description").getAsString();
                double speed = obj.get("speed").getAsDouble();

                BoatType boat = new BoatType(id);
                boat.setName(displayName);
                boat.setSpeed(speed);

                // Store model UUID as a NamespacedKey
                boat.setEntityModel(new NamespacedKey("seaborn_boats", id));
                boat.setItemModel(new NamespacedKey("seaborn_boats", "item_" + id));

                // Add description as lore
                List<String> lore = new ArrayList<>();
                lore.add(description);
                boat.setLore(lore);

                boats.put(id, boat);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return boats;
    }

}
