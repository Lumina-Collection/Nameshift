import org.apache.groovy.util.Maps

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.0"
}

dependencies {
    implementation(project(":common"))
    implementation(libs.paper)
    implementation(libs.commandapi.shade)
    api(libs.axios)
}

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
            include(dependency("${rootProject.group}:.*"))
        }
        relocate("dev.jorel.commandapi", "software.axios.libs.commandapi")
        archiveFileName.set("${rootProject.extra.get("displayName")}-Paper-${rootProject.extra.get("fullVersion")}.jar")
    }
    artifacts {
        archives(shadowJar)
    }
}