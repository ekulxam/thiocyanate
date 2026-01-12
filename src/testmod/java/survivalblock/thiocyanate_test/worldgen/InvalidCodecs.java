//? if fabric {
/*package survivalblock.thiocyanate_test.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.ApiStatus;
import survivalblock.thiocyanate_test.mixin.TrapezoidHeightAccessor;

import java.util.Map;
import java.util.Optional;

@ApiStatus.NonExtendable
public interface InvalidCodecs {

    Codec<ConfiguredFeature> NAMED_CONFIGURED_FEATURE = RecordCodecBuilder.create(
            instance -> instance.group(
                            Identifier.CODEC.fieldOf("type").forGetter(cf -> ((NamedFeatureConfiguration.Feature) (cf.feature())).id()),
                            Codec.unboundedMap(Codec.STRING, NamedFeatureConfiguration.CODEC).fieldOf("config").forGetter(cf -> cf.config() instanceof NamedFeatureConfiguration nfc && !nfc.name().isEmpty() ? Map.of("key", nfc) : Map.of())
                    )
                    .apply(instance, (id, config) -> new ConfiguredFeature<>(new NamedFeatureConfiguration.Feature(id), config.get("key"))
                    ));

    @SuppressWarnings({"OptionalOfNullableMisuse", "OptionalGetWithoutIsPresent"})
    Codec<PlacedFeature> OPTIONAL_PLACED_FEATURE = RecordCodecBuilder.create(
            instance -> instance.group(
                            ConfiguredFeature.CODEC.optionalFieldOf("feature").forGetter(placedFeature -> Optional.ofNullable(placedFeature.feature())),
                            PlacementModifier.CODEC.listOf().optionalFieldOf("placement").forGetter(placedFeature -> Optional.ofNullable(placedFeature.placement()))
                    )
                    .apply(instance, (feature, placement) -> new PlacedFeature(feature.get(), placement.get()))
    );

    @SuppressWarnings("DataFlowIssue")
    MapCodec<TrapezoidHeight> INCONCLUSIVE_TRAPEZOID_HEIGHT = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codec.INT.fieldOf("min_inconclusive").forGetter(trapezoidHeight -> ((TrapezoidHeightAccessor) trapezoidHeight).thiocyanate_test$getMinInclusive().resolveY(null)),
                            VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter(trapezoidHeight -> ((TrapezoidHeightAccessor) trapezoidHeight).thiocyanate_test$getMaxInclusive()),
                            Codec.INT.optionalFieldOf("plateau", 0).forGetter(trapezoidHeight -> ((TrapezoidHeightAccessor) trapezoidHeight).thiocyanate_test$getPlateau())
                    )
                    .apply(instance, (min, max, plateau) -> TrapezoidHeight.of(VerticalAnchor.absolute(min), max, plateau))
    );

    Codec<Biome.TemperatureModifier> PEACHY_TEMPERATURE_MODIFIER = Codec.STRING.xmap(Biome.TemperatureModifier::valueOf, temperatureModifier -> temperatureModifier == Biome.TemperatureModifier.NONE ? temperatureModifier.getSerializedName() : "peachy");
}
*///?}