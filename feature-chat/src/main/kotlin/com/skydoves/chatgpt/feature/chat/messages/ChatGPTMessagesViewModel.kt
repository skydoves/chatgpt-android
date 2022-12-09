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

package com.skydoves.chatgpt.feature.chat.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.chatgpt.core.data.coroutines.WhileSubscribedOrRetained
import com.skydoves.chatgpt.core.data.repository.GPTMessageRepository
import com.skydoves.chatgpt.core.model.GPTChatRequest
import com.skydoves.chatgpt.core.model.GPTContent
import com.skydoves.chatgpt.core.model.GPTMessage
import com.skydoves.chatgpt.core.network.BuildConfig.CONVERSATION_ID
import com.skydoves.chatgpt.core.network.BuildConfig.PARENT_MESSAGE_ID
import com.skydoves.sandwich.messageOrNull
import com.skydoves.sandwich.onFailure
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Message
import io.getstream.log.streamLog
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ChatGPTMessagesViewModel @Inject constructor(
  private val GPTMessageRepository: GPTMessageRepository,
  private val chatClient: ChatClient
) : ViewModel() {

  private val messageItemSet = MutableStateFlow<Set<String>>(setOf())
  val isLoading: StateFlow<Boolean> = messageItemSet.map {
    it.isNotEmpty()
  }.stateIn(viewModelScope, WhileSubscribedOrRetained, false)

  fun sendHelloMessage(channelId: String) {
    viewModelScope.launch {
      sendStreamMessage(channelId, "Hi, I'm OpenAI's ChatGPT. Ask me anything!")
    }
  }

  fun sendMessage(channelId: String, text: String) {
    messageItemSet.value += text
    viewModelScope.launch {
      val request = GPTChatRequest(
        messages = listOf(
          GPTMessage(
            id = UUID.randomUUID().toString(),
            content = GPTContent(parts = listOf(text))
          )
        ),
        conversation_id = CONVERSATION_ID,
        parent_message_id = PARENT_MESSAGE_ID
      )
      val result = GPTMessageRepository.sendMessage(request)
      result.collect {
        it.suspendOnSuccess {
          streamLog { "onResponse: $data" }
          messageItemSet.value -= text
          sendStreamMessage(channelId, data)
        }.onFailure {
          streamLog { "Failure: $messageOrNull" }
        }
      }
    }
  }

  private suspend fun sendStreamMessage(channelId: String, text: String) {
    val channelClient = chatClient.channel(channelId)
    channelClient.sendMessage(
      message = Message(
        id = UUID.randomUUID().toString(),
        cid = channelClient.cid,
        text = text,
        extraData = mutableMapOf("ChatGPT" to true)
      )
    ).await()
  }
}
