package fr.akashisei.reveildeschasseurs.menus;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import fr.akashisei.reveildeschasseurs.quests.Quest;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuManager {
    private final ReveilDesChasseurs plugin;

    public MenuManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    public void openMainMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&6Menu Principal"));
        
        // Bouton des donjons
        ItemStack dungeonsButton = createItem(Material.NETHER_BRICK, "&cDonjons", Arrays.asList(
            "&7Accédez à la liste des donjons",
            "&7disponibles pour votre niveau."
        ));
        menu.setItem(11, dungeonsButton);

        // Bouton du profil
        ItemStack profileButton = createItem(Material.PLAYER_HEAD, "&aProfil", Arrays.asList(
            "&7Consultez votre profil",
            "&7et vos statistiques."
        ));
        menu.setItem(13, profileButton);

        // Bouton des quêtes
        ItemStack questsButton = createItem(Material.BOOK, "&eQuêtes", Arrays.asList(
            "&7Voir vos quêtes en cours",
            "&7et disponibles."
        ));
        menu.setItem(15, questsButton);

        player.openInventory(menu);
    }

    public void openDungeonsMenu(Player player) {
        List<Dungeon> availableDungeons = plugin.getDungeonManager().getAvailableDungeons(player);
        DungeonMenu dungeonMenu = new DungeonMenu(plugin, player, availableDungeons);
        player.openInventory(dungeonMenu.getInventory());
    }

    public void openProfileMenu(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

        Inventory menu = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&aProfil de " + player.getName()));
        
        // Tête du joueur
        ItemStack playerHead = createItem(Material.PLAYER_HEAD, "&6" + player.getName(), Arrays.asList(
            "&fNiveau: &e" + playerData.getLevel(),
            "&fXP: &e" + playerData.getExperience() + "&7/&e" + playerData.getNextLevelExperience(),
            "&fClasse: &e" + playerData.getClassName(),
            "&fRang: &e" + playerData.getRankName()
        ));
        menu.setItem(13, playerHead);

        // Bouton retour
        ItemStack backButton = createItem(Material.BARRIER, "&cRetour", Arrays.asList(
            "&7Retourner au menu principal"
        ));
        menu.setItem(26, backButton);

        player.openInventory(menu);
    }

    public void openQuestsMenu(Player player) {
        List<Quest> availableQuests = plugin.getQuestManager().getAvailableQuests(player);
        QuestMenu questMenu = new QuestMenu(plugin, player, availableQuests);
        questMenu.open(player);
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(coloredLore);
            item.setItemMeta(meta);
        }
        return item;
    }
} 