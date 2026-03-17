plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom") version "1.15.1" apply false
    id("net.neoforged.moddev") version "2.0.134" apply false
    id("com.modrinth.minotaur") version "2.+" apply false
    kotlin("jvm") version "2.3.0" apply false
    id("com.google.devtools.ksp") version "2.3.0" apply false
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22" apply false
    id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.22" apply false
}
stonecutter active "26.1-fabric"

stonecutter parameters {
    val minecraft = (node.project.findProperty("deps.minecraft") ?: node.metadata.version) as String
    replacements.string {
        direction = eval(minecraft, ">1.21.10")
        replace("ResourceLocation", "Identifier")
    }
    replacements.string {
        direction = eval(minecraft, ">1.21.11")
        replace("FabricDataOutput", "FabricPackOutput")
    }
    val loader = ("${node.project.property("deps.compatibleLoaders")}".split(", ").toList())[0]
    constants.match(
        loader,
        "fabric",
        "neoforge"
    )
}