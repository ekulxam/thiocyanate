package survivalblock.thiocyanate_test.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.thiocyanate_test.datagen.ThiocyanateTestDataGenerator;

@Mixin(FabricDataGenerator.class)
public class FabricDataGeneratorMixin {

    @ModifyExpressionValue(method = "createBuiltinResourcePack", at = @At(value = "CONSTANT", args = "stringValue=resourcepacks"))
    private String genDataPack(String original) {
        return ThiocyanateTestDataGenerator.datapacking ? "datapacks" : original;
    }
}
