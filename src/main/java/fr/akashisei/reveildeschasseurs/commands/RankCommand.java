package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor {
    private final ReveilDesChasseurs plugin;

    public RankCommand(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        Player player = (Player) sender;
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

        if (args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "Votre rang actuel : " + ChatColor.WHITE + playerData.getRank());
            return true;
        }

        String newRank = args[0].toLowerCase();
        if (!isValidRank(newRank)) {
            player.sendMessage(ChatColor.RED + "Rang invalide. Rangs disponibles :");
            player.sendMessage(ChatColor.WHITE + "- novice");
            player.sendMessage(ChatColor.WHITE + "- apprenti");
            player.sendMessage(ChatColor.WHITE + "- expert");
            player.sendMessage(ChatColor.WHITE + "- maître");
            player.sendMessage(ChatColor.WHITE + "- grand maître");
            return true;
        }

        playerData.setRank(newRank);
        plugin.getPlayerDataManager().savePlayerData(player);
        player.sendMessage(ChatColor.GREEN + "Votre rang a été changé en : " + ChatColor.WHITE + newRank);
        return true;
    }

    private boolean isValidRank(String rank) {
        return rank.equals("novice") ||
               rank.equals("apprenti") ||
               rank.equals("expert") ||
               rank.equals("maître") ||
               rank.equals("grand maître");
    }
} 