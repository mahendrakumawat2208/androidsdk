import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("net.bytebuddy.byte-buddy-gradle-plugin")
}

android {
    namespace = "com.example.androidinstrumentation"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.androidinstrumentation"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        debug {
            // Route OTLP over USB with adb reverse when using a physical device.
            buildConfigField("String", "OTLP_ENDPOINT", "\"http://127.0.0.1:32002\"")
            buildConfigField("String", "DEMO_API_BASE_URL", "\"http://127.0.0.1:8000\"")
        }
        release {
            buildConfigField("String", "OTLP_ENDPOINT", "\"http://127.0.0.1:32002\"")
            buildConfigField("String", "DEMO_API_BASE_URL", "\"http://127.0.0.1:8000\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "META-INF/net.bytebuddy/build.plugins"
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

configurations.configureEach {
    exclude(group = "io.opentelemetry.android.instrumentation", module = "compose-click")
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    implementation("com.jio.otel:otel-lib:1.1.3")
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}
