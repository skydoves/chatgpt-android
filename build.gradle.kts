buildscript {
  repositories {
    google()
    maven("https://plugins.gradle.org/m2/")
  }
}

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.hilt) apply false
  alias(libs.plugins.spotless) apply false
  alias(libs.plugins.gms.googleServices) apply false
  alias(libs.plugins.firebase.crashlytics) apply false
}
