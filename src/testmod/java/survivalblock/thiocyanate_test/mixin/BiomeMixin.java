package survivalblock.thiocyanate_test.mixin;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
//? if fabric
import survivalblock.thiocyanate_test.datagen.ThiocyanateTestDataGenerator;
import survivalblock.thiocyanate_test.worldgen.InvalidCodecs;

@Mixin(Biome.class)
public class BiomeMixin {

    @SuppressWarnings("unused")
    @Mixin(Biome.TemperatureModifier.class)
    public static class TemperatureModifierMixin {
        //? if fabric {
        @Shadow
        @Final
        public static Codec<Biome.TemperatureModifier> CODEC;

        static {
            Codec<Biome.TemperatureModifier> original = StringRepresentable.fromEnum(Biome.TemperatureModifier::values);
            CODEC = new Codec<>() {
                @Override
                public <T> DataResult<Pair<Biome.TemperatureModifier, T>> decode(DynamicOps<T> dynamicOps, T t) {
                    return original.decode(dynamicOps, t);
                }

                @Override
                public <T> DataResult<T> encode(Biome.TemperatureModifier temperatureModifier, DynamicOps<T> dynamicOps, T t) {
                    return (ThiocyanateTestDataGenerator.tellMinecraftThatEverythingIsFine ? InvalidCodecs.PEACHY_TEMPERATURE_MODIFIER : original).encode(temperatureModifier, dynamicOps, t);
                }
            };
        }
        //?}
    }
}