package fr.akashisei.reveildeschasseurs.menus;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MainMenu extends Menu {
    public MainMenu(ReveilDesChasseurs plugin) {
        super(plugin, ChatColor.translateAlternateColorCodes('&', "&6Menu Principal"), 27);
    }

    @Override
    public void update(Player player) {
        // Bouton Donjons
        setItem(11, createMenuItem(
            Material.NETHER_BRICK,
            ChatColor.RED + "Donjons",
            Arrays.asList(
                ChatColor.GRAY + "Accédez à la liste des donjons",
                ChatColor.GRAY + "disponibles pour votre niveau."
            )
        ));

        // Bouton Profil
        setItem(13, createMenuItem(
            Material.PLAYER_HEAD,
            ChatColor.GREEN + "Profil",
            Arrays.asList(
                ChatColor.GRAY + "Consultez votre profil",
                ChatColor.GRAY + "et vos statistiques."
            )
        ));

        // Bouton Quêtes
        setItem(15, createMenuItem(
            Material.BOOK,
            ChatColor.YELLOW + "Quêtes",
            Arrays.asList(
                ChatColor.GRAY + "Voir vos quêtes en cours",
                ChatColor.GRAY + "et disponibles."
            )
        ));
    }

    @Override
    public void onClick(Player player, int slot) {
        switch (slot) {
            case 11: // Donjons
                plugin.getMenuManager().openDungeonsMenu(player);
                break;
            case 13: // Profil
                plugin.getMenuManager().openProfileMenu(player);
                break;
            case 15: // Quêtes
                plugin.getMenuManager().openQuestsMenu(player);
                break;
        }
    }

    private ItemStack createMenuItem(Material material, String name, java.util.List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
} 