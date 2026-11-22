plugins {
    id("koraInsight.android.library")
    id("koraInsight.android.hilt")
}

android {
    namespace = "com.zeyadmamoun.ai"
}

dependencies {
    api(libs.litert.android)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xskip-metadata-version-check")
    }
}