package survivalblock.thiocyanate.cyanide.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;

import java.util.stream.Stream;

public final class MixinHooks {
    private MixinHooks() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T) o;
    }

    public static <T> MapCodec<T> fieldOf(MapCodec<T> codec, String field) {
        return new MapCodec<>() {
            @Override
            public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
                return codec.keys(ops);
            }

            @Override
            public <T1> DataResult<T> decode(DynamicOps<T1> ops, MapLike<T1> input) {
                return codec.decode(ops, input).mapError(e -> e + "\n  at '" + field + "'");
            }

            @Override
            public <T1> RecordBuilder<T1> encode(T input, DynamicOps<T1> ops, RecordBuilder<T1> prefix) {
                return codec.encode(input, ops, prefix);
            }

            @Override
            public String toString() {
                return "[ThiocyanateWrapped: " + codec + "]";
            }
        };
    }

    //~ if >=26.1 'c.getValue()' -> 'c.value()' {
    //~ if >=26.1 'getMinValue()' -> 'min()' {
    //~ if >=26.1 'getMaxValue()' -> 'max()' {
    public static <T extends FloatProvider> Codec<T> validate(float min, float max, Codec<T> codec) {
        return codec.validate(provider -> {
            if (provider.min() < min) {
                return DataResult.error(() -> "Value provider too low (must be >= %g), got %s".formatted(min, prettyPrint(provider)));
            }
            if ( provider.max() > max) {
                return DataResult.error(() -> "Value provider too high (must be <= %g), got %s".formatted(max, prettyPrint(provider)));
            }
            return DataResult.success(provider);
        });
    }

    //? if >=26.2 {
    public static <T extends FloatProvider> Codec<T> validate(float min, Codec<T> codec) {
        return codec.validate(provider -> {
            if (provider.min() < min) {
                return DataResult.error(() -> "Value provider too low (must be >= %g), got %s".formatted(min, prettyPrint(provider)));
            }
            return DataResult.success(provider);
        });
    }
    //?}

    public static String prettyPrint(FloatProvider provider) {
        if (provider instanceof ConstantFloat c) return "" + c./*? >=26.1.1 {*/ value /*?} else {*/ /*getValue *//*?}*/();
        final Identifier id = BuiltInRegistries.FLOAT_PROVIDER_TYPE.getKey(provider./*? >=26.1.1 {*/ codec /*?} else {*/ /*getType *//*?}*/());
        return "%s[min=%g, max=%g]".formatted(id == null ? "" : id, provider./*? >=26.1.1 {*/ min /*?} else {*/ /*getMinValue *//*?}*/(), provider./*? >=26.1.1 {*/ max /*?} else {*/ /*getMaxValue *//*?}*/());
    }
    //~}
    //~}

    //~ if >=26.1 'getMinValue()' -> 'minInclusive()' {
    //~ if >=26.1 'getMaxValue()' -> 'maxInclusive()' {
    public static <T extends IntProvider> Codec<T> validate(int min, int max, Codec<T> codec) {
        return codec.validate(provider -> {
            if (provider.minInclusive() < min) {
                return DataResult.error(() -> "Value provider too low (must be >= %d), got %s".formatted(min, prettyPrint(provider)));
            }
            if (provider.maxInclusive() > max) {
                return DataResult.error(() -> "Value provider too high (must be <= %d), got %s".formatted(max, prettyPrint(provider)));
            }
            return DataResult.success(provider);
        });
    }

    public static String prettyPrint(IntProvider provider) {
        // This is a pretty good heuristic, that's better than showing "[-1--1]" for a constant -1
        if (provider instanceof ConstantInt c) return "" + c.value();
        //~ if >=26.1 'getType()' -> 'codec()'
        final Identifier id = BuiltInRegistries.INT_PROVIDER_TYPE.getKey(provider.codec());
        return "%s[min=%d, max=%d]".formatted(id == null ? "" : id, provider.minInclusive(), provider.maxInclusive());
    }
    //~}
    //~}
    //~}
}
