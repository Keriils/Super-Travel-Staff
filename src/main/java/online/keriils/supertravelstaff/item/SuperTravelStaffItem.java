package online.keriils.supertravelstaff.item;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import online.keriils.supertravelstaff.config.CommonConfig;
import online.keriils.supertravelstaff.handler.StsTravelHandler;

@ParametersAreNonnullByDefault
public class SuperTravelStaffItem extends Item {

    public SuperTravelStaffItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (performAction(level, player)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    @NotNull
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null && performAction(context.getLevel(), context.getPlayer())) {
            return InteractionResult.sidedSuccess(
                context.getLevel()
                    .isClientSide());
        }
        return InteractionResult.FAIL;
    }

    public boolean performAction(Level level, Player player) {
        if (StsTravelHandler.teleport(level, player)) {
            player.getCooldowns()
                .addCooldown(this, CommonConfig.coolDownTick.get());
            return true;
        }
        return false;
    }
}
