package fr.akashisei.reveildeschasseurs.generators;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.dungeon.DungeonInstance;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import fr.akashisei.reveildeschasseurs.utils.CustomItemManager;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.Optional;

public class DungeonBuilder {
    private final ReveilDesChasseurs plugin;
    private final Random random;
    private final Map<Location, String> roomLocations;
    private final Map<Location, String> spawnerLocations;
    private final Map<Location, PortalType> portalLocations;
    private final Map<String, Location> bossRoomLocations;
    private boolean isBossDefeated;
    private BukkitTask exitTimer;
    private Dungeon currentDungeon;

    public DungeonBuilder(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.roomLocations = new HashMap<>();
        this.spawnerLocations = new HashMap<>();
        this.portalLocations = new HashMap<>();
        this.bossRoomLocations = new HashMap<>();
        this.isBossDefeated = false;
    }

    public enum PortalType {
        RANG_E(Material.NETHER_PORTAL, "§7Portail de Rang E"),
        RANG_D(Material.NETHER_PORTAL, "§fPortail de Rang D"),
        RANG_C(Material.NETHER_PORTAL, "§aPortail de Rang C"),
        RANG_B(Material.NETHER_PORTAL, "§9Portail de Rang B"),
        RANG_A(Material.NETHER_PORTAL, "§6Portail de Rang A"),
        RANG_S(Material.END_PORTAL, "§5Portail de Rang S");

        private final Material material;
        private final String displayName;

        PortalType(Material material, String displayName) {
            this.material = material;
            this.displayName = displayName;
        }

        public Material getMaterial() {
            return material;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public DungeonInstance generateDungeon(String dungeonId, World world, Location center, Player player) {
        // Récupérer le donjon
        currentDungeon = plugin.getDungeonManager().getDungeon(dungeonId);
        if (currentDungeon == null) return null;

        // Créer le portail d'entrée
        createDungeonPortal(world, center, currentDungeon.getRank());

        // Créer une nouvelle instance de donjon
        Location spawnLoc = center.clone().add(0, 2, 0);
        DungeonInstance instance = new DungeonInstance(currentDungeon, player, spawnLoc);

        // Générer la structure du donjon
        generateStructure(world, center);
        
        // Générer les spawners de mobs
        generateSpawners(world);
        
        // Générer les coffres et récompenses
        generateLoot(world);

        return instance;
    }

    private void createDungeonPortal(World world, Location center, String rank) {
        PortalType portalType;
        switch(rank.toUpperCase()) {
            case "S": portalType = PortalType.RANG_S; break;
            case "A": portalType = PortalType.RANG_A; break;
            case "B": portalType = PortalType.RANG_B; break;
            case "C": portalType = PortalType.RANG_C; break;
            case "D": portalType = PortalType.RANG_D; break;
            default: portalType = PortalType.RANG_E; break;
        }

        // Créer le cadre du portail
        for(int x = -2; x <= 2; x++) {
            for(int y = 0; y <= 4; y++) {
                for(int z = -1; z <= 1; z++) {
                    Location loc = center.clone().add(x, y, z);
                    if(x == -2 || x == 2 || y == 0 || y == 4) {
                        loc.getBlock().setType(Material.OBSIDIAN);
                    } else if(z == 0) {
                        loc.getBlock().setType(portalType.getMaterial());
                    }
                }
            }
        }

        // Ajouter des particules et effets
        world.spawnParticle(Particle.PORTAL, center, 50, 1, 2, 1, 0.1);
        world.playSound(center, Sound.BLOCK_PORTAL_AMBIENT, 1.0f, 1.0f);
        
        portalLocations.put(center, portalType);
    }

    private void generateStructure(World world, Location center) {
        // Générer la salle principale
        generateMainRoom(world, center);
        
        // Générer les couloirs
        generateCorridors(world, center);
        
        // Générer les salles secondaires
        generateSideRooms(world, center);
        
        // Générer la salle du boss
        generateBossRoom(world, center);
    }

    private void generateMainRoom(World world, Location center) {
        // Dimensions de la salle principale
        int width = 15;
        int length = 15;
        int height = 6; // Augmenté pour plus d'atmosphère

        // Générer les murs avec des matériaux plus sombres
        for (int x = -width/2; x <= width/2; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = -length/2; z <= length/2; z++) {
                    Location loc = center.clone().add(x, y, z);
                    
                    // Murs extérieurs
                    if (Math.abs(x) == width/2 || Math.abs(z) == length/2) {
                        loc.getBlock().setType(Material.DEEPSLATE_BRICKS);
                    }
                    // Sol
                    else if (y == 0) {
                        loc.getBlock().setType(Material.DEEPSLATE_TILES);
                    }
                    // Plafond
                    else if (y == height - 1) {
                        loc.getBlock().setType(Material.DEEPSLATE_BRICKS);
                    }
                    // Intérieur
                    else {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }

        // Ajouter des cristaux magiques (comme dans Solo Leveling)
        addMagicCrystals(world, center, width, length, height);
        
        // Ajouter des décorations
        addDecorations(world, center, width, length, height);
    }

    private void addMagicCrystals(World world, Location center, int width, int length, int height) {
        // Ajouter des cristaux d'améthyste pour simuler les cristaux magiques
        for (int i = 0; i < 4; i++) {
            int x = random.nextInt(width - 2) - (width/2 - 1);
            int z = random.nextInt(length - 2) - (length/2 - 1);
            Location crystalLoc = center.clone().add(x, 1, z);
            
            // Créer un petit cluster de cristaux
            crystalLoc.getBlock().setType(Material.AMETHYST_BLOCK);
            crystalLoc.clone().add(0, 1, 0).getBlock().setType(Material.AMETHYST_CLUSTER);
            
            // Ajouter de la lumière ambiante
            crystalLoc.clone().add(0, 2, 0).getBlock().setType(Material.LIGHT);
        }
    }

    private void generateCorridors(World world, Location center) {
        // Générer 4 couloirs principaux
        int corridorLength = 10;
        int corridorWidth = 3;
        int corridorHeight = 3;

        // Couloir Nord
        generateCorridor(world, center.clone().add(0, 0, -corridorLength/2), 
            corridorLength, corridorWidth, corridorHeight, BlockFace.NORTH);
        
        // Couloir Sud
        generateCorridor(world, center.clone().add(0, 0, corridorLength/2), 
            corridorLength, corridorWidth, corridorHeight, BlockFace.SOUTH);
        
        // Couloir Est
        generateCorridor(world, center.clone().add(corridorLength/2, 0, 0), 
            corridorLength, corridorWidth, corridorHeight, BlockFace.EAST);
        
        // Couloir Ouest
        generateCorridor(world, center.clone().add(-corridorLength/2, 0, 0), 
            corridorLength, corridorWidth, corridorHeight, BlockFace.WEST);
    }

    private void generateCorridor(World world, Location start, int length, int width, int height, BlockFace direction) {
        Vector dir = direction.getDirection();
        Vector right = new Vector(-dir.getZ(), 0, dir.getX());
        
        for (int i = 0; i < length; i++) {
            Location current = start.clone().add(dir.clone().multiply(i));
            
            // Générer le couloir
            for (int w = -width/2; w <= width/2; w++) {
                for (int h = 0; h < height; h++) {
                    Location loc = current.clone().add(right.clone().multiply(w)).add(0, h, 0);
                    
                    // Murs
                    if (Math.abs(w) == width/2 || h == 0 || h == height - 1) {
                        loc.getBlock().setType(Material.STONE_BRICKS);
                    }
                    // Intérieur
                    else {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    private void generateSideRooms(World world, Location center) {
        // Générer 4 salles secondaires aux extrémités des couloirs
        int roomSize = 7;
        int roomHeight = 4;
        
        // Salle Nord
        generateRoom(world, center.clone().add(0, 0, -15), roomSize, roomHeight);
        
        // Salle Sud
        generateRoom(world, center.clone().add(0, 0, 15), roomSize, roomHeight);
        
        // Salle Est
        generateRoom(world, center.clone().add(15, 0, 0), roomSize, roomHeight);
        
        // Salle Ouest
        generateRoom(world, center.clone().add(-15, 0, 0), roomSize, roomHeight);
    }

    private void generateRoom(World world, Location center, int size, int height) {
        for (int x = -size/2; x <= size/2; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = -size/2; z <= size/2; z++) {
                    Location loc = center.clone().add(x, y, z);
                    
                    // Murs
                    if (Math.abs(x) == size/2 || Math.abs(z) == size/2 || y == 0 || y == height - 1) {
                        loc.getBlock().setType(Material.STONE_BRICKS);
                    }
                    // Intérieur
                    else {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    private void generateBossRoom(World world, Location center) {
        // Salle du boss plus grande et plus impressionnante
        int bossRoomSize = 15;
        int bossRoomHeight = 8;
        
        BlockFace[] directions = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
        BlockFace bossDirection = directions[random.nextInt(directions.length)];
        Location bossRoomCenter = center.clone().add(
            bossDirection.getModX() * 30,
            0,
            bossDirection.getModZ() * 30
        );
        
        // Générer une salle circulaire pour le boss
        for (int x = -bossRoomSize; x <= bossRoomSize; x++) {
            for (int y = 0; y < bossRoomHeight; y++) {
                for (int z = -bossRoomSize; z <= bossRoomSize; z++) {
                    if (x*x + z*z <= bossRoomSize*bossRoomSize) {
                        Location loc = bossRoomCenter.clone().add(x, y, z);
                        if (y == 0) {
                            loc.getBlock().setType(Material.POLISHED_DEEPSLATE);
                        } else if (y == bossRoomHeight - 1) {
                            loc.getBlock().setType(Material.DEEPSLATE_TILES);
                        } else if (x*x + z*z >= (bossRoomSize-1)*(bossRoomSize-1)) {
                            loc.getBlock().setType(Material.DEEPSLATE_BRICKS);
                        } else {
                            loc.getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
        }

        // Ajouter un autel pour le boss
        Location altarLoc = bossRoomCenter.clone().add(0, 1, 0);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                altarLoc.clone().add(x, 0, z).getBlock().setType(Material.POLISHED_BLACKSTONE);
            }
        }
        altarLoc.clone().add(0, 1, 0).getBlock().setType(Material.ENCHANTING_TABLE);
        
        // Ajouter des cristaux magiques autour de l'autel
        for (int i = 0; i < 4; i++) {
            double angle = i * Math.PI / 2;
            int x = (int) (Math.cos(angle) * 5);
            int z = (int) (Math.sin(angle) * 5);
            Location crystalLoc = bossRoomCenter.clone().add(x, 1, z);
            crystalLoc.getBlock().setType(Material.AMETHYST_BLOCK);
            crystalLoc.clone().add(0, 1, 0).getBlock().setType(Material.AMETHYST_CLUSTER);
        }

        // Spawn le boss
        spawnBoss(bossRoomCenter, currentDungeon.getRank());
    }

    private String getRandomMythicMob(String dungeonRank) {
        // Mobs de Solo Leveling avec MythicMobs
        Map<String, Double> mobTypes = new HashMap<>();
        switch(dungeonRank) {
            case "S":
                mobTypes.put("SoloLeveling_ArchLich", 0.4);
                mobTypes.put("SoloLeveling_DragonKing", 0.3);
                mobTypes.put("SoloLeveling_ShadowMonarch", 0.2);
                mobTypes.put("SoloLeveling_DemonKing", 0.1);
                break;
            case "A":
                mobTypes.put("SoloLeveling_HighOrcLord", 0.3);
                mobTypes.put("SoloLeveling_GiantKing", 0.3);
                mobTypes.put("SoloLeveling_EliteKnight", 0.2);
                mobTypes.put("SoloLeveling_DarkElf", 0.2);
                break;
            case "B":
                mobTypes.put("SoloLeveling_OrcChampion", 0.3);
                mobTypes.put("SoloLeveling_EliteAssassin", 0.3);
                mobTypes.put("SoloLeveling_DarkMage", 0.2);
                mobTypes.put("SoloLeveling_StoneGolem", 0.2);
                break;
            case "C":
                mobTypes.put("SoloLeveling_OrcWarrior", 0.3);
                mobTypes.put("SoloLeveling_SkeletonKnight", 0.3);
                mobTypes.put("SoloLeveling_DarkSpider", 0.2);
                mobTypes.put("SoloLeveling_IceGolem", 0.2);
                break;
            case "D":
                mobTypes.put("SoloLeveling_Orc", 0.4);
                mobTypes.put("SoloLeveling_Skeleton", 0.3);
                mobTypes.put("SoloLeveling_Spider", 0.2);
                mobTypes.put("SoloLeveling_Ghost", 0.1);
                break;
            default: // Rang E
                mobTypes.put("SoloLeveling_Goblin", 0.4);
                mobTypes.put("SoloLeveling_Wolf", 0.3);
                mobTypes.put("SoloLeveling_Zombie", 0.2);
                mobTypes.put("SoloLeveling_Bat", 0.1);
                break;
        }

        double rand = random.nextDouble();
        double cumulativeProbability = 0.0;
        
        for(Map.Entry<String, Double> entry : mobTypes.entrySet()) {
            cumulativeProbability += entry.getValue();
            if(rand <= cumulativeProbability) {
                return entry.getKey();
            }
        }
        
        return mobTypes.keySet().iterator().next();
    }

    private void spawnBoss(Location location, String dungeonRank) {
        String bossType;
        switch(dungeonRank) {
            case "S": bossType = "SoloLeveling_Monarch"; break;
            case "A": bossType = "SoloLeveling_ArchLich"; break;
            case "B": bossType = "SoloLeveling_GiantKing"; break;
            case "C": bossType = "SoloLeveling_OrcLord"; break;
            case "D": bossType = "SoloLeveling_EliteOrc"; break;
            default: bossType = "SoloLeveling_GoblinChief"; break;
        }

        // Spawn le boss avec MythicMobs
        Optional<MythicMob> mythicMob = MythicBukkit.inst().getMobManager().getMythicMob(bossType);
        if (mythicMob.isPresent()) {
            ActiveMob boss = mythicMob.get().spawn(BukkitAdapter.adapt(location), 1);
            // Enregistrer le boss pour le suivi
            bossRoomLocations.put(boss.getUniqueId().toString(), location);
            
            // Appliquer les effets spéciaux au boss
            plugin.getMobEffectManager().applyMobEffect(boss);
            
            // Écouter la mort du boss
            plugin.getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onMythicMobDeath(MythicMobDeathEvent event) {
                    if (event.getMob().getUniqueId().toString().equals(boss.getUniqueId().toString())) {
                        handleBossDeath(event.getKiller(), location);
                    }
                }
            }, plugin);
        }
    }

    private void handleBossDeath(Entity killer, Location bossLocation) {
        if (killer instanceof Player) {
            Player player = (Player) killer;
            isBossDefeated = true;
            
            // Récompenses pour avoir vaincu le boss
            player.sendMessage("§a§lFélicitations ! Vous avez vaincu le boss du donjon !");
            player.giveExp(1000);
            
            // Ouvrir le coffre du boss
            Location chestLoc = bossLocation.clone().add(0, 1, 0);
            if (chestLoc.getBlock().getType() == Material.CHEST) {
                Chest chest = (Chest) chestLoc.getBlock().getState();
                fillBossChestWithLoot(chest);
            }
            
            // Démarrer le timer de sortie
            startExitTimer();
        }
    }

    private void fillBossChestWithLoot(Chest chest) {
        Inventory inv = chest.getInventory();
        
        // Utiliser notre CustomItemManager pour les objets personnalisés
        List<ItemStack> bossLoot = Arrays.asList(
            CustomItemManager.getCrystalFragment(),
            CustomItemManager.getMagicCore(),
            CustomItemManager.getAncientRune(),
            CustomItemManager.getRankStone()
        );
        
        // Ajouter des objets aléatoires du butin
        for (ItemStack item : bossLoot) {
            if (item != null) {
                int slot = random.nextInt(inv.getSize());
                inv.setItem(slot, item);
            }
        }
    }

    private void teleportPlayersOut() {
        World world = plugin.getServer().getWorld("world"); // monde principal
        Location spawn = world.getSpawnLocation();

        // Téléporter tous les joueurs dans le donjon
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isInDungeon(player)) {
                player.teleport(spawn);
                player.sendMessage("§6Le donjon disparaît... Vous êtes téléporté à l'extérieur !");
            }
        }
    }

    private boolean isInDungeon(Player player) {
        // Vérifier si le joueur est dans une des salles du donjon
        Location playerLoc = player.getLocation();
        for (Location roomLoc : roomLocations.keySet()) {
            if (roomLoc.getWorld().equals(playerLoc.getWorld()) &&
                roomLoc.distance(playerLoc) < 100) { // rayon du donjon
                return true;
            }
        }
        return false;
    }

    private void destroyDungeon() {
        // Supprimer toutes les salles et structures
        for (Location loc : roomLocations.keySet()) {
            clearArea(loc, 50); // Nettoyer une zone de 50 blocs autour de chaque salle
        }
        
        // Réinitialiser les collections
        roomLocations.clear();
        spawnerLocations.clear();
        portalLocations.clear();
        bossRoomLocations.clear();
        
        if (exitTimer != null) {
            exitTimer.cancel();
        }
    }

    private void clearArea(Location center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = center.clone().add(x, y, z);
                    loc.getBlock().setType(Material.AIR);
                }
            }
        }
    }

    public Map<Location, String> getRoomLocations() {
        return roomLocations;
    }

    public Map<Location, String> getSpawnerLocations() {
        return spawnerLocations;
    }

    private void generateSpawners(World world) {
        // Générer des spawners dans les salles
        for (Map.Entry<Location, String> entry : roomLocations.entrySet()) {
            Location roomLoc = entry.getKey();
            
            // 50% de chance d'avoir un spawner dans une salle
            if (random.nextDouble() < 0.5) {
                Location spawnerLoc = roomLoc.clone().add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                
                // Utiliser MythicMobs pour spawner les monstres
                Optional<MythicMob> mythicMob = MythicBukkit.inst().getMobManager().getMythicMob(getRandomMythicMob(currentDungeon.getRank()));
                if (mythicMob.isPresent()) {
                    spawnerLoc.getBlock().setType(Material.SPAWNER);
                    ActiveMob mob = mythicMob.get().spawn(BukkitAdapter.adapt(spawnerLoc), 1);
                    spawnerLocations.put(spawnerLoc, mythicMob.get().getInternalName());
                    
                    // Appliquer les effets spéciaux au monstre
                    plugin.getMobEffectManager().applyMobEffect(mob);
                }
            }
        }
    }

    private void generateLoot(World world) {
        // Générer des coffres dans les salles
        for (Map.Entry<Location, String> entry : roomLocations.entrySet()) {
            Location roomLoc = entry.getKey();
            
            // 40% de chance d'avoir un coffre dans une salle
            if (random.nextDouble() < 0.4) {
                Location chestLoc = roomLoc.clone().add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                chestLoc.getBlock().setType(Material.CHEST);
                
                if (chestLoc.getBlock().getState() instanceof Chest chest) {
                    fillChestWithLoot(chest);
                }
            }
        }
    }

    private void fillChestWithLoot(Chest chest) {
        Inventory inv = chest.getInventory();
        int itemCount = random.nextInt(6) + 3;
        
        // Utiliser notre CustomItemManager pour les objets personnalisés
        List<ItemStack> commonLoot = Arrays.asList(
            CustomItemManager.getHealingPotion(),
            CustomItemManager.getManaPotion(),
            CustomItemManager.getBasicRune()
        );
        
        List<ItemStack> uncommonLoot = Arrays.asList(
            CustomItemManager.getMagicStone(),
            CustomItemManager.getEnhancementStone(),
            CustomItemManager.getRareRune()
        );
        
        for (int i = 0; i < itemCount; i++) {
            double lootTier = random.nextDouble();
            List<ItemStack> lootTable = lootTier < 0.25 ? uncommonLoot : commonLoot;
            ItemStack loot = lootTable.get(random.nextInt(lootTable.size()));
            if (loot != null) {
                inv.setItem(random.nextInt(inv.getSize()), loot);
            }
        }
    }

    private void addDecorations(World world, Location center, int width, int length, int height) {
        // Ajouter des torches magiques
        for (int x = -width/2; x <= width/2; x += 3) {
            for (int z = -length/2; z <= length/2; z += 3) {
                if (Math.abs(x) == width/2 || Math.abs(z) == length/2) {
                    Location torchLoc = center.clone().add(x, height - 2, z);
                    torchLoc.getBlock().setType(Material.SOUL_LANTERN);
                }
            }
        }

        // Ajouter des colonnes avec des runes
        for (int x = -width/3; x <= width/3; x += width/3) {
            for (int z = -length/3; z <= length/3; z += length/3) {
                if (x != 0 || z != 0) {
                    Location pillarLoc = center.clone().add(x, 0, z);
                    for (int y = 0; y < height; y++) {
                        Material material = y == height-1 ? Material.CHISELED_DEEPSLATE : Material.DEEPSLATE_BRICKS;
                        pillarLoc.clone().add(0, y, 0).getBlock().setType(material);
                    }
                }
            }
        }
    }

    private void startExitTimer() {
        // Implémentation du démarrage du timer de sortie
    }
} 