plugins {
    id("net.minecraftforge.gradle") version "6.0.24"
    id("eclipse")
    id("maven-publish")
}

group = "com.sharecodechestbypycoder"
version = "1.0.0"

base {
    archivesName.set("sharecodechestbypycoder")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

minecraft {
    mappings("official", "1.20.1")
    copyIdeResources.set(true)

    runs {
        create("client") {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            mods {
                create("sharecodechestbypycoder") {
                    source(sourceSets.main.get())
                }
            }
        }

        create("server") {
            workingDirectory(project.file("run-server"))
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
            workingDirectory(project.file("run-data"))
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


