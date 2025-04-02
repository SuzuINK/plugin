package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Weapon;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WeaponCommand implements CommandExecutor {
    private final ReveilDesChasseurs plugin;

    public WeaponCommand(ReveilDesChasseurs plugin) {
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
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list":
                listWeapons(player);
                break;
            case "info":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /weapon info <nom>");
                    return true;
                }
                showWeaponInfo(player, args[1]);
                break;
            case "give":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /weapon give <nom>");
                    return true;
                }
                giveWeapon(player, args[1]);
                break;
            case "upgrade":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /weapon upgrade <nom>");
                    return true;
                }
                upgradeWeapon(player, args[1]);
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Commandes d'armes ===");
        player.sendMessage(ChatColor.YELLOW + "/weapon list " + ChatColor.WHITE + "- Liste toutes les armes disponibles");
        player.sendMessage(ChatColor.YELLOW + "/weapon info <nom> " + ChatColor.WHITE + "- Affiche les informations d'une arme");
        player.sendMessage(ChatColor.YELLOW + "/weapon give <nom> " + ChatColor.WHITE + "- Donne une arme au joueur");
        player.sendMessage(ChatColor.YELLOW + "/weapon upgrade <nom> " + ChatColor.WHITE + "- Améliore une arme");
    }

    private void listWeapons(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Armes disponibles ===");
        for (Weapon weapon : plugin.getWeaponManager().getAllWeapons()) {
            player.sendMessage(ChatColor.YELLOW + "- " + weapon.getName() + 
                ChatColor.WHITE + " (Niveau " + weapon.getLevel() + ")");
        }
    }

    private void showWeaponInfo(Player player, String weaponName) {
        Weapon weapon = plugin.getWeaponManager().getWeapon(weaponName);
        if (weapon == null) {
            player.sendMessage(ChatColor.RED + "Cette arme n'existe pas.");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "=== Informations sur " + weapon.getName() + " ===");
        player.sendMessage(ChatColor.YELLOW + "Niveau requis: " + ChatColor.WHITE + weapon.getRequiredLevel());
        player.sendMessage(ChatColor.YELLOW + "Dégâts: " + ChatColor.WHITE + weapon.getDamage());
        player.sendMessage(ChatColor.YELLOW + "Durabilité: " + ChatColor.WHITE + weapon.getDurability());
        player.sendMessage(ChatColor.YELLOW + "Prix: " + ChatColor.WHITE + weapon.getPrice() + " pièces");
    }

    private void giveWeapon(Player player, String weaponName) {
        Weapon weapon = plugin.getWeaponManager().getWeapon(weaponName);
        if (weapon == null) {
            player.sendMessage(ChatColor.RED + "Cette arme n'existe pas.");
            return;
        }

        if (plugin.getWeaponManager().giveWeapon(player, weapon)) {
            player.sendMessage(ChatColor.GREEN + "Vous avez reçu l'arme " + weapon.getName());
        } else {
            player.sendMessage(ChatColor.RED + "Vous ne pouvez pas recevoir cette arme.");
        }
    }

    private void upgradeWeapon(Player player, String weaponName) {
        Weapon weapon = plugin.getWeaponManager().getWeapon(weaponName);
        if (weapon == null) {
            player.sendMessage(ChatColor.RED + "Cette arme n'existe pas.");
            return;
        }

        if (plugin.getWeaponManager().upgradeWeapon(player, weapon)) {
            player.sendMessage(ChatColor.GREEN + "Votre arme " + weapon.getName() + " a été améliorée.");
        } else {
            player.sendMessage(ChatColor.RED + "Vous ne pouvez pas améliorer cette arme.");
        }
    }
} 