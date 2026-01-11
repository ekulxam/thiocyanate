package survivalblock.thiocyanate_test.datagen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import survivalblock.thiocyanate.Thiocyanate;
import survivalblock.thiocyanate_test.NamedFeatureConfiguration;
import survivalblock.thiocyanate_test.ThiocyanateTestmod;

import java.util.Map;

import static survivalblock.thiocyanate_test.ThiocyanateTestmod.*;

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
            Codec<ConfiguredFeature> namedCodec = RecordCodecBuilder.create(
                    instance -> instance.group(
                                    Identifier.CODEC.fieldOf("type").forGetter(cf -> ((NamedFeatureConfiguration.Feature) (cf.feature())).id()),
                                    Codec.unboundedMap(Codec.STRING, NamedFeatureConfiguration.CODEC).fieldOf("config").forGetter(cf -> cf.config() instanceof NamedFeatureConfiguration nfc && !nfc.empty() ? Map.of("key", nfc) : Map.of())
                            )
                            .apply(instance, (id, config) -> new ConfiguredFeature<>(new NamedFeatureConfiguration.Feature(id), config.get("key"))
                            ));
            test.addProvider((fabricDataOutput, completableFuture) ->
                    new CyanideCodecGenerator(fabricDataOutput, completableFuture, "worldgen/configured_feature", namedCodec, INVALID_JSON, MISSING_FEATURE));

            registriesGenerator.attach(MISSING_FEATURE);
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
