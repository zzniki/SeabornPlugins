package com.seaborn.seabornFishing.api;

import com.google.gson.*;
import com.seaborn.seabornFishing.*;
import org.bukkit.NamespacedKey;

import javax.naming.Name;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

import static com.seaborn.seabornFishing.api.APISettings.API_BASE;

public class APILoader {

    private static final Gson gson = new Gson();

    private final SeabornFishing plugin;

    public APILoader(SeabornFishing plugin) {
        this.plugin = plugin;
    }

    public SortedMap<String, Fish> loadFish(Map<String, Rarity> rarityMap) throws Exception {
        String endpoint = "/items/Fish?fields=*";
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .timeout(Duration.ofSeconds(5))
                .uri(URI.create(API_BASE + endpoint))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        InputStreamReader reader = new InputStreamReader(response.body());
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        JsonArray data = root.getAsJsonArray("data");

        SortedMap<String, Fish> fishMap = new TreeMap<>();

        for (JsonElement element : data) {
            JsonObject obj = element.getAsJsonObject();

            String id = getAsString(obj, "id");
            String name = getAsString(obj, "display_name");
            String description = getAsString(obj, "description");
            String shortDescription = getAsString(obj, "short_description");
            float minWeight = getAsFloat(obj, "min_weight", 0f);
            float maxWeight = getAsFloat(obj, "max_weight", 0f);
            float worthMultiplier = getAsFloat(obj, "worth_multiplier", 1f);
            float randomWeight = getAsFloat(obj, "random_weight", 1f);

            // Resolve rarity
            String rarityId = getAsString(obj, "rarity");
            Rarity rarity = rarityMap.get(rarityId);
            if (rarity == null) {
                plugin.getLogger().warning("Unknown rarity: " + rarityId + " for fish: " + id);
                continue;
            }

            // Generate a namespaced key for the item model using the texture UUID
            NamespacedKey itemModel = new NamespacedKey("seaborn_fish", id);

            Fish fish = new Fish(rarity, id, name, minWeight, maxWeight, itemModel);
            fish.setDescription(description);
            fish.setShortDescription(shortDescription);
            fish.setWorthMultiplier(worthMultiplier);
            fish.setRandomWeight(randomWeight);

            fishMap.put(id, fish);
        }

        return fishMap;
    }

    private String getAsString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : "";
    }

    private float getAsFloat(JsonObject obj, String key, float def) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsFloat() : def;
    }

    public Map<String, Rarity> loadRarities() throws Exception {
        String endpoint = "/items/Rarities?fields=*";
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .timeout(Duration.ofSeconds(5))
                .uri(URI.create(API_BASE + endpoint))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        InputStreamReader reader = new InputStreamReader(response.body());
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        JsonArray data = root.getAsJsonArray("data");

        Map<String, Rarity> rarityMap = new HashMap<>();

        for (JsonElement element : data) {
            JsonObject obj = element.getAsJsonObject();

            String id = obj.get("id").getAsString();
            String displayName = obj.get("display_name").getAsString();
            float worthMultiplier = getAsFloat(obj, "worth_multiplier", 0.1f);
            float weight = getAsFloat(obj, "weight", 100f);

            Rarity rarity = new Rarity(id, displayName, worthMultiplier, weight);
            rarityMap.put(id, rarity);
        }

        return rarityMap;
    }

    public Map<String, Region> loadRegions(Map<String, Fish> fishMap) throws Exception {
        String endpoint = "/items/Regions?fields=*,available_fish.Fish_id";
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .timeout(Duration.ofSeconds(5))
                .uri(URI.create(API_BASE + endpoint))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        InputStreamReader reader = new InputStreamReader(response.body());
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        JsonArray data = root.getAsJsonArray("data");

        Map<String, Region> regionMap = new HashMap<>();

        for (JsonElement element : data) {
            JsonObject obj = element.getAsJsonObject();

            String id = getAsString(obj, "id");
            String displayName = getAsString(obj, "display_name");
            int priority = obj.has("priority") ? obj.get("priority").getAsInt() : 0;

            Region region = new Region(id, displayName, id); // using id as worldguardId
            region.setPriority(priority);

            JsonArray fishArray = obj.getAsJsonArray("available_fish");
            for (JsonElement fishElement : fishArray) {
                JsonObject fishObj = fishElement.getAsJsonObject();
                String fishId = getAsString(fishObj, "Fish_id");

                Fish fish = fishMap.get(fishId);
                if (fish != null) {
                    region.addFish(fish);
                } else {
                    plugin.getLogger().warning("Fish with ID '" + fishId + "' not found for region: " + id);
                }
            }

            regionMap.put(id, region);
        }

        return regionMap;
    }

    public Map<String, CustomRod> loadRods() throws Exception {
        String endpoint = "/items/Rods?fields=*";
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .timeout(Duration.ofSeconds(5))
                .uri(URI.create(API_BASE + endpoint))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        InputStreamReader reader = new InputStreamReader(response.body());
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        JsonArray data = root.getAsJsonArray("data");

        Map<String, CustomRod> rodMap = new HashMap<>();

        for (JsonElement element : data) {
            JsonObject obj = element.getAsJsonObject();

            String id = getAsString(obj, "id");
            String displayName = getAsString(obj, "display_name");
            float baseLuck = obj.has("base_luck") ? obj.get("base_luck").getAsFloat() : 1f;
            float difficulty = obj.has("difficulty") ? obj.get("difficulty").getAsFloat() : 1f;
            String description = obj.has("description") && obj.get("description") != JsonNull.INSTANCE ? obj.get("description").getAsString() : null;
            int uses = obj.has("uses") && obj.get("uses") != JsonNull.INSTANCE ? obj.get("uses").getAsInt() : 64;

            CustomRod rod = new CustomRod(id, displayName);
            rod.setLuckMultiplier(baseLuck);
            rod.setDifficulty(difficulty);
            rod.setItemModel(NamespacedKey.fromString("seaborn_rods:" + id));
            rod.setDescription(description);
            rod.setUses(uses);

            rodMap.put(id, rod);
        }

        return rodMap;
    }

}
