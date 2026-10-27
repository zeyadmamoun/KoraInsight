plugins {
    id("koraInsight.android.library")
}

android {
    namespace = "com.zeyadmamoun.core"
}

dependencies {
    api(project(":core:common"))
    implementation(libs.material)
}
