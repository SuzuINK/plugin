package fr.akashisei.reveildeschasseurs.models;

import org.bukkit.Material;
import java.util.Arrays;
import java.util.List;

public enum DungeonTheme {
    MINE(
        "Mine Abandonnée",
        Arrays.asList(Material.STONE, Material.COBBLESTONE, Material.ANDESITE),
        Arrays.asList(Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE),
        Material.LANTERN,
        Arrays.asList("ZOMBIE_MINEUR", "SQUELETTE_MINEUR", "CREEPER_CRISTAL"),
        "GOLEM_DE_PIERRE"
    ),
    TEMPLE(
        "Temple Ancien",
        Arrays.asList(Material.SANDSTONE, Material.SMOOTH_SANDSTONE, Material.CUT_SANDSTONE),
        Arrays.asList(Material.GOLD_BLOCK, Material.EMERALD_BLOCK),
        Material.REDSTONE_TORCH,
        Arrays.asList("MOMIE", "GARDIEN_TEMPLE", "SCARABEE_GEANT"),
        "PHARAON_MAUDIT"
    ),
    FORET(
        "Forêt Mystique",
        Arrays.asList(Material.OAK_LOG, Material.SPRUCE_LOG, Material.MOSS_BLOCK),
        Arrays.asList(Material.EMERALD_ORE, Material.DIAMOND_ORE),
        Material.SHROOMLIGHT,
        Arrays.asList("ESPRIT_FORET", "LOUP_ANCESTRAL", "ARAIGNEE_VENIMEUSE"),
        "ANCIEN_GARDIEN"
    ),
    NETHER(
        "Citadelle Infernale",
        Arrays.asList(Material.NETHER_BRICKS, Material.BLACKSTONE, Material.BASALT),
        Arrays.asList(Material.ANCIENT_DEBRIS, Material.GILDED_BLACKSTONE),
        Material.SOUL_LANTERN,
        Arrays.asList("BLAZE_ELITE", "PIGLIN_BRUTE", "DEMON_MINEUR"),
        "SEIGNEUR_DEMON"
    ),
    OCEAN(
        "Cité Engloutie",
        Arrays.asList(Material.PRISMARINE, Material.PRISMARINE_BRICKS, Material.DARK_PRISMARINE),
        Arrays.asList(Material.SEA_LANTERN, Material.CONDUIT),
        Material.SEA_LANTERN,
        Arrays.asList("GARDIEN_ANCIEN", "SIRENE_CORROMPUE", "REQUIN_ABYSSAL"),
        "KRAKEN"
    );

    private final String displayName;
    private final List<Material> mainBlocks;
    private final List<Material> decorativeBlocks;
    private final Material lightSource;
    private final List<String> commonMobs;
    private final String bossMob;

    DungeonTheme(String displayName, List<Material> mainBlocks, List<Material> decorativeBlocks, 
                 Material lightSource, List<String> commonMobs, String bossMob) {
        this.displayName = displayName;
        this.mainBlocks = mainBlocks;
        this.decorativeBlocks = decorativeBlocks;
        this.lightSource = lightSource;
        this.commonMobs = commonMobs;
        this.bossMob = bossMob;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<Material> getMainBlocks() {
        return mainBlocks;
    }

    public List<Material> getDecorativeBlocks() {
        return decorativeBlocks;
    }

    public Material getLightSource() {
        return lightSource;
    }

    public List<String> getCommonMobs() {
        return commonMobs;
    }

    public String getBossMob() {
        return bossMob;
    }
} 