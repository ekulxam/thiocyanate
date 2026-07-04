package survivalblock.thiocyanate.cyanide.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.util.valueproviders.FloatProvider;
//? >=26.1.1
import net.minecraft.util.valueproviders.FloatProviders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.thiocyanate.cyanide.core.MixinHooks;

//? if >=26.1.1 {
@Mixin(FloatProviders.class)
//?} else {
/*@Mixin(FloatProvider.class)
*///?}
public abstract class FloatProviderMixin {
    @Inject(method = "codec(FF)Lcom/mojang/serialization/Codec;", at = @At("HEAD"), cancellable = true)
    private static void codecWithBetterValidation(float minInclusive, float maxInclusive, CallbackInfoReturnable<Codec<FloatProvider>> cir) {
        cir.setReturnValue(MixinHooks.validate(minInclusive, maxInclusive, /*? >=26 {*/ FloatProviders  /*?} else {*/ /*FloatProvider *//*?}*/.CODEC));
    }

    //? if >=26.2 {
    @Inject(method = "codec(F)Lcom/mojang/serialization/Codec;", at = @At("HEAD"), cancellable = true)
    private static void codecWithBetterValidation(float minInclusive, CallbackInfoReturnable<Codec<FloatProvider>> cir) {
        cir.setReturnValue(MixinHooks.validate(minInclusive, /*? >=26 {*/ FloatProviders  /*?} else {*/ /*FloatProvider *//*?}*/.CODEC));
    }
    //?}
}
