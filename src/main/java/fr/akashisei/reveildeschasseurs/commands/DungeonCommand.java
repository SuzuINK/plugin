package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
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
            sender.sendMessage("§cCette commande ne peut être utilisée que par un joueur !");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§6=== Commandes de donjon ===\n" +
                            "§e/dungeon enter <nom> §7- Entrer dans un donjon\n" +
                            "§e/dungeon list §7- Voir la liste des donjons disponibles\n" +
                            "§e/dungeon info <nom> §7- Voir les informations d'un donjon");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "enter":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /dungeon enter <nom>");
                    return true;
                }
                if (plugin.getDungeonManager().enterDungeon(player, args[1])) {
                    player.sendMessage("§aEntrée dans le donjon " + args[1]);
                } else {
                    player.sendMessage("§cImpossible d'entrer dans ce donjon. Vérifiez le nom ou votre niveau.");
                }
                break;

            case "list":
                player.sendMessage("§6=== Donjons disponibles ===");
                plugin.getDungeonManager().getAvailableDungeons(player).forEach(dungeon -> 
                    player.sendMessage(String.format("§7- %s (Niveau %d)", dungeon.getName(), dungeon.getMinLevel()))
                );
                break;

            case "info":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /dungeon info <nom>");
                    return true;
                }
                var dungeon = plugin.getDungeonManager().getDungeonByName(args[1]);
                if (dungeon != null) {
                    player.sendMessage(String.format("§6=== Informations sur le donjon %s ===\n" +
                                    "§7Niveau requis: %d\n" +
                                    "§7Monstres: %d-%d\n" +
                                    "§7Récompenses: %d-%d pièces",
                            dungeon.getName(),
                            dungeon.getMinLevel(),
                            (int)(dungeon.getMinRooms() * dungeon.getMobDensity()),
                            (int)(dungeon.getMaxRooms() * dungeon.getMobDensity()),
                            dungeon.getBaseRewardMoney(),
                            (long)(dungeon.getBaseRewardMoney() * dungeon.getDifficultyMultiplier())));
                } else {
                    player.sendMessage("§cDonjon non trouvé.");
                }
                break;

            default:
                player.sendMessage("§cCommande inconnue. Utilisez /dungeon pour voir les commandes disponibles.");
                break;
        }

        return true;
    }
} 