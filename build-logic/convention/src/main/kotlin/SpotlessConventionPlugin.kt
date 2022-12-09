import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class SpotlessConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply("com.diffplug.spotless")

      extensions.configure<SpotlessExtension> {
        kotlin {
          target("**/*.kt")
          targetExclude("**/build/**/*.kt")
          ktlint()
            .setUseExperimental(true)
            .userData(mapOf("android" to "true"))
            .editorConfigOverride(mapOf("indent_size" to 2, "continuation_indent_size" to 2))
          licenseHeaderFile(rootProject.file("$rootDir/spotless/copyright.kt"))
        }
        format("kts") {
          target("**/*.kts")
          targetExclude("**/build/**/*.kts")
          // Look for the first line that doesn't have a block comment (assumed to be the license)
          licenseHeaderFile(rootProject.file("spotless/copyright.kts"), "(^(?![\\/ ]\\*).*$)")
        }
        format("xml") {
          target("**/*.xml")
          targetExclude("**/build/**/*.xml")
          // Look for the first XML tag that isn't a comment (<!--) or the xml declaration (<?xml)
          licenseHeaderFile(rootProject.file("spotless/copyright.xml"), "(<[^!?])")
        }
      }
    }
  }
}
