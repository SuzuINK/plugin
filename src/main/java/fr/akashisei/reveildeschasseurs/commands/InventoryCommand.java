package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InventoryCommand implements CommandExecutor {
    private final ReveilDesChasseurs plugin;

    public InventoryCommand(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "save":
                saveInventory(player);
                break;
            case "load":
                loadInventory(player);
                break;
            case "clear":
                clearInventory(player);
                break;
            default:
                showHelp(player);
                break;
        }

        return true;
    }

    private void showHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Commandes d'inventaire ===");
        player.sendMessage(ChatColor.YELLOW + "/inventory save " + ChatColor.GRAY + "- Sauvegarde votre inventaire");
        player.sendMessage(ChatColor.YELLOW + "/inventory load " + ChatColor.GRAY + "- Charge votre inventaire sauvegardé");
        player.sendMessage(ChatColor.YELLOW + "/inventory clear " + ChatColor.GRAY + "- Vide votre inventaire");
    }

    private void saveInventory(Player player) {
        plugin.getInventoryManager().savePlayerInventory(player);
        player.sendMessage(ChatColor.GREEN + "Votre inventaire a été sauvegardé.");
    }

    private void loadInventory(Player player) {
        plugin.getInventoryManager().loadPlayerInventory(player);
        player.sendMessage(ChatColor.GREEN + "Votre inventaire a été chargé.");
    }

    private void clearInventory(Player player) {
        plugin.getInventoryManager().clearPlayerInventory(player);
        player.sendMessage(ChatColor.GREEN + "Votre inventaire a été vidé.");
    }
} 