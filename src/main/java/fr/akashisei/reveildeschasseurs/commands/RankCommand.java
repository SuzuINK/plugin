package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerRank;
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
            sender.sendMessage("§cCette commande ne peut être utilisée que par un joueur !");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // TODO: Récupérer le rang réel du joueur
            PlayerRank rank = plugin.getRankManager().getPlayerRank(player);
            player.sendMessage("§6=== Votre rang ===\n" +
                            "§7Rang actuel: " + rank.getDisplayName() + "\n" +
                            "§7Points requis pour le prochain rang: " + rank.getRequiredPoints() + "\n" +
                            "§7Niveau requis: " + rank.getRequiredLevel());
            return true;
        }

        player.sendMessage("§cUsage: /rank");
        return true;
    }
} 