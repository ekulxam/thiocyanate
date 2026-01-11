package survivalblock.thiocyanate.cyanide.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Registry.class)
public interface RegistryMixin {
    /**
     * Replace this error message, because it's generically terrible.
     */
    @Inject(
        method = "method_57063",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void missingElementInRegistryError(Holder holder, CallbackInfoReturnable<String> cir, @Local Identifier id) {
        cir.setReturnValue("Unknown registry key in " + ((Registry<?>) this).key().identifier() + ": '" + id + "'");
    }
}
