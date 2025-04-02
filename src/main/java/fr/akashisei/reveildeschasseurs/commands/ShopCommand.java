package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.menus.ShopMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {
    private final ReveilDesChasseurs plugin;
    private final ShopMenu shopMenu;

    public ShopCommand(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.shopMenu = new ShopMenu(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        if (!sender.hasPermission("reveildeschasseurs.shop")) {
            sender.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;
        shopMenu.openMainMenu(player);
        return true;
    }
} 