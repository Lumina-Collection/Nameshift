import java.io.ByteArrayOutputStream

defaultTasks("build")

subprojects {
    apply(plugin = "java-library")

    group = "net.luminacollection"
    version = "1.0-SNAPSHOT"
    rootProject.extra.set("displayName", "Nameshift")

    plugins.withType<JavaPlugin> {
        configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    fun determinePatchVersion(): Any {
        val tagInfo = ByteArrayOutputStream()
        exec {
            commandLine("git", "describe", "--tags")
            standardOutput = tagInfo
        }
        val tagInfoString = tagInfo.toString()
        if (!tagInfoString.contains('-')) return 0
        return tagInfoString.split('-')[1]
    }

    rootProject.extra.set("majorVersion", 1)
    rootProject.extra.set("minorVersion", 0)
    rootProject.extra.set("patchVersion", determinePatchVersion())
    rootProject.extra.set("apiVersion", rootProject.extra.get("majorVersion").toString() + "." + rootProject.extra.get("minorVersion"))
    rootProject.extra.set("fullVersion", rootProject.extra.get("apiVersion").toString() + "." + rootProject.extra.get("patchVersion"))

    repositories {
        val GITHUB_TOKEN = uri("https://gpr-token.loapu.dev/").toURL().readText()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://Loapu:${GITHUB_TOKEN}@maven.pkg.github.com/Loapu/Axios")
        maven("https://jitpack.io")
        flatDir { dirs(project.rootDir.path + "/libs") }
        maven("https://repo.sayandev.org/snapshots/")
    }
}