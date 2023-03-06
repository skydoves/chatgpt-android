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

package com.skydoves.chatgpt.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val DarkChatGPTColorScheme = darkColorScheme(
  primary = STREAM_PRIMARY,
  primaryContainer = STREAM_PRIMARY,
  secondary = STREAM_PRIMARY,
  background = STREAM_PRIMARY,
  tertiary = WHITE200,
  onTertiary = GRAY200
)

private val LightChatGPTColorScheme = lightColorScheme(
  primary = STREAM_PRIMARY,
  primaryContainer = STREAM_PRIMARY,
  secondary = STREAM_PRIMARY,
  background = WHITE200,
  tertiary = WHITE200,
  onTertiary = GRAY200
)

/** Light Android background theme */
private val LightAndroidBackgroundTheme = BackgroundTheme(color = Color.White)

/** Dark Android background theme */
private val DarkAndroidBackgroundTheme = BackgroundTheme(color = BACKGROUND900)

@Composable
fun ChatGPTComposeTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkChatGPTColorScheme else LightChatGPTColorScheme
  val backgroundTheme = if (darkTheme) DarkAndroidBackgroundTheme else LightAndroidBackgroundTheme

  CompositionLocalProvider(
    LocalBackgroundTheme provides backgroundTheme
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
