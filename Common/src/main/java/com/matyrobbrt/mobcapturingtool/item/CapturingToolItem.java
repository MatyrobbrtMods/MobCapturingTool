package com.matyrobbrt.mobcapturingtool.item;

import com.matyrobbrt.mobcapturingtool.util.Config;
import com.matyrobbrt.mobcapturingtool.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import java.util.Objects;
import java.util.function.BooleanSupplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CapturingToolItem extends Item {
    public static final String CAPTURED_ENTITY_TAG = "CapturedEntity";
    public static final List<String> TAGS_TO_REMOVE = List.of(
            "SleepingX", "SleepingY", "SleepingZ" // We need to remove sleeping tags because they case issues
    );

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
        return InteractionResult.SUCCESS;
    }

    public static boolean capture(ItemStack stack, LivingEntity target) {
        return capture(stack, target, null);
    }

    public static boolean capture(ItemStack stack, LivingEntity target, @Nullable Player player) {
        if (target.level().isClientSide || getEntityType(stack) != null)
            return false;
        if (target instanceof Player || (player != null && !target.canChangeDimensions(target.level(), player.level())) || !target.isAlive())
            return false;
        if (isBlacklisted(stack, target, player)) {
            if (player != null) {
                final var regName = EntityType.getKey(target.getType()).toString();
                player.displayClientMessage(Constants.getTranslation("blacklisted",
                        Component.literal(regName).withStyle(ChatFormatting.GOLD)), true);
            }
            return false;
        }
        final var nbt = new CompoundTag();
        target.saveWithoutId(nbt);
        TAGS_TO_REMOVE.forEach(nbt::remove);
        stack.set(MCTItems.CAPTURED_ENTITY_TYPE.get(), EntityType.getKey(target.getType()));
        stack.set(MCTItems.CAPTURED_ENTITY.get(), nbt);
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
        var tag = stack.get(MCTItems.CAPTURED_ENTITY.get());
        if (entity != null && tag != null) {
            entity.load(tag);
            BlockPos blockPos = pos.relative(facing);
            entity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
            stack.remove(MCTItems.CAPTURED_ENTITY.get());
            stack.remove(MCTItems.CAPTURED_ENTITY_TYPE.get());
            level.addFreshEntity(entity);
            return true;
        }
        return false;
    }

    public static boolean isBlacklisted(ItemStack stack, LivingEntity target, @Nullable Player player) {
        final var regName = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
        var cfg = Config.getInstance();
        if (cfg.blacklistedEntities.contains(regName.toString())) {
            return true;
        } else if (cfg.blacklistedMods.contains(regName.getNamespace())) {
            return true;
        }
        return !Objects.requireNonNullElse(CapturingPredicates.PREDICATES
                .get(target.level().registryAccess()).get(regName), CapturingPredicates.CHECK_TAG)
                .canPickup(stack, target, player);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        final var entity = getEntityType(stack);
        if (entity != null) {
            final var entityTag = stack.get(MCTItems.CAPTURED_ENTITY.get());
            tooltipComponents.add(Constants.getTranslation("captured_entity", Component.literal(BuiltInRegistries.ENTITY_TYPE.getKey(entity).toString())
                    .withStyle(ChatFormatting.AQUA)));
            tooltipComponents.add(Constants.getTranslation("captured_entity.health", Component.literal(String.valueOf(entityTag.getDouble("Health")))
                    .withStyle(ChatFormatting.AQUA)));
        }
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    @Override
    public Component getName(ItemStack stack) {
        final var entity = getEntityType(stack);
        if (entity != null) {
            final var eName = Component.translatable(entity.getDescriptionId());
            return Component.translatable(super.getDescriptionId(stack))
                    .append(" (")
                    .append(eName)
                    .append(")");
        }
        return super.getName(stack);
    }

    @Nullable
    public static EntityType<?> getEntityType(ItemStack stack) {
        if (stack.has(MCTItems.CAPTURED_ENTITY_TYPE.get())) {
            final var typeStr = stack.get(MCTItems.CAPTURED_ENTITY_TYPE.get());
            return BuiltInRegistries.ENTITY_TYPE.getOptional(typeStr).orElse(null);
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

            final var facing = source.state().getValue(DispenserBlock.FACING);
            final var targetPos = source.pos().relative(facing);
            if (stack.has(MCTItems.CAPTURED_ENTITY.get())) {
                release(
                        targetPos,
                        facing,
                        source.level(),
                        stack
                );
            } else {
                List<LivingEntity> list = source.level().getEntitiesOfClass(LivingEntity.class,
                        new AABB(targetPos), (livingEntity) -> livingEntity.isAlive() && !(livingEntity instanceof Player));
                //noinspection ResultOfMethodCallIgnored
                list.stream().anyMatch(en -> capture(stack, en));
            }

            return stack;
        }
    }

}
