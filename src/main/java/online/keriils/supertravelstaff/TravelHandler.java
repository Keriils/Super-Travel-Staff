package online.keriils.supertravelstaff;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTeleportEvent;

import org.jetbrains.annotations.Nullable;

/**
 * Thanks to the original developers of <a href="https://github.com/castcrafter/travel_anchors">Travel Anchors</a>
 * for creating the initial implementation, and to the developers of
 * <a href="https://github.com/Team-EnderIO/EnderIO">EnderIO</a> for releasing their adaptation under the CC0 license
 * (public domain).
 *
 * Original Project: Travel Anchors
 * License: Apache License 2.0
 * Source: <a href="https://github.com/castcrafter/travel_anchors">Travel Anchors</a>
 *
 * Adapted by EnderIO:
 * Class: TravelHandler
 * Source: <a href="https://github.com/Team-EnderIO/EnderIO/">EnderIO</a>
 */
public class TravelHandler {

    public static boolean teleport(Level level, Player player) {
        Optional<Vec3> pos = teleportPosition(level, player,Config.maxDistance.get());
        if (pos.isPresent()) {
            if (player instanceof ServerPlayer serverPlayer) {
                Optional<Vec3> eventPos = teleportEvent(player, pos.get());
                if (eventPos.isPresent()) {
                    player.teleportTo(eventPos.get().x(), eventPos.get().y(), eventPos.get().z());
                    serverPlayer.connection.resetPosition();
                    player.fallDistance = 0;
                    player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                } else {
                    player.playNotifySound(SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1F, 1F);
                }
            }
            return true;
        } else {
            return false;
        }
        //if (player instanceof ServerPlayer serverPlayer) {
        //    Optional<Vec3> pos = teleportPosition(level, player, Config.maxDistance.get());
        //    if (pos.isEmpty()) return false;
        //    if (Config.shouldFireTeleportEvent.get()) pos = teleportEvent(player, pos.get());
        //    if (pos.isEmpty()) {
        //        player.playNotifySound(SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1F, 1F);
        //    } else {
        //        player.teleportTo(
        //            pos.get()
        //                .x(),
        //            pos.get()
        //                .y(),
        //            pos.get()
        //                .z());
        //        serverPlayer.connection.resetPosition();
        //        player.fallDistance = 0;
        //        player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
        //    }
        //    return true;
        //}
        //return false;
    }

    public static Optional<Vec3> teleportPosition(Level level, Player player, int maxRange) {
        @Nullable
        BlockPos target = null;
        double floorHeight = 0;

        // inspired by Entity#pick
        Vec3 playerPos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle()
            .normalize();
        Vec3 toPos = playerPos.add(lookVec.scale(maxRange));

        ClipContext clipCtx = new ClipContext(playerPos, toPos, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null);
        BlockHitResult bhr = level.clip(clipCtx);

        // process the result
        if (bhr.getType() == HitResult.Type.MISS) {
            target = bhr.getBlockPos();
        } else if (bhr.getType() == HitResult.Type.BLOCK) {
            Direction dir = bhr.getDirection();
            if (dir == Direction.UP) {
                // teleport the player *inside* the target block, then later push them up by the block's height
                // warning: relies on the fact that isTeleportClear works with heights >= 1
                target = bhr.getBlockPos();
            } else if (dir == Direction.DOWN) {
                target = bhr.getBlockPos()
                    .below((int) Math.ceil(player.getBbHeight()));
            } else {
                target = bhr.getBlockPos()
                    .offset(dir.getStepX(), 0, dir.getStepZ());
                if (level.getBlockState(target)
                    .getCollisionShape(level, target)
                    .isEmpty()) {
                    target = target.below();
                }
            }
        }

        // if target block is close, also try to teleport through
        // eventually this distance should become configurable client-side
        if (playerPos.distanceToSqr(bhr.getLocation()) < 9) {
            // add small amount to make sure it starts at the correct block
            Vec3 traverseFrom = bhr.getLocation()
                .add(lookVec.scale(0.01));

            // since we can't return null from the fail condition, instead use an invalid position
            BlockPos failPosition = new BlockPos(0, Integer.MAX_VALUE, 0);

            boolean aimingUp = lookVec.y > 0.5;

            // can reuse same toPos and clipCtx because this traversal should be along the same line
            BlockPos newTarget = BlockGetter
                .traverseBlocks(traverseFrom, toPos, clipCtx, (traverseCtx, traversePos) -> {
                    if (!aimingUp) {
                        // check underneath first, since that's more likely to be where the player wants to teleport
                        BlockPos checkBelow = traversalCheck(level, traversePos.below());
                        if (checkBelow != null) {
                            return checkBelow;
                        }
                    }

                    return traversalCheck(level, traversePos);
                }, (failCtx) -> failPosition);
            if (newTarget != failPosition) {
                target = newTarget.immutable();
            }
        }

        if (target != null) {
            Optional<Double> ground = isTeleportPositionClear(level, target.below());
            if (ground.isPresent()) { // to use the same check as the anchors use the position below
                floorHeight = ground.get();
            } else {
                target = null;
            }
        }

        if (target == null || player.blockPosition()
            .distManhattan(target) < 2) {
            return Optional.empty();
        }
        return Optional.of(
            Vec3.atBottomCenterOf(target)
                .add(0, floorHeight, 0));
    }

    @Nullable
    private static BlockPos traversalCheck(Level level, BlockPos traversePos) {
        BlockState blockState = level.getBlockState(traversePos);
        var collision = blockState.getCollisionShape(level, traversePos);
        if (collision.isEmpty() && isTeleportPositionClear(level, traversePos.below()).isPresent()) {
            return traversePos;
        }
        return null;
    }

    /**
     * @return Optional.empty if it can't teleport and the height where to place the player. This is so you can tp on
     *         top of carpets up to a whole block
     */
    public static Optional<Double> isTeleportPositionClear(BlockGetter level, BlockPos target) {
        if (level.isOutsideBuildHeight(target)) {
            return Optional.empty();
        }

        BlockPos above = target.above();
        double height = level.getBlockState(above)
            .getCollisionShape(level, above)
            .max(Direction.Axis.Y);
        if (height <= 0.2d) {
            return Optional.of(Math.max(height, 0));
        }

        above = above.above();
        boolean noCollisionAbove = level.getBlockState(above)
            .getCollisionShape(level, above)
            .isEmpty();
        if (noCollisionAbove) {
            return Optional.of(Math.max(height, 0));
        }

        return Optional.empty();
    }

    private static Optional<Vec3> teleportEvent(Player player, Vec3 target) {
        EntityTeleportEvent event = new EntityTeleportEvent(player, target.x(), target.y(), target.z());
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return Optional.empty();
        }
        return Optional.of(new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ()));
    }

}
