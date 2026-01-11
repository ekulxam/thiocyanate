package survivalblock.thiocyanate.cyanide.mixin.generic;

import java.util.function.Consumer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Wide-targeting mixin to remove references to {@link Codec#promotePartial(Consumer)}. This is a terrible
 * error handling mechanism, and it obfuscates actual errors while making it difficult for us to track down root causes.
 * <p>
 * As we are not able to modify DFU, effectively, this will just turn these calls into no-ops
 */
@Mixin({
    BiomeGenerationSettings.class,
    MobSpawnSettings.class
})
@SuppressWarnings("MixinAnnotationTarget")
public abstract class PromotePartialMixin {
    @WrapOperation(
        method = "*",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/serialization/Codec;promotePartial(Ljava/util/function/Consumer;)Lcom/mojang/serialization/Codec;",
            remap = false
        ),
        require = 0
    )
    private static <A> Codec<A> dontPromotePartialErrors(Codec<A> instance, Consumer<String> onError, Operation<Codec<A>> original) {
        return instance;
    }
}
