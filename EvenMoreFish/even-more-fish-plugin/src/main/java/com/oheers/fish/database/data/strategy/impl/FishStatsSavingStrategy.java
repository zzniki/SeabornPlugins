package com.oheers.fish.database.data.strategy.impl;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.database.data.strategy.ImmediateSavingStrategy;
import com.oheers.fish.database.model.fish.FishStats;


public class FishStatsSavingStrategy extends ImmediateSavingStrategy<FishStats> {
    public FishStatsSavingStrategy() {
        super(fishStats -> EvenMoreFish.getInstance().getPluginDataManager().getDatabase().upsertFishStats(fishStats));
    }
}
