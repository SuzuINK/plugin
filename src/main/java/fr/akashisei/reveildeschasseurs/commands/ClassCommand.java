package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ClassCommand implements CommandExecutor {
    private final ReveilDesChasseurs plugin;
    private final Map<String, Integer> classLevelRequirements;
    private final Map<UUID, Long> lastClassChange;
    private final Map<UUID, String> pendingClassChanges;
    private static final long CLASS_CHANGE_COOLDOWN = TimeUnit.HOURS.toMillis(24); // 24 heures de cooldown

    public ClassCommand(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.lastClassChange = new HashMap<>();
        this.pendingClassChanges = new HashMap<>();
        this.classLevelRequirements = new HashMap<>();
        initializeClassRequirements();
    }

    private void initializeClassRequirements() {
        classLevelRequirements.put("guerrier", 1);  // Classe de départ
        classLevelRequirements.put("archer", 5);    // Niveau 5 requis
        classLevelRequirements.put("mage", 10);     // Niveau 10 requis
        classLevelRequirements.put("assassin", 15); // Niveau 15 requis
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        Player player = (Player) sender;
        
        // Vérification des permissions
        if (!player.hasPermission("reveildeschasseurs.command.class")) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'utiliser cette commande.");
            return true;
        }

        PlayerData playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);

        if (args.length == 0) {
            showClassInfo(player, playerData);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        
        if (subCommand.equals("confirm")) {
            handleConfirmation(player, playerData);
            return true;
        }
        
        if (subCommand.equals("cancel")) {
            cancelClassChange(player);
            return true;
        }

        // Si ce n'est pas une sous-commande, c'est probablement un nom de classe
        switch (subCommand) {
            case "guerrier":
            case "archer":
            case "mage":
            case "assassin":
                initiateClassChange(player, playerData, subCommand);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Commande invalide. Utilisez :");
                player.sendMessage(ChatColor.YELLOW + "/class - Voir les informations");
                player.sendMessage(ChatColor.YELLOW + "/class <classe> - Choisir une classe");
                player.sendMessage(ChatColor.YELLOW + "/class confirm - Confirmer le changement");
                player.sendMessage(ChatColor.YELLOW + "/class cancel - Annuler le changement");
                break;
        }

        return true;
    }

    private void showClassInfo(Player player, PlayerData playerData) {
        String currentClass = playerData.getPlayerClass();
        int playerLevel = playerData.getLevel();

        player.sendMessage(ChatColor.GOLD + "=== Informations sur votre classe ===");
        player.sendMessage(ChatColor.YELLOW + "Classe actuelle : " + ChatColor.WHITE + currentClass);
        player.sendMessage(ChatColor.YELLOW + "Niveau actuel : " + ChatColor.WHITE + playerLevel);
        player.sendMessage(ChatColor.YELLOW + "Classes disponibles :");
        
        // Afficher les classes avec leurs prérequis
        for (Map.Entry<String, Integer> entry : classLevelRequirements.entrySet()) {
            String className = entry.getKey();
            int reqLevel = entry.getValue();
            ChatColor color = playerLevel >= reqLevel ? ChatColor.GREEN : ChatColor.RED;
            player.sendMessage(color + "- " + capitalize(className) + " (Niveau requis: " + reqLevel + ")");
        }

        // Afficher le cooldown restant si applicable
        Long lastChange = lastClassChange.get(player.getUniqueId());
        if (lastChange != null) {
            long timeLeft = (lastChange + CLASS_CHANGE_COOLDOWN) - System.currentTimeMillis();
            if (timeLeft > 0) {
                long hoursLeft = TimeUnit.MILLISECONDS.toHours(timeLeft);
                player.sendMessage(ChatColor.YELLOW + "Temps restant avant prochain changement : " + 
                    ChatColor.WHITE + hoursLeft + " heures");
            }
        }

        player.sendMessage(ChatColor.YELLOW + "Pour changer de classe : /class <nom_de_la_classe>");
        player.sendMessage(ChatColor.YELLOW + "Pour confirmer : /class confirm");
        player.sendMessage(ChatColor.YELLOW + "Pour annuler : /class cancel");
    }

    private void initiateClassChange(Player player, PlayerData playerData, String className) {
        // Vérifier si le joueur a déjà une demande en attente
        if (pendingClassChanges.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Vous avez déjà une demande de changement de classe en attente.");
            player.sendMessage(ChatColor.YELLOW + "Utilisez '/class confirm' pour confirmer ou '/class cancel' pour annuler.");
            return;
        }

        // Vérifier le cooldown
        Long lastChange = lastClassChange.get(player.getUniqueId());
        if (lastChange != null) {
            long timeLeft = (lastChange + CLASS_CHANGE_COOLDOWN) - System.currentTimeMillis();
            if (timeLeft > 0) {
                long hoursLeft = TimeUnit.MILLISECONDS.toHours(timeLeft);
                player.sendMessage(ChatColor.RED + "Vous devez attendre encore " + hoursLeft + 
                    " heures avant de pouvoir changer de classe.");
                return;
            }
        }

        // Vérifier le niveau requis
        int requiredLevel = classLevelRequirements.get(className);
        if (playerData.getLevel() < requiredLevel) {
            player.sendMessage(ChatColor.RED + "Vous devez être niveau " + requiredLevel + 
                " pour devenir " + className + ".");
            return;
        }

        // Vérifier si c'est la même classe
        if (playerData.getPlayerClass().equalsIgnoreCase(className)) {
            player.sendMessage(ChatColor.RED + "Vous êtes déjà un " + className + ".");
            return;
        }

        // Stocker la demande de changement
        pendingClassChanges.put(player.getUniqueId(), className);

        // Envoyer le message de confirmation
        player.sendMessage(ChatColor.GOLD + "=== Changement de classe ===");
        player.sendMessage(ChatColor.YELLOW + "Vous êtes sur le point de devenir un " + className + ".");
        player.sendMessage(ChatColor.RED + "ATTENTION : Ce changement a un cooldown de 24 heures !");
        player.sendMessage(ChatColor.YELLOW + "Tapez '/class confirm' pour confirmer ou '/class cancel' pour annuler.");
    }

    private void handleConfirmation(Player player, PlayerData playerData) {
        String newClass = pendingClassChanges.get(player.getUniqueId());
        if (newClass == null) {
            player.sendMessage(ChatColor.RED + "Vous n'avez aucune demande de changement de classe en attente.");
            return;
        }

        String oldClass = playerData.getPlayerClass();
        playerData.setPlayerClass(newClass);
        lastClassChange.put(player.getUniqueId(), System.currentTimeMillis());
        pendingClassChanges.remove(player.getUniqueId());

        // Sauvegarder les données du joueur
        plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());

        // Envoyer les messages de confirmation
        player.sendMessage(ChatColor.GREEN + "Votre classe a été changée de " + oldClass + " à " + newClass + ".");
        player.sendMessage(ChatColor.YELLOW + "Prochain changement possible dans 24 heures.");

        // Donner les items de départ de la classe si nécessaire
        giveClassStarterItems(player, newClass);
    }

    private void cancelClassChange(Player player) {
        if (pendingClassChanges.remove(player.getUniqueId()) != null) {
            player.sendMessage(ChatColor.YELLOW + "Changement de classe annulé.");
        } else {
            player.sendMessage(ChatColor.RED + "Vous n'avez aucune demande de changement de classe en attente.");
        }
    }

    private void giveClassStarterItems(Player player, String className) {
        // Vider l'inventaire du joueur des items de classe précédents
        player.getInventory().clear();

        // Donner les items selon la classe
        switch (className.toLowerCase()) {
            case "guerrier":
                player.getInventory().addItem(
                    new ItemStack(Material.IRON_SWORD),
                    new ItemStack(Material.IRON_HELMET),
                    new ItemStack(Material.IRON_CHESTPLATE),
                    new ItemStack(Material.IRON_LEGGINGS),
                    new ItemStack(Material.IRON_BOOTS),
                    new ItemStack(Material.SHIELD)
                );
                break;
            case "archer":
                player.getInventory().addItem(
                    new ItemStack(Material.BOW),
                    new ItemStack(Material.ARROW, 64),
                    new ItemStack(Material.LEATHER_HELMET),
                    new ItemStack(Material.LEATHER_CHESTPLATE),
                    new ItemStack(Material.LEATHER_LEGGINGS),
                    new ItemStack(Material.LEATHER_BOOTS)
                );
                break;
            case "mage":
                player.getInventory().addItem(
                    new ItemStack(Material.BLAZE_ROD),
                    new ItemStack(Material.BOOK),
                    new ItemStack(Material.GOLDEN_HELMET),
                    new ItemStack(Material.GOLDEN_CHESTPLATE),
                    new ItemStack(Material.GOLDEN_LEGGINGS),
                    new ItemStack(Material.GOLDEN_BOOTS)
                );
                break;
            case "assassin":
                player.getInventory().addItem(
                    new ItemStack(Material.IRON_SWORD),
                    new ItemStack(Material.CHAINMAIL_HELMET),
                    new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                    new ItemStack(Material.CHAINMAIL_LEGGINGS),
                    new ItemStack(Material.CHAINMAIL_BOOTS),
                    new ItemStack(Material.ENDER_PEARL, 16)
                );
                break;
        }

        // Donner des items de base à toutes les classes
        player.getInventory().addItem(
            new ItemStack(Material.COOKED_BEEF, 16)
        );

        // Ajouter des potions de soin instantanée
        ItemStack healingPotion = new ItemStack(Material.POTION, 3);
        org.bukkit.inventory.meta.PotionMeta potionMeta = (org.bukkit.inventory.meta.PotionMeta) healingPotion.getItemMeta();
        potionMeta.setBasePotionData(new org.bukkit.potion.PotionData(org.bukkit.potion.PotionType.INSTANT_HEAL));
        healingPotion.setItemMeta(potionMeta);
        player.getInventory().addItem(healingPotion);

        player.sendMessage(ChatColor.GREEN + "Vous avez reçu votre équipement de " + className + ".");
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
} 