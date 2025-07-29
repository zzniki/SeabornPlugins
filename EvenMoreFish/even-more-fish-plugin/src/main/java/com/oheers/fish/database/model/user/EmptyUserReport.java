package com.oheers.fish.database.model.user;


import com.oheers.fish.database.data.FishRarityKey;

import java.util.UUID;

public class EmptyUserReport extends UserReport{
    public EmptyUserReport(UUID uuid) {
        super(uuid, FishRarityKey.empty(), FishRarityKey.empty(), FishRarityKey.empty(), FishRarityKey.empty(),0, 0, 0, 0, -1, 0, 0, 0);
    }
}
