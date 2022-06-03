package com.matyrobbrt.mobcapturingtool;

import com.matyrobbrt.mobcapturingtool.item.CapturingToolItem;
import com.matyrobbrt.mobcapturingtool.item.MCTItems;
import com.matyrobbrt.mobcapturingtool.util.Config;
import com.matyrobbrt.mobcapturingtool.util.Constants;
import com.matyrobbrt.mobcapturingtool.util.FileWatcher;
import net.minecraft.world.level.block.DispenserBlock;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class MCTInit {

    public static void modInit() {
        MCTItems.loadClass();
        Config.load();
        try {
            FileWatcher.defaultInstance().addWatch(Config.getConfigPath(), () -> {
                if (Config.load())
                    Constants.LOG.info("Reloaded MobCapturingTool config");
            });
        } catch (IOException e) {
            Constants.LOG.error("Could not watch MobCapturingTool config due to an exception: ", e);
        }
    }

    public static void commonSetup() {
        DispenserBlock.registerBehavior(MCTItems.CAPTURING_TOOL.get(), new CapturingToolItem.DispenseBehaviour(() -> Config.getInstance().enableDispenserBehaviour));
    }
}
