package survivalblock.thiocyanate_test.mixin;

import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrapezoidHeight.class)
public interface TrapezoidHeightAccessor {
    @Accessor("minInclusive")
    VerticalAnchor thiocyanate_test$getMinInclusive();
    @Accessor("maxInclusive")
    VerticalAnchor thiocyanate_test$getMaxInclusive();
    @Accessor("plateau")
    int thiocyanate_test$getPlateau();
}
