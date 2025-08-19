package com.seaborn.seabornCrews;

import com.seaborn.seabornCrews.dao.CrewDAO;
import com.seaborn.seabornCrews.service.CrewService;
import org.bukkit.plugin.java.JavaPlugin;

public class SeabornCrews extends JavaPlugin {

    private static CrewService crewService;
    private static CrewDAO crewDAO;

    @Override
    public void onEnable() {

        String host = getConfig().getString("database.host");
        int port = getConfig().getInt("database.port");
        String dbName = getConfig().getString("database.name");
        String user = getConfig().getString("database.user");
        String pass = getConfig().getString("database.password");

        DatabaseManager dbManager = new DatabaseManager(
                host, port, dbName, user, pass
        );

        DatabaseInitializer.runMigrations(dbManager);

        crewDAO = new CrewDAO(dbManager.getDataSource());
        crewService = new CrewService(crewDAO);

    }

    @Override
    public void onDisable() {}

    public static CrewService getCrewService() { return crewService; }

}
