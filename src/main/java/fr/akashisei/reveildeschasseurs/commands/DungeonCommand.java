package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DungeonCommand implements CommandExecutor {
    private final ReveilDesChasseurs plugin;

    public DungeonCommand(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande ne peut être utilisée que par un joueur !");
            return true;
        }

        Player player = (Player) sender;
        PlayerData playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);

        if (args.length == 0) {
            // Ouvrir le menu des donjons
            plugin.getMenuManager().openDungeonMenu(player);
            return true;
        }

        if (args.length == 1) {
            String dungeonId = args[0].toLowerCase();
            Dungeon dungeon = plugin.getDungeonManager().getDungeonById(dungeonId);

            if (dungeon == null) {
                player.sendMessage(ChatColor.RED + "Ce donjon n'existe pas !");
                return true;
            }

            // Vérifier le niveau requis
            if (playerData.getLevel() < dungeon.getMinLevel()) {
                player.sendMessage(ChatColor.RED + "Vous devez être niveau " + dungeon.getMinLevel() + " pour entrer dans ce donjon !");
                return true;
            }

            // Démarrer le donjon
            plugin.getDungeonManager().startDungeon(player, dungeonId);
            return true;
        }

        // Afficher l'aide si la commande est mal utilisée
        player.sendMessage(ChatColor.GOLD + "=== Aide des Donjons ===");
        player.sendMessage(ChatColor.YELLOW + "/donjon " + ChatColor.WHITE + "- Ouvre le menu des donjons");
        player.sendMessage(ChatColor.YELLOW + "/donjon <nom> " + ChatColor.WHITE + "- Entre dans un donjon spécifique");
        return true;
    }
} 