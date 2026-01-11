package survivalblock.thiocyanate.cyanide.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.util.valueproviders.FloatProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.thiocyanate.cyanide.core.MixinHooks;

@Mixin(FloatProvider.class)
public abstract class FloatProviderMixin {
    @Inject(method = "codec", at = @At("HEAD"), cancellable = true)
    private static void codecWithBetterValidation(float minInclusive, float maxInclusive, CallbackInfoReturnable<Codec<FloatProvider>> cir) {
        cir.setReturnValue(MixinHooks.validate(minInclusive, maxInclusive, FloatProvider.CODEC));
    }
}
