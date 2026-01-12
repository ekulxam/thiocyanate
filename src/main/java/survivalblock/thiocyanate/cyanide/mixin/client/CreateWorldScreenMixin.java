package survivalblock.thiocyanate.cyanide.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {

    @WrapOperation(
        //? if <=26 && fabric {
        /*method = "method_49629"
        *///?} else if fabric {
        /*method = "lambda$applyNewPackConfig$5"
        *///?} else if neoforge {
        method = "lambda$applyNewPackConfig$25"
        //?}
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Throwable;)V",
            remap = false
        ),
        remap = false
    )
    private void preventPrintingExceptionInErrorMessage(Logger logger, String message, Throwable throwable, Operation<Void> original) {
        logger.warn(message); // Don't dump the stack trace, because it's meaningless
        logger.warn(throwable.getMessage());
    }
}
