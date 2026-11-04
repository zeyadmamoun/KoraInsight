plugins {
    id("koraInsight.android.library")
    id("koraInsight.android.library.compose")
    id("koraInsight.android.feature")
    id("koraInsight.android.hilt")
}

android {
    namespace = "com.zeyadmamoun.fixtures"
}

dependencies {
    implementation(libs.material)
    implementation(project(":core:data"))
}