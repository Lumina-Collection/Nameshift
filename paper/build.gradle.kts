import org.apache.groovy.util.Maps

plugins {
    id("io.github.goooler.shadow") version "8.1.7"
    id("io.papermc.paperweight.userdev") version "1.+"
}

dependencies {
    paperweight.paperDevBundle(libs.paper.get().version)
    implementation(project(":common"))
    implementation(libs.commandapi.shade)
    api(libs.axios)
    implementation(libs.supervanish)
    implementation(libs.luckperms)
    implementation(libs.sayanvanish.api)
    implementation(libs.sayanvanish.bukkit)
}
paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
tasks {
    processResources {
        outputs.upToDateWhen { false }
        filesMatching("**/*.yml") {
            val properties = Maps.of(
                "name", rootProject.extra.get("displayName"),
                "version", rootProject.extra.get("fullVersion"),
                "group", project.group
            )
            expand(properties)
        }
    }
    shadowJar {
        dependencies {
            include(dependency("${project.group}:.*"))
        }
        relocate("dev.jorel.commandapi", "software.axios.libs.commandapi")
        archiveFileName.set("${rootProject.extra.get("displayName")}-Paper-${rootProject.extra.get("fullVersion")}.jar")
    }
    artifacts {
        archives(shadowJar)
    }
}