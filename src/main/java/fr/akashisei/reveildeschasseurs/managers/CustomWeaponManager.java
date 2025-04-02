package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Weapon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomWeaponManager extends WeaponManager {
    public CustomWeaponManager(ReveilDesChasseurs plugin) {
        super(plugin);
    }

    @Override
    public boolean giveWeapon(Player player, Weapon weapon) {
        if (player.getLevel() < weapon.getRequiredLevel()) {
            player.sendMessage("§cVous n'avez pas le niveau requis pour utiliser cette arme !");
            return false;
        }

        ItemStack itemStack = weapon.createItemStack();
        player.getInventory().addItem(itemStack);
        player.sendMessage("§aVous avez reçu l'arme : " + weapon.getName());
        return true;
    }

    @Override
    public boolean upgradeWeapon(Player player, Weapon weapon) {
        // TODO: Implémenter la logique d'amélioration des armes
        return false;
    }
} 