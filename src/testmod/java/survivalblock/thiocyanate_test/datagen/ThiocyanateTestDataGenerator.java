package survivalblock.thiocyanate_test.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import survivalblock.thiocyanate.Thiocyanate;
import survivalblock.thiocyanate_test.ThiocyanateTestmod;

import static survivalblock.thiocyanate_test.ThiocyanateTestmod.*;

public class ThiocyanateTestDataGenerator implements DataGeneratorEntrypoint {
    public static volatile boolean datapacking = false;

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack featureCycle = createBuiltinDataPack(fabricDataGenerator, Thiocyanate.id("feature_cycle"));
        CyanideDynamicRegistriesGenerator featureCycleGenerator = featureCycle.addProvider(CyanideDynamicRegistriesGenerator::new);
        featureCycleGenerator.attach(NOOP_1, NOOP_2, NOOP_3);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, ThiocyanateTestmod::bootstrapConfiguredFeatures);
        registryBuilder.add(Registries.PLACED_FEATURE, ThiocyanateTestmod::bootstrapPlacedFeatures);
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
