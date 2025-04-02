package fr.akashisei.reveildeschasseurs.utils;

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

    public void installSkins() {
        File skinsFolder = new File(plugin.getDataFolder(), "skins");
        if (!skinsFolder.exists()) {
            skinsFolder.mkdirs();
        }

        for (String type : skinsConfig.getKeys(false)) {
            for (String name : skinsConfig.getConfigurationSection(type).getKeys(false)) {
                for (String rarity : skinsConfig.getConfigurationSection(type + "." + name).getKeys(false)) {
                    String url = getSkinUrl(type, name, rarity);
                    if (url != null) {
                        downloadSkin(url, type, name, rarity);
                    }
                }
            }
        }
    }

    private void downloadSkin(String url, String type, String name, String rarity) {
        try {
            String fileName = type + "_" + name + "_" + rarity + ".png";
            Path targetPath = Paths.get(plugin.getDataFolder().getPath(), "skins", fileName);
            
            if (!Files.exists(targetPath)) {
                plugin.getLogger().info("Téléchargement du skin: " + fileName);
                Files.copy(new URL(url).openStream(), targetPath);
                plugin.getLogger().info("Skin téléchargé avec succès: " + fileName);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Erreur lors du téléchargement du skin: " + e.getMessage());
        }
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
} 