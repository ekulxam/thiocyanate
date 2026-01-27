//? if neoforge {
/*package survivalblock.thiocyanate.cyanide.platform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import survivalblock.thiocyanate.Thiocyanate;

import java.util.Optional;

@Mod(Thiocyanate.MOD_ID)
public class ThiocyanateNeoforge extends Thiocyanate {
    public ThiocyanateNeoforge() {
        Thiocyanate.setInstance(this);
    }

    // begin credit: cyanide
    @Override
    public <T> Decoder<Optional<T>> getNeoForgeConditionalCodec(Codec<T> codec) {
        return ConditionalOps.createConditionalCodec(NeoForgeExtraCodecs.decodeOnly(codec));
    }
    // end credit
}
*///?}