plugins {
  id("skydoves.android.library")
  id("skydoves.android.library.compose")
  id("skydoves.android.feature")
  id("skydoves.android.hilt")
  id("skydoves.spotless")
}

dependencies {
  implementation(libs.androidx.lifecycle.runtimeCompose)
  implementation(libs.androidx.lifecycle.viewModelCompose)

  implementation(libs.webview.inspector)
}
