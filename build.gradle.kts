import org.yaml.snakeyaml.Yaml
import xyz.jpenilla.runpaper.task.RunServer
import java.net.URI

plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "8.3.3"
}

group = "org.evlis"
version = "0.5.4"

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
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
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
    modrinth("decentholograms", "2.8.12")
    url("https://github.com/Ifiht/Cardinal/raw/refs/heads/main/plugin_jars/mc1-21-1/DHS-0.6.1_for_MC-1.21.1.jar")
    modrinth("discordsrv", "1.29.0")
    modrinth("interactionvisualizer", "1.18.13")
    url("https://github.com/Ifiht/Cardinal/raw/refs/heads/main/plugin_jars/mc1-21-1/LuckPerms-Bukkit-5.4.131.jar")
    modrinth("lunamatic", "1.2.0")
    url("https://cdn.discordapp.com/attachments/1317024623484735509/1338130058572857394/multiverse-core-5.0.0-alpha.15.jar?ex=67a9f63b&is=67a8a4bb&hm=de3556d88c8098549add7adb9d1a43aaeafdf73f09a4e08f8c32d7bfdd964bba&") // MV5-CORE
    url("https://cdn.discordapp.com/attachments/1317024623484735509/1338130059063463936/multiverse-inventories-5.0.0-alpha.15.jar?ex=67ab47bb&is=67a9f63b&hm=4e25df9f56a6818f167c125cbaf974dc43c6210629eddaae5baf3727d5178ad4&") // MV5-INVENTORIES
    url("https://cdn.discordapp.com/attachments/1317024623484735509/1338130060531728486/multiverse-portals-5.0.0-alpha.15.jar?ex=67ab47bb&is=67a9f63b&hm=e5bf2184a761ee7e9496e408e415a63f83ae9b84c4ac234679c6096aa8085721&") // MV5-PORTALS
    url("https://cdn.discordapp.com/attachments/1317024623484735509/1338130059889741865/multiverse-netherportals-5.0.0-alpha.15.jar?ex=67ab47bb&is=67a9f63b&hm=eba127435ef0fea811d04a054248e5a1a74248e78ac3d626da5aa2365bfebfac&") // MV5-NETHERPORTALS
    url("https://drive.google.com/file/d/122f61aaEhutK6KIsO8H1e01SQR1-q1Fc/view?usp=sharing") // TERRA
    modrinth("seemore", "1.0.2")
    modrinth("simple-voice-chat", "bukkit-2.5.26")
    modrinth("soul-graves", "1.2.1")
    url("https://cdn.modrinth.com/data/PFb7ZqK6/versions/DB47ULQI/squaremap-paper-mc1.21.4-1.3.4.jar")
    url("https://github.com/Ifiht/Cardinal/blob/main/plugin_jars/mc1-21-4/Terra-bukkit-6.5.1-BETA%2B0a952cff4-shaded.jar")
    modrinth("toolstats", "1.8.7")
    url("https://cdn.modrinth.com/data/1u6JkXh5/versions/4jRlujfz/worldedit-bukkit-7.3.10.jar")
    modrinth("worldguard", "7.0.13-beta-2")
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
    dependsOn(tasks.named("injectBotToken"))
    minecraftVersion("1.21.4")
    downloadPlugins.from(testPlugins)
    pluginJars.from(tasks.shadowJar)
}
// Start a local PaperMC test server for login & manual testing
tasks.register<RunServer>("runServerInteractive") {
    dependsOn(tasks.shadowJar)
    dependsOn(tasks.named("injectBotToken"))
    minecraftVersion("1.21.4")
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