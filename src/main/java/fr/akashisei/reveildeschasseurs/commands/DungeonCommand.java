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
                // TODO: Implémenter l'entrée dans un donjon
                player.sendMessage("§aEntrée dans le donjon " + args[1]);
                break;

            case "list":
                // TODO: Implémenter la liste des donjons
                player.sendMessage("§6=== Donjons disponibles ===\n" +
                                "§7- Donjon des Ombres (Niveau 1)\n" +
                                "§7- Donjon des Ténèbres (Niveau 2)\n" +
                                "§7- Donjon des Abysses (Niveau 3)");
                break;

            case "info":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /dungeon info <nom>");
                    return true;
                }
                // TODO: Implémenter les informations d'un donjon
                player.sendMessage("§6=== Informations sur le donjon " + args[1] + " ===\n" +
                                "§7Niveau: 1\n" +
                                "§7Monstres: 5-8\n" +
                                "§7Récompenses: 100-200 pièces");
                break;

            default:
                player.sendMessage("§cCommande inconnue. Utilisez /dungeon pour voir les commandes disponibles.");
                break;
        }

        return true;
    }
} 