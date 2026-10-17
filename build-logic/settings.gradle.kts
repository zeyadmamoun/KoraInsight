/**
 * here is the build-logic version of settings.gradle.kts
 * it contains the following:
 * 1 - dependencyResolutionManagement  ---> from where our depenedencies will be imported and our toml file.
 * 2 - root project. name = build-logic ---> it's a gradle prject that build the other modules.
 * 3 - included modules required for build process which is the build-logic module.
 * */
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")