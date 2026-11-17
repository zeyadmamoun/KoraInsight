plugins {
    id("koraInsight.android.library")
    id("koraInsight.android.library.compose")
    id("koraInsight.android.feature")
    id("koraInsight.android.hilt")
}

android {
    namespace = "com.zeyadmamoun.matchdetails"
}

dependencies {
    implementation(libs.material)
    implementation(libs.lottie)
}
