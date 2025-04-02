package fr.akashisei.reveildeschasseurs.listeners;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ShopMenuListener implements Listener {
    private final ReveilDesChasseurs plugin;

    public ShopMenuListener(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (!title.equals("§6Boutique")) {
            return;
        }

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) {
            return;
        }

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

        switch (event.getSlot()) {
            case 19: // Armes
                // TODO: Ouvrir le menu des armes
                player.sendMessage("§aCette fonctionnalité sera bientôt disponible !");
                break;
            case 21: // Armures
                // TODO: Ouvrir le menu des armures
                player.sendMessage("§aCette fonctionnalité sera bientôt disponible !");
                break;
            case 23: // Potions
                // TODO: Ouvrir le menu des potions
                player.sendMessage("§aCette fonctionnalité sera bientôt disponible !");
                break;
            case 25: // Spécial
                // TODO: Ouvrir le menu des objets spéciaux
                player.sendMessage("§aCette fonctionnalité sera bientôt disponible !");
                break;
        }
    }
} 