package survivalblock.thiocyanate_test.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.resources.Identifier;

public class ThiocyanateTestDataGenerator implements DataGeneratorEntrypoint {
    public static volatile boolean datapacking = false;

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack featureCycle = createBuiltinDataPack(fabricDataGenerator, Thiocy);
    }

    /**
     * This is a blocking operation. Asynchronous execution is not supported.
     */
    public FabricDataGenerator.Pack createBuiltinDataPack(FabricDataGenerator fabricDataGenerator, Identifier id) {
        datapacking = true;
        fabricDataGenerator.createBuiltinResourcePack(id);
        datapacking = false;
    }
}
