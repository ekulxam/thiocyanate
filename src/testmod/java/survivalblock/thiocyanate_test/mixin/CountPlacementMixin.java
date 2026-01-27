package survivalblock.thiocyanate_test.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CountPlacement.class)
public class CountPlacementMixin {

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "CONSTANT", args = "intValue=0"))
    private static int allowInvalidValuesDuringDatagen(int original) {
        return -1;
    }
}