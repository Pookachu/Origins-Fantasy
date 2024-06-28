package com.starshootercity.originsfantasy;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.AbstractProjectile;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftAllay;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public class FantasyNMSInvokerV1_19_2 extends FantasyNMSInvoker {
    @Override
    public void launchArrow(Entity projectile, Entity entity, float roll, float force, float divergence) {
        ((AbstractProjectile) projectile).getHandle().shootFromRotation(((CraftEntity) entity).getHandle(), entity.getLocation().getPitch(), entity.getLocation().getYaw(), roll, force, divergence);
    }

    @Override
    public void transferDamageEvent(LivingEntity entity, EntityDamageEvent event) {
        entity.damage(event.getDamage());
    }

    @Override
    public void boostArrow(Arrow arrow) {
        if (((CraftArrow) arrow).getHandle() instanceof net.minecraft.world.entity.projectile.Arrow a) {
            for (MobEffectInstance instance : PotionUtils.getPotion(a.getPickupItem()).getEffects()) {
                a.addEffect(new MobEffectInstance(instance.getEffect(), instance.getDuration(), instance.getAmplifier() + 1));
            }
        }
    }

    @Override
    public @Nullable Attribute getGenericScaleAttribute() {
        return null;
    }

    @Override
    public @NotNull Attribute getGenericJumpStrengthAttribute() {
        return Attribute.HORSE_JUMP_STRENGTH;
    }

    @Override
    public boolean duplicateAllay(Allay allay) {
        if (allay.getDuplicationCooldown() > 0) return false;
        allay.duplicateAllay();
        ((CraftWorld) allay.getWorld()).getHandle().broadcastEntityEvent(((CraftAllay) allay).getHandle(), (byte) 18);
        return true;
    }

    @Override
    public @NotNull Enchantment getFortuneEnchantment() {
        return Enchantment.LOOT_BONUS_BLOCKS;
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {
        event.setCancelled(!new FantasyEntityDismountEvent(event.getEntity(), event.getDismounted(), event.isCancellable()).callEvent());
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {
        event.setCancelled(!new FantasyEntityMountEvent(event.getEntity(), event.getMount()).callEvent());
    }
}