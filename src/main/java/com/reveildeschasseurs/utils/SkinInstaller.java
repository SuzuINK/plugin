package com.reveildeschasseurs.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class SkinInstaller {
    private final Plugin plugin;
    private final File skinsFile;
    private FileConfiguration skinsConfig;
    private final Map<String, String> skinCache;

    public SkinInstaller(Plugin plugin) {
        this.plugin = plugin;
        this.skinsFile = new File(plugin.getDataFolder(), "skins.yml");
        this.skinCache = new HashMap<>();
        loadConfig();
    }

    private void loadConfig() {
        if (!skinsFile.exists()) {
            plugin.saveResource("skins.yml", false);
        }
        skinsConfig = YamlConfiguration.loadConfiguration(skinsFile);
    }

    public String getSkinUrl(String type, String name, String rarity) {
        String path = type + "." + name + "." + rarity;
        String url = skinsConfig.getString(path);
        
        if (url == null) {
            plugin.getLogger().warning("Skin non trouvé: " + path);
            return null;
        }
        
        return url;
    }

    public void downloadAndInstallSkin(String type, String name, String rarity) {
        String url = getSkinUrl(type, name, rarity);
        if (url == null) return;

        String fileName = type + "_" + name + "_" + rarity + ".png";
        Path skinPath = Paths.get(plugin.getDataFolder().getPath(), "skins", fileName);

        try {
            Files.createDirectories(skinPath.getParent());
            
            if (!Files.exists(skinPath)) {
                URL skinUrl = new URL(url);
                Files.copy(skinUrl.openStream(), skinPath);
                plugin.getLogger().info("Skin téléchargé avec succès: " + fileName);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Erreur lors du téléchargement du skin: " + fileName, e);
        }
    }

    public void installAllSkins() {
        for (String type : skinsConfig.getConfigurationSection("").getKeys(false)) {
            for (String name : skinsConfig.getConfigurationSection(type).getKeys(false)) {
                for (String rarity : skinsConfig.getConfigurationSection(type + "." + name).getKeys(false)) {
                    downloadAndInstallSkin(type, name, rarity);
                }
            }
        }
    }

    public String getCachedSkinPath(String type, String name, String rarity) {
        String key = type + "_" + name + "_" + rarity;
        return skinCache.computeIfAbsent(key, k -> {
            String fileName = k + ".png";
            return new File(plugin.getDataFolder(), "skins/" + fileName).getPath();
        });
    }
} 