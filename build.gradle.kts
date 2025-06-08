import org.yaml.snakeyaml.Yaml
import xyz.jpenilla.runpaper.task.RunServer
import java.net.URI

plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "8.3.3"
}

group = "org.evlis"
version = "0.6.1"

val targetJavaVersion = 21

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = URI("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = URI("https://oss.sonatype.org/content/groups/public/")
    }
    maven { // https://github.com/aikar/commands/wiki
        name = "aikars-framework"
        url = URI("https://repo.aikar.co/content/groups/aikar/")
    }
    maven { // https://github.com/mfnalex/ChestSort/blob/master/HOW_TO_USE_API.md
        name = "jeff-media-public"
        url = URI("https://repo.jeff-media.com/public/")
    }
    maven { // repo for Multiverse 5
        name = "onarandombox-public"
        url = URI("https://repo.onarandombox.com/content/groups/public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("org.mvplugins.multiverse.core:multiverse-core:5.0.0-SNAPSHOT")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("de.jeff_media:ChestSortAPI:13.0.0-SNAPSHOT")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

java {
    sourceCompatibility = JavaVersion.toVersion(targetJavaVersion)
    targetCompatibility = JavaVersion.toVersion(targetJavaVersion)
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        // Add dependencies for use in gradle itself
        classpath("org.yaml:snakeyaml:2.3")
    }
}

// Main Plugin List (core server needs only)
val prodPlugins = runPaper.downloadPluginsSpec {
    modrinth("chunky", "1.4.28")
    modrinth("luckperms", "v5.5.0-bukkit")
    modrinth("lunamatic", "2.0.1")
    modrinth("multiverse-core", "5.0.1")
    modrinth("multiverse-inventories", "5.0.1")
    modrinth("multiverse-portals", "5.0.1")
    modrinth("multiverse-netherportals", "5.0.1")
    modrinth("terra", "6.6.1-BETA-bukkit")
    hangar("WorldEdit", "7.3.14")
    modrinth("worldguard", "7.0.13")
}

// Plugin List for automated testing
val testPlugins = runPaper.downloadPluginsSpec {
    from(prodPlugins) // Copy everything from prod
    github("Ifiht", "AutoStop", "v1.2.0", "AutoStop-1.2.0.jar")
}

tasks.withType<JavaCompile>().all {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
    options.isFork = true
    options.release.set(targetJavaVersion)
}

tasks {
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    build { dependsOn(shadowJar) }
    shadowJar {
        relocate("co.aikar.commands", "Lunamatic.acf")
        relocate("co.aikar.locales", "Lunamatic.locales")
    }
    test {
        useJUnitPlatform()
        testLogging { events("passed", "skipped", "failed") }
    }
    runServer {
        // Keep runServer task to inherit project plugin
        downloadPlugins.from(prodPlugins)
        minecraftVersion("1.21.4")
    }
}

// Test PaperMC run & immediately shut down, for github actions
tasks.register<RunServer>("runServerTest") {
    dependsOn(tasks.shadowJar)
    minecraftVersion("1.21.4")
    downloadPlugins.from(testPlugins)
    pluginJars.from(tasks.shadowJar)
}
// Start a local PaperMC test server for login & manual testing
tasks.register<RunServer>("runServerInteractive") {
    dependsOn(tasks.shadowJar)
    minecraftVersion("1.21.4")
    downloadPlugins.from(prodPlugins)
    pluginJars.from(tasks.shadowJar)
}

tasks.register("checkServerLogs") {
    doLast {
        // Path to the latest.log file
        val logFile = File("run/logs/latest.log")

        // Check if the log file exists
        if (!logFile.exists()) {
            throw GradleException("Log file not found: " + logFile.absolutePath)
        }

        // Read the log file line by line
        val logContent = logFile.readLines()

        // Find lines that contain the " ERROR]:" substring
        val errorLines = logContent.filter { it.contains("ERROR]:") }

        if (!errorLines.isEmpty()) {
            println("Errors were found:")
            errorLines.forEach(::println)
            throw GradleException("Errors found in log file.")
        } else {
            println("No errors found in log file.")
        }
    }
}