package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import fr.akashisei.reveildeschasseurs.models.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.stream.Collectors;

public class QuestManager {
    private final ReveilDesChasseurs plugin;
    private final Map<String, Quest> quests;
    private final Map<UUID, Set<String>> completedQuests;

    public QuestManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.quests = new HashMap<>();
        this.completedQuests = new HashMap<>();
        loadQuests();
    }

    public void loadQuests() {
        ConfigurationSection questsSection = plugin.getConfig().getConfigurationSection("quests");
        if (questsSection == null) return;

        for (String questId : questsSection.getKeys(false)) {
            ConfigurationSection questSection = questsSection.getConfigurationSection(questId);
            if (questSection == null) continue;

            Quest quest = new Quest(
                questId,
                questSection.getString("name"),
                questSection.getString("description"),
                questSection.getInt("minLevel", 1)
            );
            quests.put(questId, quest);
        }
    }

    public void saveQuests() {
        ConfigurationSection questsSection = plugin.getConfig().createSection("quests");
        for (Map.Entry<String, Quest> entry : quests.entrySet()) {
            Quest quest = entry.getValue();
            ConfigurationSection questSection = questsSection.createSection(entry.getKey());
            questSection.set("name", quest.getName());
            questSection.set("description", quest.getDescription());
            questSection.set("minLevel", quest.getMinLevel());
        }
        plugin.saveConfig();
    }

    public void saveQuest(Quest quest) {
        plugin.getConfigManager().saveQuest(quest);
    }

    public Quest getQuest(String questId) {
        return quests.get(questId);
    }

    public List<Quest> getAvailableQuests(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) {
            return new ArrayList<>();
        }

        return quests.values().stream()
                .filter(quest -> canAcceptQuest(playerData, quest))
                .collect(Collectors.toList());
    }

    private boolean canAcceptQuest(PlayerData playerData, Quest quest) {
        // Vérifier le niveau minimum
        if (playerData.getLevel() < quest.getMinLevel()) {
            return false;
        }

        // Vérifier si la quête est déjà active
        if (playerData.hasActiveQuest(quest.getId())) {
            return false;
        }

        // Vérifier si la quête est en cooldown
        if (playerData.isQuestOnCooldown(quest.getId())) {
            return false;
        }

        // Vérifier les quêtes requises
        for (String requiredQuestId : quest.getRequiredQuests()) {
            if (!playerData.hasCompletedQuest(requiredQuestId)) {
                return false;
            }
        }

        // Vérifier les items requis
        // TODO: Implémenter la vérification des items requis

        // Vérifier la classe du joueur
        if (!quest.getAllowedClasses().isEmpty() && !quest.getAllowedClasses().contains(playerData.getPlayerClass())) {
            return false;
        }

        // Vérifier le rang du joueur
        if (!quest.getAllowedRanks().isEmpty() && !quest.getAllowedRanks().contains(playerData.getRank())) {
            return false;
        }

        return true;
    }

    public boolean acceptQuest(Player player, String questId) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) {
            return false;
        }

        Quest quest = getQuest(questId);
        if (quest == null || !canAcceptQuest(playerData, quest)) {
            return false;
        }

        playerData.addActiveQuest(questId);
        playerData.setQuestProgress(questId, 0);
        plugin.getPlayerDataManager().savePlayerData(playerData);

        player.sendMessage("§aVous avez accepté la quête : " + quest.getName());
        player.sendMessage("§7" + quest.getDescription());
        return true;
    }

    public void completeQuest(Player player, String questId) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) {
            return;
        }

        Quest quest = getQuest(questId);
        if (quest == null || !playerData.hasActiveQuest(questId)) {
            return;
        }

        // Donner les récompenses
        playerData.addExperience(quest.getRewardExp());
        playerData.addMoney(quest.getRewardMoney());

        // Mettre à jour le statut de la quête
        playerData.removeActiveQuest(questId);
        playerData.incrementQuestCompletions(questId);
        if (quest.getCooldown() > 0) {
            playerData.setQuestCooldown(questId, System.currentTimeMillis() + quest.getCooldown());
        }

        plugin.getPlayerDataManager().savePlayerData(playerData);

        player.sendMessage("§aVous avez terminé la quête : " + quest.getName());
        player.sendMessage("§7Récompenses :");
        player.sendMessage("§6" + quest.getRewardExp() + " XP");
        player.sendMessage("§6" + quest.getRewardMoney() + " pièces");
    }

    public void abandonQuest(Player player, String questId) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) {
            return;
        }

        Quest quest = getQuest(questId);
        if (quest == null || !playerData.hasActiveQuest(questId)) {
            return;
        }

        playerData.removeActiveQuest(questId);
        plugin.getPlayerDataManager().savePlayerData(playerData);

        player.sendMessage("§cVous avez abandonné la quête : " + quest.getName());
    }

    public boolean hasCompletedQuest(UUID playerId, String questId) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(playerId);
        return playerData != null && playerData.hasCompletedQuest(questId);
    }

    public void updateQuestProgress(Player player, String questId, String objective, int progress) {
        PlayerData playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
        Quest quest = getQuest(questId);
        
        if (quest == null) {
            plugin.getLogger().warning("Tentative de mise à jour d'une quête inexistante : " + questId);
            return;
        }

        if (!playerData.getActiveQuests().contains(questId)) {
            return;
        }

        // Mettre à jour la progression
        int currentProgress = playerData.getQuestProgress(questId);
        playerData.updateQuestProgress(questId, currentProgress + progress);

        // Vérifier si la quête est terminée
        if (currentProgress + progress >= quest.getObjectives().size()) {
            completeQuest(player, questId);
        } else {
            // Envoyer un message de progression
            player.sendMessage(ChatColor.GREEN + "Progression de la quête " + quest.getName() + " : " + 
                (currentProgress + progress) + "/" + quest.getObjectives().size());
        }
    }
} 