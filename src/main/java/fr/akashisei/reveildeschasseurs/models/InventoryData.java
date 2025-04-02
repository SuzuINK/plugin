package fr.akashisei.reveildeschasseurs.models;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("InventoryData")
public class InventoryData implements ConfigurationSerializable {
    private UUID playerId;
    private String inventoryData;
    private String armorData;
    private String offHandData;

    public InventoryData(UUID playerId) {
        this.playerId = playerId;
        this.inventoryData = "";
        this.armorData = "";
        this.offHandData = "";
    }

    public void saveInventory(Inventory inventory) {
        try {
            // Sauvegarder l'inventaire principal
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(inventory.getSize());
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }
            dataOutput.close();
            inventoryData = Base64.getEncoder().encodeToString(outputStream.toByteArray());

            // Sauvegarder l'armure
            outputStream = new ByteArrayOutputStream();
            dataOutput = new BukkitObjectOutputStream(outputStream);
            for (int i = 36; i < 40; i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }
            dataOutput.close();
            armorData = Base64.getEncoder().encodeToString(outputStream.toByteArray());

            // Sauvegarder l'item en main secondaire
            outputStream = new ByteArrayOutputStream();
            dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(inventory.getItem(45));
            dataOutput.close();
            offHandData = Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadInventory(Inventory inventory) {
        try {
            // Charger l'inventaire principal
            if (!inventoryData.isEmpty()) {
                byte[] data = Base64.getDecoder().decode(inventoryData);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                int size = dataInput.readInt();
                for (int i = 0; i < size; i++) {
                    inventory.setItem(i, (ItemStack) dataInput.readObject());
                }
                dataInput.close();
            }

            // Charger l'armure
            if (!armorData.isEmpty()) {
                byte[] data = Base64.getDecoder().decode(armorData);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                for (int i = 36; i < 40; i++) {
                    inventory.setItem(i, (ItemStack) dataInput.readObject());
                }
                dataInput.close();
            }

            // Charger l'item en main secondaire
            if (!offHandData.isEmpty()) {
                byte[] data = Base64.getDecoder().decode(offHandData);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                inventory.setItem(45, (ItemStack) dataInput.readObject());
                dataInput.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("playerId", playerId.toString());
        data.put("inventoryData", inventoryData);
        data.put("armorData", armorData);
        data.put("offHandData", offHandData);
        return data;
    }

    public static InventoryData deserialize(Map<String, Object> data) {
        InventoryData inventoryData = new InventoryData(UUID.fromString((String) data.get("playerId")));
        inventoryData.inventoryData = (String) data.get("inventoryData");
        inventoryData.armorData = (String) data.get("armorData");
        inventoryData.offHandData = (String) data.get("offHandData");
        return inventoryData;
    }
} 