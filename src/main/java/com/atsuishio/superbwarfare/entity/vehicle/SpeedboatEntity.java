package com.atsuishio.superbwarfare.entity.vehicle;

import com.atsuishio.superbwarfare.ModUtils;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.vehicle.base.*;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.ProjectileWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.init.*;
import com.atsuishio.superbwarfare.network.ModVariables;
import com.atsuishio.superbwarfare.network.message.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.AmmoType;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.InventoryTool;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.mojang.math.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;

import static com.atsuishio.superbwarfare.tools.ParticleTool.sendParticle;

public class SpeedboatEntity extends ContainerMobileVehicleEntity implements GeoEntity, ArmedVehicleEntity, WeaponVehicleEntity, LandArmorEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SpeedboatEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.SPEEDBOAT.get(), world);
    }

    public SpeedboatEntity(EntityType<SpeedboatEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        new ProjectileWeapon()
                                .damage(VehicleConfig.HEAVY_MACHINE_GUN_DAMAGE.get())
                                .headShot(2)
                                .zoom(false)
                                .icon(ModUtils.loc("textures/screens/vehicle_weapon/gun_12_7mm.png"))
                }
        };
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(3, 1, 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() - 0.8;
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .multiply(0.5f)
                .multiply(0.2f, DamageTypes.ARROW)
                .multiply(0.4f, DamageTypes.TRIDENT)
                .multiply(0.4f, DamageTypes.MOB_ATTACK)
                .multiply(0.4f, DamageTypes.MOB_ATTACK_NO_AGGRO)
                .multiply(0.4f, DamageTypes.MOB_PROJECTILE)
                .multiply(0.4f, DamageTypes.PLAYER_ATTACK)
                .multiply(4, DamageTypes.LAVA)
                .multiply(4, DamageTypes.EXPLOSION)
                .multiply(4, DamageTypes.PLAYER_EXPLOSION)
                .multiply(0.8f, ModDamageTypes.CANNON_FIRE)
                .multiply(0.16f, ModTags.DamageTypes.PROJECTILE)
                .multiply(2, ModDamageTypes.VEHICLE_STRIKE)
                .reduce(2);
    }

    @Override
    public void baseTick() {
        super.baseTick();

        double fluidFloat;
        fluidFloat = 0.12 * getSubmergedHeight(this);
        this.setDeltaMovement(this.getDeltaMovement().add(0.0, fluidFloat, 0.0));

        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.2, 0.85, 0.2));
        } else {
            float f = 0.74f + 0.09f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90;
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).normalize().scale(0.04 * this.getDeltaMovement().horizontalDistance())));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f, 0.85, f));
        }


        if (this.level() instanceof ServerLevel serverLevel && this.isInWater() && this.getDeltaMovement().length() > 0.1) {
            sendParticle(serverLevel, ParticleTypes.CLOUD, this.getX() + 0.5 * this.getDeltaMovement().x, this.getY() + getSubmergedHeight(this) - 0.2, this.getZ() + 0.5 * this.getDeltaMovement().z, (int) (2 + 4 * this.getDeltaMovement().length()), 0.65, 0, 0.65, 0, true);
            sendParticle(serverLevel, ParticleTypes.BUBBLE_COLUMN_UP, this.getX() + 0.5 * this.getDeltaMovement().x, this.getY() + getSubmergedHeight(this) - 0.2, this.getZ() + 0.5 * this.getDeltaMovement().z, (int) (2 + 10 * this.getDeltaMovement().length()), 0.65, 0, 0.65, 0, true);
            sendParticle(serverLevel, ParticleTypes.BUBBLE_COLUMN_UP, this.getX() - 4.5 * this.getLookAngle().x, this.getY() - 0.25, this.getZ() - 4.5 * this.getLookAngle().z, (int) (40 * Mth.abs(this.entityData.get(POWER))), 0.15, 0.15, 0.15, 0.02, true);
        }

        if (this.level() instanceof ServerLevel) {
            this.handleAmmo();
        }

        turretAngle(40, 40);
        lowHealthWarning();
        collideBlock();
        if (this.getDeltaMovement().length() > 0.15) {
            collideHardBlock();
        }

        this.refreshDimensions();
    }

    private void handleAmmo() {
        if (!(this.getFirstPassenger() instanceof Player player)) return;

        int ammoCount = this.getItemStacks().stream().filter(stack -> {
            if (stack.is(ModItems.AMMO_BOX.get())) {
                return AmmoType.HEAVY.get(stack) > 0;
            }
            return false;
        }).mapToInt(AmmoType.HEAVY::get).sum() + countItem(ModItems.HEAVY_AMMO.get());


        this.entityData.set(AMMO, ammoCount);
    }

    public boolean zooming() {
        Entity driver = this.getFirstPassenger();
        if (driver == null) return false;
        if (driver instanceof Player player) {
            return player.getCapability(ModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new ModVariables.PlayerVariables()).zoom;
        }
        return false;
    }

    /**
     * 机枪塔开火
     */

    public Vec3 shootPos(float ticks) {
        Matrix4f transform = getBarrelTransform(ticks);
        Vector4f worldPosition = transformPosition(transform, 0, 0, 0);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }


    @Override
    public void vehicleShoot(Player player, int type) {
        if (this.cannotFire) return;


        var projectile = ((ProjectileWeapon) getWeapon(0)).create(player);

        projectile.bypassArmorRate(0.4f);
        projectile.setPos(shootPos(1).x - 1.1 * this.getDeltaMovement().x, shootPos(1).y, shootPos(1).z - 1.1 * this.getDeltaMovement().z);
        projectile.shoot(player, getBarrelVector(1).x, getBarrelVector(1).y + 0.005f, getBarrelVector(1).z, 20,
                (float) 0.4);
        this.level().addFreshEntity(projectile);

        float pitch = this.entityData.get(HEAT) <= 60 ? 1 : (float) (1 - 0.011 * Math.abs(60 - this.entityData.get(HEAT)));

        if (!player.level().isClientSide) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.playSound(ModSounds.M_2_FIRE_3P.get(), 4, pitch);
                serverPlayer.playSound(ModSounds.M_2_FAR.get(), 12, pitch);
                serverPlayer.playSound(ModSounds.M_2_VERYFAR.get(), 24, pitch);
            }
        }

        Level level = player.level();
        final Vec3 center = new Vec3(this.getX(), this.getEyeY(), this.getZ());

        for (Entity target : level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(4), e -> true).stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(center))).toList()) {
            if (target instanceof ServerPlayer serverPlayer) {
                ModUtils.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ShakeClientMessage(6, 5, 5, this.getX(), this.getEyeY(), this.getZ()));
            }
        }

        this.entityData.set(CANNON_RECOIL_TIME, 30);
        this.entityData.set(YAW, getTurretYRot());

        this.entityData.set(HEAT, this.entityData.get(HEAT) + 4);
        this.entityData.set(FIRE_ANIM, 3);

        boolean hasCreativeAmmo = false;
        for (int i = 0; i < getMaxPassengers() - 1; i++) {
            if (getNthEntity(i) instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                hasCreativeAmmo = true;
            }
        }

        if (!hasCreativeAmmo) {
            ItemStack ammoBox = this.getItemStacks().stream().filter(stack -> {
                if (stack.is(ModItems.AMMO_BOX.get())) {
                    return AmmoType.HEAVY.get(stack) > 0;
                }
                return false;
            }).findFirst().orElse(ItemStack.EMPTY);

            if (!ammoBox.isEmpty()) {
                AmmoType.HEAVY.add(ammoBox, -1);
            } else {
                this.getItemStacks().stream().filter(stack -> stack.is(ModItems.HEAVY_AMMO.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            }
        }
    }

    @Override
    public void travel() {
        Entity passenger0 = this.getFirstPassenger();

        if (this.getEnergy() <= 0) return;

        if (passenger0 == null) {
            this.leftInputDown = false;
            this.rightInputDown = false;
            this.forwardInputDown = false;
            this.backInputDown = false;
        }

        if (forwardInputDown) {
            this.entityData.set(POWER, this.entityData.get(POWER) + 0.02f);
        }

        if (backInputDown) {
            this.entityData.set(POWER, this.entityData.get(POWER) - 0.02f);
            if (rightInputDown) {
                this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) + 0.2f);
            } else if (leftInputDown) {
                this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) - 0.2f);
            }
        } else {
            if (rightInputDown) {
                this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) - 0.1f);
            } else if (this.leftInputDown) {
                this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) + 0.1f);
            }
        }

        if (this.forwardInputDown || this.backInputDown) {
            this.consumeEnergy(VehicleConfig.SPEEDBOAT_ENERGY_COST.get());
        }

        if (level().isClientSide) {
            level().playLocalSound(this.getX(), this.getY() + this.getBbHeight() * 0.5, this.getZ(), this.getEngineSound(), this.getSoundSource(), Math.min((this.forwardInputDown || this.backInputDown ? 7.5f : 5f) * 2 * Mth.abs(this.entityData.get(POWER)), 0.25f), (random.nextFloat() * 0.1f + 1f), false);
        }

        this.entityData.set(POWER, this.entityData.get(POWER) * 0.87f);
        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) * 0.8f);

        this.setRotorRot(this.getRotorRot() + 10 * this.entityData.get(POWER));
        this.setRudderRot(Mth.clamp(this.getRudderRot() - this.entityData.get(DELTA_ROT), -1.25f, 1.25f) * 0.7f * (this.entityData.get(POWER) > 0 ? 1 : -1));

        if (this.isInWater() || this.isUnderWater()) {
            this.setYRot((float) (this.getYRot() - Math.max(12 * this.getDeltaMovement().length(), 0.8) * this.entityData.get(DELTA_ROT)));
            this.setDeltaMovement(this.getDeltaMovement().add(Mth.sin(-this.getYRot() * 0.017453292F) * this.entityData.get(POWER), 0.0, Mth.cos(this.getYRot() * 0.017453292F) * this.entityData.get(POWER)));
        }
    }

    public Matrix4f getBarrelTransform(float ticks) {
        Matrix4f transformT = getTurretTransform(ticks);
        float x = 0f;
        float y = 0.5088375f;
        float z = 0.04173125f;
        Vector4f worldPosition = transformPosition(transformT, x, y, z);

        Matrix4f transform = new Matrix4f();
        transform.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transform.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, turretYRotO - yRotO, getTurretYRot() - getYRot())));
        transform.rotate(Axis.XP.rotationDegrees(Mth.lerp(ticks, turretXRotO, getTurretXRot())));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, prevRoll, getRoll())));
        return transform;
    }

    public Matrix4f getTurretTransform(float ticks) {
        Matrix4f transformT = getVehicleTransform(ticks);
        float x = 0f;
        float y = 2.4616625f;
        float z = -0.565625f;
        Vector4f worldPosition = transformPosition(transformT, x, y, z);

        Matrix4f transform = new Matrix4f();
        transform.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transform.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, turretYRotO - yRotO, getTurretYRot() - getYRot())));
        transform.rotate(Axis.XP.rotationDegrees(Mth.lerp(ticks, xRotO, getXRot())));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, prevRoll, getRoll())));
        return transform;
    }

    @Override
    public SoundEvent getEngineSound() {
        return ModSounds.BOAT_ENGINE.get();
    }

    @Override
    protected void positionRider(Entity pPassenger, MoveFunction pCallback) {
        if (this.hasPassenger(pPassenger)) {
            double posY = this.getY() + this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset();

            if (!zooming() && (this.isInWater() || this.isUnderWater())) {
                pPassenger.setYRot((float) (pPassenger.getYRot() - Math.max(5 * this.getDeltaMovement().length(), 0.3) * this.entityData.get(DELTA_ROT)));
                pPassenger.setYHeadRot((float) (pPassenger.getYHeadRot() - Math.max(5 * this.getDeltaMovement().length(), 0.3) * this.entityData.get(DELTA_ROT)));
            }

            if (this.getOrderedPassengers().size() > 1) {
                int i = this.getSeatIndex(pPassenger);
                if (i == 0) {
                    pCallback.accept(pPassenger, this.getX(), posY, this.getZ());
                    return;
                }

                double zOffset = -0.8;
                if (i % 2 == 0) {
                    zOffset = 0.8;
                }

                double xOffset = (int) -((i - 1) / 2.0 + 1) * 0.95;
                Vec3 vec3 = (new Vec3(xOffset, 0.0D, zOffset)).yRot(-this.getYRot() * ((float) Math.PI / 180F) - ((float) Math.PI / 2F));
                pCallback.accept(pPassenger, this.getX() + vec3.x, posY, this.getZ() + vec3.z);
            } else {
                pCallback.accept(pPassenger, this.getX(), posY, this.getZ());
            }
        }
    }

    @Override
    public void destroy() {
        if (level() instanceof ServerLevel) {
            CustomExplosion explosion = new CustomExplosion(this.level(), this,
                    ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), getAttacker(), getAttacker()), 80f,
                    this.getX(), this.getY(), this.getZ(), 5f, ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP).setDamageMultiplier(1);
            explosion.explode();
            net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this.level(), explosion);
            explosion.finalizeExplosion(false);
            ParticleTool.spawnMediumExplosionParticles(this.level(), this.position());
        }

        explodePassengers();
        this.discard();
    }

    protected void clampRotation(Entity entity) {
        float f = Mth.wrapDegrees(entity.getXRot());
        float f1 = Mth.clamp(f, -40.0F, 20F);
        entity.xRotO += f1 - f;
        entity.setXRot(entity.getXRot() + f1 - f);

        entity.setYBodyRot(this.getYRot());
        float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
        float f3 = Mth.clamp(f2, -140.0F, 140.0F);
        entity.yRotO += f3 - f2;
        entity.setYRot(entity.getYRot() + f3 - f2);
        entity.setYBodyRot(this.getYRot());
    }

    @Override
    public void onPassengerTurned(Entity entity) {
        this.clampRotation(entity);
    }

    private PlayState firePredicate(AnimationState<SpeedboatEntity> event) {
        if (this.entityData.get(FIRE_ANIM) > 1) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.speedboat.fire"));
        }

        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.speedboat.idle"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 0, this::firePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public int getMaxPassengers() {
        return 5;
    }

    @Override
    public int getMaxEnergy() {
        return VehicleConfig.SPEEDBOAT_MAX_ENERGY.get();
    }

    @Override
    public float getMaxHealth() {
        return VehicleConfig.SPEEDBOAT_HP.get();
    }

    @Override
    public int mainGunRpm(Player player) {
        return 500;
    }

    @Override
    public boolean canShoot(Player player) {
        return (this.entityData.get(AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player))
                && !cannotFire;
    }

    @Override
    public int getAmmoCount(Player player) {
        return this.entityData.get(AMMO);
    }

    @Override
    public boolean hidePassenger(Entity entity) {
        return false;
    }

    @Override
    public int zoomFov() {
        return 1;
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return ModUtils.loc("textures/vehicle_icon/speedboat_icon.png");
    }

    @Override
    public Vec3 getGunVec(float ticks) {
        return getBarrelVector(ticks);
    }
}
