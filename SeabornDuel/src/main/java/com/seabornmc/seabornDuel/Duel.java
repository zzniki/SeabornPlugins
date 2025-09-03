package com.seabornmc.seabornDuel;

import com.google.common.base.Function;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

public class Duel implements Listener {

    private List<Player> team1;
    private List<Player> team2;
    private List<Player> aliveTeam1;
    private List<Player> aliveTeam2;

    private BoundingBox loc1;
    private BoundingBox loc2;

    private final SeabornDuel plugin;

    private Map<Player, Location> oldLocation = new HashMap<>();

    private float spawnRange = 1f;

    private boolean inProgress = false;

    private int passedTicks;
    private int maxTicks;

    private Function<Void, Void> onEndFunction = null;

    public Duel(SeabornDuel plugin, List<Player> team1, List<Player> team2, BoundingBox loc1, BoundingBox loc2, int maxTicks) {
        this.team1 = team1;
        this.team2 = team2;

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
            this.endDuel();
        }

    }


    public Location getLocation(World world, BoundingBox teamloc, Random random) {
        Location result = new Location(
                world,
                random.nextDouble(teamloc.getMinX(), teamloc.getMaxX()),
                random.nextDouble(teamloc.getMinY(), teamloc.getMaxY()),
                random.nextDouble(teamloc.getMinZ(), teamloc.getMaxZ())
        );

        Block block = result.getBlock();
        if (block.getType() != Material.AIR || block.getLocation().add(new Vector(0,-1,0)).getBlock().getType()==Material.AIR ) {
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
            player.teleport(getLocation(world, loc1, random));
        }

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

    }

    private void markAsDead(Player player) {
        if (aliveTeam1.contains(player)) {
            aliveTeam1.remove(player);

            player.setGameMode(GameMode.SPECTATOR);

        } else if (aliveTeam2.contains(player)) {

            player.setGameMode(GameMode.SURVIVAL);

            aliveTeam2.remove(player);
        }


        if (aliveTeam1.isEmpty() || aliveTeam2.isEmpty()) {
            this.endDuel();
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

    public void endDuel(String winner) {

        this.inProgress = false;

        for(Player player : team1) {
            player.teleport(oldLocation.get(player));
        }
        for(Player player : team2) {
            player.teleport(oldLocation.get(player));
        }
        this.inProgress = false;

        PlayerQuitEvent.getHandlerList().unregister(this);
        PlayerDeathEvent.getHandlerList().unregister(this);

        if (this.onEndFunction != null) this.onEndFunction.apply(null);

        Title title = Title.title(
                Utils.formatText("<green>" + winner + "has won!"),
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
