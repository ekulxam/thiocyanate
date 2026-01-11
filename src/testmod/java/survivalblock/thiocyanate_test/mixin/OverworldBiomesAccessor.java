package survivalblock.thiocyanate_test.mixin;

import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(OverworldBiomes.class)
public interface OverworldBiomesAccessor {
    @Invoker("baseOcean")
    static Biome.BiomeBuilder thiocyanate_test$invokeBaseOcean() {
        throw new UnsupportedOperationException();
    }

    @Invoker("baseBiome")
    static Biome.BiomeBuilder thiocyanate_test$invokeBaseBiome(float temperature, float downfall) {
        throw new UnsupportedOperationException();
    }

    @Invoker("globalOverworldGeneration")
    static void thiocyanate_test$invokeGlobalOverworldGeneration(BiomeGenerationSettings.Builder builder) {
        throw new UnsupportedOperationException();
    }
}
