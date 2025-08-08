package com.seabornmc.seabornDuel;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Duel {

    private List<Player> team1;
    private List<Player> team2;

    private BoundingBox loc1;
    private BoundingBox loc2;

    private final SeabornDuel plugin;

    private Map<Player, Location> oldLocation = new HashMap<>();

    private float spawnRange = 1f;


    private boolean inProgress = false;

    public Duel(SeabornDuel plugin, List<Player> team1, List<Player> team2, BoundingBox loc1, BoundingBox loc2) {
        this.team1 = team1;
        this.team2 = team2;
        this.loc1 = loc1;
        this.loc2 = loc2;

        this.plugin = plugin;
    }

    public Duel setSpawnRange(float spawnRange) {
        this.spawnRange = spawnRange;
        return this;
    }




    public Location getLocation(World world, BoundingBox teamloc, Random random) {
        Location result = new Location(
                world,
                random.nextDouble(teamloc.getMinX(), teamloc.getMaxX()),
                random.nextDouble(teamloc.getMinY(), teamloc.getMaxY()),
                random.nextDouble(teamloc.getMinZ(), teamloc.getMaxZ())
        );

        Block block = result.getBlock();
        if (
                block.getType() != Material.AIR || block.getLocation().add(new Vector(0,-1,0)).getBlock().getType()==Material.AIR ) {
            return getLocation(world, teamloc, random);
        }

        return result;
    }
    public void startDuel (){
        World world = this.plugin.getServer().getWorld("world");
        Random random = new Random();

        for(Player player : team1){
            this.oldLocation.put(player, player.getLocation());
            player.teleport(getLocation(world, loc1, random))
        }
        for(Player player : team2){
            this.oldLocation.put(player, player.getLocation());
            player.teleport(getLocation(world, loc1, random))
        }
    }

    public void endDuel(){
        
        this.inProgress = false;

        for(Player player : team1){
            player.teleport(oldLocation.get(player));
        }
        for(Player player : team2){
            player.teleport(oldLocation.get(player));
        }
        this.inProgress = false;
    }

    public List<Player> getTeam2() {
        return team2;
    }

    public void setTeam2(List<Player> team2) {
        this.team2 = team2;
    }

    public Location getLoc1() {
        return loc1;
    }

    public void setLoc1(Location loc1) {
        this.loc1 = loc1;
    }

    public Location getLoc2() {
        return loc2;
    }

    public void setLoc2(Location loc2) {
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
