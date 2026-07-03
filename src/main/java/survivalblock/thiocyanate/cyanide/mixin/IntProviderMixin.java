package survivalblock.thiocyanate.cyanide.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.util.valueproviders.IntProvider;
//? >=26.1.1
import net.minecraft.util.valueproviders.IntProviders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.thiocyanate.cyanide.core.MixinHooks;

//? if >=26.1.1 {
@Mixin(IntProviders.class)
//?} else {
/*@Mixin(IntProvider.class)
 *///?}
public abstract class IntProviderMixin {
    @Inject(method = "validateCodec", at = @At("HEAD"), cancellable = true)
    private static <T extends IntProvider> void codecWithBetterValidation(int min, int max, Codec<T> codec, CallbackInfoReturnable<Codec<T>> cir) {
        cir.setReturnValue(MixinHooks.validate(min, max, codec));
    }
}
