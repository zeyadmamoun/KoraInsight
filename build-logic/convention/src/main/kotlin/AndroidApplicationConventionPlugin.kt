import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target){
            // 1 - apply the android application plugin.
            with(pluginManager){
                apply("com.android.application")
            }

            // 2 - configure the android application similar to android block in build.gradle.kts.
            extensions.configure<ApplicationExtension>{
                compileSdk {
                    version = release(37)
                }

                defaultConfig {
                    targetSdk = 37
                    minSdk = 24
                    versionCode = 1
                    versionName = "1.0"
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }
            }

            // 3. Add Base Dependencies common to all applications
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies.apply {
                add("implementation", libs.findLibrary("androidx-core-ktx").get())
                add("implementation", libs.findLibrary("androidx-navigation3-runtime").get())
                add("implementation", libs.findLibrary("androidx-navigation3-ui").get())
                add("testImplementation", libs.findLibrary("junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx-espresso-core").get())

                // module dependency here
                add("implementation", project(":feature:fixtures"))
                add("implementation", project(":feature:leagues"))
                add("implementation", project(":feature:profile"))
                add("implementation", project(":feature:news"))
                add("implementation", project(":feature:match-details"))
            }
        }
    }
}