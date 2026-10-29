plugins {
    id("koraInsight.android.library")
    id("koraInsight.android.library.compose")
    id("koraInsight.android.feature")
}

android {
    namespace = "com.zeyadmamoun.fixtures"
}

dependencies {
    implementation(libs.material)
    implementation(project(":core:network"))
}