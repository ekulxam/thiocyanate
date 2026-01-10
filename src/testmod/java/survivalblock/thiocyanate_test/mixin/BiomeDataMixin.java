package survivalblock.thiocyanate_test.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.biome.BiomeData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BiomeData.class)
public class BiomeDataMixin {
    
    @WrapOperation(method = "bootstrap", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/worldgen/BootstrapContext;register(Lnet/minecraft/resources/ResourceKey;Ljava/lang/Object;)Lnet/minecraft/core/Holder$Reference;"))
    private static Holder.Reference<Biome> preventSomeRegistration(BootstrapContext<Biome> instance, ResourceKey<Biome> resourceKey, Object biome, Operation<Holder.Reference<Biome>> original) {
        return resourceKey == Biomes.OCEAN || resourceKey == Biomes.PLAINS ? null : original.call(instance, resourceKey, biome);
    }
}
