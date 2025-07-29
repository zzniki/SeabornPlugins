package com.oheers.fish.fishing.items;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.FishUtils;
import com.oheers.fish.api.FileUtil;
import com.oheers.fish.api.requirement.Requirement;
import com.oheers.fish.api.requirement.RequirementContext;
import com.oheers.fish.competition.Competition;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.fishing.Processor;
import com.oheers.fish.fishing.items.config.FishConversions;
import com.oheers.fish.fishing.items.config.RarityConversions;
import com.oheers.fish.fishing.rods.CustomRod;
import com.oheers.fish.utils.WeightedRandom;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.ToDoubleFunction;

import static org.bukkit.Bukkit.getServer;

public class FishManager {

    private static FishManager instance;

    private final TreeMap<String, Rarity> rarityMap;
    private boolean loaded = false;

    private FishManager() {
        rarityMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        new RarityConversions().performCheck();
        new FishConversions().performCheck();
    }

    public static FishManager getInstance() {
        if (instance == null) {
            instance = new FishManager();
        }
        return instance;
    }

    public void load() {
        if (isLoaded()) {
            return;
        }
        loadRarities();
        logLoadedItems();
        loaded = true;
    }

    public void reload() {
        if (!isLoaded()) {
            return;
        }
        rarityMap.clear();
        loadRarities();
        logLoadedItems();
    }

    public void unload() {
        if (!isLoaded()) {
            return;
        }
        rarityMap.clear();
        loaded = false;
    }

    public boolean isLoaded() {
        return loaded;
    }

    // Getters for Rarities and Fish

    public @Nullable Rarity getRarity(@NotNull String rarityName) {
        return rarityMap.get(rarityName);
    }

    public @Nullable Fish getFish(@NotNull String rarityName, @NotNull String fishName) {
        Rarity rarity = getRarity(rarityName);
        if (rarity == null) {
            return null;
        }
        return rarity.getFish(fishName);
    }

    public Rarity getRandomWeightedRarity(Player fisher, double luck, double boostRate, @NotNull Set<Rarity> boostedRarities, Set<Rarity> totalRarities, @Nullable CustomRod customRod) {
        if (fisher != null) {
            Map<UUID, Rarity> decidedRarities = EvenMoreFish.getInstance().getDecidedRarities();
            if (decidedRarities.containsKey(fisher.getUniqueId())) {
                return decidedRarities.remove(fisher.getUniqueId());
            }
        }

        List<Rarity> allowedRarities = getAllowedRarities(fisher, boostRate, boostedRarities, totalRarities);

        // Remove all rarities that are not allowed by the custom rod.
        if (customRod != null) {
            allowedRarities.retainAll(customRod.getAllowedRarities());
        }

        if (allowedRarities.isEmpty()) {
            String fisherName = fisher == null ? "N/A" : fisher.getName();
            EvenMoreFish.getInstance().getLogger().severe("There are no rarities for the user " + fisherName + " to fish. They have received no fish.");
            return null;
        }

        Rarity selected = WeightedRandom.pick(
                allowedRarities,
                Rarity::getWeight,
                boostRate,
                boostedRarities,
                luck,
                EvenMoreFish.getInstance().getRandom()
        );

        if (selected == null) {
            return null;
        }

        if (!Competition.isActive() && EvenMoreFish.getInstance().isRaritiesCompCheckExempt()) {
            if (selected.hasCompExemptFish()) {
                return selected;
            }
        }

        if (Competition.isActive() || !MainConfig.getInstance().isFishCatchOnlyInCompetition()) {
            return selected;
        }
        return null;

    }

    private @NotNull List<Rarity> getAllowedRarities(
            Player fisher,
            double boostRate,
            Set<Rarity> boostedRarities,
            Set<Rarity> totalRarities
    ) {
        if (fisher == null) {
            return new ArrayList<>(totalRarities);
        }

        List<Rarity> allowedRarities = new ArrayList<>();
        String region = FishUtils.getRegionName(fisher.getLocation());
        RequirementContext context = new RequirementContext(fisher.getWorld(), fisher.getLocation(), fisher, null, null);

        for (Rarity rarity : rarityMap.values()) {
            if (shouldSkipRarity(rarity, boostRate, boostedRarities, fisher)) {
                continue;
            }

            if (!rarity.getRequirement().meetsRequirements(context)) {
                continue;
            }

            double regionBoost = MainConfig.getInstance().getRegionBoost(region, rarity.getId());
            addRarityMultipleTimes(allowedRarities, rarity, regionBoost);
        }

        return allowedRarities;
    }

    private boolean shouldSkipRarity(
            @NotNull Rarity rarity,
            double boostRate,
            Set<Rarity> boostedRarities,
            Player fisher
    ) {
        boolean isBoostFiltered = boostedRarities != null && boostRate == -1 && !boostedRarities.contains(rarity);
        boolean lacksPermission = rarity.getPermission() != null && !fisher.hasPermission(rarity.getPermission());
        return isBoostFiltered || lacksPermission;
    }

    private void addRarityMultipleTimes(List<Rarity> list, Rarity rarity, double times) {
        for (int i = 0; i < times; i++) {
            list.add(rarity);
        }
    }



    public Fish getRandomWeightedFish(@NotNull List<Fish> fishList, double boostRate, List<Fish> boostedFish) {
        if (fishList.isEmpty()) return null;

        // Define a weight function that handles zero weights as 1
        ToDoubleFunction<Fish> weightFunction = fish -> {
            double weight = fish.getWeight();
            return weight == 0.0d ? 1.0 : weight;
        };

        Set<Fish> boostedSet = boostedFish == null ? Collections.emptySet() : new HashSet<>(boostedFish);

        return WeightedRandom.pick(fishList, weightFunction, boostRate, boostedSet, EvenMoreFish.getInstance().getRandom());
    }


    public Fish getFish(Rarity r, Location l, Player p, double boostRate, List<Fish> boostedFish, boolean doRequirementChecks, @Nullable Processor<?> processor, @Nullable CustomRod customRod) {
        if (r == null) return null;
        // will store all the fish that match the player's biome or don't discriminate biomes

        // Protection against /emf admin reload causing the plugin to be unable to get the rarity
        if (r.getOriginalFishList().isEmpty()) {
            r = getRandomWeightedRarity(p, 0, 1, Collections.emptySet(), Set.copyOf(rarityMap.values()), customRod);
        }

        List<Fish> customRodFish = customRod == null ? List.of() : customRod.getAllowedFish();

        World world = l == null ? null : l.getWorld();
        RequirementContext context = new RequirementContext(world, l, p, null, null);

        List<Fish> available = r.getFishList().stream()
                .filter(fish -> {
                    if (!customRodFish.isEmpty() && !customRodFish.contains(fish)) {
                        return false;
                    }
                    if (!(boostRate != -1 || boostedFish == null || boostedFish.contains(fish))) {
                        return false;
                    }
                    if (processor != null && !processor.canUseFish(fish)) {
                        return false;
                    }
                    if (doRequirementChecks) {
                        Requirement requirement = fish.getRequirement();
                        return requirement.meetsRequirements(context);
                    }
                    return true;
                })
                .toList();

        // if the config doesn't define any fish that can be fished in this biome.
        if (available.isEmpty()) {
            EvenMoreFish.getInstance().getLogger().warning("There are no fish of the rarity " + r.getId() + " that can be fished at (x=" + l.getX() + ", y=" + l.getY() + ", z=" + l.getZ() + ")");
            return null;
        }

        Fish returningFish;

        // checks whether weight calculations need doing for fish
        returningFish = getRandomWeightedFish(available, boostRate, boostedFish);

        if (Competition.isActive() || !MainConfig.getInstance().isFishCatchOnlyInCompetition() || (EvenMoreFish.getInstance().isRaritiesCompCheckExempt() && returningFish.isCompExemptFish())) {
            return returningFish;
        } else {
            return null;
        }
    }

    public TreeMap<String, Rarity> getRarityMap() {
        return rarityMap;
    }

    // Loading things

    private void logLoadedItems() {
        int allFish = 0;
        for (Rarity rarity : rarityMap.values()) {
            allFish += rarity.getOriginalFishList().size();
        }
        EvenMoreFish.getInstance().getLogger().info("Loaded FishManager with " + rarityMap.size() + " Rarities and " + allFish + " Fish.");
    }

    private void loadRarities() {

        rarityMap.clear();

        File raritiesFolder = new File(EvenMoreFish.getInstance().getDataFolder(), "rarities");
        if (!raritiesFolder.exists()) {
            loadDefaultFiles(raritiesFolder);
        }
        regenExampleFile(raritiesFolder);
        List<File> rarityFiles = FileUtil.getFilesInDirectory(raritiesFolder, true, true);

        if (rarityFiles.isEmpty()) {
            return;
        }

        rarityFiles.forEach(file -> {
            EvenMoreFish.getInstance().debug("Loading " + file.getName() + " rarity");
            Rarity rarity;
            try {
                rarity = new Rarity(file);
                // Skip invalid configs.
            } catch (InvalidConfigurationException exception) {
                return;
            }
            // Skip disabled files.
            if (rarity.isDisabled()) {
                return;
            }
            // Skip duplicate IDs
            String id = rarity.getId();
            if (rarityMap.containsKey(id)) {
                EvenMoreFish.getInstance().getLogger().warning("A rarity with the id: " + id + " already exists! Skipping.");
                return;
            }
            rarityMap.put(id, rarity);
        });
    }

    private void regenExampleFile(@NotNull File targetDirectory) {
        new File(targetDirectory, "_example.yml").delete();
        FileUtil.loadFileOrResource(targetDirectory, "_example.yml", "rarities/_example.yml", EvenMoreFish.getInstance());
    }

    private void loadDefaultFiles(@NotNull File targetDirectory) {
        EvenMoreFish.getInstance().getLogger().info("Loading default rarity configs.");
        FileUtil.loadFileOrResource(targetDirectory, "common.yml", "rarities/common.yml", EvenMoreFish.getInstance());
        FileUtil.loadFileOrResource(targetDirectory, "junk.yml", "rarities/junk.yml", EvenMoreFish.getInstance());
        FileUtil.loadFileOrResource(targetDirectory, "rare.yml", "rarities/rare.yml", EvenMoreFish.getInstance());
        FileUtil.loadFileOrResource(targetDirectory, "epic.yml", "rarities/epic.yml", EvenMoreFish.getInstance());
        FileUtil.loadFileOrResource(targetDirectory, "legendary.yml", "rarities/legendary.yml", EvenMoreFish.getInstance());
    }

}