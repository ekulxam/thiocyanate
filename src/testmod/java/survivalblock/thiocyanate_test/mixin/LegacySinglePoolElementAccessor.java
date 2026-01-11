package survivalblock.thiocyanate_test.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(LegacySinglePoolElement.class)
public interface LegacySinglePoolElementAccessor {

    @Invoker("<init>")
    static LegacySinglePoolElement thiocyanate_test$invokeInit(Either<Identifier, StructureTemplate> either,
                                                               Holder<StructureProcessorList> holder,
                                                               StructureTemplatePool.Projection projection,
                                                               Optional<LiquidSettings> optional) {
        throw new UnsupportedOperationException("Mixin invoker");
    }
}
