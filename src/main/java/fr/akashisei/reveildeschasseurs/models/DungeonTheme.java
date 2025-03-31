package fr.akashisei.reveildeschasseurs.models;

import org.bukkit.Material;
import java.util.Arrays;
import java.util.List;

public enum DungeonTheme {
    STONE(
        Material.STONE,
        Arrays.asList(Material.COBBLESTONE, Material.MOSSY_COBBLESTONE, Material.STONE_BRICKS),
        Arrays.asList(Material.IRON_BARS, Material.COBWEB),
        Arrays.asList(Material.TORCH, Material.LANTERN)
    ),
    NETHER(
        Material.NETHERRACK,
        Arrays.asList(Material.NETHER_BRICKS, Material.RED_NETHER_BRICKS, Material.BLACKSTONE),
        Arrays.asList(Material.CHAIN, Material.SOUL_SAND),
        Arrays.asList(Material.GLOWSTONE, Material.SHROOMLIGHT)
    ),
    ICE(
        Material.PACKED_ICE,
        Arrays.asList(Material.ICE, Material.BLUE_ICE, Material.SNOW_BLOCK),
        Arrays.asList(Material.POWDER_SNOW, Material.SNOW),
        Arrays.asList(Material.SEA_LANTERN, Material.ICE)
    ),
    JUNGLE(
        Material.MOSSY_COBBLESTONE,
        Arrays.asList(Material.MOSS_BLOCK, Material.MOSSY_STONE_BRICKS, Material.VINE),
        Arrays.asList(Material.OAK_LEAVES, Material.BAMBOO),
        Arrays.asList(Material.GLOW_BERRIES, Material.LANTERN)
    ),
    DESERT(
        Material.SANDSTONE,
        Arrays.asList(Material.SMOOTH_SANDSTONE, Material.CUT_SANDSTONE, Material.SAND),
        Arrays.asList(Material.CACTUS, Material.DEAD_BUSH),
        Arrays.asList(Material.TORCH, Material.GLOWSTONE)
    );

    private final Material mainMaterial;
    private final List<Material> decorativeMaterials;
    private final List<Material> obstacles;
    private final List<Material> lightSources;

    DungeonTheme(Material mainMaterial, List<Material> decorativeMaterials, List<Material> obstacles, List<Material> lightSources) {
        this.mainMaterial = mainMaterial;
        this.decorativeMaterials = decorativeMaterials;
        this.obstacles = obstacles;
        this.lightSources = lightSources;
    }

    public Material getMainMaterial() {
        return mainMaterial;
    }

    public List<Material> getDecorativeMaterials() {
        return decorativeMaterials;
    }

    public List<Material> getObstacles() {
        return obstacles;
    }

    public List<Material> getLightSources() {
        return lightSources;
    }
} 