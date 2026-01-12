package survivalblock.thiocyanate_test.mixin;

import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProcessorLists.class)
public interface ProcessorListsAccessor {
    @Accessor("EMPTY")
    static ResourceKey<StructureProcessorList> thiocyanate_test$getEmpty() {
        throw new UnsupportedOperationException();
    }
}
