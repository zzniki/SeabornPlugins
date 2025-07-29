package com.oheers.fish.database.data.strategy.impl;


import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.database.data.strategy.ImmediateSavingStrategy;
import com.oheers.fish.database.model.user.UserReport;


public class UserReportsSavingStrategy extends ImmediateSavingStrategy<UserReport> {
    public UserReportsSavingStrategy() {
        super(report -> EvenMoreFish.getInstance().getPluginDataManager().getDatabase().upsertUserReport(report));
    }
}
