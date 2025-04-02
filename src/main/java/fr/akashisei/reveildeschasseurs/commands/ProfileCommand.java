package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.HunterClass;
import fr.akashisei.reveildeschasseurs.models.PlayerRank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfileCommand implements CommandExecutor {
    private final ReveilDesChasseurs plugin;

    public ProfileCommand(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCette commande ne peut être utilisée que par un joueur !");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            player.sendMessage("§cUsage: /profile");
            return true;
        }

        // TODO: Récupérer les informations réelles du joueur
        PlayerRank rank = plugin.getRankManager().getPlayerRank(player);
        HunterClass hunterClass = HunterClass.WARRIOR; // À remplacer par la classe réelle
        int level = 1; // À remplacer par le niveau réel
        int experience = 0; // À remplacer par l'expérience réelle

        player.sendMessage("§6=== Profil de " + player.getName() + " ===\n" +
                        "§7Rang: " + rank.getDisplayName() + "\n" +
                        "§7Classe: " + hunterClass.getDisplayName() + "\n" +
                        "§7Niveau: " + level + "\n" +
                        "§7Expérience: " + experience + "/" + (level * 1000));

        return true;
    }
} 