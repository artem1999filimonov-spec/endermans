package me.artem.endermanchaos;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class EndermanListener implements Listener {
    private final JavaPlugin plugin;
    private final Random random = new Random();

    public EndermanListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEndermanTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Enderman enderman)) return;
        if (!(event.getTarget() instanceof Player player)) return;

        // 3. Телепорт на 1000 блоков (20 тиков)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!enderman.isValid() || !player.isOnline()) return;
                Location newLoc = player.getLocation().add(random.nextInt(2000) - 1000, 0, random.nextInt(2000) - 1000);
                newLoc.setY(newLoc.getWorld().getHighestBlockYAt(newLoc) + 1);
                player.teleport(newLoc);
                enderman.teleport(newLoc);
            }
        }.runTaskLater(plugin, 20L);

        // 1. Кража блока (1 секунда)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!enderman.isValid() || !player.isOnline()) return;
                World world = enderman.getWorld();
                Location loc = enderman.getLocation();
                Block targetBlock = null;
                for (int x = -3; x <= 3; x++) {
                    for (int y = 0; y <= (int) enderman.getEyeHeight(); y++) {
                        for (int z = -3; z <= 3; z++) {
                            Block b = world.getBlockAt(loc.clone().add(x, y, z));
                            if (b.getType() != Material.AIR && b.getType() != Material.BEDROCK) {
                                targetBlock = b;
                                break;
                            }
                        }
                        if (targetBlock != null) break;
                    }
                    if (targetBlock != null) break;
                }
                
                if (targetBlock != null) {
                    Material type = targetBlock.getType();
                    targetBlock.setType(Material.AIR);
                    enderman.setCarriedBlock(type.createBlockData());
                    
                    Location returnLoc = player.getLocation().add(1, 0, 0);
                    enderman.teleport(returnLoc);
                    player.getWorld().getBlockAt(returnLoc).setType(type);
                    enderman.setCarriedBlock(null);
                }
            }
        }.runTaskLater(plugin, 20L);
    }

    // 2. Ломаем лодку (1 тик)
    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;
        if (event.getVehicle().getType() == org.bukkit.entity.EntityType.BOAT) {
            for (org.bukkit.entity.Entity entity : event.getVehicle().getNearbyEntities(5, 5, 5)) {
                if (entity instanceof Enderman) {
                    event.getVehicle().remove();
                    break;
                }
            }
        }
    }
}
