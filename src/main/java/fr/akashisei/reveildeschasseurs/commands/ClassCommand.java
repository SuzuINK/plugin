package fr.akashisei.reveildeschasseurs.commands;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.managers.ClassChangeManager;
import fr.akashisei.reveildeschasseurs.models.HunterClass;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClassCommand implements CommandExecutor {
    private final ClassChangeManager classChangeManager;

    public ClassCommand(ReveilDesChasseurs plugin) {
        this.classChangeManager = new ClassChangeManager(plugin.getEconomyManager(), plugin.getRankManager());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande ne peut être utilisée que par un joueur !");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            classChangeManager.showAvailableClasses(player);
            return true;
        }

        if (args.length == 1) {
            HunterClass newClass = HunterClass.fromString(args[0]);
            if (newClass == null) {
                player.sendMessage(ChatColor.RED + "Classe invalide ! Utilisez /class pour voir les classes disponibles.");
                return true;
            }

            if (classChangeManager.changeClass(player, newClass)) {
                player.sendMessage(ChatColor.GREEN + "Vous avez changé votre classe avec succès !");
            }
            return true;
        }

        player.sendMessage(ChatColor.RED + "Usage : /class [classe]");
        return true;
    }
} 