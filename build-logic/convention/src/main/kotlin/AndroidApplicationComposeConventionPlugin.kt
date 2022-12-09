import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.skydoves.chatgpt.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply("com.android.application")
      val extension = extensions.getByType<BaseAppModuleExtension>()
      configureAndroidCompose(extension)
    }
  }
}
