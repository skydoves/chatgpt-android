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

package com.skydoves.chatgpt.feature.chat.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.skydoves.chatgpt.core.designsystem.theme.BACKGROUND900
import com.skydoves.chatgpt.core.designsystem.theme.STREAM_PRIMARY
import com.skydoves.chatgpt.core.designsystem.theme.STREAM_PRIMARY_LIGHT
import com.skydoves.chatgpt.feature.chat.reactions.ChatGPTReactionFactory
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.theme.StreamColors

@Composable
fun ChatGPTStreamTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val streamColors = if (darkTheme) {
    StreamColors.defaultDarkColors().copy(
      appBackground = BACKGROUND900,
      primaryAccent = STREAM_PRIMARY,
      ownMessagesBackground = STREAM_PRIMARY
    )
  } else {
    StreamColors.defaultColors().copy(
      primaryAccent = STREAM_PRIMARY,
      ownMessagesBackground = STREAM_PRIMARY_LIGHT
    )
  }

  ChatTheme(
    colors = streamColors,
    reactionIconFactory = ChatGPTReactionFactory(),
    content = content
  )
}
