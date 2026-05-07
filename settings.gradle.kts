import java.io.File
import java.util.Properties

pluginManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenLocal()
        mavenCentral()
    }
}

rootProject.name = "AndroidInstrumentation"
include(":app")

fun parseVersionTokens(name: String): List<Int> =
    name.split(".").map { it.toIntOrNull() ?: 0 }

fun resolveSdkAapt2Path(rootDir: File): String? {
    val localFile = rootDir.resolve("local.properties")
    if (!localFile.isFile) return null
    val props = Properties()
    localFile.inputStream().use { props.load(it) }
    val sdkDir = props.getProperty("sdk.dir") ?: return null
    val toolsRoot = File(sdkDir, "build-tools")
    if (!toolsRoot.isDirectory) return null
    val bestDir = toolsRoot.listFiles()
        ?.filter { it.isDirectory && File(it, "aapt2").isFile }
        ?.maxWithOrNull { a, b ->
            val va = parseVersionTokens(a.name)
            val vb = parseVersionTokens(b.name)
            val n = maxOf(va.size, vb.size)
            for (i in 0 until n) {
                val cmp = va.getOrElse(i) { 0 }.compareTo(vb.getOrElse(i) { 0 })
                if (cmp != 0) return@maxWithOrNull cmp
            }
            0
        } ?: return null
    return File(bestDir, "aapt2").absolutePath
}

// AGP reads this as a -P project property; extra/beforeProject alone is not enough.
val sdkAapt2Path = resolveSdkAapt2Path(settings.rootDir)
if (sdkAapt2Path != null) {
    val merged = gradle.startParameter.projectProperties.toMutableMap()
    merged["android.aapt2FromMavenOverride"] = sdkAapt2Path
    gradle.startParameter.projectProperties = merged
    gradle.beforeProject {
        extra["android.aapt2FromMavenOverride"] = sdkAapt2Path
    }
}
