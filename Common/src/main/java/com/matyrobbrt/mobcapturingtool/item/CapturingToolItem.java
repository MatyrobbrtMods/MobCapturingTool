package com.matyrobbrt.mobcapturingtool.item;

import com.matyrobbrt.mobcapturingtool.util.Config;
import com.matyrobbrt.mobcapturingtool.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.BooleanSupplier;

@ParametersAreNonnullByDefault
public class CapturingToolItem extends Item {
    public static final String CAPTURED_ENTITY_TAG = "CapturedEntity";
    public static final String ENTITY_TYPE_TAG = "EntityType";

    public CapturingToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!release(context.getClickedPos(), context.getClickedFace(), context.getLevel(), context.getItemInHand()))
            return InteractionResult.FAIL;
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (!capture(stack, interactionTarget, player))
            return InteractionResult.FAIL;
        player.swing(usedHand);
        player.setItemInHand(usedHand, stack);
        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }

    public static boolean capture(ItemStack stack, LivingEntity target) {
        return capture(stack, target, null);
    }

    public static boolean capture(ItemStack stack, LivingEntity target, @Nullable Player player) {
        if (target.getLevel().isClientSide || getEntityType(stack) != null)
            return false;
        if (target instanceof Player || !target.canChangeDimensions() || !target.isAlive())
            return false;
        if (isBlacklisted(target.getType())) {
            if (player != null) {
                final var regName = EntityType.getKey(target.getType()).toString();
                player.displayClientMessage(Constants.getTranslation("blacklisted",
                        new TextComponent(regName).withStyle(ChatFormatting.GOLD)), true);
            }
            return false;
        }
        final var nbt = new CompoundTag();
        nbt.putString(ENTITY_TYPE_TAG, EntityType.getKey(target.getType()).toString());
        target.saveWithoutId(nbt);
        stack.getOrCreateTag().put(CAPTURED_ENTITY_TAG, nbt);
        target.remove(Entity.RemovalReason.KILLED);
        return true;
    }

    public static boolean release(BlockPos pos, Direction facing, Level level, ItemStack stack) {
        if (level.isClientSide)
            return false;
        final var entityType = getEntityType(stack);
        if (entityType == null)
            return false;
        final var entity = entityType.create(level);
        if (entity != null) {
            entity.load(stack.getOrCreateTagElement(CAPTURED_ENTITY_TAG));
            BlockPos blockPos = pos.relative(facing);
            entity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
            stack.getOrCreateTag().remove(CAPTURED_ENTITY_TAG);
            level.addFreshEntity(entity);
            return true;
        }
        return false;
    }

    public static boolean isBlacklisted(EntityType<?> entity) {
        final var regName = Registry.ENTITY_TYPE.getKey(entity).toString();
        if (Config.getInstance().blacklistedEntities.contains(regName))
            return true;
        return entity.is(Constants.BLACKLISTED_TAG);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        final var entity = getEntityType(stack);
        if (entity != null) {
            final var entityTag = stack.getOrCreateTagElement(CAPTURED_ENTITY_TAG);
            tooltipComponents.add(Constants.getTranslation("captured_entity", new TextComponent(Registry.ENTITY_TYPE.getKey(entity).toString())
                    .withStyle(ChatFormatting.AQUA)));
            tooltipComponents.add(Constants.getTranslation("captured_entity.health", new TextComponent(String.valueOf(entityTag.getDouble("Health")))
                    .withStyle(ChatFormatting.AQUA)));
        }
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    @Override
    public Component getName(ItemStack stack) {
        final var entity = getEntityType(stack);
        if (entity != null) {
            final var eName = new TranslatableComponent(entity.getDescriptionId());
            return new TranslatableComponent(super.getDescriptionId(stack))
                    .append(" (")
                    .append(eName)
                    .append(")");
        }
        return super.getName(stack);
    }

    @Nullable
    public static EntityType<?> getEntityType(ItemStack stack) {
        if (stack.getOrCreateTag().contains(CAPTURED_ENTITY_TAG, Tag.TAG_COMPOUND)) {
            final var typeStr = stack.getOrCreateTagElement(CAPTURED_ENTITY_TAG).getString(ENTITY_TYPE_TAG);
            final var rl = new ResourceLocation(typeStr);
            return Registry.ENTITY_TYPE.getOptional(rl).orElse(null);
        }
        return null;
    }

    public static final class DispenseBehaviour implements DispenseItemBehavior {

        private final BooleanSupplier condition;

        public DispenseBehaviour(BooleanSupplier condition) {
            this.condition = condition;
        }

        @Override
        public ItemStack dispense(BlockSource source, ItemStack stack) {
            if (!condition.getAsBoolean())
                return stack;

            final var tag = stack.getOrCreateTag();
            final var facing = source.getBlockState().getValue(DispenserBlock.FACING);
            final var targetPos = source.getPos().relative(facing);
            if (tag.contains(CAPTURED_ENTITY_TAG, Tag.TAG_COMPOUND)) {
                release(
                        targetPos,
                        facing,
                        source.getLevel(),
                        stack
                );
            } else {
                List<LivingEntity> list = source.getLevel().getEntitiesOfClass(LivingEntity.class,
                        new AABB(targetPos), (livingEntity) -> livingEntity.isAlive() && !(livingEntity instanceof Player));
                list.stream().anyMatch(en -> capture(stack, en));
            }

            return stack;
        }
    }

}
