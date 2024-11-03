package org.evlis.cardinal.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.evlis.cardinal.GlobalVars;

import java.util.Comparator;
import java.util.Optional;

public class EntitySpawn implements Listener {
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        World world = entity.getWorld();
        if (GlobalVars.shatteredWorlds.contains(world.getName())) {
            if (entity instanceof Monster monster) { // Check if the entity is a hostile mob
                switch (monster) {
                    case Creeper creeper -> creeper.setPowered(true);
                    case ZombieVillager zombieVillager -> {
                        event.setCancelled(true);
                        Location loc = event.getLocation();
                        loc.getWorld().spawnEntity(loc, EntityType.RAVAGER);
                    }
                    case Drowned drowned -> {
                        ItemStack trident = new ItemStack(Material.TRIDENT);
                        drowned.getEquipment().setItemInMainHand(trident);
                    }
                    case Zombie zombie -> {
                        event.setCancelled(true);
                        Location loc = event.getLocation();
                        loc.getWorld().spawnEntity(loc, EntityType.WITHER_SKELETON);
                    }
                    case Spider spider -> {
                        event.setCancelled(true);
                        Location loc = event.getLocation();
                        loc.getWorld().spawnEntity(loc, EntityType.PILLAGER);
                    }
                    case Enderman enderman -> {
                        event.setCancelled(true);
                        Location loc = event.getLocation();
                        loc.getWorld().spawnEntity(loc, EntityType.VEX);
                    } case Skeleton skeleton -> {
                        Optional<LivingEntity> nearestEntity = skeleton.getNearbyEntities(20, 20, 20).stream()
                                .filter(nearEntity -> nearEntity instanceof LivingEntity)
                                .map(nearEntity -> (LivingEntity) nearEntity)
                                .min(Comparator.comparingDouble(nearEntity ->
                                        nearEntity.getLocation().distanceSquared(skeleton.getLocation())
                                ));
                        // Set the skeleton's target to the nearest living entity, if found
                        nearestEntity.ifPresent(monster::setTarget);
                    }
                    default -> {
                    }
                }
                monster.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 0));
                monster.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, -1, 0));
            }
        }
    }
}
