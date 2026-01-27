//? if >=26 {
package survivalblock.thiocyanate.cyanide.mixin.accessor;

import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.ResourceManagerRegistryLoadTask;
import net.minecraft.server.packs.repository.KnownPack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings({"UnusedMixin", "unused"})
@Mixin(ResourceManagerRegistryLoadTask.class)
public interface ResourceManagerRegistryLoadTaskAccessor {
    @Accessor("REGISTRATION_INFO_CACHE")
    static Function<Optional<KnownPack>, RegistrationInfo> thiocyanate$getRegistrationInfoCache() {
        throw new UnsupportedOperationException("Mixin accessor");
    }
}
//?}