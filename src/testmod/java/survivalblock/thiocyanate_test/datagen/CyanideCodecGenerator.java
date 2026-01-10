package survivalblock.thiocyanate_test.datagen;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public final class CyanideCodecGenerator<T> extends FabricCodecDataProvider<T> {

    private final Set<ResourceKey<T>> keys = new HashSet<>();

    public CyanideCodecGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture, String directoryName, Codec<T> codec, ResourceKey<T>... keys) {
        super(dataOutput, registriesFuture, PackOutput.Target.DATA_PACK, directoryName, codec);
        this.keys.addAll(Arrays.asList(keys));
    }

    @Override
    protected void configure(BiConsumer<Identifier, T> provider, HolderLookup.Provider lookup) {
        this.keys.forEach(key ->
                provider.accept(
                        key.identifier(),
                        lookup.getOrThrow(key).value()
                )
        );
    }

    @Override
    public String getName() {
        return "Codec";
    }
}
