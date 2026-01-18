//? if fabric {
package survivalblock.thiocyanate.cyanide.platform;

import com.google.gson.JsonElement;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.impl.registry.sync.DynamicRegistryViewImpl;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import survivalblock.thiocyanate.Thiocyanate;

import java.util.Map;

public class ThiocyanateFabric extends Thiocyanate implements ModInitializer {
    @Override
    public void onInitialize() {
        Thiocyanate.setInstance(this);
    }

    // begin credit: cyanide
    /**
     * @see net.fabricmc.fabric.mixin.registry.sync.RegistryDataLoaderMixin
     */
    @Override
    public void postFabricBeforeRegistryLoadEvent(Map<ResourceKey<? extends Registry<?>>, Registry<?>> registryMap) {
        DynamicRegistrySetupCallback.EVENT.invoker().onRegistrySetup(new DynamicRegistryViewImpl(registryMap));
    }

    /**
     * @see net.fabricmc.fabric.mixin.resource.conditions.RegistryDataLoaderMixin
     */
    @Override
    public boolean checkFabricConditions(JsonElement json, ResourceKey<?> key, RegistryOps.RegistryInfoLookup lookup) {
        return json.isJsonObject() && !ResourceConditionsImpl.applyResourceConditions(json.getAsJsonObject(), key.registry().toString(), key.identifier(), lookup);
    }
    // end credit
}
//?}