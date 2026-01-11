package survivalblock.thiocyanate_test.datagen;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import survivalblock.thiocyanate_test.ThiocyanateTestmod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public class ExistingJsonDataGenerator implements DataProvider {

    private static final String OUTPUT = System.getProperty("fabric-api.datagen.output-dir");
    private static final String PACK_OUTPUT = OUTPUT + "/datapacks/test/data/cyanide/worldgen/";

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return CompletableFuture.allOf(
                CompletableFuture.runAsync(
                        () -> modifyFile(PACK_OUTPUT + "biome/invalid_precipitation.json", string -> string.replace("\"has_precipitation\": true", "\"precipitation\": \"very rainy\""))
                ),
                CompletableFuture.runAsync(
                        () -> modifyFile(PACK_OUTPUT + "configured_feature/invalid_json.json", string -> string.contains("config") ? string.replace(":", "") : string)
                )
        );
    }

    private static void modifyFile(String path, UnaryOperator<String> lineModifier) {
        File file = new File(path);
        List<String> outputLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                outputLines.add(lineModifier.apply(line));
            }
        } catch (IOException e) {
            ThiocyanateTestmod.LOGGER.error("Error reading file", e);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            outputLines.forEach(writer::println);
        } catch (IOException e) {
            ThiocyanateTestmod.LOGGER.error("Error printing to file", e);
        }
    }

    @Override
    public String getName() {
        return "Modifying existing jsons";
    }
}
