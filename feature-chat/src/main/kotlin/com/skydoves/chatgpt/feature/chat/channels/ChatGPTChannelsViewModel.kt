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

package com.skydoves.chatgpt.feature.chat.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.chatgpt.core.data.repository.GPTChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.utils.onError
import io.getstream.chat.android.client.utils.onSuccessSuspend
import io.getstream.log.streamLog
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ChatGPTChannelsViewModel @Inject constructor(
  private val gptChannelRepository: GPTChannelRepository
) : ViewModel() {

  private val channelsMutableUiState =
    MutableStateFlow<GPTChannelUiState>(GPTChannelUiState.Nothing)
  val channelUiState: StateFlow<GPTChannelUiState> = channelsMutableUiState

  private val isBalloonDisplayedMutableState =
    MutableStateFlow(gptChannelRepository.isBalloonChannelDisplayed())
  val isBalloonDisplayedState: StateFlow<Boolean> = isBalloonDisplayedMutableState

  init {
    viewModelScope.launch {
      gptChannelRepository.streamUserFlow().collect { user ->
        user?.let {
          gptChannelRepository.joinTheCommonChannel(it)
        } ?: run {
          streamLog {
            "User is null. Please check the app README.md and ensure " +
              "**Disable Auth Checks** is ON in the Dashboard"
          }
        }
      }
    }
  }

  fun handleEvents(gptChannelEvent: GPTChannelEvent) {
    when (gptChannelEvent) {
      GPTChannelEvent.CreateChannel -> createRandomChannel()
    }
  }

  private fun createRandomChannel() {
    viewModelScope.launch {
      channelsMutableUiState.value = GPTChannelUiState.Loading
      val result = gptChannelRepository.createRandomChannel()
      result.onSuccessSuspend {
        channelsMutableUiState.value = GPTChannelUiState.Success(it.id)
        delay(100L)
        channelsMutableUiState.value = GPTChannelUiState.Nothing
      }.onError {
        channelsMutableUiState.value = GPTChannelUiState.Error
      }
    }
  }

  fun balloonChannelDisplayed() {
    isBalloonDisplayedMutableState.value = true
    gptChannelRepository.balloonChannelDisplayed()
  }
}

sealed interface GPTChannelEvent {
  object CreateChannel : GPTChannelEvent
}

sealed interface GPTChannelUiState {
  object Nothing : GPTChannelUiState
  object Loading : GPTChannelUiState
  data class Success(val channelId: String) : GPTChannelUiState
  object Error : GPTChannelUiState
}
