package online.keriils.supertravelstaff.mixin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;

import online.keriils.supertravelstaff.config.MixinConfig;

public class MixinPlugin implements IMixinConfigPlugin {

    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onLoad(String mixinPackage) {
        Gson gson = new GsonBuilder().setPrettyPrinting()
            .create();
        File configDir = new File("config/supertravelstaff");
        File configFile = new File(configDir, "mixin_cfg.json");

        if (!configDir.exists() && !configDir.mkdirs()) {
            LOGGER.warn("Failed to create config directory: {}", configDir.getAbsolutePath());
            return;
        }

        if (!configFile.exists()) {
            LOGGER.warn("Mixin config file not found, creating default config.");
            MixinConfig.INSTANCE = MixinConfig.DEFAULT_CONFIG;
        } else {
            try (FileReader fileReader = new FileReader(configFile)) {
                MixinConfig.INSTANCE = gson.fromJson(fileReader, MixinConfig.class);
                LOGGER.info("Mixin config loaded from: {}", configFile.getAbsolutePath());
            } catch (IOException e) {
                LOGGER.warn("Failed to load mixin config.", e);
                del(configFile);
            }
        }

        try (FileWriter fileWriter = new FileWriter(configFile)) {
            gson.toJson(MixinConfig.INSTANCE, MixinConfig.class, fileWriter);
            LOGGER.info("Default mixin config saved to: {}", configFile.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.warn("Failed to save mixin config.", e);
            del(configFile);
        }
    }

    private void del(File file) {
        if (!file.delete()) {
            LOGGER.warn("Failed to delete corrupted config file: {}", file.getAbsolutePath());
        }
    }

    @Override
    public String getRefMapperConfig() {
        return "mixins.supertravelstaff.refmap.json";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (MixinConfig.INSTANCE.withEnderIO.value()) {
            if (mixinClassName.contains("EnderIO.StaffItemHandler_Mixin")) return true;
        }
        if (MixinConfig.INSTANCE.withTravelAnchors.value()) {
            if (mixinClassName.contains("TravelAnchors.EventInterception_Mixin")) return true;
            if (mixinClassName.contains("TravelAnchors.StaffItemHandler_Mixin")) return true;
        }
        return false;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
