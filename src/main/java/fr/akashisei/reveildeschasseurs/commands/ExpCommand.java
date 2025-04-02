package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpCommand implements CommandExecutor {
    private final ReveilDesChasseurs plugin;

    public ExpCommand(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("reveildeschasseurs.exp")) {
            sender.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
            return true;
        }

        if (args.length < 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cCette commande ne peut être utilisée que par un joueur.");
                return true;
            }
            // Afficher l'expérience du joueur
            Player player = (Player) sender;
            PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
            sender.sendMessage("§6Votre niveau : §e" + playerData.getHunterLevel());
            sender.sendMessage("§6Votre expérience : §e" + playerData.getHunterExp() + "§6/§e" + playerData.getNextHunterLevelExp());
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give":
                if (args.length < 3) {
                    sender.sendMessage("§cUtilisation : /exp give <joueur> <montant>");
                    return true;
                }
                handleGive(sender, args[1], args[2]);
                break;
            case "set":
                if (args.length < 3) {
                    sender.sendMessage("§cUtilisation : /exp set <joueur> <montant>");
                    return true;
                }
                handleSet(sender, args[1], args[2]);
                break;
            case "info":
                if (args.length < 2) {
                    sender.sendMessage("§cUtilisation : /exp info <joueur>");
                    return true;
                }
                handleInfo(sender, args[1]);
                break;
            default:
                sender.sendMessage("§cCommande inconnue. Utilisez /exp [give|set|info]");
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
            playerData.addHunterExp(amount);
            sender.sendMessage("§aVous avez donné §e" + amount + " §apoints d'expérience à §e" + target.getName());
            target.sendMessage("§aVous avez reçu §e" + amount + " §apoints d'expérience");
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
            playerData.setHunterExp(amount);
            sender.sendMessage("§aVous avez défini l'expérience de §e" + target.getName() + " §aà §e" + amount);
            target.sendMessage("§aVotre expérience a été définie à §e" + amount);
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
        sender.sendMessage("§6Niveau de " + target.getName() + " : §e" + playerData.getHunterLevel());
        sender.sendMessage("§6Expérience de " + target.getName() + " : §e" + playerData.getHunterExp() + "§6/§e" + playerData.getNextHunterLevelExp());
    }
} 