package survivalblock.thiocyanate_test;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ThiocyanateTestmod implements ModInitializer {
	public static final String MOD_ID = "thiocyanate_testmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ResourceKey<ConfiguredFeature<?, ?>> NO_OP = FeatureUtils.createKey("no_op");

    public static final ResourceKey<PlacedFeature> NOOP_1 = ofPlacedFeature(cyanide("noop_1"));
    public static final ResourceKey<PlacedFeature> NOOP_2 = ofPlacedFeature(cyanide("noop_2"));
    public static final ResourceKey<PlacedFeature> NOOP_3 = ofPlacedFeature(cyanide("noop_3"));

    @Override
    public void onInitialize() {

    }

    public static Identifier cyanide(String path) {
        return Identifier.fromNamespaceAndPath("cyanide", path);
    }

    public static ResourceKey<PlacedFeature> ofPlacedFeature(Identifier id) {
        return ResourceKey.create(Registries.PLACED_FEATURE, id);
    }

    public static void bootstrapConfiguredFeatures(BootstrapContext<ConfiguredFeature<? ,?>> context) {
        FeatureUtils.register(context, NO_OP, Feature.NO_OP);
    }

    public static void bootstrapPlacedFeatures(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> noop = configuredFeatures.getOrThrow(NO_OP);
        PlacementUtils.register(context, NOOP_1, noop, List.of());
        PlacementUtils.register(context, NOOP_2, noop, List.of());
        PlacementUtils.register(context, NOOP_3, noop, List.of());
    }
}