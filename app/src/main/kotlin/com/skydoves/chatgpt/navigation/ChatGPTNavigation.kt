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

package com.skydoves.chatgpt.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.skydoves.chatgpt.core.navigation.AppComposeNavigator
import com.skydoves.chatgpt.core.navigation.ChatGPTScreens
import com.skydoves.chatgpt.feature.chat.channels.ChatGPTChannels
import com.skydoves.chatgpt.feature.chat.messages.ChatGPTMessages
import com.skydoves.chatgpt.ui.ChatGPTSmallTopBar

fun NavGraphBuilder.chatGPTHomeNavigation(
  composeNavigator: AppComposeNavigator
) {
  composable(route = ChatGPTScreens.Channels.name) {
    Scaffold(topBar = { ChatGPTSmallTopBar() }) { padding ->
      ChatGPTChannels(
        modifier = Modifier.padding(padding),
        composeNavigator = composeNavigator
      )
    }
  }

  composable(
    route = ChatGPTScreens.Messages.name,
    arguments = ChatGPTScreens.Messages.navArguments
  ) {
    val channelId = it.arguments?.getString("channelId") ?: return@composable
    ChatGPTMessages(
      channelId = channelId,
      composeNavigator = composeNavigator,
      viewModel = hiltViewModel()
    )
  }
}
