import org.yaml.snakeyaml.Yaml
import xyz.jpenilla.runpaper.task.RunServer
import java.net.URI

plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "8.3.3"
}

group = "org.evlis"
version = "0.4.3"

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
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
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

// Main Plugin List
val prodPlugins = runPaper.downloadPluginsSpec {
    modrinth("auraskills", "2.2.4")
    url("https://github.com/Ifiht/Cardinal/raw/refs/heads/main/plugin_jars/mc1-21-1/AuraMobs-4.0.8.jar")
    url("https://github.com/Ifiht/Cardinal/raw/refs/heads/main/plugin_jars/mc1-21-1/ChestSort-14.1.2.jar")
    modrinth("chunky", "1.4.28")
    hangar("DeathChest", "2.2.7")
    modrinth("decentholograms", "2.8.11")
    url("https://github.com/Ifiht/Cardinal/raw/refs/heads/main/plugin_jars/mc1-21-1/DHS-0.6.1_for_MC-1.21.1.jar")
    modrinth("discordsrv", "1.28.0")
    modrinth("interactionvisualizer", "1.18.11")
    url("https://github.com/Ifiht/Cardinal/raw/refs/heads/main/plugin_jars/mc1-21-1/LuckPerms-Bukkit-5.4.131.jar")
    modrinth("lunamatic", "1.0.1")
    hangar("multiverse-core", "4.3.14")
    hangar("multiverse-portals", "4.2.3")
    hangar("multiverse-inventories", "4.2.6")
    hangar("multiverse-netherportals", "4.2.3")
    url("https://github.com/Ifiht/OpeNLogin/releases/download/v4.0.0/OpenLogin-4.0.0.jar")
    modrinth("seemore", "1.0.2")
    modrinth("simple-voice-chat", "bukkit-2.5.26")
    url("https://cdn.modrinth.com/data/PFb7ZqK6/versions/2WtLC9mv/squaremap-paper-mc1.21.1-1.3.2.jar")
    modrinth("toolstats", "1.8.2")
    url("https://cdn.modrinth.com/data/1u6JkXh5/versions/ecqqLKUO/worldedit-bukkit-7.3.8.jar")
    modrinth("worldguard", "7.0.12")
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
        val props = mapOf("version" to "version")
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
        minecraftVersion("1.21.1")
    }
}

// Test Paper run & immediately shut down, for github actions
tasks.register<RunServer>("runServerTest") {
    dependsOn(tasks.shadowJar)
    dependsOn(tasks.named("injectBotToken"))
    minecraftVersion("1.21.1")
    downloadPlugins.from(testPlugins)
    pluginJars.from(tasks.shadowJar)
}
// Start a local test server for login & manual testing
tasks.register<RunServer>("runServerInteractive") {
    dependsOn(tasks.shadowJar)
    dependsOn(tasks.named("injectBotToken"))
    minecraftVersion("1.21.1")
    downloadPlugins.from(prodPlugins)
    pluginJars.from(tasks.shadowJar)
}

// Start a local Folia server for manual testing
runPaper.folia.registerTask {
    minecraftVersion("1.20.6")
}
tasks.register("injectBotToken") {
    doLast {
        val token = System.getenv("BOT_API_TOKEN")
            ?: error("Environment variable BOT_API_TOKEN is not set.")

        val yamlFile = file("run/plugins/DiscordSRV/config.yml")
        val yaml = Yaml()

        // Parse YAML
        val data: MutableMap<String, Any> = yaml.load(yamlFile.readText())

        // Update the token value
        data["BotToken"] = token
        val channels = data["Channels"] as? MutableMap<String, String>
            ?: error("'Channels' key is missing or not a map in the YAML file.")
        channels["global"] = "1320959908581081119"

        // Write the YAML back
        yamlFile.writeText(yaml.dump(data))
    }
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