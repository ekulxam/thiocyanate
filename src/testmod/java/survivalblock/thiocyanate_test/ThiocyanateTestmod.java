package survivalblock.thiocyanate_test;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.fabric.impl.resource.ResourceLoaderImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import survivalblock.thiocyanate_test.mixin.LegacySinglePoolElementAccessor;
import survivalblock.thiocyanate_test.worldgen.NamedFeatureConfiguration;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ThiocyanateTestmod implements ModInitializer {
	public static final String MOD_ID = "thiocyanate_test";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Identifier FEATURE_CYCLE_PACK_ID = Identifier.fromNamespaceAndPath(MOD_ID, "feature_cycle");
    public static final Identifier TEST_PACK_ID = Identifier.fromNamespaceAndPath(MOD_ID, "test");

    public static final ResourceKey<ConfiguredFeature<?, ?>> NO_OP = FeatureUtils.createKey("no_op");

    public static final ResourceKey<PlacedFeature> NOOP_1 = placePoison("noop_1");
    public static final ResourceKey<PlacedFeature> NOOP_2 = placePoison("noop_2");
    public static final ResourceKey<PlacedFeature> NOOP_3 = placePoison("noop_3");

    public static final ResourceKey<ConfiguredFeature<?, ?>> INVALID_JSON = ofConfiguredFeature(cyanide("invalid_json"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> MISSING_FEATURE = ofConfiguredFeature(cyanide("missing_feature"));

    public static final ResourceKey<ConfiguredWorldCarver<?>> BEEG_CAVE_CARVER = ResourceKey.create(Registries.CONFIGURED_CARVER, Identifier.withDefaultNamespace("very_beeg_cave"));
    public static final ResourceKey<PlacedFeature> MUD_LAKE = ofPlacedFeature(Identifier.withDefaultNamespace("mud_lake"));
    public static final ResourceKey<Biome> BROKEN_FEATURE = poisonWorld("broken_feature");
    public static final ResourceKey<Biome> INVALID_PRECIPITATION = poisonWorld("invalid_precipitation");
    public static final ResourceKey<Biome> INVALID_TEMPERATURE_MODIFIER = poisonWorld("invalid_temperature_modifier");
    public static final ResourceKey<Biome> THE_VOID = poisonWorld("the_void");
    public static final ResourceKey<Biome> UNKNOWN_CARVER = poisonWorld("unknown_carver");
    public static final ResourceKey<Biome> UNKNOWN_FEATURES = poisonWorld("unknown_features");

    public static final ResourceKey<ConfiguredFeature<?, ?>> INVALID_CONFIGURED_FEATURE_FEATURE = ofConfiguredFeature(cyanide("invalid_configured_feature"));
    public static final ResourceKey<PlacedFeature> ANOTHER_MISSING_REFERENCE = placePoison("another_missing_reference");
    public static final ResourceKey<PlacedFeature> BROKEN_ORE_COPPER = placePoison("broken_ore_copper");
    public static final ResourceKey<PlacedFeature> BROKEN_ORE_TIN = placePoison("broken_ore_tin");
    public static final ResourceKey<PlacedFeature> INVALID_CONFIGURED_FEATURE = placePoison("invalid_configured_feature");
    public static final ResourceKey<PlacedFeature> MISSING_CONFIGURED_FEATURE = placePoison("missing_configured_feature");

    public static final ResourceKey<StructureProcessorList> INVALID_PROCESSOR_LIST = ResourceKey.create(Registries.PROCESSOR_LIST, Identifier.withDefaultNamespace("invalid_processor_list_name"));
    public static final ResourceKey<StructureTemplatePool> INVALID_PROCESSORS = ResourceKey.create(Registries.TEMPLATE_POOL, cyanide("template_pool_invalid_processors"));

    @Override
    public void onInitialize() {
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> {
            ModContainer trueGenerated = new ModContainer() {
                @Override
                public ModMetadata getMetadata() {
                    return modContainer.getMetadata();
                }

                @Override
                public List<Path> getRootPaths() {
                    return modContainer.getRootPaths().stream().map(path -> {
                        String pathString = path.toString();
                        // probably multiversion problem
                        pathString = pathString.substring(0, pathString.indexOf("build")) + "src/testmod/generated";
                        return Path.of(pathString);
                    }).toList();
                }

                @Override
                public ModOrigin getOrigin() {
                    return modContainer.getOrigin();
                }

                @Override
                public Optional<ModContainer> getContainingMod() {
                    return modContainer.getContainingMod();
                }

                @Override
                public Collection<ModContainer> getContainedMods() {
                    return modContainer.getContainedMods();
                }

                @SuppressWarnings("deprecation")
                @Override
                public Path getRootPath() {
                    return modContainer.getRootPath();
                }

                @SuppressWarnings("deprecation")
                @Override
                public Path getPath(String file) {
                    return modContainer.getPath(file);
                }
            };

            boolean featureCycle = registerBuiltinDataPack(FEATURE_CYCLE_PACK_ID, trueGenerated, PackActivationType.DEFAULT_ENABLED);
            boolean test = registerBuiltinDataPack(TEST_PACK_ID, trueGenerated, PackActivationType.DEFAULT_ENABLED);
            if (!featureCycle || !test) {
                LOGGER.warn("Some datapacks were not properly loaded! FeatureCycle: {}, Test: {}", featureCycle, test);
            }
        });
    }

    public static Identifier cyanide(String path) {
        return Identifier.fromNamespaceAndPath("cyanide", path);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean registerBuiltinDataPack(Identifier id, ModContainer modContainer, PackActivationType activationType) {
        return ResourceLoaderImpl.registerBuiltinPack(id, "datapacks/" + id.getPath(), modContainer, activationType);
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

        FeatureUtils.register(context, INVALID_JSON, NamedFeatureConfiguration.Feature.INSTANCE, new NamedFeatureConfiguration("value"));
        FeatureUtils.register(context, MISSING_FEATURE, new NamedFeatureConfiguration.Feature(Identifier.withDefaultNamespace("not_a_real_feature")), new NamedFeatureConfiguration(""));
        FeatureUtils.register(context, INVALID_CONFIGURED_FEATURE_FEATURE, Feature.NO_OP);
    }

    public static void bootstrapPlacedFeatures(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> noop = configuredFeatures.getOrThrow(NO_OP);
        PlacementUtils.register(context, NOOP_1, noop, List.of());
        PlacementUtils.register(context, NOOP_2, noop, List.of());
        PlacementUtils.register(context, NOOP_3, noop, List.of());

        Holder<ConfiguredFeature<?, ?>> invalidConfiguredFeature = configuredFeatures.getOrThrow(INVALID_CONFIGURED_FEATURE_FEATURE);

        PlacementUtils.register(context, ANOTHER_MISSING_REFERENCE, invalidConfiguredFeature);
        PlacementUtils.register(context, INVALID_CONFIGURED_FEATURE, invalidConfiguredFeature);
        PlacementUtils.register(context,
                BROKEN_ORE_COPPER,
                configuredFeatures.getOrThrow(OreFeatures.ORE_COPPPER_SMALL),
                List.of(
                        CountPlacement.of(-1),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(112)),
                        BiomeFilter.biome()
                )
        );
        PlacementUtils.register(context,
                BROKEN_ORE_TIN,
                configuredFeatures.getOrThrow(OreFeatures.ORE_COPPPER_SMALL),
                List.of(
                        CountPlacement.of(-1),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.triangle(VerticalAnchor.absolute(3), VerticalAnchor.absolute(112)),
                        BiomeFilter.biome()
                )
        );
        PlacementUtils.register(context, MISSING_CONFIGURED_FEATURE, configuredFeatures.getOrThrow(MISSING_FEATURE));

        PlacementUtils.register(context, MUD_LAKE, invalidConfiguredFeature);
    }

    public static void bootstrapCarvers(BootstrapContext<ConfiguredWorldCarver<?>> context) {
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);

        context.register(BEEG_CAVE_CARVER, WorldCarver.CAVE
                .configured(
                        new CaveCarverConfiguration(
                                0.15F,
                                UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180)),
                                UniformFloat.of(0.1F, 0.9F),
                                VerticalAnchor.aboveBottom(8),
                                CarverDebugSettings.of(false, Blocks.CRIMSON_BUTTON.defaultBlockState()),
                                blocks.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
                                UniformFloat.of(0.7F, 1.4F),
                                UniformFloat.of(0.8F, 1.3F),
                                UniformFloat.of(-1.0F, -0.4F)
                        )
                )
        );
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

        Supplier<BiomeGenerationSettings.Builder> defaultFeatureCarverFactory = () -> {
            BiomeGenerationSettings.Builder defaultFeatureCarver = new BiomeGenerationSettings.Builder(placedFeatures, configuredFeatures);
            OverworldBiomes.globalOverworldGeneration(defaultFeatureCarver);
            BiomeDefaultFeatures.addDefaultOres(defaultFeatureCarver);
            BiomeDefaultFeatures.addDefaultFlowers(defaultFeatureCarver);
            BiomeDefaultFeatures.addDefaultMushrooms(defaultFeatureCarver);
            BiomeDefaultFeatures.addDefaultExtraVegetation(defaultFeatureCarver, true);
            return defaultFeatureCarver;
        };

        BiomeGenerationSettings.Builder brokenFeatureCarver = defaultFeatureCarverFactory.get().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, BROKEN_ORE_COPPER);

        BiomeGenerationSettings.Builder unknownCarvers = defaultFeatureCarverFactory.get().addCarver(BEEG_CAVE_CARVER);
        BiomeGenerationSettings.Builder unknownFeatures = defaultFeatureCarverFactory.get().addFeature(GenerationStep.Decoration.LAKES, MUD_LAKE);

        MobSpawnSettings emptySpawning = new MobSpawnSettings.Builder().build();

        context.register(
                Biomes.OCEAN,
                OverworldBiomes.baseOcean()
                        .generationSettings(oceanFeatureCarver.build())
                        .mobSpawnSettings(emptySpawning)
                        .build()
        );
        context.register(
                Biomes.PLAINS,
                OverworldBiomes.baseBiome(0.5F, 0.5F)
                        .generationSettings(plainsFeatureCarver.build())
                        .mobSpawnSettings(emptySpawning)
                        .build()
        );
        context.register(
                BROKEN_FEATURE,
                OverworldBiomes.baseBiome(0.5F, 0.5F)
                        .generationSettings(brokenFeatureCarver.build())
                        .mobSpawnSettings(emptySpawning)
                        .build()
        );
        context.register(
                INVALID_PRECIPITATION,
                OverworldBiomes.baseBiome(0.5F, 0.5F)
                        .generationSettings(defaultFeatureCarverFactory.get().build())
                        .mobSpawnSettings(emptySpawning)
                        .build()
        );
        context.register(
                INVALID_TEMPERATURE_MODIFIER,
                OverworldBiomes.baseBiome(0.5F, 0.5F)
                        .generationSettings(defaultFeatureCarverFactory.get().build())
                        .mobSpawnSettings(emptySpawning)
                        .temperatureAdjustment(Biome.TemperatureModifier.FROZEN)
                        .build()
        );
        context.register(
                THE_VOID,
                OverworldBiomes.baseBiome(0.5F, 0.5F)
                        .generationSettings(defaultFeatureCarverFactory.get().build())
                        .mobSpawnSettings(emptySpawning)
                        .build()
        );
        context.register(
                UNKNOWN_CARVER,
                OverworldBiomes.baseBiome(0.5F, 0.5F)
                        .generationSettings(unknownCarvers.build())
                        .mobSpawnSettings(emptySpawning)
                        .build()
        );
        context.register(
                UNKNOWN_FEATURES,
                OverworldBiomes.baseBiome(0.5F, 0.5F)
                        .generationSettings(unknownFeatures.build())
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