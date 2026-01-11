import org.gradle.jvm.tasks.Jar
import java.io.BufferedReader
import java.io.FileReader

plugins {
    id("net.fabricmc.fabric-loom")
    id("maven-publish")
    id("com.modrinth.minotaur")
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("dev.kikugie.fletching-table.fabric")
}

sourceSets {
    create("testmod") {
        compileClasspath += main.get().output + main.get().compileClasspath
        runtimeClasspath += main.get().output + main.get().runtimeClasspath
    }
}

version = "${project.property("mod_version")}+${stonecutter.current.version}"
group = project.property("maven_group") as String
val minecraft : String = if (hasProperty("deps.minecraft")) project.property("deps.minecraft") as String
    else stonecutter.current.version

base.archivesName = project.property("archives_base_name") as String

repositories {
}

fabricApi {
    configureDataGeneration {
        client = true
    }

    @Suppress("UnstableApiUsage")
    configureTests {
        createSourceSet = true
        modId = "${base.archivesName}_test"
        eula = true
    }
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${minecraft}")

    implementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    implementation("net.fabricmc.fabric-api:fabric-api:${project.property("deps.fabric_api")}")
}

stonecutter {
    replacements.string {
        direction = eval(minecraft, ">1.21.10")
        replace("ResourceLocation", "Identifier")
    }
    val loader = ("${project.property("deps.compatibleLoaders")}".split(", ").toList())[0]
    constants.match(
        loader,
        "fabric",
        "neoforge"
    )
}

fletchingTable {
    mixins.create("main") {
        // Default matches the default value in the annotation
        mixin("default", "${project.property("archives_base_name")}.mixins.json") {
            env("CLIENT", "survivalblock.thiocyanate.cyanide.mixin.client")
        }
    }
    mixins.all {
        automatic = true
    }
}

tasks.processResources {
    val modVersion = project.version
    val minecraftVersion = minecraft
    inputs.property("version", modVersion)
    inputs.property("minecraft", minecraftVersion)

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to modVersion,
                "minecraft" to minecraftVersion
            )
        )
    }
}

tasks.named<Copy>("processTestmodResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named("build") {
    finalizedBy("autoVersionChangelog")
}

tasks.register("autoVersionChangelog") {
    doLast {
        val changelog = File("changelog.md")
        val reader = BufferedReader(FileReader(changelog))
        val lines = reader.readLines().toMutableList()
        if (!lines.isEmpty()) {
            val title = "Thiocyanate ${project.property("mod_version")}"
            lines[0] = title
            changelog.bufferedWriter().use { writer ->
                for (i in 0..<lines.size) {
                    writer.write(lines[i])
                    if (i != lines.size - 1) {
                        writer.newLine()
                    }
                }
            }
            println("Changelog header successfully replaced as $title")
        }
    }
}

loom {
    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }

    runConfigs["client"].apply {
        programArgs("--username=Survivalblock", "--uuid=c45e97e6-94ef-42da-8b5e-0c3209551c3f")
    }

    runs {
        create("testmodClient") {
            client()
            configName = "Testmod Client"
            source(sourceSets["testmod"])
        }

        create("testmodServer") {
            server()
            name = "Testmod Server"
            source(sourceSets["testmod"])
        }

        create("testmodDatagen") {
            client()
            name = "Testmod Data Generation"
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/testmod/generated")}")
            vmArg("-Dfabric-api.datagen.modid=thiocyanate_test")
            runDir("build/datagen")
            source(sourceSets["testmod"])
        }
    }

    mods {
        create("thiocyanate_test") {
            sourceSet("testmod")
        }
    }

    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")

    /*mixin {
        useLegacyMixinAp = true
    }*/
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    val java = if (stonecutter.eval(minecraft, ">=26")) {
        JavaVersion.VERSION_25
    } else if (stonecutter.eval(minecraft, ">=1.20.5")) {
        JavaVersion.VERSION_21
    } else {
        JavaVersion.VERSION_17
    }

    targetCompatibility = java
    sourceCompatibility = java
}

tasks.jar {
    inputs.property("archivesName", project.base.archivesName)

    from("LICENSE") {
        rename { "${it}_${base.archivesName}"}
    }
}

modrinth {
    token = providers.environmentVariable("MODRINTH_TOKEN")
    projectId = project.base.archivesName
    version = project.version
    uploadFile.set(tasks.named<Jar>("jar").get().archiveFile)
    additionalFiles.add(tasks.named<Jar>("sourcesJar").get().archiveFile)
    gameVersions.addAll("${project.property("deps.compatibleVersions")}".split(", ").toList())
    loaders.addAll("${project.property("deps.compatibleLoaders")}".split(", ").toList())
    changelog = rootProject.file("changelog.md").readText()
    syncBodyFrom = "<!--DO NOT EDIT MANUALLY: synced from gh readme-->\n" + rootProject.file("README.md").readText()
}