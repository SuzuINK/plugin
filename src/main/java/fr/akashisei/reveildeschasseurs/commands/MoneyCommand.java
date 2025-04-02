package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {
    private final ReveilDesChasseurs plugin;

    public MoneyCommand(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("reveildeschasseurs.money")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("messages.error.no_permission"));
            return true;
        }

        if (args.length < 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getConfigManager().getMessage("messages.error.player_only"));
                return true;
            }
            // Afficher l'argent du joueur
            Player player = (Player) sender;
            PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
            sender.sendMessage("§6Votre argent : §e" + playerData.getMoney() + " §6pièces");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give":
                if (args.length < 3) {
                    sender.sendMessage("§cUtilisation : /money give <joueur> <montant>");
                    return true;
                }
                handleGive(sender, args[1], args[2]);
                break;
            case "set":
                if (args.length < 3) {
                    sender.sendMessage("§cUtilisation : /money set <joueur> <montant>");
                    return true;
                }
                handleSet(sender, args[1], args[2]);
                break;
            case "info":
                if (args.length < 2) {
                    sender.sendMessage("§cUtilisation : /money info <joueur>");
                    return true;
                }
                handleInfo(sender, args[1]);
                break;
            default:
                sender.sendMessage("§cCommande inconnue. Utilisez /money [give|set|info]");
        }
        return true;
    }

    private void handleGive(CommandSender sender, String targetName, String amountStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cJoueur introuvable : " + targetName);
            return;
        }

        try {
            int amount = Integer.parseInt(amountStr);
            if (amount <= 0) {
                sender.sendMessage("§cLe montant doit être positif");
                return;
            }

            PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(target.getUniqueId());
            playerData.setMoney(playerData.getMoney() + amount);
            sender.sendMessage("§aVous avez donné §e" + amount + " §apièces à §e" + target.getName());
            target.sendMessage("§aVous avez reçu §e" + amount + " §apièces");
        } catch (NumberFormatException e) {
            sender.sendMessage("§cMontant invalide : " + amountStr);
        }
    }

    private void handleSet(CommandSender sender, String targetName, String amountStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cJoueur introuvable : " + targetName);
            return;
        }

        try {
            int amount = Integer.parseInt(amountStr);
            if (amount < 0) {
                sender.sendMessage("§cLe montant ne peut pas être négatif");
                return;
            }

            PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(target.getUniqueId());
            playerData.setMoney(amount);
            sender.sendMessage("§aVous avez défini l'argent de §e" + target.getName() + " §aà §e" + amount + " §apièces");
            target.sendMessage("§aVotre argent a été défini à §e" + amount + " §apièces");
        } catch (NumberFormatException e) {
            sender.sendMessage("§cMontant invalide : " + amountStr);
        }
    }

    private void handleInfo(CommandSender sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cJoueur introuvable : " + targetName);
            return;
        }

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(target.getUniqueId());
        sender.sendMessage("§6Argent de " + target.getName() + " : §e" + playerData.getMoney() + " §6pièces");
    }
} 