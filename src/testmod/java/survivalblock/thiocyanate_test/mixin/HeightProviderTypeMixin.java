package survivalblock.thiocyanate_test.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.thiocyanate_test.datagen.ThiocyanateTestDataGenerator;
import survivalblock.thiocyanate_test.worldgen.InvalidCodecs;

@Mixin(HeightProviderType.class)
public interface HeightProviderTypeMixin {

    @WrapOperation(method = "register", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;register(Lnet/minecraft/core/Registry;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static <T> T sliceTrapezoidIntoTriangles(Registry<? super T> registry, String string, T object, Operation<T> original) {
        if ("trapezoid".equals(string) && object instanceof HeightProviderType<?> heightProviderType) {
            object = (T) (HeightProviderType<?>) () -> (MapCodec) (ThiocyanateTestDataGenerator.tellMinecraftThatEverythingIsFine ? InvalidCodecs.INCONCLUSIVE_TRAPEZOID_HEIGHT : heightProviderType.codec());
        }
        return original.call(registry, string, object);
    }
}
