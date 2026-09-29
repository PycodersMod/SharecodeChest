import java.nio.charset.StandardCharsets
import java.util.Base64

plugins {
    id("net.minecraftforge.gradle") version "6.0.24"
    id("eclipse")
    id("maven-publish")
}

group = "com.pycoder.sharecodechest"
version = "1.0.0"

base {
    archivesName.set("sharecodechestbypycoder")
}

val pycodersRunDir = file(providers.gradleProperty("pycodersRuntimeDir").orElse("../../runtime/legacy-import/SharecodeChest/run").get())
fun decodeArgs(name: String): List<String> = providers.gradleProperty(name).orNull?.takeIf { it.isNotEmpty() }?.split('.')?.map { if (it == "_") "" else String(Base64.getDecoder().decode(it), StandardCharsets.UTF_8) } ?: emptyList()
val pycodersGameArgs = decodeArgs("pycodersGameArgsB64")
val pycodersJavaArgs = decodeArgs("pycodersJavaArgsB64")
val pycodersUsername = providers.gradleProperty("pycodersUsername").orElse("Dev").get()

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

minecraft {
    mappings("official", "1.20.1")
    copyIdeResources.set(true)

    runs {
        create("client") {
            workingDirectory(pycodersRunDir)
            args("--username", pycodersUsername)
            pycodersGameArgs.forEach { args(it) }
            pycodersJavaArgs.forEach { jvmArg(it) }
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            mods {
                create("sharecodechestbypycoder") {
                    source(sourceSets.main.get())
                }
            }
        }

        create("server") {
            workingDirectory(pycodersRunDir)
            pycodersGameArgs.forEach { args(it) }
            pycodersJavaArgs.forEach { jvmArg(it) }
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            args("--nogui")
            mods {
                create("sharecodechestbypycoder") {
                    source(sourceSets.main.get())
                }
            }
        }

        create("data") {
            workingDirectory(pycodersRunDir)
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            args("--mod", "sharecodechestbypycoder", "--all", "--output", file("src/generated/resources/"), "--existing", file("src/main/resources/"))
            mods {
                create("sharecodechestbypycoder") {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

repositories {
    maven("https://maven.minecraftforge.net/")
    mavenCentral()
}

dependencies {
    minecraft("net.minecraftforge:forge:1.20.1-47.2.0")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}


