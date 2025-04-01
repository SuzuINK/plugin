package fr.akashisei.reveildeschasseurs.utils;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import java.util.UUID;

public class EntityUtils {
    /**
     * Récupère une entité par son UUID dans un monde donné
     * @param world Le monde dans lequel chercher l'entité
     * @param uuid L'UUID de l'entité à trouver
     * @return L'entité trouvée ou null si elle n'existe pas
     */
    public static Entity getEntity(World world, UUID uuid) {
        if (world == null || uuid == null) return null;
        
        for (Entity entity : world.getEntities()) {
            if (entity.getUniqueId().equals(uuid)) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Vérifie si une entité existe dans un monde donné
     * @param world Le monde dans lequel chercher l'entité
     * @param uuid L'UUID de l'entité à vérifier
     * @return true si l'entité existe, false sinon
     */
    public static boolean entityExists(World world, UUID uuid) {
        return getEntity(world, uuid) != null;
    }

    /**
     * Supprime une entité par son UUID dans un monde donné
     * @param world Le monde dans lequel chercher l'entité
     * @param uuid L'UUID de l'entité à supprimer
     * @return true si l'entité a été supprimée, false sinon
     */
    public static boolean removeEntity(World world, UUID uuid) {
        Entity entity = getEntity(world, uuid);
        if (entity != null) {
            entity.remove();
            return true;
        }
        return false;
    }
} 