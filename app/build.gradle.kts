plugins {
    id("koraInsight.android.application.compose")
}

android {
    namespace = "com.zeyadmamoun.korainsight"

    defaultConfig {
        applicationId = "com.zeyadmamoun.korainsight"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
}
