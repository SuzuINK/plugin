package fr.akashisei.reveildeschasseurs.utils;

import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class SkinInstaller {
    private final Plugin plugin;
    private final Logger logger;

    public SkinInstaller(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public void installSkins() {
        // Créer le dossier skins s'il n'existe pas
        File skinsDir = new File(plugin.getDataFolder(), "skins");
        if (!skinsDir.exists()) {
            skinsDir.mkdirs();
        }

        // Charger la configuration des skins
        File configFile = new File(plugin.getDataFolder(), "skins/sololeveling_skins.yml");
        if (!configFile.exists()) {
            logger.warning("Fichier de configuration des skins non trouvé!");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // Installer chaque skin
        for (String skinType : config.getKeys(false)) {
            String url = config.getString(skinType + ".url");
            if (url != null) {
                downloadAndProcessSkin(url, skinType, skinsDir);
            }
        }
    }

    private void downloadAndProcessSkin(String url, String skinType, File skinsDir) {
        try {
            // Télécharger l'image
            BufferedImage originalImage = ImageIO.read(new URL(url));
            
            // Créer une nouvelle image avec les bonnes dimensions
            BufferedImage processedImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = processedImage.createGraphics();
            
            // Dessiner l'image originale
            g2d.drawImage(originalImage, 0, 0, 64, 64, null);
            
            // Appliquer les modifications selon le type de skin
            switch (skinType.toLowerCase()) {
                case "goblin":
                    applyGoblinModifications(processedImage);
                    break;
                case "goblin_elite":
                    applyGoblinEliteModifications(processedImage);
                    break;
                case "orc_warrior":
                    applyOrcWarriorModifications(processedImage);
                    break;
                case "dark_mage":
                    applyDarkMageModifications(processedImage);
                    break;
                case "monarch":
                    applyMonarchModifications(processedImage);
                    break;
                case "dungeon_portal":
                    applyPortalModifications(processedImage);
                    break;
                case "rank_portal":
                    applyRankPortalModifications(processedImage);
                    break;
            }
            
            g2d.dispose();
            
            // Sauvegarder l'image
            File outputFile = new File(skinsDir, skinType + ".png");
            ImageIO.write(processedImage, "PNG", outputFile);
            
            logger.info("Skin " + skinType + " installé avec succès!");
            
        } catch (IOException e) {
            logger.severe("Erreur lors de l'installation du skin " + skinType + ": " + e.getMessage());
        }
    }

    private void applyGoblinModifications(BufferedImage img) {
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                Color pixelColor = new Color(img.getRGB(x, y), true);
                if (isBodyPixel(x, y)) {
                    // Assombrir la teinte verte
                    Color newColor = new Color(164, 208, 60, pixelColor.getAlpha());
                    img.setRGB(x, y, newColor.getRGB());
                }
            }
        }
    }

    private void applyGoblinEliteModifications(BufferedImage img) {
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                Color pixelColor = new Color(img.getRGB(x, y), true);
                if (isArmorPixel(x, y)) {
                    // Ajouter des détails dorés
                    Color newColor = new Color(218, 165, 32, pixelColor.getAlpha());
                    img.setRGB(x, y, newColor.getRGB());
                }
            }
        }
    }

    private void applyOrcWarriorModifications(BufferedImage img) {
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                Color pixelColor = new Color(img.getRGB(x, y), true);
                if (isBodyPixel(x, y)) {
                    // Ajouter des motifs tribaux
                    Color newColor = new Color(139, 69, 19, pixelColor.getAlpha());
                    img.setRGB(x, y, newColor.getRGB());
                }
            }
        }
    }

    private void applyDarkMageModifications(BufferedImage img) {
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                Color pixelColor = new Color(img.getRGB(x, y), true);
                if (isRobePixel(x, y)) {
                    // Ajouter des runes violettes
                    Color newColor = new Color(138, 43, 226, pixelColor.getAlpha());
                    img.setRGB(x, y, newColor.getRGB());
                }
            }
        }
    }

    private void applyMonarchModifications(BufferedImage img) {
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                Color pixelColor = new Color(img.getRGB(x, y), true);
                if (isArmorPixel(x, y)) {
                    // Ajouter des détails royaux
                    Color newColor = new Color(218, 165, 32, pixelColor.getAlpha());
                    img.setRGB(x, y, newColor.getRGB());
                }
            }
        }
    }

    private void applyPortalModifications(BufferedImage img) {
        // Couleurs du portail normal (bleu)
        Color portalColor = new Color(0, 0, 255); // Bleu
        Color runeColor = new Color(0, 191, 255); // Bleu ciel
        Color auraColor = new Color(0, 0, 128); // Bleu foncé
        Color edgeColor = new Color(135, 206, 235); // Bleu clair

        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                Color pixelColor = new Color(img.getRGB(x, y), true);
                
                // Calculer la distance par rapport au centre avec un ratio différent pour créer un ovale
                double centerX = 32;
                double centerY = 32;
                double ratioX = 0.7; // Ratio pour l'ovale horizontal
                double ratioY = 1.3; // Ratio pour l'ovale vertical
                double distance = Math.sqrt(Math.pow((x - centerX) * ratioX, 2) + Math.pow((y - centerY) * ratioY, 2));
                
                // Calculer l'angle pour l'effet vortex
                double angle = Math.atan2(y - centerY, x - centerX);
                double vortexEffect = Math.sin(angle * 12 + distance * 0.8) * 0.5 + 0.5;
                
                if (distance < 20) {
                    // Zone centrale du portail avec effet vortex
                    if (isRunePixel(x, y)) {
                        // Effet de runes en spirale
                        Color spiralColor = new Color(
                            (int)(runeColor.getRed() * vortexEffect),
                            (int)(runeColor.getGreen() * vortexEffect),
                            (int)(runeColor.getBlue() * vortexEffect),
                            pixelColor.getAlpha()
                        );
                        img.setRGB(x, y, spiralColor.getRGB());
                    } else {
                        // Effet de distorsion du portail
                        Color distortedColor = new Color(
                            (int)(portalColor.getRed() * (1 - vortexEffect * 0.3)),
                            (int)(portalColor.getGreen() * (1 - vortexEffect * 0.3)),
                            (int)(portalColor.getBlue() * (1 - vortexEffect * 0.3)),
                            pixelColor.getAlpha()
                        );
                        img.setRGB(x, y, distortedColor.getRGB());
                    }
                } else if (distance < 25) {
                    // Bordure avec effet de pulsation
                    Color pulsingEdge = new Color(
                        (int)(edgeColor.getRed() * (0.7 + vortexEffect * 0.3)),
                        (int)(edgeColor.getGreen() * (0.7 + vortexEffect * 0.3)),
                        (int)(edgeColor.getBlue() * (0.7 + vortexEffect * 0.3)),
                        pixelColor.getAlpha()
                    );
                    img.setRGB(x, y, pulsingEdge.getRGB());
                } else if (distance < 30) {
                    // Aura avec effet de vague
                    Color waveAura = new Color(
                        (int)(auraColor.getRed() * (0.5 + vortexEffect * 0.5)),
                        (int)(auraColor.getGreen() * (0.5 + vortexEffect * 0.5)),
                        (int)(auraColor.getBlue() * (0.5 + vortexEffect * 0.5)),
                        pixelColor.getAlpha()
                    );
                    img.setRGB(x, y, waveAura.getRGB());
                }
            }
        }
    }

    private void applyRankPortalModifications(BufferedImage img) {
        // Couleurs du portail de rang (rouge)
        Color portalColor = new Color(255, 0, 0); // Rouge
        Color runeColor = new Color(255, 69, 0); // Rouge-orange
        Color auraColor = new Color(139, 0, 0); // Rouge foncé
        Color edgeColor = new Color(255, 140, 0); // Orange

        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                Color pixelColor = new Color(img.getRGB(x, y), true);
                
                // Calculer la distance par rapport au centre avec un ratio différent pour créer un ovale
                double centerX = 32;
                double centerY = 32;
                double ratioX = 0.7; // Ratio pour l'ovale horizontal
                double ratioY = 1.3; // Ratio pour l'ovale vertical
                double distance = Math.sqrt(Math.pow((x - centerX) * ratioX, 2) + Math.pow((y - centerY) * ratioY, 2));
                
                // Calculer l'angle pour l'effet vortex
                double angle = Math.atan2(y - centerY, x - centerX);
                double vortexEffect = Math.sin(angle * 12 + distance * 0.8) * 0.5 + 0.5;
                
                if (distance < 20) {
                    // Zone centrale du portail avec effet vortex
                    if (isRunePixel(x, y)) {
                        // Effet de runes en spirale
                        Color spiralColor = new Color(
                            (int)(runeColor.getRed() * vortexEffect),
                            (int)(runeColor.getGreen() * vortexEffect),
                            (int)(runeColor.getBlue() * vortexEffect),
                            pixelColor.getAlpha()
                        );
                        img.setRGB(x, y, spiralColor.getRGB());
                    } else {
                        // Effet de distorsion du portail
                        Color distortedColor = new Color(
                            (int)(portalColor.getRed() * (1 - vortexEffect * 0.3)),
                            (int)(portalColor.getGreen() * (1 - vortexEffect * 0.3)),
                            (int)(portalColor.getBlue() * (1 - vortexEffect * 0.3)),
                            pixelColor.getAlpha()
                        );
                        img.setRGB(x, y, distortedColor.getRGB());
                    }
                } else if (distance < 25) {
                    // Bordure avec effet de pulsation
                    Color pulsingEdge = new Color(
                        (int)(edgeColor.getRed() * (0.7 + vortexEffect * 0.3)),
                        (int)(edgeColor.getGreen() * (0.7 + vortexEffect * 0.3)),
                        (int)(edgeColor.getBlue() * (0.7 + vortexEffect * 0.3)),
                        pixelColor.getAlpha()
                    );
                    img.setRGB(x, y, pulsingEdge.getRGB());
                } else if (distance < 30) {
                    // Aura avec effet de vague
                    Color waveAura = new Color(
                        (int)(auraColor.getRed() * (0.5 + vortexEffect * 0.5)),
                        (int)(auraColor.getGreen() * (0.5 + vortexEffect * 0.5)),
                        (int)(auraColor.getBlue() * (0.5 + vortexEffect * 0.5)),
                        pixelColor.getAlpha()
                    );
                    img.setRGB(x, y, waveAura.getRGB());
                }
            }
        }
    }

    private boolean isBodyPixel(int x, int y) {
        return y >= 8 && y < 20;
    }

    private boolean isArmorPixel(int x, int y) {
        return y >= 0 && y < 8;
    }

    private boolean isRobePixel(int x, int y) {
        return y >= 20;
    }

    private boolean isRunePixel(int x, int y) {
        // Créer un motif de runes en spirale plus complexe
        double centerX = 32;
        double centerY = 32;
        double angle = Math.atan2(y - centerY, x - centerX);
        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        
        // Créer des runes en spirale avec effet de vortex
        double spiralEffect = Math.sin(angle * 16 + distance * 0.6) * 0.5 + 0.5;
        return (distance > 5 && distance < 20) && spiralEffect > 0.7;
    }
} 