package survivalblock.thiocyanate.cyanide.mixin.accessor;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MappedRegistry.class)
public interface MappedRegistryAccessor<T> {
    @Accessor("byKey")
    Map<ResourceKey<T>, Holder.Reference<T>> thiocyanate$getByKey();
}
