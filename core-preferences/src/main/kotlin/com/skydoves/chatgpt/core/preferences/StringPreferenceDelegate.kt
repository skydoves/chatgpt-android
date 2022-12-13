/*
 * Designed and developed by 2022 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.chatgpt.core.preferences

import androidx.core.content.edit
import kotlin.reflect.KProperty

fun stringPreferences(key: String, defaultValue: String) =
  StringPreferenceDelegate(key, defaultValue)

class StringPreferenceDelegate(
  private val key: String,
  private val defaultValue: String
) {
  operator fun getValue(preferences: Preferences, property: KProperty<*>): String {
    return preferences.sharedPreferences.getString(key, null) ?: let {
      setValue(preferences, property, defaultValue)
      defaultValue
    }
  }

  operator fun setValue(preferences: Preferences, property: KProperty<*>, value: String) {
    preferences.sharedPreferences.edit { putString(key, value) }
  }
}
