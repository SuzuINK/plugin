package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import fr.akashisei.reveildeschasseurs.quests.Quest;
import fr.akashisei.reveildeschasseurs.quests.QuestType;
import fr.akashisei.reveildeschasseurs.quests.QuestReward;
import fr.akashisei.reveildeschasseurs.utils.QuestNotificationManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class QuestManager {
    private final ReveilDesChasseurs plugin;
    private final WeaponManager weaponManager;
    private final Map<String, Quest> quests;
    private final Map<UUID, Set<String>> activeQuests;
    private final Map<UUID, Map<String, Integer>> questProgress;
    private final Map<UUID, Map<String, Long>> questCooldowns;
    private final QuestNotificationManager notificationManager;
    private final File questsFile;
    private final FileConfiguration questsConfig;

    public QuestManager(ReveilDesChasseurs plugin, WeaponManager weaponManager) {
        this.plugin = plugin;
        this.weaponManager = weaponManager;
        this.quests = new HashMap<>();
        this.activeQuests = new HashMap<>();
        this.questProgress = new HashMap<>();
        this.questCooldowns = new HashMap<>();
        this.notificationManager = new QuestNotificationManager(plugin);
        this.questsFile = new File(plugin.getDataFolder(), "quests.yml");
        this.questsConfig = YamlConfiguration.loadConfiguration(questsFile);
        loadQuests();
    }

    public void loadQuests() {
        quests.clear();
        if (!questsFile.exists()) {
            plugin.saveResource("quests.yml", false);
        }

        for (String id : questsConfig.getKeys(false)) {
            String name = questsConfig.getString(id + ".name", "Quête inconnue");
            String description = questsConfig.getString(id + ".description", "Description manquante");
            String type = questsConfig.getString(id + ".type", "KILL");
            int required = questsConfig.getInt(id + ".required", 1);
            int reward = questsConfig.getInt(id + ".reward", 100);
            List<String> requirements = questsConfig.getStringList(id + ".requirements");
            List<String> rewards = questsConfig.getStringList(id + ".rewards");

            Quest quest = new Quest(id, name, description, type, required, reward, requirements, rewards);
            quests.put(id, quest);
        }
    }

    public void saveQuests() {
        for (Map.Entry<String, Quest> entry : quests.entrySet()) {
            String id = entry.getKey();
            Quest quest = entry.getValue();

            questsConfig.set(id + ".name", quest.getName());
            questsConfig.set(id + ".description", quest.getDescription());
            questsConfig.set(id + ".type", quest.getType().name());
            questsConfig.set(id + ".required", quest.getRequired());
            questsConfig.set(id + ".reward", quest.getReward());
            questsConfig.set(id + ".requirements", quest.getRequirements());
            questsConfig.set(id + ".rewards", quest.getRewards());
        }

        try {
            questsConfig.save(questsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder les quêtes : " + e.getMessage());
        }
    }

    public Quest getQuest(String id) {
        return quests.get(id);
    }

    public List<Quest> getPlayerQuests(Player player) {
        Set<String> questIds = activeQuests.get(player.getUniqueId());
        if (questIds == null) {
            return new ArrayList<>();
        }

        return questIds.stream()
            .map(quests::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public boolean acceptQuest(Player player, String questId) {
        Quest quest = quests.get(questId);
        if (quest == null) {
            player.sendMessage(ChatColor.RED + "Quête non trouvée : " + questId);
            return false;
        }

        Set<String> playerQuestIds = activeQuests.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
        if (playerQuestIds.contains(questId)) {
            player.sendMessage(ChatColor.RED + "Vous avez déjà accepté cette quête.");
            return false;
        }

        if (!canAcceptQuest(player, quest)) {
            player.sendMessage(ChatColor.RED + "Vous ne pouvez pas accepter cette quête.");
            return false;
        }

        playerQuestIds.add(questId);
        player.sendMessage(ChatColor.GREEN + "Vous avez accepté la quête : " + quest.getName());
        return true;
    }

    public void completeQuest(Player player, String questId) {
        Quest quest = quests.get(questId);
        if (quest == null) {
            player.sendMessage(ChatColor.RED + "Quête non trouvée : " + questId);
            return;
        }

        Set<String> playerQuestIds = activeQuests.get(player.getUniqueId());
        if (playerQuestIds == null || !playerQuestIds.contains(questId)) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas accepté cette quête.");
            return;
        }

        if (!isQuestCompleted(player, quest)) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas complété les objectifs de cette quête.");
            return;
        }

        giveRewards(player, quest);
        playerQuestIds.remove(questId);
        player.sendMessage(ChatColor.GREEN + "Vous avez complété la quête : " + quest.getName());
    }

    private void giveRewards(Player player, Quest quest) {
        // Donner l'argent
        plugin.getEconomyManager().addMoney(player, quest.getReward());

        // Donner les items
        for (String reward : quest.getRewards()) {
            String[] parts = reward.split(":");
            if (parts.length == 2) {
                String itemId = parts[0];
                int amount = Integer.parseInt(parts[1]);
                ItemStack item = plugin.getCustomItemManager().getCustomItem(itemId);
                if (item != null) {
                    item.setAmount(amount);
                    player.getInventory().addItem(item);
                }
            }
        }
    }

    private boolean isQuestCompleted(Player player, Quest quest) {
        Map<String, Integer> progress = questProgress.get(player.getUniqueId());
        if (progress == null) {
            return false;
        }

        for (Map.Entry<String, Integer> objective : quest.getObjectives().entrySet()) {
            Integer currentProgress = progress.get(objective.getKey());
            if (currentProgress == null || currentProgress < objective.getValue()) {
                return false;
            }
        }

        return true;
    }

    private boolean canAcceptQuest(Player player, Quest quest) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) {
            return false;
        }

        // Vérifier le niveau minimum
        if (player.getLevel() < quest.getRequiredLevel()) {
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

        return true;
    }

    public void updateQuestProgress(Player player, QuestType type, String target, int amount) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) return;

        Set<String> activeQuests = this.activeQuests.get(player.getUniqueId());
        if (activeQuests == null) return;

        for (String questId : activeQuests) {
            Quest quest = quests.get(questId);
            if (quest == null || quest.getType() != type) continue;

            String objective = type.name() + ":" + target;
            if (quest.getObjectives().containsKey(objective)) {
                updateQuestProgress(player, questId, objective, amount);
            }
        }
    }

    public List<Quest> getAvailableQuests(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) return new ArrayList<>();

        return quests.values().stream()
            .filter(quest -> canAcceptQuest(player, quest))
            .collect(Collectors.toList());
    }

    public boolean isQuestActive(Player player, String questId) {
        Set<String> playerQuests = activeQuests.get(player.getUniqueId());
        return playerQuests != null && playerQuests.contains(questId);
    }

    public int getQuestProgress(Player player, String questId) {
        Map<String, Integer> progress = questProgress.get(player.getUniqueId());
        if (progress == null) {
            return 0;
        }
        return progress.getOrDefault(questId, 0);
    }

    public Set<String> getQuestIds() {
        return new HashSet<>(quests.keySet());
    }

    public void addQuest(Quest quest) {
        quests.put(quest.getId(), quest);
        saveQuests();
    }

    public void removeQuest(String id) {
        quests.remove(id);
        saveQuests();
    }

    public List<String> getActiveQuests(Player player) {
        Set<String> questIds = activeQuests.get(player.getUniqueId());
        return questIds != null ? new ArrayList<>(questIds) : new ArrayList<>();
    }

    private boolean checkCondition(Player player, String condition) {
        // Implémenter la logique de vérification des conditions
        // Par exemple: "PLAYER_LEVEL_50" -> player.getLevel() >= 50
        return true;
    }

    public boolean hasCompletedQuest(UUID playerId, String questId) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(playerId);
        return playerData != null && playerData.hasCompletedQuest(questId);
    }

    public void updateQuestProgress(Player player, String questId, String objective, int progress) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) return;

        if (!playerData.getActiveQuests().contains(questId)) return;

        Quest quest = quests.get(questId);
        if (quest == null || !quest.getObjectives().containsKey(objective)) return;

        int currentProgress = playerData.getQuestProgress(questId);
        playerData.updateQuestProgress(questId, currentProgress + progress);

        if (isQuestCompleted(player, quest)) {
            completeQuest(player, questId);
        }
    }

    public void assignQuest(Player player, String questId) {
        UUID playerId = player.getUniqueId();
        if (!activeQuests.containsKey(playerId)) {
            activeQuests.put(playerId, new HashSet<>());
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

    public List<String> getCompletedQuests(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(playerData.getCompletedQuests());
    }

    public void savePlayerData(Player player) {
        // Sauvegarder les quêtes actives
        if (activeQuests.containsKey(player.getUniqueId())) {
            plugin.getConfig().set("players." + player.getUniqueId() + ".active_quests",
                    new ArrayList<>(activeQuests.get(player.getUniqueId())));
        }

        // Sauvegarder la progression
        if (questProgress.containsKey(player.getUniqueId())) {
            plugin.getConfig().set("players." + player.getUniqueId() + ".quest_progress",
                    questProgress.get(player.getUniqueId()));
        }

        // Sauvegarder les cooldowns
        if (questCooldowns.containsKey(player.getUniqueId())) {
            plugin.getConfig().set("players." + player.getUniqueId() + ".quest_cooldowns",
                    questCooldowns.get(player.getUniqueId()));
        }

        plugin.saveConfig();
    }

    public void loadPlayerData(Player player) {
        // Charger les quêtes actives
        if (plugin.getConfig().contains("players." + player.getUniqueId() + ".active_quests")) {
            List<String> questIds = plugin.getConfig().getStringList("players." + player.getUniqueId() + ".active_quests");
            activeQuests.put(player.getUniqueId(), new HashSet<>(questIds));
        }

        // Charger la progression
        if (plugin.getConfig().contains("players." + player.getUniqueId() + ".quest_progress")) {
            Map<String, Integer> progress = new HashMap<>();
            for (String key : plugin.getConfig().getConfigurationSection("players." + player.getUniqueId() + ".quest_progress").getKeys(false)) {
                progress.put(key, plugin.getConfig().getInt("players." + player.getUniqueId() + ".quest_progress." + key));
            }
            questProgress.put(player.getUniqueId(), progress);
        }

        // Charger les cooldowns
        if (plugin.getConfig().contains("players." + player.getUniqueId() + ".quest_cooldowns")) {
            Map<String, Long> cooldowns = new HashMap<>();
            for (String key : plugin.getConfig().getConfigurationSection("players." + player.getUniqueId() + ".quest_cooldowns").getKeys(false)) {
                cooldowns.put(key, plugin.getConfig().getLong("players." + player.getUniqueId() + ".quest_cooldowns." + key));
            }
            questCooldowns.put(player.getUniqueId(), cooldowns);
        }
    }

    public boolean hasCompletedQuest(Player player, String questId) {
        return hasCompletedQuest(player.getUniqueId(), questId);
    }

    public void toggleQuestTracking(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) return;

        String trackedQuest = playerData.getTrackedQuest();
        if (trackedQuest == null) {
            Set<String> activeQuests = this.activeQuests.get(player.getUniqueId());
            if (activeQuests != null && !activeQuests.isEmpty()) {
                trackQuest(player, activeQuests.iterator().next());
            }
        } else {
            untrackQuest(player);
        }
    }

    public void trackQuest(Player player, String questId) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) return;

        Set<String> activeQuests = this.activeQuests.get(player.getUniqueId());
        if (activeQuests == null || !activeQuests.contains(questId)) return;

        playerData.setTrackedQuest(questId);
        Quest quest = quests.get(questId);
        if (quest != null) {
            player.sendMessage(ChatColor.GREEN + "Vous suivez maintenant la quête : " + quest.getName());
        }
    }

    public void untrackQuest(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) return;

        String trackedQuest = playerData.getTrackedQuest();
        if (trackedQuest != null) {
            playerData.setTrackedQuest(null);
            Quest quest = quests.get(trackedQuest);
            if (quest != null) {
                player.sendMessage(ChatColor.YELLOW + "Vous ne suivez plus la quête : " + quest.getName());
            }
        }
    }

    public void abandonQuest(Player player, String questId) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) return;

        Set<String> activeQuests = this.activeQuests.get(player.getUniqueId());
        if (activeQuests == null || !activeQuests.contains(questId)) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas cette quête active.");
            return;
        }

        Quest quest = quests.get(questId);
        if (quest == null) {
            player.sendMessage(ChatColor.RED + "Cette quête n'existe pas.");
            return;
        }

        activeQuests.remove(questId);
        playerData.removeActiveQuest(questId);
        player.sendMessage(ChatColor.YELLOW + "Vous avez abandonné la quête : " + quest.getName());
    }
} 