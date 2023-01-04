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

import android.content.SharedPreferences
import com.skydoves.chatgpt.core.preferences.delegate.booleanPreferences
import com.skydoves.chatgpt.core.preferences.delegate.stringPreferences
import java.util.UUID
import javax.inject.Inject

class Preferences @Inject constructor(
  val sharedPreferences: SharedPreferences
) {

  val userUUID: String by stringPreferences(
    key = KEY_UUID,
    defaultValue = UUID.randomUUID().toString()
  )

  var authorization: String by stringPreferences(
    key = KEY_AUTHORIZATION,
    defaultValue = String.Empty
  )

  var cookie: String by stringPreferences(
    key = KEY_COOKIE,
    defaultValue = String.Empty
  )

  var userAgent: String by stringPreferences(
    key = KEY_USER_AGENT,
    defaultValue = String.Empty
  )

  var balloonChannelDisplayed: Boolean by booleanPreferences(
    key = KEY_BALLOON_CHANNEL_DISPLAYED,
    defaultValue = false
  )

  companion object {
    private const val KEY_UUID: String = "key_uuid"
    private const val KEY_AUTHORIZATION: String = "key_authorization"
    private const val KEY_COOKIE: String = "key_cookie"
    private const val KEY_USER_AGENT: String = "key_user_agent"
    private const val KEY_BALLOON_CHANNEL_DISPLAYED = "key_balloon_channel_displayed"
  }
}

val String.Companion.Empty
  inline get() = ""
