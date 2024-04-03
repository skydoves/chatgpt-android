/*
 * Designed and developed by 2024 skydoves (Jaewoong Eum)
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

package com.skydoves.chatgpt.feature.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.chatgpt.core.designsystem.theme.BACKGROUND900
import com.skydoves.chatgpt.core.designsystem.theme.WHITE200
import com.skydoves.chatgpt.core.navigation.AppComposeNavigator
import com.skydoves.chatgpt.core.navigation.ChatGPTScreens
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.InitializationState

@Composable
fun ChatGPTLogin(
  composeNavigator: AppComposeNavigator
) {
  val initializationState
    by ChatClient.instance().clientState.initializationState.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = initializationState) {
    if (initializationState == InitializationState.COMPLETE) {
      composeNavigator.navigateAndClearBackStack(ChatGPTScreens.Channels.name)
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(BACKGROUND900)
  ) {
    if (initializationState == InitializationState.INITIALIZING) {
      CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    } else if (initializationState == InitializationState.NOT_INITIALIZED) {
      Text(
        modifier = Modifier.padding(14.dp),
        text = stringResource(id = R.string.error_chat_sdk_initialization),
        color = WHITE200
      )
    }
  }
}
