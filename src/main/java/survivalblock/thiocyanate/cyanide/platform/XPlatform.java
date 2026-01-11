package survivalblock.thiocyanate.cyanide.platform;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

import java.util.Map;
import java.util.Optional;

public interface XPlatform {
    XPlatform INSTANCE = getInstance();

    private static XPlatform getInstance() {
        //? if fabric {
        return new FabricPlatform();
        //?} else {
        /*return new XPlatform(){};
        *///?}
    }

    default void postFabricBeforeRegistryLoadEvent(Map<ResourceKey<? extends Registry<?>>, Registry<?>> registryMap) {}

    /**
     * @return {@code true} to skip this registry element from loading
     */
    default boolean checkFabricConditions(JsonElement json, ResourceKey<?> key, RegistryOps.RegistryInfoLookup lookup) {
        return false;
    }

    default <T> Decoder<Optional<T>> getNeoForgeConditionalCodec(Codec<T> codec) {
        return codec.map(Optional::of);
    }
}
