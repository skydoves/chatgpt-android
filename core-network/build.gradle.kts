/*
 * Designed and developed by 2022 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

/*
 * Designed and developed by 2022 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
  id("skydoves.android.library")
  id("skydoves.android.hilt")
  id("skydoves.spotless")
  id("com.google.devtools.ksp")
}

dependencies {
  implementation(project(":core-model"))

  api(libs.okhttp.logging)
  api(libs.retrofit.core)
  api(libs.sandwich)
}

android {
  defaultConfig {
    buildConfigField(
      "String",
      "GPT_SESSION",
      "\"" + gradleLocalProperties(rootDir).getProperty("GPT_SESSION", "") + "\""
    )
    buildConfigField(
      "String",
      "CF_CLEARANCE",
      "\"" + gradleLocalProperties(rootDir).getProperty("CF_CLEARANCE", "") + "\""
    )
  }
}
