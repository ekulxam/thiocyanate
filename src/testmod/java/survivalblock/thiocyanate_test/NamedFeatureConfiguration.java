package survivalblock.thiocyanate_test;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record NamedFeatureConfiguration(String name, boolean empty) implements FeatureConfiguration {

    public static final Codec<NamedFeatureConfiguration> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.STRING.fieldOf("name").forGetter(nfc -> nfc.name),
                            Codec.BOOL.fieldOf("empty").forGetter(nfc -> nfc.empty)
                    )
                    .apply(instance, NamedFeatureConfiguration::new)
    );

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
            return this.id;
        }
    }
}
