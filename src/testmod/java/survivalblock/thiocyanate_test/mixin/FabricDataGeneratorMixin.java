package survivalblock.thiocyanate_test.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
//? if fabric
/*import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if fabric
/*import survivalblock.thiocyanate_test.datagen.ThiocyanateTestDataGenerator;*/

@Mixin(/*? fabric {*/ /*FabricDataGenerator *//*?} else {*/ Object/*?}*/.class)
public class FabricDataGeneratorMixin {

    //? if fabric {
    /*@ModifyExpressionValue(method = "createBuiltinResourcePack", at = @At(value = "CONSTANT", args = "stringValue=resourcepacks"))
    private String genDataPack(String original) {
        return ThiocyanateTestDataGenerator.datapacking ? "datapacks" : original;
    }
    *///?}
}