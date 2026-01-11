package survivalblock.thiocyanate_test;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import survivalblock.thiocyanate_test.mixin.LegacySinglePoolElementAccessor;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ThiocyanateTestmod implements ModInitializer {
	public static final String MOD_ID = "thiocyanate_testmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ResourceKey<ConfiguredFeature<?, ?>> NO_OP = FeatureUtils.createKey("no_op");

    public static final ResourceKey<PlacedFeature> NOOP_1 = placePoison("noop_1");
    public static final ResourceKey<PlacedFeature> NOOP_2 = placePoison("noop_2");
    public static final ResourceKey<PlacedFeature> NOOP_3 = placePoison("noop_3");

    public static final ResourceKey<ConfiguredFeature<?, ?>> INVALID_JSON = ofConfiguredFeature(cyanide("invalid_json"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> MISSING_FEATURE = ofConfiguredFeature(cyanide("missing_feature"));

    public static final ResourceKey<Biome> BROKEN_FEATURE = poisonWorld("broken_feature");
    public static final ResourceKey<Biome> INVALID_PRECIPITATION = poisonWorld("invalid_precipitation");
    public static final ResourceKey<Biome> INVALID_TEMPERATURE_MODIFIER = poisonWorld("invalid_temperature_modifier");
    public static final ResourceKey<Biome> THE_VOID = poisonWorld("the_void");
    public static final ResourceKey<Biome> UKNOWN_CARVER = poisonWorld("unknown_carver");
    public static final ResourceKey<Biome> UNKNOWN_FEATURES = poisonWorld("unknown_features");

    public static final ResourceKey<PlacedFeature> ANOTHER_MISSING_REFERENCE = placePoison("another_missing_reference");
    public static final ResourceKey<PlacedFeature> BROKEN_ORE_COPPER = placePoison("broken_ore_copper");
    public static final ResourceKey<PlacedFeature> BROKEN_ORE_TIN = placePoison("broken_ore_tin");
    public static final ResourceKey<PlacedFeature> INVALID_CONFIGURED_FEATURE = placePoison("invalid_configured_feature");
    public static final ResourceKey<PlacedFeature> MISSING_CONFIGURED_FEATURE = placePoison("missing_configured_feature");

    public static final ResourceKey<StructureProcessorList> INVALID_PROCESSOR_LIST = ResourceKey.create(Registries.PROCESSOR_LIST, Identifier.withDefaultNamespace("invalid_processor_list_name"));
    public static final ResourceKey<StructureTemplatePool> INVALID_PROCESSORS = ResourceKey.create(Registries.TEMPLATE_POOL, cyanide("template_pool_invalid_processors"));

    @Override
    public void onInitialize() {

    }

    public static Identifier cyanide(String path) {
        return Identifier.fromNamespaceAndPath("cyanide", path);
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> ofConfiguredFeature(Identifier id) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, id);
    }

    public static ResourceKey<PlacedFeature> placePoison(String path) {
        return ofPlacedFeature(cyanide(path));
    }

    public static ResourceKey<PlacedFeature> ofPlacedFeature(Identifier id) {
        return ResourceKey.create(Registries.PLACED_FEATURE, id);
    }

    public static ResourceKey<Biome> poisonWorld(String path) {
        return ofBiome(cyanide(path));
    }

    public static ResourceKey<Biome> ofBiome(Identifier id) {
        return ResourceKey.create(Registries.BIOME, id);
    }

    public static void bootstrapConfiguredFeatures(BootstrapContext<ConfiguredFeature<? ,?>> context) {
        FeatureUtils.register(context, NO_OP, Feature.NO_OP);

        FeatureUtils.register(context, INVALID_JSON, NamedFeatureConfiguration.Feature.INSTANCE, new NamedFeatureConfiguration("value", false));
        FeatureUtils.register(context, MISSING_FEATURE, new NamedFeatureConfiguration.Feature(Identifier.withDefaultNamespace("not_a_real_feature")), new NamedFeatureConfiguration("", true));
    }

    public static void bootstrapPlacedFeatures(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> noop = configuredFeatures.getOrThrow(NO_OP);
        PlacementUtils.register(context, NOOP_1, noop, List.of());
        PlacementUtils.register(context, NOOP_2, noop, List.of());
        PlacementUtils.register(context, NOOP_3, noop, List.of());
    }

    public static void bootstrapBiomes(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> configuredFeatures = context.lookup(Registries.CONFIGURED_CARVER);

        BiomeGenerationSettings.Builder oceanFeatureCarver = new BiomeGenerationSettings.Builder(placedFeatures, configuredFeatures);
        oceanFeatureCarver.addCarver(Carvers.CAVE)
                .addCarver(Carvers.CAVE_EXTRA_UNDERGROUND)
                .addCarver(Carvers.CANYON)
                .addFeature(GenerationStep.Decoration.RAW_GENERATION /* 0 */, NOOP_2)
                .addFeature(GenerationStep.Decoration.RAW_GENERATION, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                .addFeature(GenerationStep.Decoration.RAW_GENERATION, MiscOverworldPlacements.LAKE_LAVA_SURFACE)
                .addFeature(GenerationStep.Decoration.RAW_GENERATION, NOOP_1);

        BiomeGenerationSettings.Builder plainsFeatureCarver = new BiomeGenerationSettings.Builder(placedFeatures, configuredFeatures);
        plainsFeatureCarver.addCarver(Carvers.CAVE)
                .addCarver(Carvers.CAVE_EXTRA_UNDERGROUND)
                .addCarver(Carvers.CANYON)
                .addFeature(GenerationStep.Decoration.RAW_GENERATION /* 0 */, NOOP_1)
                .addFeature(GenerationStep.Decoration.RAW_GENERATION, NOOP_2)
                .addFeature(GenerationStep.Decoration.RAW_GENERATION, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND)
                .addFeature(GenerationStep.Decoration.RAW_GENERATION, MiscOverworldPlacements.LAKE_LAVA_SURFACE)
                .addFeature(GenerationStep.Decoration.RAW_GENERATION, NOOP_3);

        MobSpawnSettings emptySpawning = new MobSpawnSettings.Builder().build();

        context.register(Biomes.OCEAN,
                OverworldBiomes.baseOcean()
                        .generationSettings(oceanFeatureCarver.build())
                        .mobSpawnSettings(emptySpawning)
                        .build()
        );
        context.register(Biomes.PLAINS,
                OverworldBiomes.baseBiome(0.5F, 0.5F)
                        .generationSettings(plainsFeatureCarver.build())
                        .mobSpawnSettings(emptySpawning)
                        .build()
        );
    }

    public static void bootstrapInvalidProcessor(BootstrapContext<StructureProcessorList> context) {
        context.register(INVALID_PROCESSOR_LIST, new StructureProcessorList(List.of()));
    }

    public static void bootstrapTemplatePools(BootstrapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> vanillaPools = context.lookup(Registries.TEMPLATE_POOL);
        HolderGetter<StructureProcessorList> processors = context.lookup(Registries.PROCESSOR_LIST);

        Holder<StructureTemplatePool> terminator = vanillaPools.getOrThrow(ResourceKey.create(Registries.TEMPLATE_POOL, Identifier.withDefaultNamespace("village/plains/terminators")));
        Holder<StructureProcessorList> emptyProcessor = processors.getOrThrow(ProcessorLists.EMPTY);
        Holder<StructureProcessorList> invalidProcessor = processors.getOrThrow(INVALID_PROCESSOR_LIST);

        context.register(INVALID_PROCESSORS,
                new StructureTemplatePool(
                        terminator,
                        List.of(
                                Pair.of(
                                        LegacySinglePoolElementAccessor.thiocyanate_test$invokeInit(
                                                Either.left(Identifier.withDefaultNamespace("village/plains/houses/plains_small_house_1")),
                                                emptyProcessor,
                                                StructureTemplatePool.Projection.RIGID,
                                                Optional.empty()
                                        ),
                                        2
                                ),
                                Pair.of(
                                        LegacySinglePoolElementAccessor.thiocyanate_test$invokeInit(
                                                Either.left(Identifier.withDefaultNamespace("village/plains/houses/plains_small_house_2")),
                                                invalidProcessor,
                                                StructureTemplatePool.Projection.RIGID,
                                                Optional.empty()
                                        ),
                                        2
                                ),
                                Pair.of(
                                        LegacySinglePoolElementAccessor.thiocyanate_test$invokeInit(
                                                Either.left(Identifier.withDefaultNamespace("village/plains/houses/plains_small_house_3")),
                                                emptyProcessor,
                                                StructureTemplatePool.Projection.RIGID,
                                                Optional.empty()
                                        ),
                                        2
                                ),
                                Pair.of(
                                        LegacySinglePoolElementAccessor.thiocyanate_test$invokeInit(
                                                Either.left(Identifier.withDefaultNamespace("village/plains/houses/plains_small_house_4")),
                                                emptyProcessor,
                                                StructureTemplatePool.Projection.RIGID,
                                                Optional.empty()
                                        ),
                                        2
                                ),
                                Pair.of(
                                        EmptyPoolElement.INSTANCE,
                                        10
                                )
                        )
                )
        );
    }
}