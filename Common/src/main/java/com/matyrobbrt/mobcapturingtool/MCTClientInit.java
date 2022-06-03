package com.matyrobbrt.mobcapturingtool;

import com.matyrobbrt.mobcapturingtool.item.CapturingToolItem;
import com.matyrobbrt.mobcapturingtool.item.MCTItems;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ItemLike;

public class MCTClientInit {

    public static void registerItemColours(ColourConsumer consumer) {
        consumer.register((stack, tint) -> {
            if (tint == 1 || tint == 2) {
                final var entity = CapturingToolItem.getEntityType(stack);
                if (entity != null) {
                    final var egg = SpawnEggItem.byId(entity);
                    if (egg != null)
                        return egg.getColor(tint - 1);
                }
            }
            return 0xFFFFFF;
        }, MCTItems.CAPTURING_TOOL.get());
    }

    public interface ColourConsumer {
        void register(ItemColor colour, ItemLike... items);
    }

}
