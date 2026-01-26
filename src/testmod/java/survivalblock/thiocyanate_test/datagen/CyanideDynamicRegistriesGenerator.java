//? if fabric {
/*package survivalblock.thiocyanate_test.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CyanideDynamicRegistriesGenerator extends FabricDynamicRegistryProvider {
    protected final Set<ResourceKey<?>> keys = new HashSet<>();

    public CyanideDynamicRegistriesGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider provider, Entries entries) {
        // IT'S SO CLEANNNNNNN
        this.keys.stream()
                .map(provider::getOrThrow)
                .forEach(entries::add);
    }

    public void attach(ResourceKey<?>... keys) {
        this.keys.addAll(Arrays.asList(keys));
    }

    @Override
    public String getName() {
        return "Dynamic registries";
    }
}
*///?}