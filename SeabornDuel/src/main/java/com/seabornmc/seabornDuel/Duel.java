package com.seabornmc.seabornDuel;

import com.google.common.base.Function;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class Duel implements Listener {

    private List<Player> team1;
    private List<Player> team2;
    private List<Player> aliveTeam1;
    private List<Player> aliveTeam2;

    private String team1Name;
    private String team2Name;

    private BoundingBox loc1;
    private BoundingBox loc2;

    private final SeabornDuel plugin;

    private Map<Player, Location> oldLocation = new HashMap<>();

    private float spawnRange = 1f;

    private boolean inProgress = false;

    private int passedTicks;
    private int maxTicks;

    private Function<Void, Void> onEndFunction = null;

    public Duel(SeabornDuel plugin, List<Player> team1, List<Player> team2, String team1Name, String team2Name, BoundingBox loc1, BoundingBox loc2, int maxTicks) {
        this.team1 = team1;
        this.team2 = team2;

        this.team1Name = team1Name;
        this.team2Name = team2Name;

        this.aliveTeam1 = new ArrayList<>(team1);
        this.aliveTeam2 = new ArrayList<>(team2);

        this.loc1 = loc1;
        this.loc2 = loc2;

        this.maxTicks = maxTicks;

        this.plugin = plugin;
    }

    public Duel setSpawnRange(float spawnRange) {
        this.spawnRange = spawnRange;
        return this;
    }

    public void tick() {
        this.passedTicks++;

        if (this.passedTicks >= this.maxTicks) {
            this.endDuel(-1);
        }

    }


    public Location getLocation(World world, BoundingBox teamloc, Random random) {
        // Normalize min/max for each axis
        double minX = Math.min(teamloc.getMinX(), teamloc.getMaxX());
        double maxX = Math.max(teamloc.getMinX(), teamloc.getMaxX());
        double x = (minX == maxX) ? minX : random.nextDouble(minX, maxX);

        double minY = Math.min(teamloc.getMinY(), teamloc.getMaxY());
        double maxY = Math.max(teamloc.getMinY(), teamloc.getMaxY());
        double y = (minY == maxY) ? minY : random.nextDouble(minY, maxY);

        double minZ = Math.min(teamloc.getMinZ(), teamloc.getMaxZ());
        double maxZ = Math.max(teamloc.getMinZ(), teamloc.getMaxZ());
        double z = (minZ == maxZ) ? minZ : random.nextDouble(minZ, maxZ);

        Location result = new Location(world, x, y, z);

        Block block = result.getBlock();
        if (block.getType() != Material.AIR ||
                block.getLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) {
            // Retry until we find a valid location
            return getLocation(world, teamloc, random);
        }

        return result;
    }


    public void setOnEnd(Function<Void, Void> func) {
        this.onEndFunction = func;
    }

    public void startDuel() {

        World world = this.plugin.getServer().getWorld("world");
        Random random = new Random();

        for (Player player : team1){
            this.oldLocation.put(player, player.getLocation());
            player.teleport(getLocation(world, loc1, random));
        }
        for (Player player : team2){
            this.oldLocation.put(player, player.getLocation());
            player.teleport(getLocation(world, loc2, random));
        }

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

    }

    private void markAsDead(Player player) {

        int winnerId;

        if (aliveTeam1.contains(player)) {
            aliveTeam1.remove(player);

            player.setGameMode(GameMode.SPECTATOR);
            winnerId = 2;

        } else if (aliveTeam2.contains(player)) {

            player.setGameMode(GameMode.SPECTATOR);

            aliveTeam2.remove(player);
            winnerId = 1;
        } else {
            winnerId = -1;
        }


        if (aliveTeam1.isEmpty() || aliveTeam2.isEmpty()) {

            Bukkit.getScheduler().runTaskLater(SeabornDuel.getInstance(), () -> {
                this.endDuel(winnerId);
            }, 20L);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (aliveTeam1.contains(player) || aliveTeam2.contains(player)) {
            event.setCancelled(true);
            this.markAsDead(player);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (team1.contains(player) || team2.contains(player)) {
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(this.oldLocation.get(player));

            if (aliveTeam1.contains(player) || aliveTeam2.contains(player)) {
                this.markAsDead(player);
            }

        }
    }

    public void endDuel(int winnerId) {

        this.inProgress = false;

        for (Player player : team1) {
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(oldLocation.get(player));
        }
        for (Player player : team2) {
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(oldLocation.get(player));
        }
        this.inProgress = false;

        PlayerQuitEvent.getHandlerList().unregister(this);
        PlayerDeathEvent.getHandlerList().unregister(this);

        if (this.onEndFunction != null) this.onEndFunction.apply(null);

        String winner = winnerId == 1 ? team1Name : team2Name;
        String titleStr = winnerId == -1 ? "<green>It's a tie!" : "<green>" + winner + " has won!";

        Title title = Title.title(
                Utils.formatText(titleStr),
                Utils.formatText("<white>The battle has ended.")
        );

        for (Player player : team1) {
            player.showTitle(title);
        }

        for (Player player : team2) {
            player.showTitle(title);
        }

    }

    public List<Player> getTeam2() {
        return team2;
    }

    public void setTeam2(List<Player> team2) {
        this.team2 = team2;
    }

    public BoundingBox getLoc1() {
        return loc1;
    }

    public void setLoc1(BoundingBox loc1) {
        this.loc1 = loc1;
    }

    public BoundingBox getLoc2() {
        return loc2;
    }

    public void setLoc2(BoundingBox loc2) {
        this.loc2 = loc2;
    }

    public float getSpawnRange() {
        return spawnRange;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public List<Player> getTeam1() {
        return team1;
    }

    public void setTeam1(List<Player> team1) {
        this.team1 = team1;
    }
}
