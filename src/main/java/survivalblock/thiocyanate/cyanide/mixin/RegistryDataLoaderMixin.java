package survivalblock.thiocyanate.cyanide.mixin;

import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.thiocyanate.cyanide.core.RegistryLoader;

@Mixin(RegistryDataLoader.class)
public abstract class RegistryDataLoaderMixin {
    @Inject(
        // We only are concerned with loading from disk, not from network
        method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void loadAndReportErrors(ResourceManager resourceManager, List<HolderLookup.RegistryLookup<?>> list, List<RegistryDataLoader.RegistryData<?>> list2, CallbackInfoReturnable<RegistryAccess.Frozen> cir) {
        cir.setReturnValue(RegistryLoader.load(resourceManager, list, list2));
    }
}
