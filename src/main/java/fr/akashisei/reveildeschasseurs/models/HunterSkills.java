package fr.akashisei.reveildeschasseurs.models;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class HunterSkills {
    private final Map<String, HunterSkill> unlockedSkills;
    private final Map<String, Long> skillCooldowns;
    private final Player player;
    private int mana;
    private int maxMana;
    private double manaRegenRate;
    private final ReveilDesChasseurs plugin;

    public HunterSkills(Player player, ReveilDesChasseurs plugin) {
        this.player = player;
        this.plugin = plugin;
        this.unlockedSkills = new HashMap<>();
        this.skillCooldowns = new HashMap<>();
        this.maxMana = 100;
        this.mana = maxMana;
        this.manaRegenRate = 1.0;
        loadSkills();
        startManaRegeneration();
    }

    private void loadSkills() {
        // Charger les compétences depuis la configuration
        // TODO: Implémenter le chargement des compétences depuis le fichier de configuration
    }

    public boolean unlockSkill(HunterSkill skill) {
        if (unlockedSkills.containsKey(skill.getId())) {
            return false;
        }
        unlockedSkills.put(skill.getId(), skill);
        return true;
    }

    public boolean useSkill(String skillId) {
        HunterSkill skill = unlockedSkills.get(skillId);
        if (skill == null) {
            return false;
        }

        // Vérifier le cooldown
        if (isOnCooldown(skillId)) {
            return false;
        }

        // Vérifier le mana
        if (mana < skill.getEffectiveManaCost()) {
            return false;
        }

        // Utiliser le mana
        mana -= skill.getEffectiveManaCost();

        // Appliquer la compétence
        skill.apply(player);

        // Mettre en cooldown
        setCooldown(skillId, skill.getEffectiveCooldown());

        // Ajouter de l'expérience
        addSkillExperience(skillId);

        return true;
    }

    public void addSkillExperience(String skillId) {
        HunterSkill skill = unlockedSkills.get(skillId);
        if (skill == null) {
            return;
        }

        // Calculer l'expérience gagnée selon le type de compétence
        int expGained = calculateExperienceGain(skill);
        skill.addExperience(expGained);

        // Vérifier si le niveau a augmenté
        if (skill.getMasteryLevel() > 1) {
            player.sendMessage("§aVotre compétence " + skill.getName() + " a atteint le niveau " + skill.getMasteryLevel() + " !");
        }
    }

    private int calculateExperienceGain(HunterSkill skill) {
        // Base d'expérience selon le type de compétence
        int baseExp = switch (skill.getType()) {
            case ACTIVE -> 10;
            case ULTIMATE -> 25;
            case PASSIVE -> 5;
            case BUFF -> 15;
            case DEBUFF -> 15;
            case HEAL -> 20;
            case SHIELD -> 15;
        };

        // Multiplicateur selon le rang du joueur
        PlayerRank rank = ReveilDesChasseurs.getInstance().getRankManager().getPlayerRank(player);
        double rankMultiplier = rank.getRewardMultiplier();

        // Multiplicateur selon le niveau de maîtrise actuel
        double masteryMultiplier = 1.0 - (skill.getMasteryLevel() - 1) * 0.1; // -10% par niveau

        return (int) (baseExp * rankMultiplier * masteryMultiplier);
    }

    public boolean isOnCooldown(String skillId) {
        Long cooldownEnd = skillCooldowns.get(skillId);
        return cooldownEnd != null && cooldownEnd > System.currentTimeMillis();
    }

    public void setCooldown(String skillId, int seconds) {
        skillCooldowns.put(skillId, System.currentTimeMillis() + (seconds * 1000L));
    }

    public long getCooldownRemaining(String skillId) {
        Long cooldownEnd = skillCooldowns.get(skillId);
        if (cooldownEnd == null) {
            return 0;
        }
        return Math.max(0, cooldownEnd - System.currentTimeMillis());
    }

    private void startManaRegeneration() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    regenerateMana();
                }
            }
        }.runTaskTimer(ReveilDesChasseurs.getInstance(), 20L, 20L); // Régénération toutes les secondes
    }

    private void regenerateMana() {
        mana = Math.min(maxMana, mana + (int) manaRegenRate);
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = Math.min(maxMana, Math.max(0, mana));
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
        this.mana = Math.min(mana, maxMana);
    }

    public double getManaRegenRate() {
        return manaRegenRate;
    }

    public void setManaRegenRate(double manaRegenRate) {
        this.manaRegenRate = manaRegenRate;
    }

    public List<HunterSkill> getUnlockedSkills() {
        return new ArrayList<>(unlockedSkills.values());
    }

    public HunterSkill getSkill(String skillId) {
        return unlockedSkills.get(skillId);
    }

    public boolean hasSkill(String skillId) {
        return unlockedSkills.containsKey(skillId);
    }

    public void clearCooldowns() {
        skillCooldowns.clear();
    }

    public Map<String, Integer> getSkillLevels() {
        Map<String, Integer> levels = new HashMap<>();
        for (Map.Entry<String, HunterSkill> entry : unlockedSkills.entrySet()) {
            levels.put(entry.getKey(), entry.getValue().getMasteryLevel());
        }
        return levels;
    }

    public Map<String, Integer> getSkillExperience() {
        Map<String, Integer> exp = new HashMap<>();
        for (Map.Entry<String, HunterSkill> entry : unlockedSkills.entrySet()) {
            exp.put(entry.getKey(), entry.getValue().getExperience());
        }
        return exp;
    }

    public Map<String, Integer> getSkillMaxExperience() {
        Map<String, Integer> maxExp = new HashMap<>();
        for (Map.Entry<String, HunterSkill> entry : unlockedSkills.entrySet()) {
            maxExp.put(entry.getKey(), entry.getValue().getMaxExperience());
        }
        return maxExp;
    }
} 