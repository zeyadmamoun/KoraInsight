import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

dependencies{
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
}

gradlePlugin {
    plugins{
        register("androidApplication"){
            id = "koraInsight.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("androidLibrary"){
            id = "koraInsight.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}