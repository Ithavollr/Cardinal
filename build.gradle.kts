import org.yaml.snakeyaml.Yaml
import xyz.jpenilla.runpaper.task.RunServer
import java.net.URI

plugins {
    java
    id("com.github.ben-manes.versions") version "0.52.0"
    //id("ca.cutterslade.analyze") version "1.10.0" // uncomment to check, fails build tho
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "8.3.3"
    id("org.flywaydb.flyway") version "11.9.1"
}

group = "org.evlis"
version = "0.6.3-SEED"

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
    maven { // repo for Multiverse 5
        name = "onarandombox-public"
        url = URI("https://repo.onarandombox.com/content/groups/public/")
    }
}

dependencies { // run ./gradlew dependencyUpdates to update, ./gradlew analyzeDependencies to check
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    // Multiverse
    compileOnly("org.mvplugins.multiverse.core:multiverse-core:5.3.3")
    compileOnly("org.mvplugins.multiverse.portals:multiverse-portals:5.1.1")
    compileOnly("org.mvplugins.multiverse.netherportals:multiverse-netherportals:5.0.3")
    // Lombok - simplify getting and setting in DTOs:
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")
    // post-compiles
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("org.jdbi:jdbi3-core:3.49.4")
    implementation("org.jdbi:jdbi3-sqlobject:3.49.4")
    implementation("org.flywaydb:flyway-core:11.9.1")
    testImplementation(platform("org.junit:junit-bom:5.13.0"))
    testImplementation("org.mockito:mockito-core:5.18.0")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.50.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.13.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.12.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.13.1")
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
        classpath("org.yaml:snakeyaml:2.4")
    }
}

// Main Plugin List (core server needs only)
val prodPlugins = runPaper.downloadPluginsSpec {
    modrinth("chunky", "1.4.28")
    modrinth("luckperms", "v5.5.0-bukkit")
    modrinth("lunamatic", "2.0.7")
    modrinth("multiverse-core", "5.3.3")
    modrinth("multiverse-inventories", "5.2.0")
    modrinth("multiverse-portals", "5.1.1")
    modrinth("multiverse-netherportals", "5.0.3")
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