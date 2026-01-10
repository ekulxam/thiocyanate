package survivalblock.thiocyanate_test;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record NamedFeatureConfiguration(String name) implements FeatureConfiguration {

    public static final Codec<NamedFeatureConfiguration> CODEC = Codec.STRING.xmap(NamedFeatureConfiguration::new, fc -> fc.name);

    public static class Feature extends net.minecraft.world.level.levelgen.feature.Feature<NamedFeatureConfiguration> {

        public static final Feature INSTANCE = new Feature(Identifier.withDefaultNamespace("no_op"));
        protected final Identifier id;

        public Feature(Codec<NamedFeatureConfiguration> codec, Identifier id) {
            super(codec);
            this.id = id;
        }

        public Feature(Identifier id) {
            this(CODEC, id);
        }


        @Override
        public boolean place(FeaturePlaceContext<NamedFeatureConfiguration> featurePlaceContext) {
            return false;
        }

        public Identifier id() {
            return this.id
        }
    }
}
