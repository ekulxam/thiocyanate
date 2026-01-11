package survivalblock.thiocyanate_test.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CountPlacement.class)
public class CountPlacementMixin {

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "CONSTANT", args = "intValue=0"))
    private static int allowInvalidValuesDuringDatagen(int original) {
        if (FabricDataGenHelper.ENABLED) {
            return -1;
        }
        return original;
    }
}
