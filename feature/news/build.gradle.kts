plugins {
    id("koraInsight.android.library")
    id("koraInsight.android.library.compose")
    id("koraInsight.android.feature")
    id("koraInsight.android.hilt")
}

android {
    namespace = "com.zeyadmamoun.news"
}

dependencies {
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.material)
}