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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.skydoves.chatgpt.core.data.chat.commonChannelId
import com.skydoves.chatgpt.core.data.coroutines.WhileSubscribedOrRetained
import com.skydoves.chatgpt.core.data.repository.GPTMessageRepository
import com.skydoves.chatgpt.core.navigation.ChatGPTScreens.Companion.argument_channel_id
import com.skydoves.chatgpt.core.preferences.Empty
import com.skydoves.chatgpt.feature.chat.worker.ChatGPTMessageWorker
import com.skydoves.chatgpt.feature.chat.worker.ChatGPTMessageWorker.Companion.DATA_FAILURE
import com.skydoves.chatgpt.feature.chat.worker.ChatGPTMessageWorker.Companion.DATA_MESSAGE_ID
import com.skydoves.chatgpt.feature.chat.worker.ChatGPTMessageWorker.Companion.DATA_SUCCESS
import com.skydoves.chatgpt.feature.chat.worker.ChatGPTMessageWorker.Companion.MESSAGE_EXTRA_CHAT_GPT
import com.skydoves.chatgpt.feature.chat.worker.ChatGPTMessageWorker.Companion.MESSAGE_EXTRA_CONVERSATION_ID
import com.skydoves.viewmodel.lifecycle.viewModelLifecycleOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.compose.state.messages.list.MessageItemState
import io.getstream.chat.android.compose.state.messages.list.MessageListItemState
import io.getstream.log.streamLog
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ChatGPTMessagesViewModel @Inject constructor(
  GPTMessageRepository: GPTMessageRepository,
  private val chatClient: ChatClient,
  private val workManager: WorkManager,
  savedStateHandle: SavedStateHandle
) : ViewModel() {

  private val channelId: String = savedStateHandle.get<String>(argument_channel_id) ?: String.Empty

  private val messageItemSet = MutableStateFlow<Set<String>>(setOf())
  val isLoading: StateFlow<Boolean> = messageItemSet.map {
    it.isNotEmpty()
  }.stateIn(viewModelScope, WhileSubscribedOrRetained, false)

  private val mutableError: MutableStateFlow<String> = MutableStateFlow(String.Empty)
  val errorMessage: StateFlow<String> = mutableError
    .filter { it.isNotEmpty() }
    .stateIn(viewModelScope, WhileSubscribedOrRetained, String.Empty)

  val isMessageEmpty: StateFlow<Boolean> =
    GPTMessageRepository.watchIsChannelMessageEmpty(channelId)
      .stateIn(viewModelScope, SharingStarted.Lazily, false)

  fun sendStreamChatMessage(text: String) {
    if (channelId != commonChannelId) {
      viewModelScope.launch { sendStreamMessage(text) }
    }
  }

  fun sendMessage(text: String, messagesItems: List<MessageListItemState>) {
    messageItemSet.value += text
    viewModelScope.launch {
      val lastGptMessage = messagesItems
        .filterIsInstance<MessageItemState>()
        .filter { it.message.extraData[MESSAGE_EXTRA_CHAT_GPT] == true }
        .maxByOrNull { it.message.createdAt?.time ?: 0 }
        ?.message
      val parentId = lastGptMessage?.id ?: UUID.randomUUID().toString()
      val conversationId = lastGptMessage?.extraData?.get(MESSAGE_EXTRA_CONVERSATION_ID) as? String
      val workRequest = buildGPTMessageWorkerRequest(text, parentId, conversationId)
      workManager.enqueue(workRequest)

      val workInfo = workManager.getWorkInfoByIdLiveData(workRequest.id)
      workInfo.observe(viewModelLifecycleOwner) {
        if (it.state == WorkInfo.State.SUCCEEDED) {
          val gptMessageText = it.outputData.getString(DATA_SUCCESS)
          val gptMessageId = it.outputData.getString(DATA_MESSAGE_ID)
          streamLog { "gpt message worker success: $gptMessageId $gptMessageText" }
          messageItemSet.value -= text
        } else if (it.state == WorkInfo.State.FAILED) {
          val error = it.outputData.getString(DATA_FAILURE) ?: ""
          streamLog { "gpt message worker failed: $error" }
          messageItemSet.value -= messageItemSet.value
          mutableError.value = error
        }
      }
    }
  }

  private suspend fun sendStreamMessage(text: String) {
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

  private fun buildGPTMessageWorkerRequest(
    text: String,
    parentId: String,
    conversationId: String?
  ): OneTimeWorkRequest {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val data = Data.Builder()
      .putString(ChatGPTMessageWorker.DATA_TEXT, text)
      .putString(ChatGPTMessageWorker.DATA_CHANNEL_ID, channelId)
      .putString(ChatGPTMessageWorker.DATA_PARENT_ID, parentId)
      .putString(ChatGPTMessageWorker.DATA_CONVERSATION_ID, conversationId)
      .build()

    return OneTimeWorkRequest.Builder(ChatGPTMessageWorker::class.java)
      .setConstraints(constraints)
      .setInputData(data)
      .build()
  }
}
