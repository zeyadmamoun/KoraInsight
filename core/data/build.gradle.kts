plugins {
    id("koraInsight.android.library")
    id("koraInsight.android.hilt")
}

android {
    namespace = "com.zeyadmamoun.data"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:common"))
    implementation(libs.material)
}