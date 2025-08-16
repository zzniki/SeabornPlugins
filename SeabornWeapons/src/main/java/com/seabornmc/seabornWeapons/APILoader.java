package com.seabornmc.seabornWeapons;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.seabornmc.seabornWeapons.weapons.Sword;
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

    private static final String API_URL = "http://localhost:8055/items/Swords?fields=*";

    public static Map<String, Sword> loadSwords() {
        Map<String, Sword> swords = new HashMap<>();
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .timeout(Duration.ofSeconds(5))
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
                float price = obj.get("price").getAsFloat();
                float minRep = obj.get("min_rep").getAsFloat();
                String modelId = obj.get("model").getAsString();
                float damage = obj.get("damage").getAsFloat();

                String shortDescription = obj.get("short_description").isJsonNull()
                        ? "" : obj.get("short_description").getAsString();

                List<String> description = new ArrayList<>();
                if (!obj.get("description").isJsonNull()) {
                    for (JsonElement line : obj.get("description").getAsJsonArray()) {
                        description.add(line.getAsString());
                    }
                }

                Sword sword = new Sword(id);
                sword.setDisplayName(displayName);
                sword.setPrice(price);
                sword.setMinRep(minRep);
                sword.setDamage(damage);
                sword.setShortDescription(shortDescription);
                sword.setDescription(description);
                sword.setItemModel(new NamespacedKey("seaborn_swords", id));

                swords.put(id, sword);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return swords;
    }

}
