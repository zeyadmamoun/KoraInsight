plugins {
    id("koraInsight.android.library")
}

android {
    namespace = "com.zeyadmamoun.core"
}

dependencies {
    api(libs.kotlinx.datetime)
    implementation(libs.material)
}
