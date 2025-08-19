package com.seaborn.seabornCore.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SerializeCommon {

    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter())
            .create();

    /**
     * Serialize a Bukkit ConfigurationSerializable into a Base64 string.
     */
    public static String toBase64(ConfigurationSerializable object) {
        String json = gson.toJson(object, ConfigurationSerializable.class);
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Deserialize a Base64 string back into a ConfigurationSerializable object.
     */
    public static ConfigurationSerializable fromBase64(String base64) {
        String json = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
        return gson.fromJson(json, ConfigurationSerializable.class);
    }

}
