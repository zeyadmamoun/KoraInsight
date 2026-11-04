plugins {
    id("koraInsight.android.application.compose")
    id("koraInsight.android.hilt")
}

android {
    namespace = "com.zeyadmamoun.korainsight"

    defaultConfig {
        applicationId = "com.zeyadmamoun.korainsight"
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
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
