package survivalblock.thiocyanate_test.datagen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import survivalblock.thiocyanate.Thiocyanate;
import survivalblock.thiocyanate_test.worldgen.InvalidCodecs;
import survivalblock.thiocyanate_test.worldgen.NamedFeatureConfiguration;
import survivalblock.thiocyanate_test.ThiocyanateTestmod;

import java.util.Map;
import java.util.Optional;

import static survivalblock.thiocyanate_test.ThiocyanateTestmod.*;

/**
 * Instructions:
 * Run datagen
 * Remove : after config in datapacks/test/data/cyanide/worldgen/configured_feature/invalid_json.json
 * Run minecraft and see what thiocyanate says
 * Remove problematic jsons
 * Repeat
 */
public class ThiocyanateTestDataGenerator implements DataGeneratorEntrypoint {
    public static volatile boolean datapacking = false;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        {
            FabricDataGenerator.Pack featureCycle = createBuiltinDataPack(fabricDataGenerator, Thiocyanate.id("feature_cycle"));
            CyanideDynamicRegistriesGenerator registriesGenerator = featureCycle.addProvider(CyanideDynamicRegistriesGenerator::new);
            registriesGenerator.attach(NOOP_1, NOOP_2, NOOP_3);
            registriesGenerator.attach(Biomes.OCEAN, Biomes.PLAINS);
        }

        {
            FabricDataGenerator.Pack test = createBuiltinDataPack(fabricDataGenerator, Thiocyanate.id("test"));
            CyanideDynamicRegistriesGenerator registriesGenerator = test.addProvider(CyanideDynamicRegistriesGenerator::new);
            //registriesGenerator.attach(BROKEN_FEATURE, INVALID_PRECIPITATION, INVALID_TEMPERATURE_MODIFIER, THE_VOID, UKNOWN_CARVER, UNKNOWN_FEATURES);

            test.addProvider((fabricDataOutput, completableFuture) ->
                    new CyanideCodecGenerator(fabricDataOutput, completableFuture, "worldgen/configured_feature", InvalidCodecs.NAMED_CONFIGURED_FEATURE, INVALID_JSON, MISSING_FEATURE));

            registriesGenerator.attach(MISSING_FEATURE);

            test.addProvider((fabricDataOutput, completableFuture) ->
                    new CyanideCodecGenerator(
                            fabricDataOutput,
                            completableFuture,
                            "worldgen/placed_feature",
                            InvalidCodecs.OPTIONAL_PLACED_FEATURE,
                            ANOTHER_MISSING_REFERENCE,
                            INVALID_CONFIGURED_FEATURE,
                            BROKEN_ORE_COPPER,
                            BROKEN_ORE_TIN,
                            MISSING_CONFIGURED_FEATURE
                    ));
            //registriesGenerator.attach(ANOTHER_MISSING_REFERENCE, BROKEN_ORE_COPPER, BROKEN_ORE_TIN, INVALID_CONFIGURED_FEATURE, MISSING_CONFIGURED_FEATURE);
            test.addProvider((fabricDataOutput, completableFuture) -> new CyanideCodecGenerator<>(fabricDataOutput, completableFuture, "worldgen/template_pools", StructureTemplatePool.DIRECT_CODEC, INVALID_PROCESSORS));
        }
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, ThiocyanateTestmod::bootstrapConfiguredFeatures);
        registryBuilder.add(Registries.PLACED_FEATURE, ThiocyanateTestmod::bootstrapPlacedFeatures);
        registryBuilder.add(Registries.BIOME, ThiocyanateTestmod::bootstrapBiomes);
        registryBuilder.add(Registries.PROCESSOR_LIST, ThiocyanateTestmod::bootstrapInvalidProcessor);
        registryBuilder.add(Registries.TEMPLATE_POOL, ThiocyanateTestmod::bootstrapTemplatePools);
    }

    /**
     * This is a blocking operation. Asynchronous execution is not supported.
     */
    public FabricDataGenerator.Pack createBuiltinDataPack(FabricDataGenerator fabricDataGenerator, Identifier id) {
        datapacking = true;
        FabricDataGenerator.Pack datapack = fabricDataGenerator.createBuiltinResourcePack(id);
        datapacking = false;
        return datapack;
    }
}
