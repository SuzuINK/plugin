package fr.akashisei.reveildeschasseurs.menus;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ShopMenu {
    private final ReveilDesChasseurs plugin;

    public ShopMenu(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    public void openMainMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 54, "§6Boutique");
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

        // Informations du joueur
        ItemStack infoItem = createItem(Material.PLAYER_HEAD, "§6Informations",
                "§7Argent : §e" + playerData.getMoney(),
                "§7Niveau : §e" + playerData.getHunterLevel(),
                "§7Classe : §e" + playerData.getClassName(),
                "§7Rang : §e" + playerData.getRankName());
        menu.setItem(4, infoItem);

        // Catégories
        menu.setItem(19, createItem(Material.DIAMOND_SWORD, "§6Armes", "§7Cliquez pour voir les armes"));
        menu.setItem(21, createItem(Material.DIAMOND_CHESTPLATE, "§6Armures", "§7Cliquez pour voir les armures"));
        menu.setItem(23, createItem(Material.POTION, "§6Potions", "§7Cliquez pour voir les potions"));
        menu.setItem(25, createItem(Material.ENCHANTED_BOOK, "§6Spécial", "§7Cliquez pour voir les objets spéciaux"));

        // Bordure décorative
        ItemStack borderItem = createItem(Material.BLACK_STAINED_GLASS_PANE, "§r");
        for (int i = 0; i < 9; i++) {
            menu.setItem(i, borderItem);
            menu.setItem(45 + i, borderItem);
        }
        for (int i = 0; i < 6; i++) {
            menu.setItem(i * 9, borderItem);
            menu.setItem(i * 9 + 8, borderItem);
        }

        player.openInventory(menu);
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }
        item.setItemMeta(meta);
        return item;
    }
} 