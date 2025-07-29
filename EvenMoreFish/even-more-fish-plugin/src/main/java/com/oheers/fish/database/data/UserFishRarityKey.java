package com.oheers.fish.database.data;

import com.oheers.fish.fishing.items.Fish;
import org.jetbrains.annotations.NotNull;

public class UserFishRarityKey {
    private final int userId;
    private final String fishName;
    private final String fishRarity;

    public UserFishRarityKey(int userId, String fishName, String fishRarity) {
        this.userId = userId;
        this.fishName = fishName;
        this.fishRarity = fishRarity;
    }

    public static @NotNull UserFishRarityKey of(final int userId, final String fishName, final String fishRarity) {
        return new UserFishRarityKey(userId, fishName, fishRarity);
    }

    public static @NotNull UserFishRarityKey of(final int userId, final @NotNull Fish fish) {
        return new UserFishRarityKey(userId, fish.getName(), fish.getRarity().getId());
    }


    @Override
    public String toString() {
        return userId + "." +fishName + "." + fishRarity;
    }

    public String toStringDefault() {
        return "UserFishRarityKey{" +
                "userId='" + userId +'\'' +
                "fishName='" + fishName + '\'' +
                ", fishRarity='" + fishRarity + '\'' +
                '}';
    }
}