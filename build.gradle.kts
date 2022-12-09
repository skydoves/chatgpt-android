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
  id("com.android.library") version "7.3.0" apply false
  id("org.jetbrains.kotlin.android") version "1.7.20" apply false
}
