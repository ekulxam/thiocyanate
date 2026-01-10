package survivalblock.thiocyanate_test.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class CyanideDynamicRegistriesGenerator extends FabricDynamicRegistryProvider {
    public CyanideDynamicRegistriesGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider provider, Entries entries) {
        // IT'S SO CLEANNNNNNN
        Stream.of(Registries.PLACED_FEATURE)
                .map(provider::lookupOrThrow)
                .forEach(entries::addAll);
    }

    @Override
    public String getName() {
        return "Dynamic registries";
    }
}
