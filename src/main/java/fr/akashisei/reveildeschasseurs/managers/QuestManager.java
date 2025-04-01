package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import fr.akashisei.reveildeschasseurs.quests.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class QuestManager {
    private final ReveilDesChasseurs plugin;
    private final CustomWeaponManager weaponManager;
    private final Map<String, Quest> quests;
    private final Map<UUID, List<String>> activeQuests;
    private final Map<UUID, Map<String, Integer>> questProgress;

    public QuestManager(ReveilDesChasseurs plugin, CustomWeaponManager weaponManager) {
        this.plugin = plugin;
        this.weaponManager = weaponManager;
        this.quests = new HashMap<>();
        this.activeQuests = new HashMap<>();
        this.questProgress = new HashMap<>();
        loadQuests();
    }

    public void loadQuests() {
        File questFile = new File(plugin.getDataFolder(), "quests/quests.yml");
        if (!questFile.exists()) {
            plugin.saveResource("quests/quests.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(questFile);
        ConfigurationSection questsSection = config.getConfigurationSection("quests");

        if (questsSection != null) {
            for (String questId : questsSection.getKeys(false)) {
                ConfigurationSection questSection = questsSection.getConfigurationSection(questId);
                if (questSection != null) {
                    Quest quest = new Quest(
                        questId,
                        questSection.getString("name"),
                        questSection.getString("description"),
                        questSection.getString("type"),
                        questSection.getInt("required_amount"),
                        questSection.getString("target"),
                        questSection.getString("reward_weapon"),
                        questSection.getString("tier"),
                        questSection.getInt("required_level", 1),
                        new HashMap<>(), // requiredItems
                        questSection.getStringList("rewards"),
                        questSection.getBoolean("repeatable", false),
                        questSection.getInt("max_completions", 1),
                        questSection.getInt("reward_exp", 0),
                        questSection.getInt("reward_money", 0),
                        new HashSet<>(questSection.getStringList("required_quests")),
                        new HashSet<>(questSection.getStringList("allowed_classes")),
                        new HashSet<>(questSection.getStringList("allowed_ranks")),
                        questSection.getLong("cooldown", 0)
                    );
                    quests.put(questId, quest);
                }
            }
        }
    }

    public void saveQuests() {
        ConfigurationSection questsSection = plugin.getConfig().createSection("quests");
        for (Map.Entry<String, Quest> entry : quests.entrySet()) {
            Quest quest = entry.getValue();
            ConfigurationSection questSection = questsSection.createSection(entry.getKey());
            questSection.set("name", quest.getName());
            questSection.set("description", quest.getDescription());
            questSection.set("type", quest.getType());
            questSection.set("required_amount", quest.getRequiredAmount());
            questSection.set("target", quest.getTarget());
            questSection.set("reward_weapon", quest.getRewardWeapon());
            questSection.set("tier", quest.getTier());
            questSection.set("required_level", quest.getRequiredLevel());
            questSection.set("required_items", quest.getRequiredItems());
            questSection.set("rewards", quest.getRewards());
            questSection.set("repeatable", quest.isRepeatable());
            questSection.set("max_completions", quest.getMaxCompletions());
            questSection.set("reward_exp", quest.getRewardExp());
            questSection.set("reward_money", quest.getRewardMoney());
            questSection.set("required_quests", new ArrayList<>(quest.getRequiredQuests()));
            questSection.set("allowed_classes", new ArrayList<>(quest.getAllowedClasses()));
            questSection.set("allowed_ranks", new ArrayList<>(quest.getAllowedRanks()));
            questSection.set("cooldown", quest.getCooldown());
            questSection.set("objectives", quest.getObjectives());
        }
        plugin.saveConfig();
    }

    private void saveQuest(Quest quest) {
        File questFile = new File(plugin.getDataFolder(), "quests/quests.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(questFile);
        
        String questPath = "quests." + quest.getId();
        Map<String, Object> questData = quest.toConfig();
        
        for (Map.Entry<String, Object> entry : questData.entrySet()) {
            config.set(questPath + "." + entry.getKey(), entry.getValue());
        }
        
        try {
            config.save(questFile);
        } catch (Exception e) {
            plugin.getLogger().warning("Erreur lors de la sauvegarde de la quête : " + e.getMessage());
        }
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
        if (playerData.getLevel() < quest.getRequiredLevel()) {
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
        Player player = plugin.getServer().getPlayer(playerData.getUuid());
        if (player != null) {
            for (Map.Entry<ItemStack, Integer> entry : quest.getRequiredItems().entrySet()) {
                ItemStack requiredItem = entry.getKey();
                int requiredAmount = entry.getValue();
                int playerAmount = 0;

                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.isSimilar(requiredItem)) {
                        playerAmount += item.getAmount();
                    }
                }

                if (playerAmount < requiredAmount) {
                    return false;
                }
            }
        }

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

    public void assignQuest(Player player, String questId) {
        UUID playerId = player.getUniqueId();
        if (!activeQuests.containsKey(playerId)) {
            activeQuests.put(playerId, new ArrayList<>());
        }
        
        if (!activeQuests.get(playerId).contains(questId)) {
            activeQuests.get(playerId).add(questId);
            questProgress.computeIfAbsent(playerId, k -> new HashMap<>()).put(questId, 0);
            
            Quest quest = quests.get(questId);
            if (quest != null) {
                player.sendMessage("§6[Quête] §eNouvelle quête acceptée : " + quest.getName());
                player.sendMessage("§7" + quest.getDescription());
            }
        }
    }

    public void updateQuestProgress(Player player, String questType, String target) {
        UUID playerId = player.getUniqueId();
        if (!activeQuests.containsKey(playerId)) return;

        for (String questId : activeQuests.get(playerId)) {
            Quest quest = quests.get(questId);
            if (quest != null && quest.getType().equals(questType) && quest.getTarget().equals(target)) {
                int currentProgress = questProgress.get(playerId).get(questId);
                questProgress.get(playerId).put(questId, currentProgress + 1);

                // Vérifier si la quête est complétée
                if (currentProgress + 1 >= quest.getRequiredAmount()) {
                    completeQuest(player, questId);
                } else {
                    // Afficher la progression
                    player.sendMessage(String.format("§6[Quête] §7Progression : %d/%d", 
                        currentProgress + 1, quest.getRequiredAmount()));
                }
            }
        }
    }

    public List<String> getActiveQuests(Player player) {
        return activeQuests.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }

    public int getQuestProgress(Player player, String questId) {
        UUID playerId = player.getUniqueId();
        if (questProgress.containsKey(playerId) && questProgress.get(playerId).containsKey(questId)) {
            return questProgress.get(playerId).get(questId);
        }
        return 0;
    }
} 