plugins {
    id("koraInsight.android.library")
}

android {
    namespace = "com.zeyadmamoun.feature"
}

dependencies {
    implementation(libs.material)
    implementation(project(":core"))
}