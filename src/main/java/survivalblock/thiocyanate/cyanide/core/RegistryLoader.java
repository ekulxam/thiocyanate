package survivalblock.thiocyanate.cyanide.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
//? if >=26
/*import net.minecraft.core.registries.ConcurrentHolderGetter;*/
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryDataLoader.RegistryData;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
//? if >=26
/*import net.minecraft.util.thread.ParallelMapTransform;*/
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import survivalblock.thiocyanate.Thiocyanate;
import survivalblock.thiocyanate.cyanide.mixin.accessor.MappedRegistryAccessor;
import survivalblock.thiocyanate.cyanide.platform.XPlatform;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
//? if >=26
/*import java.util.concurrent.CompletableFuture;*/
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static survivalblock.thiocyanate.cyanide.mixin.accessor.RegistryDataLoaderAccessor.*;
import static survivalblock.thiocyanate.Thiocyanate.LOGGER;

/**
 * Cyanide's rewrite of {@link RegistryDataLoader}, with an increased emphasis on proper error reporting.
 * <p>
 * <strong>N.B.</strong> this implementation has to respect both NeoForge patches, and Fabric API mixins which allow mods to provide
 * datapack registries.
 */
@SuppressWarnings("JavadocReference")
public final class RegistryLoader {
    public static final Pattern PATTERN_LINE = Pattern.compile("at line (\\d+) column (\\d+)");

    private RegistryLoader() {
    }

    /**
     * @see RegistryDataLoader#load(RegistryDataLoader.LoadingFunction, List, List)
     * @see RegistryDataLoader#load(RegistryDataLoader.LoaderFactory, List, List, Executor)  
     */
    public static /*? <=26 {*/ RegistryAccess.Frozen /*?} else {*/ /*CompletableFuture<RegistryAccess.Frozen>*//*?}*/ load(
        ResourceManager resourceManager,
        List<HolderLookup.RegistryLookup<?>> contextRegistries,
        List<RegistryData<?>> registriesToLoad
        /*? >=26 {*/ /*, Executor executor *//*?}*/
    ) {
        //? if >=26 {
        /*return CompletableFuture.supplyAsync(
                () -> {
        *///?}
                    final Reporter reporter = new Reporter();
                    final List<Loader<?>> registryLoader = registriesToLoad.stream().<Loader<?>>map(Loader::new).toList();
                    final Map<ResourceKey<? extends Registry<?>>, RegistryOps.RegistryInfo<?>> registryLookup = new IdentityHashMap<>();
                    final RegistryOps.RegistryInfoLookup lookup = new RegistryOps.RegistryInfoLookup() {
                        @Override
                        @SuppressWarnings("unchecked")
                        public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryKey) {
                            return Optional.ofNullable((RegistryOps.RegistryInfo<T>) registryLookup.get(registryKey));
                        }
                    };

                    // Trigger Fabric's callback before any loading is complete
                    final Map<ResourceKey<? extends Registry<?>>, Registry<?>> registryMap = new IdentityHashMap<>(registriesToLoad.size());
                    registryLoader.forEach(loader -> registryMap.put(loader.registry.key(), loader.registry));
                    XPlatform.getInstance().postFabricBeforeRegistryLoadEvent(registryMap);

                    // Populate lookup with both static registries, and dynamic (datapack) ones
                    // Create new empty registries for the dynamic ones, and record them in the top-level registry map
                    //
                    // Note that we want to use a custom registration lookup for new registries - this allows us to track when elements are being
                    // referenced, and then resolve these references as they get defined, rather than only knowing after the fact what element
                    // was trying to reference something that never got defined.

                    contextRegistries.forEach(entry -> registryLookup.put(entry.key(), thiocyanate$createInfoForContextRegistry(entry)));
                    registryLoader.forEach(entry -> registryLookup.put(entry.data.key(), createNewRegistryInfo(/*? <=26 {*/ thiocyanate$createInfoForNewRegistry(entry.registry) /*?} else {*/ /*entry.createRegistryInfo()*//*?}*/, reporter)));

                    // Load each registry content sequentially
                    //? if <26
                    registryLoader.forEach(
                    //? if >=26 {
                    /*CompletableFuture<Void> loadCompletions = CompletableFuture.allOf(
                    registryLoader.stream().map(
                    *///?}
                            loader -> loadRegistry(resourceManager, lookup, loader, reporter/*? >=26 {*//*, executor *//*?}*/)
                    )/*? >=26 {*/ /*.toArray(CompletableFuture[]::new)) *//*?}*/;

                    //? if >=26
                    /*return loadCompletions.thenApplyAsync(ignored -> {*/
                        // Attempt to freeze registries. This will fail if there are unbound elements in the registry, which we should be able to handle
                        // gracefully, because we know what causes these errors
                        registryLoader.forEach(loader -> freezeRegistry(loader, reporter));

                        // At this point, we have collected all errors. If any have been raised, we build and print an informative error with the
                        // causes.
                        if (reporter.hasError()) {
                            throw Thiocyanate.createException(reporter.buildError());
                        }

                        // Bake registries - everything should be fine at this point
                        final List<? extends WritableRegistry<?>> registries = registryLoader.stream()
                                .map(loader -> loader.registry)
                                .toList();

                        return new RegistryAccess.ImmutableRegistryAccess(registries).freeze();
        //? if >=26 {
                    /*}, executor);
                }).thenCompose(cf -> cf);
        *///?}
    }

    /**
     * @see MappedRegistry#createRegistrationLookup()
     */
    public static <T> RegistryOps.RegistryInfo<T> createNewRegistryInfo(RegistryOps.RegistryInfo<T> originalInfo, Reporter reporter) {
        final var originalLookup = originalInfo.getter();
        return new RegistryOps.RegistryInfo<>(originalInfo.owner(), new HolderGetter<>() {
            @Override
            public Optional<Holder.Reference<T>> get(ResourceKey<T> key) {
                // The original will always do this when lookup up, leaving unbound values later
                return Optional.of(getOrThrow(key));
            }

            @Override
            public Holder.Reference<T> getOrThrow(ResourceKey<T> key) {
                // This is the core location where we are able to track "unbound holders in registry"
                // When a holder gets referenced, it's in one of two states: unbound or bound
                // 1. If it is bound, this element has already been resolved, meaning we don't need to track it
                // 2. If it is un-bound, we record the source context that we accessed this with (the resource key of the
                //    accessor, and resource).
                //
                // Later, as we register elements, we remove any elements from out "unbound references" list that get
                // registered. In the end, we should have a map of any unbound holders to the location(s) where they got
                // referenced!
                final Holder.Reference<T> holder = originalLookup.getOrThrow(key);
                if (reporter.currentReference != null && !holder.isBound()) {
                    // A source reference is known, and we are accessing an unbound holder, so make sure we record the
                    // reference here
                    reporter.unboundReferences.computeIfAbsent(key, k -> new ArrayList<>()).add(reporter.currentReference);
                }
                return holder;
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> tagKey) {
                return originalLookup.get(tagKey);
            }

            @Override
            public HolderSet.Named<T> getOrThrow(TagKey<T> tagKey) {
                return originalLookup.getOrThrow(tagKey);
            }
        }, originalInfo.elementsLifecycle());
    }

    /**
     * @see RegistryDataLoader#loadContentsFromManager
     * @see RegistryDataLoader.ResourceManagerRegistryLoadTask#load(RegistryOps.RegistryInfoLookup, Executor) 
     */
    public static <T> /*? <=26 {*/ void /*?} else {*/ /*CompletableFuture<?>*//*?}*/ loadRegistry(
        ResourceManager resourceManager,
        RegistryOps.RegistryInfoLookup lookup,
        Loader<T> loader,
        Reporter reporter
        /*? >=26 {*/ /*, Executor executor *//*?}*/
    ) {
        final String registryPath = Registries.elementsDirPath(loader.registry.key());
        final FileToIdConverter converter = FileToIdConverter.json(registryPath);
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, lookup);

        final Function<Optional<KnownPack>, RegistrationInfo> infoCache = thiocyanate$getRegistrationInfoCache();
        //? if <26
        final RegistryReporter registryReporter = reporter.registry(loader.data.key());

        // Modify the element codec to add conditions, as per NeoForge's patch
        Decoder<Optional<T>> decoder = XPlatform.getInstance().getNeoForgeConditionalCodec(loader.data.elementCodec());
        //? if <26
        for (Map.Entry<Identifier, Resource> entry : converter.listMatchingResources(resourceManager).entrySet()) {
        //? if >=26 {
        /*return CompletableFuture.supplyAsync(() -> converter.listMatchingResources(resourceManager), executor)
                .thenCompose(registryResources ->
                        ParallelMapTransform.schedule(registryResources, (id, resource) -> {
        *///?}
                            //? if <26
                            final Identifier id = entry.getKey();
                            final ResourceKey<T> key = ResourceKey.create(loader.registry.key(), converter.fileToId(id));
                            //? if <26
                            final Resource resource = entry.getValue();
                            final RegistrationInfo info = infoCache.apply(resource.knownPackInfo());
                            //? if >=26
                            /*final RegistryReporter registryReporter = reporter.registry(loader.data.key());*/

                            // Populate the unbound reference, as we are able to track any references this element makes
                            reporter.currentReference = new UnboundReference(resource, key);

                            final JsonElement json;
                            try (Reader reader = resource.openAsReader()) {
                                json = JsonParser.parseReader(reader);
                            } catch (JsonSyntaxException e) {
                                // If the JSON was malformed, we try and extract a line number from the output, and we print the JSON if we can
                                final StringBuilder error = new StringBuilder();

                                // JsonParser.parseReader wraps everything, so grab the cause for the more relevant error
                                error.append("Syntax Error: %s\n".formatted(e.getCause() instanceof MalformedJsonException ? e.getCause().getMessage() : e.getMessage()));

                                try (Reader reader = resource.openAsReader()) {
                                    final List<String> rawText = IOUtils.readLines(reader);
                                    final Matcher match = PATTERN_LINE.matcher(e.getMessage());

                                    if (match.find()) {
                                        final int lineNo = Integer.parseInt(match.group(1));
                                        final int columnNo = Integer.parseInt(match.group(2));

                                        final String spacing = " ".repeat(Math.max(columnNo - 2, 0));
                                        final List<String> contextLines = rawText.subList(Math.max(0, lineNo - 5), Math.min(rawText.size(), lineNo));

                                        error.append("  at:\n%s\n%s^\n%shere\n".formatted(
                                            String.join("\n", contextLines),
                                            spacing,
                                            spacing
                                        ));
                                    }
                                } catch (IOException o) {
                                    LOGGER.warn("Unable to read raw text", o);
                                }

                                registryReporter.loadingErrors.put(key, new LoadingError(resource.sourcePackId(), error.toString()));
                                /*? <=26 {*/ continue; /*?} else {*/ /*return AlmostRegistered.error(key, info, registryReporter, resource);*//*?}*/
                            } catch (JsonIOException | IOException e) {
                                registryReporter.loadingErrors.put(key, new LoadingError(resource.sourcePackId(), "IO Error: " + e.getMessage()));
                                /*? <=26 {*/ continue; /*?} else {*/ /*return AlmostRegistered.error(key, info, registryReporter, resource);*//*?}*/
                            }

                            // Before parsing, consider conditions. Both loaders implement some variant of them.
                            // - Fabric implements conditions as a basic check on the JSON itself
                            // - NeoForge implements conditions using a wrapped decoder
                            //
                            // So, we support both
                            if (XPlatform.getInstance().checkFabricConditions(json, key, lookup)) {
                                /*? <=26 {*/ continue; /*?} else {*/ /*return AlmostRegistered.error(key, info, registryReporter, resource);*//*?}*/
                            }

                            // The optional will be null if NeoForge's conditions fail to pass
                            // In this case, we log the same message, otherwise we register the element
                            final DataResult<Optional<T>> result = decoder.parse(ops, json);
        //? if >=26 {
                            /*return new AlmostRegistered<>(key, result, info, registryReporter, resource);
                        }, executor)).thenAcceptAsync(loadedEntries -> {
                            synchronized (loader.writeLock) {
                                loadedEntries.forEach((identifier, almostRegistered) -> {
                                    ResourceKey<T> key = almostRegistered.key;
                                    DataResult<Optional<T>> result = almostRegistered.result;
                                    RegistrationInfo info = almostRegistered.info;
                                    RegistryReporter registryReporter = almostRegistered.reporter;
        *///?}
                                    result.ifSuccess(candidate ->
                                            candidate.ifPresentOrElse(
                                                    value -> loader.registry.register(key, value, info),
                                                    () -> LOGGER.debug("Skipping loading registry entry {} as its conditions were not met", key)
                                            ));
                                    result.ifError(error -> registryReporter.loadingErrors.put(key, new LoadingError(
                                            /*? <=26 {*/ resource.sourcePackId() /*?} else {*/ /*almostRegistered.sourcePackId*//*?}*/,
                                            "Parsing Error: " + error.message()
                                    )));

                                    // In addition to registering the object, we need to remove it from a list of possibly unbound references
                                    // This prevents us from hanging on to various other references when we know the object exists
                                    //
                                    // Note that we do this **even if there was a loading error**. Why? Because if there was a loading error,
                                    // we would already have a more accurate error than "unbound holder", because we know it was at least a file
                                    // we were trying to load in the first place.
                                    reporter.unboundReferences.remove(key);
                                    reporter.currentReference = null;
        //? if <26
        }
        //? if >=26 {
                                /*});
                            }
                            var key = loader.registry.key();
                            TagLoader.ElementLookup<Holder<T>> tagElementLookup = TagLoader.ElementLookup.fromGetters(
                                    key, loader.concurrentRegistrationGetter, loader.registry
                            );
                            Map<TagKey<T>, List<Holder<T>>> pendingTags = TagLoader.loadTagsForRegistry(resourceManager, key, tagElementLookup);
                            synchronized (loader.writeLock) {
                                loader.registry.bindTags(pendingTags);
                            }
                        }, executor);
        *///?}

        //? if <26
        TagLoader.loadTagsForRegistry(resourceManager, loader.registry);
    }

    @SuppressWarnings("unchecked")
    public static <T> void freezeRegistry(Loader<T> loader, Reporter reporter) {
        final RegistryReporter registryReporter = reporter.registry(loader.data.key());

        // Registry freezing has two conditions:
        // 1. Check that all holders are bound,
        // 2. Check that there are no unregistered intrusive holders
        //
        // For the first check, we can do it more gracefully by checking manually, as we can classify and
        // exclude errors based on i.e. if we already know something failed to parse, any references to it will be broken.
        //
        // For the second, this is only caused by a mod failing to register something - not a datapack. So we assume this won't
        // occur, and have minimal error handling for this case.
        ((MappedRegistryAccessor<T>) loader.registry).thiocyanate$getByKey()
            .entrySet()
            .stream()
            // Exclude unbound value errors that were caused by an element we already have an associated loading error from,
            // as the loading error is likely much more informative
            .filter(e -> !e.getValue().isBound() && !registryReporter.loadingErrors.containsKey(e.getKey()))
            .forEach(e -> {
                final StringBuilder error = new StringBuilder();

                error.append("Missing File Error: '%s' was referenced but not defined\n".formatted(prettyId(e.getKey())));

                // Consult the map of unbound references to figure out who was referencing this object
                reporter.unboundReferences.getOrDefault(e.getKey(), List.of())
                    .stream()
                    .map(ref -> at(ref.resource.sourcePackId(), ref.key))
                    .distinct()
                    .sorted()
                    .forEach(message -> error
                        .append(message)
                        .append("\n"));

                registryReporter.unboundErrors.add(error.toString());
            });

        try {
            loader.registry.freeze();
        } catch (IllegalStateException e) {
            final String message = e.getMessage();

            if (message.startsWith("Some intrusive holders were not registered")) {
                // Intrusive holder error - this is a mod error
                registryReporter.miscErrors.add("Likely Mod Error: " + e.getMessage());
            } else if (!message.startsWith("Unbound values in registry")) {
                // Unknown error - try as best we can to raise it
                registryReporter.miscErrors.add("Unknown Error: " + e.getMessage());
            }
            // If we hit an unbound registry error, we must have marked the unbound elements earlier,
            // either because they were hit with a loading error, or added to the unbound errors
            //
            // So, we don't need to do anything here for that case
        }

        // Only report empty registry errors if there were no loading errors for the registry,
        // as this would then be a knock on effect
        if (loader.data.requiredNonEmpty() && loader.registry.isEmpty() && registryReporter.loadingErrors.isEmpty()) {
            registryReporter.miscErrors.add("Empty registry: " + loader.data.key().identifier());
        }
    }

    /**
     * Returns a pretty-printed reference for a resource key. For example, {@code "namespace:worldgen/configured_feature/path"}
     */
    public static Identifier prettyId(ResourceKey<?> key) {
        return key.identifier().withPrefix(key.registry().getPath() + "/");
    }

    public static String at(String sourcePackId, ResourceKey<?> key) {
        return "  at '%s' defined in '%s'".formatted(prettyId(key), sourcePackId);
    }

    public static class Loader<T> {
        public final RegistryData<T> data;
        public final WritableRegistry<T> registry;
        //? if >=26 {
        /*public final ConcurrentHolderGetter<T> concurrentRegistrationGetter;
        public final Object writeLock = new Object();
        *///?}

        public Loader(RegistryData<T> data) {
            this.data = data;
            this.registry = new MappedRegistry<>(data.key(), Lifecycle.stable());
            /*? >=26 {*/ /*this.concurrentRegistrationGetter = new ConcurrentHolderGetter<>(this.writeLock, this.registry.createRegistrationLookup()); *//*?}*/
        }

        //? if >=26 {
        /*public RegistryOps.RegistryInfo<T> createRegistryInfo() {
            return new RegistryOps.RegistryInfo<>(this.registry, this.concurrentRegistrationGetter, this.registry.registryLifecycle());
        }
        *///?}
    }

    public static class Reporter {
        public final Map<ResourceKey<? extends Registry<?>>, RegistryReporter> errors = new IdentityHashMap<>();
        public final Map<ResourceKey<?>, List<UnboundReference>> unboundReferences = new IdentityHashMap<>();
        public @Nullable UnboundReference currentReference = null;

        public RegistryReporter registry(ResourceKey<? extends Registry<?>> key) {
            return this.errors.computeIfAbsent(key, k -> new RegistryReporter(new IdentityHashMap<>(), new ArrayList<>(), new ArrayList<>()));
        }

        public boolean hasError() {
            return this.errors.values()
                .stream()
                .anyMatch(RegistryReporter::hasError);
        }

        public String buildError() {
            final StringBuilder builder = new StringBuilder();

            builder.append("registry loading\n\n===== An error occurred loading registries =====\n\n");
            this.errors.forEach((key, reporter) -> {
                if (reporter.hasError()) {
                    reporter.buildError(builder, key);
                }
            });

            return builder.toString();
        }
    }

    public record RegistryReporter(
        Map<ResourceKey<?>, LoadingError> loadingErrors,
        List<String> unboundErrors,
        List<String> miscErrors
    ) {
        public boolean hasError() {
            return !this.loadingErrors.isEmpty()
                || !this.unboundErrors.isEmpty()
                || !this.miscErrors.isEmpty();
        }

        public void buildError(StringBuilder builder, ResourceKey<? extends Registry<?>> key) {
            builder.append("Registry '%s':\n\n".formatted(key.identifier()));

            this.loadingErrors.entrySet()
                .stream()
                .sorted(Comparator.comparing(e -> e.getKey().identifier()))
                .forEach(entry -> builder
                    .append(entry.getValue().message)
                    .append("\n")
                    .append(at(entry.getValue().sourcePackId, entry.getKey()))
                    .append("\n\n"));

            this.unboundErrors.forEach(error -> builder
                .append(error)
                .append("\n"));

            this.miscErrors.forEach(error -> builder
                .append(error)
                .append("\n"));

            builder.append("-----\n");
        }
    }

    public record UnboundReference(Resource resource, ResourceKey<?> key) {}
    public record LoadingError(String sourcePackId, String message) {}

    //? if >=26 {
    /*public record AlmostRegistered<T>(ResourceKey<T> key, DataResult<Optional<T>> result, RegistrationInfo info, RegistryReporter reporter, String sourcePackId) {
        public AlmostRegistered(ResourceKey<T> key, DataResult<Optional<T>> result, RegistrationInfo info, RegistryReporter reporter, Resource resource) {
            this(key, result, info, reporter, resource.sourcePackId());
        }

        public static <T> AlmostRegistered<T> error(ResourceKey<T> key, RegistrationInfo info, RegistryReporter reporter, Resource resource) {
            return new AlmostRegistered<>(key, DataResult.error(() -> ""), info, reporter, resource);
        }
    }
    *///?}
}
